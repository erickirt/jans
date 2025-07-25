// This software is available under the Apache-2.0 license.
// See https://www.apache.org/licenses/LICENSE-2.0.txt for full text.
//
// Copyright (c) 2024, Gluu, Inc.

use std::sync::RwLock;

use super::InitLockServiceError;
use super::interface::{Indexed, LogStorage, LogWriter, Loggable};
use super::memory_logger::MemoryLogger;
use super::nop_logger::NopLogger;
use super::stdout_logger::StdOutLogger;
use crate::app_types::{ApplicationName, PdpID};
use crate::bootstrap_config::log_config::{LogConfig, LogTypeConfig};
use crate::lock::LockService;
use serde::Serialize;

pub(crate) struct LogStrategy {
    logger: LogStrategyLogger,
    pdp_id: PdpID,
    app_name: Option<ApplicationName>,
    lock_service: RwLock<Option<LockService>>,
}

/// LogStrategy implements strategy pattern for logging.
/// It is used to provide a single point of access for logging and same api for different loggers.
pub(crate) enum LogStrategyLogger {
    Off(NopLogger),
    MemoryLogger(MemoryLogger),
    StdOut(StdOutLogger),
}

impl LogStrategy {
    /// Creates a new `LogStrategy` based on the provided configuration.
    /// Initializes the corresponding logger accordingly.
    pub async fn new(
        config: &LogConfig,
        pdp_id: PdpID,
        app_name: Option<ApplicationName>,
    ) -> Result<Self, InitLockServiceError> {
        let logger = match &config.log_type {
            LogTypeConfig::Off => LogStrategyLogger::Off(NopLogger),
            LogTypeConfig::Memory(memory_config) => LogStrategyLogger::MemoryLogger(
                MemoryLogger::new(memory_config.clone(), config.log_level, pdp_id, app_name.clone()),
            ),
            LogTypeConfig::StdOut => LogStrategyLogger::StdOut(StdOutLogger::new(config.log_level)),
        };
        Ok(Self {
            logger,
            pdp_id,
            app_name,
            lock_service: RwLock::new(None),
        })
    }

    pub fn new_with_logger(
        logger: LogStrategyLogger,
        pdp_id: PdpID,
        app_name: Option<ApplicationName>,
        lock_service: Option<LockService>,
    ) -> Self {
        Self {
            logger,
            pdp_id,
            app_name,
            lock_service: RwLock::new(lock_service),
        }
    }

    pub(crate) fn logger(&self) -> &LogStrategyLogger {
        &self.logger
    }

    pub fn set_lock_service(&self, lock_service: LockService) {
        *self
            .lock_service
            .write()
            .expect("obtain lock_service write lock") = Some(lock_service);
    }

    pub async fn shut_down(&self) {
        let lock = self
            .lock_service
            .write()
            .expect("obtain lock_service write lock")
            .take();

        if let Some(mut lock_service) = lock {
            lock_service.shut_down().await
        }
    }
}

#[derive(Serialize, PartialEq, Clone)]
pub(crate) struct LogEntryWithClientInfo<Entry: Loggable> {
    #[serde(flatten)]
    entry: Entry,
    pdp_id: PdpID,
    #[serde(rename = "application_id", skip_serializing_if = "Option::is_none")]
    app_name: Option<ApplicationName>,
}

impl<Entry: Loggable> Indexed for LogEntryWithClientInfo<Entry> {
    fn get_id(&self) -> uuid7::Uuid {
        self.entry.get_id()
    }

    fn get_additional_ids(&self) -> Vec<uuid7::Uuid> {
        self.entry.get_additional_ids()
    }

    fn get_tags(&self) -> Vec<&str> {
        self.entry.get_tags()
    }
}

impl<Entry: Loggable + Indexed> Loggable for LogEntryWithClientInfo<Entry> {
    fn get_log_level(&self) -> Option<super::LogLevel> {
        self.entry.get_log_level()
    }
}

// Implementation of LogWriter
impl LogWriter for LogStrategy {
    fn log_any<T: Loggable>(&self, entry: T) {
        let entry =
            LogEntryWithClientInfo::from_loggable(entry, self.pdp_id, self.app_name.clone());
        if let Some(lock_service) = self
            .lock_service
            .read()
            .expect("obtain lock_service read lock")
            .as_ref()
        {
            lock_service.log_any(entry.clone());
        }
        match &self.logger {
            LogStrategyLogger::Off(log) => log.log_any(entry),
            LogStrategyLogger::MemoryLogger(memory_logger) => memory_logger.log_any(entry),
            LogStrategyLogger::StdOut(std_out_logger) => std_out_logger.log_any(entry),
        }
    }
}

impl<E: Loggable> LogEntryWithClientInfo<E> {
    pub fn from_loggable(entry: E, pdp_id: PdpID, app_name: Option<ApplicationName>) -> Self {
        Self {
            entry,
            pdp_id,
            app_name,
        }
    }
}

// Implementation of LogStorage
// for cases where we not use memory logger we return default value
impl LogStorage for LogStrategy {
    fn pop_logs(&self) -> Vec<serde_json::Value> {
        match &self.logger {
            LogStrategyLogger::MemoryLogger(memory_logger) => memory_logger.pop_logs(),
            _ => Vec::new(),
        }
    }

    fn get_log_by_id(&self, id: &str) -> Option<serde_json::Value> {
        match &self.logger {
            LogStrategyLogger::MemoryLogger(memory_logger) => memory_logger.get_log_by_id(id),
            _ => None,
        }
    }

    fn get_log_ids(&self) -> Vec<String> {
        match &self.logger {
            LogStrategyLogger::MemoryLogger(memory_logger) => memory_logger.get_log_ids(),
            _ => Vec::new(),
        }
    }

    fn get_logs_by_tag(&self, tag: &str) -> Vec<serde_json::Value> {
        match &self.logger {
            LogStrategyLogger::MemoryLogger(memory_logger) => memory_logger.get_logs_by_tag(tag),
            _ => Vec::new(),
        }
    }

    fn get_logs_by_request_id(&self, request_id: &str) -> Vec<serde_json::Value> {
        match &self.logger {
            LogStrategyLogger::MemoryLogger(memory_logger) => {
                memory_logger.get_logs_by_request_id(request_id)
            },
            // always empty vector for not MemoryLogger
            _ => Vec::new(),
        }
    }

    fn get_logs_by_request_id_and_tag(&self, id: &str, tag: &str) -> Vec<serde_json::Value> {
        match &self.logger {
            LogStrategyLogger::MemoryLogger(memory_logger) => {
                memory_logger.get_logs_by_request_id_and_tag(id, tag)
            },
            _ => Vec::new(),
        }
    }
}

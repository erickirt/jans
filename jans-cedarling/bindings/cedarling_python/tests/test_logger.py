# This software is available under the Apache-2.0 license.
# See https://www.apache.org/licenses/LICENSE-2.0.txt for full text.
#
# Copyright (c) 2024, Gluu, Inc.

from cedarling_python import Cedarling
from config import load_bootstrap_config

# reuse already defined tokens
from test_authorize import REQUEST


# In python unit tests we not cover all possible scenarios, but most common.

def test_invalid_log_config():
    try:
        # when we set invalid log configuration it should raise ValueError
        load_bootstrap_config(log_type="String")
    except ValueError:
        pass
    else:
        assert False, "ValueError was not raised when setting invalid log_type"


def test_memory_logger():
    # map fixture to variable with shorter name for readability
    config = load_bootstrap_config(log_type="memory", log_ttl=60)

    # on initialize Cedarling we should have logs
    cedarling = Cedarling(config)

    # check that we have logs in memory
    active_log_ids = cedarling.get_log_ids()
    assert len(active_log_ids) != 0
    # check if we have access to log entries by id
    for id in active_log_ids:
        log_entry = cedarling.get_log_by_id(id)
        assert log_entry is not None

    assert len(
        cedarling.get_logs_by_tag("System")
    ) != 0, "Logs by tag 'System' were not found"

    assert len(
        cedarling.get_logs_by_tag("DEBUG")
    ) != 0, "Logs by tag 'DEBUG' were not found"

    # check that we can pop logs from memory
    assert len(cedarling.pop_logs()) != 0

    # after popping all logs, we should have no more active logs in memory
    assert len(cedarling.get_log_ids()) == 0
    assert len(cedarling.pop_logs()) == 0

    # execute request and get logs from memory logger
    authorize_result = cedarling.authorize(REQUEST)

    request_id = authorize_result.request_id()

    # get logs by request id
    assert len(
        cedarling.get_logs_by_request_id(request_id)
    ) != 0, "Logs by request id were not found"

    # get logs by request id + tags
    assert len(
        cedarling.get_logs_by_request_id_and_tag(request_id, "DEBUG")
    ) != 0, "Logs by request id and tag 'DEBUG' were not found"


def test_off_logger():
    # map fixture to variable with shorter name for readability
    config = load_bootstrap_config(log_type="off")

    cedarling = Cedarling(config)

    # we should not have logs in memory
    assert len(cedarling.get_log_ids()) == 0
    assert len(cedarling.pop_logs()) == 0


def test_stdout_logger():
    # Map fixture to variable with shorter name for readability
    config = load_bootstrap_config(log_type="std_out")

    cedarling = Cedarling(config)

    # for some reason python capsys doesn't capture the output in correct time
    # so we skip the reading stdout

    # We should not have logs in memory (confirm logs are printed only to stdout)
    assert len(cedarling.get_log_ids()) == 0
    assert len(cedarling.pop_logs()) == 0

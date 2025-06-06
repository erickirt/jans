/*
 * oxTrust is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package io.jans.link.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.jans.config.link.Configuration;
import io.jans.config.link.LinkAttributeMapping;
import io.jans.model.ldap.GluuLdapConfiguration;
import jakarta.enterprise.inject.Vetoed;
import java.util.List;

/**
 * Link configuration
 *
 * @author Yuriy Movchan Date: 07.13.2011
 */
@Vetoed
@JsonIgnoreProperties(ignoreUnknown = true)
public class LinkConfiguration implements Configuration {

    private List<GluuLdapConfiguration> sourceConfigs;
    private GluuLdapConfiguration inumConfig;
    private GluuLdapConfiguration targetConfig;

    private int ldapSearchSizeLimit;

    private List<String> keyAttributes;
    private List<String> keyObjectClasses;
    private List<String> sourceAttributes;

    private String customLdapFilter;

    private String updateMethod;

    private boolean defaultInumServer;

    private boolean keepExternalPerson;

    private boolean useSearchLimit;

    private List<LinkAttributeMapping> attributeMapping;

    private String snapshotFolder;
    private int snapshotMaxCount;

    public List<GluuLdapConfiguration> getSourceConfigs() {
        return sourceConfigs;
    }

    public void setSourceConfigs(List<GluuLdapConfiguration> sourceConfigs) {
        this.sourceConfigs = sourceConfigs;
    }

    public GluuLdapConfiguration getInumConfig() {
        return inumConfig;
    }

    public void setInumConfig(GluuLdapConfiguration inumConfig) {
        this.inumConfig = inumConfig;
    }

    public GluuLdapConfiguration getTargetConfig() {
        return targetConfig;
    }

    public void setTargetConfig(GluuLdapConfiguration targetConfig) {
        this.targetConfig = targetConfig;
    }

    public int getLdapSearchSizeLimit() {
        return ldapSearchSizeLimit;
    }

    public void setLdapSearchSizeLimit(int ldapSearchSizeLimit) {
        this.ldapSearchSizeLimit = ldapSearchSizeLimit;
    }

    public List<String> getKeyAttributes() {
        return keyAttributes;
    }

    public void setKeyAttributes(List<String> keyAttributes) {
        this.keyAttributes = keyAttributes;
    }

    public List<String> getKeyObjectClasses() {
        return keyObjectClasses;
    }

    public void setKeyObjectClasses(List<String> keyObjectClasses) {
        this.keyObjectClasses = keyObjectClasses;
    }

    public List<String> getSourceAttributes() {
        return sourceAttributes;
    }

    public void setSourceAttributes(List<String> sourceAttributes) {
        this.sourceAttributes = sourceAttributes;
    }

    public String getCustomLdapFilter() {
        return customLdapFilter;
    }

    public void setCustomLdapFilter(String customLdapFilter) {
        this.customLdapFilter = customLdapFilter;
    }

    public String getUpdateMethod() {
        return updateMethod;
    }

    public void setUpdateMethod(String updateMethod) {
        this.updateMethod = updateMethod;
    }

    public boolean isKeepExternalPerson() {
        return keepExternalPerson;
    }

    public void setKeepExternalPerson(boolean keepExternalPerson) {
        this.keepExternalPerson = keepExternalPerson;
    }

    public boolean isDefaultInumServer() {
        return defaultInumServer;
    }

    public void setDefaultInumServer(boolean defaultInumServer) {
        this.defaultInumServer = defaultInumServer;
    }

    public boolean isUseSearchLimit() {
        return useSearchLimit;
    }

    public void setUseSearchLimit(boolean useSearchLimit) {
        this.useSearchLimit = useSearchLimit;
    }

    public List<LinkAttributeMapping> getAttributeMapping() {
        return attributeMapping;
    }

    public void setAttributeMapping(List<LinkAttributeMapping> attributeMapping) {
        this.attributeMapping = attributeMapping;
    }

    public String getSnapshotFolder() {
        return snapshotFolder;
    }

    public void setSnapshotFolder(String snapshotFolder) {
        this.snapshotFolder = snapshotFolder;
    }

    public int getSnapshotMaxCount() {
        return snapshotMaxCount;
    }

    public void setSnapshotMaxCount(int snapshotMaxCount) {
        this.snapshotMaxCount = snapshotMaxCount;
    }

}

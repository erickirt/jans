package io.jans.configapi.model.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collection;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuditLogConf {

    @Schema(description = "Flag to enable and disable audit log.")
    private boolean enabled;
    
    @Schema(description = "Flag to enable and disable loggin g data.")
    private boolean logData;

    @Schema(description = "HTTP methods for which audit is disabled.")
    private Collection<String> ignoreHttpMethod;

    @Schema(description = "List of header HTTP attributes whose value is to be logged.")
    private List<String> headerAttributes;

    @Schema(description = "Audit log file location.")
    private String auditLogFilePath;

    @Schema(description = "Audit log file name.")
    private String auditLogFileName;
    
    @Schema(description = "Date format in audit log file.")
    private String auditLogDateFormat;
    
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    } 
    
    public boolean isLogData() {
        return logData;
    }

    public void setLogData(boolean logData) {
        this.logData = logData;
    }

    public Collection<String> getIgnoreHttpMethod() {
        return ignoreHttpMethod;
    }

    public void setIgnoreHttpMethod(Collection<String> ignoreHttpMethod) {
        this.ignoreHttpMethod = ignoreHttpMethod;
    }

    public List<String> getHeaderAttributes() {
        return headerAttributes;
    }

    public void setHeaderAttributes(List<String> headerAttributes) {
        this.headerAttributes = headerAttributes;
    }

    public String getAuditLogFilePath() {
        return auditLogFilePath;
    }

    public void setAuditLogFilePath(String auditLogFilePath) {
        this.auditLogFilePath = auditLogFilePath;
    }

    public String getAuditLogFileName() {
        return auditLogFileName;
    }

    public void setAuditLogFileName(String auditLogFileName) {
        this.auditLogFileName = auditLogFileName;
    }
    
    public String getAuditLogDateFormat() {
        return auditLogDateFormat;
    }

    public void setAuditLogDateFormat(String auditLogDateFormat) {
        this.auditLogDateFormat = auditLogDateFormat;
    }

    @Override
    public String toString() {
        return "AuditLogConf [enabled=" + enabled + ", logData=" + logData + ", ignoreHttpMethod=" + ignoreHttpMethod
                + ", headerAttributes=" + headerAttributes + ", auditLogFilePath=" + auditLogFilePath
                + ", auditLogFileName=" + auditLogFileName + ", auditLogDateFormat=" + auditLogDateFormat + "]";
    }

}

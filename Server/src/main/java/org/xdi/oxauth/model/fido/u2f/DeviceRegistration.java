/*
 * oxAuth is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */
package org.xdi.oxauth.model.fido.u2f;

import org.gluu.persist.model.base.BaseEntry;
import org.gluu.site.ldap.persistence.annotation.LdapAttribute;
import org.gluu.site.ldap.persistence.annotation.LdapEntry;
import org.gluu.site.ldap.persistence.annotation.LdapJsonObject;
import org.gluu.site.ldap.persistence.annotation.LdapObjectClass;
import org.xdi.oxauth.exception.fido.u2f.InvalidDeviceCounterException;
import org.xdi.oxauth.model.fido.u2f.exception.BadInputException;
import org.xdi.oxauth.model.fido.u2f.protocol.DeviceData;
import org.xdi.oxauth.model.util.Base64Util;

import java.io.Serializable;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * U2F Device registration
 *
 * @author Yuriy Movchan Date: 05/14/2015
 */
@LdapEntry(sortBy = "creationDate")
@LdapObjectClass(values = {"top", "oxDeviceRegistration"})
public class DeviceRegistration extends BaseEntry implements Serializable {

	private static final long serialVersionUID = -4542931562244920585L;

	@LdapAttribute(ignoreDuringUpdate = true, name = "oxId")
	private String id;

	@LdapAttribute
	private String displayName;

	@LdapAttribute
	private String description;

	@LdapAttribute(name = "oxNickName")
	private String nickname;

    @LdapJsonObject
    @LdapAttribute(name = "oxDeviceRegistrationConf")
	private DeviceRegistrationConfiguration deviceRegistrationConfiguration;

    @LdapJsonObject
    @LdapAttribute(name = "oxDeviceNotificationConf")
    private String deviceNotificationConf;

    @LdapAttribute(name = "oxCounter")
	private long counter;

    @LdapAttribute(name = "oxStatus")
	private DeviceRegistrationStatus status;

	@LdapAttribute(name = "oxApplication")
	private String application;

	@LdapAttribute(name = "oxDeviceKeyHandle")
	private String keyHandle;

	@LdapAttribute(name = "oxDeviceHashCode")
	private Integer keyHandleHashCode;

    @LdapJsonObject
	@LdapAttribute(name = "oxDeviceData")
	private DeviceData deviceData;

	@LdapAttribute(name = "creationDate")
	private Date creationDate;

    @LdapAttribute(name = "oxLastAccessTime")
    private Date lastAccessTime;

    @LdapAttribute(name = "oxAuthExpiration")
    private Date expirationDate;

    @LdapAttribute(name = "oxDeletable")
    private boolean deletable = true;
	
	public DeviceRegistration() {}

	public DeviceRegistration(String keyHandle, String publicKey, String attestationCert, long counter, DeviceRegistrationStatus status,
			String application, Integer keyHandleHashCode, Date creationDate) {
		this.deviceRegistrationConfiguration = new DeviceRegistrationConfiguration(publicKey, attestationCert);
		this.counter = counter;
		this.status = status;
		this.application = application;
		this.keyHandle = keyHandle;
		this.keyHandleHashCode = keyHandleHashCode;
		this.creationDate = creationDate;

        updateExpirationDate();
	}

	public DeviceRegistration(String keyHandle, String publicKey, X509Certificate attestationCert, long counter) throws BadInputException {
		this.keyHandle = keyHandle;
		try {
			String attestationCertDecoded = Base64Util.base64urlencode(attestationCert.getEncoded());
			this.deviceRegistrationConfiguration = new DeviceRegistrationConfiguration(publicKey, attestationCertDecoded);
		} catch (CertificateEncodingException e) {
			throw new BadInputException("Malformed attestation certificate", e);
		}

		this.counter = counter;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public DeviceRegistrationConfiguration getDeviceRegistrationConfiguration() {
		return deviceRegistrationConfiguration;
	}

	public void setDeviceRegistrationConfiguration(DeviceRegistrationConfiguration deviceRegistrationConfiguration) {
		this.deviceRegistrationConfiguration = deviceRegistrationConfiguration;
	}

	public String getDeviceNotificationConf() {
        return deviceNotificationConf;
    }

	public void setDeviceNotificationConf(String deviceNotificationConf) {
        this.deviceNotificationConf = deviceNotificationConf;
    }

    public long getCounter() {
		return counter;
	}

	public void setCounter(long counter) {
		this.counter = counter;
	}

	public DeviceRegistrationStatus getStatus() {
		return status;
	}

	public void setStatus(DeviceRegistrationStatus status) {
		this.status = status;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getKeyHandle() {
		return keyHandle;
	}

	public void setKeyHandle(String keyHandle) {
		this.keyHandle = keyHandle;
	}

	public Integer getKeyHandleHashCode() {
		return keyHandleHashCode;
	}

	public void setKeyHandleHashCode(Integer keyHandleHashCode) {
		this.keyHandleHashCode = keyHandleHashCode;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
        updateExpirationDate();
    }

    private void updateExpirationDate() {
        if (creationDate != null) {
            Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
            calendar.setTime(creationDate);
            calendar.add(Calendar.SECOND, 90);
            this.expirationDate = calendar.getTime();
        }
    }

    public DeviceData getDeviceData() {
		return deviceData;
	}

	public void setDeviceData(DeviceData deviceData) {
		this.deviceData = deviceData;
	}

	public Date getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(Date lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public boolean isCompromised() {
		return DeviceRegistrationStatus.COMPROMISED == this.status;
	}

	public void markCompromised() {
		this.status = DeviceRegistrationStatus.COMPROMISED;
	}

	public void checkAndUpdateCounter(long clientCounter) throws InvalidDeviceCounterException {
		if (clientCounter <= counter) {
			markCompromised();
			throw new InvalidDeviceCounterException(this);
		}
		counter = clientCounter;
	}

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    @Override
    public String toString() {
        return "DeviceRegistration{" +
                "id='" + id + '\'' +
                ", displayName='" + displayName + '\'' +
                ", description='" + description + '\'' +
                ", nickname='" + nickname + '\'' +
                ", deviceRegistrationConfiguration=" + deviceRegistrationConfiguration +
                ", deviceNotificationConf='" + deviceNotificationConf + '\'' +
                ", counter=" + counter +
                ", status=" + status +
                ", application='" + application + '\'' +
                ", keyHandle='" + keyHandle + '\'' +
                ", keyHandleHashCode=" + keyHandleHashCode +
                ", deviceData=" + deviceData +
                ", creationDate=" + creationDate +
                ", lastAccessTime=" + lastAccessTime +
                ", expirationDate=" + expirationDate +
                ", deletable=" + deletable +
                "} " + super.toString();
    }
}

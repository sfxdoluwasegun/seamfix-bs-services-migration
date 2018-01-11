package com.sf.biocapture.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@RevisionEntity(AuditListener.class)
@Entity
@Table(name = "ZZ_AUDITOR")
public class AuditEntity implements Serializable{

	private static final long serialVersionUID = -4843038202646682861L;

	@Id
	@GeneratedValue
	@RevisionNumber
	@Column(name = "REV_NO")
	private Long revNumber;

	@Temporal(TemporalType.TIMESTAMP)
	@RevisionTimestamp
	@Column(name = "REV_TIMESTAMP")
	private Date revTimestamp;

	@Column(name = "PORTAL_URL")
	private String currentURL;

	@Column(name = "USER_AGENT", updatable = false)
	private String userAgent;

	@Column(name = "REMOTE_HOST", updatable = false)
	private String remoteHost;

	@Column(name = "REMOTE_ADDR", updatable = false)
	private String remoteAddress;

	@Column(name = "ORBITA_ID", updatable = false)
	private Long orbitaId;

	@Column(name = "EMAIL_ADDR", updatable = false)
	private String emailAddress;

	public Long getOrbitaId() {
		return orbitaId;
	}

	public void setOrbitaId(Long orbitaId) {
		this.orbitaId = orbitaId;
	}

	public Long getRevNumber() {
		return revNumber;
	}

	public void setRevNumber(Long revNumber) {
		this.revNumber = revNumber;
	}

	public Date getRevTimestamp() {
		return revTimestamp;
	}

	public void setRevTimestamp(Date revTimestamp) {
		this.revTimestamp = revTimestamp;
	}

	public String getCurrentURL() {
		return currentURL;
	}

	public void setCurrentURL(String currentURL) {
		this.currentURL = currentURL;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

}

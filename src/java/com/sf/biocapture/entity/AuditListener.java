package com.sf.biocapture.entity;

import nw.commons.NeemClazz;

import org.hibernate.envers.RevisionListener;

/**
 * Used by envers for audit purposes
 * @author nuke
 *
 */
public class AuditListener extends NeemClazz implements RevisionListener {


	@Override
	public void newRevision(Object entity) {
		AuditEntity ae = (AuditEntity) entity;
	        ae.setOrbitaId(0L);
	        ae.setEmailAddress("kycservices@mtnkyc.com");
	        ae.setRemoteAddress("127.0.0.1");
	        ae.setRemoteHost("127.0.0.1");
	        ae.setUserAgent("System");
	        ae.setCurrentURL("localhost");
	}

}

package com.sf.biocapture.ws;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;


public class KsSecurityContext implements SecurityContext{

	private KsPrincipal user;

	public KsSecurityContext() {
		super();
	}

	public KsSecurityContext(KsPrincipal user) {
		this.user = user;
	}

	@Override
	public Principal getUserPrincipal() {
		return user;
	}

	@Override
	public boolean isUserInRole(String role) {
		for(String r: user.getRoles()){
			if(r.equals(role)){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSecure() {
		return false;
	}

	@Override
	public String getAuthenticationScheme() {
		return "KYC";
	}

}

package com.sf.biocapture.ws;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

public class KsPrincipal implements Principal{

	private String name;

	private Set<String> roles;

	public KsPrincipal(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public void addRole(String role){
		getRoles().add(role);
	}

	public Set<String> getRoles() {
		if(roles == null){
			roles = new HashSet<String>();
		}
		return roles;
	}

}

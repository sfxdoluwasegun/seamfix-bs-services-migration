package com.sf.biocapture.ws.access;

import java.util.HashSet;
import java.util.Set;

import com.sf.biocapture.ws.ResponseCodeEnum;
import com.sf.biocapture.ws.ResponseData;

/**
 * @author Nnanna
 * @since 02/09/2016, 09:21
 */
public class FetchPrivilegesResponse extends ResponseData {
	private String email;
	private String firstName;
	private String surname;
	private boolean cached = false;
	private Set<String> privileges = new HashSet<String>();
	
	public FetchPrivilegesResponse(){
		super(ResponseCodeEnum.ERROR);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isCached() {
		return cached;
	}

	public void setCached(boolean cached) {
		this.cached = cached;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public Set<String> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(Set<String> privileges) {
		this.privileges = privileges;
	}
}
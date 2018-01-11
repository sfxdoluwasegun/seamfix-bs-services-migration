package com.sf.biocapture.ws.access;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class AccessResponse {
	private int status = -1; //0: success, -1: incorrect credentials
	private String message = "";
	private String name;
	private List<String> roles = new ArrayList<String>();
	private boolean firstLogin = false;
	private String passwordSyntaxRegex;
	private String passwordSyntaxGuide;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	public boolean isFirstLogin() {
		return firstLogin;
	}
	public void setFirstLogin(boolean firstLogin) {
		this.firstLogin = firstLogin;
	}

	public String getPasswordSyntaxRegex() {
		return passwordSyntaxRegex;
	}

	public void setPasswordSyntaxRegex(String passwordSyntaxRegex) {
		this.passwordSyntaxRegex = passwordSyntaxRegex;
	}

	public String getPasswordSyntaxGuide() {
		return passwordSyntaxGuide;
	}

	public void setPasswordSyntaxGuide(String passwordSyntaxGuide) {
		this.passwordSyntaxGuide = passwordSyntaxGuide;
	}
}
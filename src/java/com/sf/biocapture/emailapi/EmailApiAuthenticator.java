/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.emailapi;

/**
 *
 * @author Marcel
 * @since Jul 18, 2017 - 12:00:59 AM
 */
public class EmailApiAuthenticator {

    private String username;
    private String password;
    private int port;
    private String hostname;
    private String fromTitle;

    public EmailApiAuthenticator(String username, String password, int port, String hostname, String fromTitle) {
        this.username = username;
        this.password = password;
        this.port = port;
        this.hostname = hostname;
        this.fromTitle = fromTitle;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getFromTitle() {
        return fromTitle;
    }

    public void setFromTitle(String fromTitle) {
        this.fromTitle = fromTitle;
    }

    @Override
    public String toString() {
        return "EmailApiAuthenticator{" + "username=" + username + ", password=" + password + ", port=" + port + ", hostname=" + hostname + ", fromTitle=" + fromTitle + '}';
    }
    
}

package com.sf.biocapture.ws.access;

/**
 *
 * @author seamfix
 */
public class PasswordResetResponse {
    int statusCode;
    String statusMessage;
    
    public PasswordResetResponse(){
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
    
    
}

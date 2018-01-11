
package com.sf.biocapture;

/**
 *
 * @author @wizzyclems
 */
public class StatusResponse {
    protected int status = -1;
    protected String message;

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
}

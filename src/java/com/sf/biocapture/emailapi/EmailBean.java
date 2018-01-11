/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.emailapi;

import java.io.File;

/**
 *
 * @author Marcel
 * @since Jul 18, 2017 - 12:01:23 AM
 */
public class EmailBean {

    private EmailApiAuthenticator apiAuthenticator;
    private String[] recipients;
    private String message;
    private File attachment;
    private String toName;
    private String subject;
    private String[] ccEmails;

    public EmailBean(EmailApiAuthenticator apiAuthenticator,  String message, String[] recipients, File attachment, String toName, String subject) {
        this.apiAuthenticator = apiAuthenticator;
        this.recipients = recipients;
        this.message = message;
        this.attachment = attachment;
        this.toName = toName;
        this.subject = subject;
    }
    
    public EmailBean(EmailApiAuthenticator apiAuthenticator,  String message,String[] recipients,String[] ccEmails) {
        this.apiAuthenticator = apiAuthenticator;
        this.recipients = recipients;
        this.message = message;
        this.ccEmails = ccEmails;
        
    }
       public EmailBean(EmailApiAuthenticator apiAuthenticator, String[] recipients, String message, String toName, String subject) {
        this.apiAuthenticator = apiAuthenticator;
        this.recipients = recipients;
        this.message = message;
        this.toName = toName;
        this.subject = subject;
       
    }
    public String[] getRecipients() {
        return recipients;
    }

    public void setRecipients(String[] recipients) {
        this.recipients = recipients;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public File getAttachment() {
        return attachment;
    }

    public void setAttachment(File attachment) {
        this.attachment = attachment;
    }

    public EmailApiAuthenticator getApiAuthenticator() {
        return apiAuthenticator;
    }

    public void setApiAuthenticator(EmailApiAuthenticator apiAuthenticator) {
        this.apiAuthenticator = apiAuthenticator;
    }

    public String[] getCcEmails() {
        return ccEmails;
    }

    public void setCcEmails(String[] ccEmails) {
        this.ccEmails = ccEmails;
    }
    
    

}

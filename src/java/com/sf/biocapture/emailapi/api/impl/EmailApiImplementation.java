/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.emailapi.api.impl;

import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.emailapi.EmailBean;
import com.sf.biocapture.emailapi.api.IEmailApi;

/**
 *
 * @author Trojan
 */
public class EmailApiImplementation extends BsClazz  implements IEmailApi {

    public EmailApiImplementation() {
    }
    
    
    
    @Override
    public void sendMail(EmailBean email) {

//        HtmlEmail htmlEmail = new HtmlEmail();
//
//        EmailApiAuthenticator apiAuthenticator = email.getApiAuthenticator();
//        htmlEmail.setSSLOnConnect(true);
//        htmlEmail.setHostName(apiAuthenticator.getHostname());
//        htmlEmail.setSmtpPort(apiAuthenticator.getPort());
//        htmlEmail.setAuthenticator(new DefaultAuthenticator(apiAuthenticator.getUsername(), apiAuthenticator.getPassword()));
//        try {
//            htmlEmail.setFrom(apiAuthenticator.getUsername(), apiAuthenticator.getFromTitle());
//            htmlEmail.setSubject(email.getSubject());
//            htmlEmail.setHtmlMsg(email.getMessage());
//
//            htmlEmail.addTo(email.getRecipients());
//            htmlEmail.send();
//        } catch (EmailException ex) {
//            log.error("Unable to send mail", ex);
//        }
    }

    @Override
    public void sendAttachment(EmailBean email) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

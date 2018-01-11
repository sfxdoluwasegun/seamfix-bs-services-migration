/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.emailapi.api;

import com.sf.biocapture.emailapi.EmailBean;

/**
 *
 * @author Trojan
 */
public interface IEmailApi {
     public void sendMail(EmailBean email);

    public void sendAttachment(EmailBean email) ;
    
}

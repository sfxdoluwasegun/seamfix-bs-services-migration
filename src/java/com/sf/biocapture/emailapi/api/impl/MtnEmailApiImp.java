/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.emailapi.api.impl;

import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.emailapi.EmailApiAuthenticator;
import com.sf.biocapture.emailapi.EmailBean;

import com.sf.biocapture.emailapi.api.IEmailApi;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author Trojan
 */
public class MtnEmailApiImp extends BsClazz implements  IEmailApi{
    
    private static final String falseLiteral =  "false";

    public MtnEmailApiImp() {
    }

    
    @SuppressWarnings("CPD-START")
     @Override
    public void sendMail(EmailBean email)  {

        try {
            EmailApiAuthenticator authenticator = email.getApiAuthenticator();
            logger.debug(authenticator.toString());
            Properties pros = new Properties();
            pros.put("mail.smtp.auth", falseLiteral);
            pros.put("mail.smtp.starttls.enable", falseLiteral);
            pros.put("mail.smtp.host", authenticator.getHostname());
            pros.put("mail.smtp.port", authenticator.getPort());
            pros.put("mail.smtp.ssl.enable", falseLiteral);
            pros.put("mail.debug", falseLiteral);
 
            Session instance = Session.getInstance(pros,
                    new javax.mail.Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(authenticator.getUsername(), authenticator.getPassword());
                        }
                    });
            Message mimeMessage = new MimeMessage(instance);
            InternetAddress ia = new InternetAddress(authenticator.getFromTitle());
            mimeMessage.setFrom(ia);
            List<InternetAddress> addressList = new ArrayList<>();
            if (email.getRecipients() != null && email.getRecipients().length > 0) {
                for (String recipient : email.getRecipients()) {
                    addressList.add(new InternetAddress(recipient));
                }
            }
            InternetAddress[] addressArray = new InternetAddress[addressList.size()];
            for (int i = 0; i < addressList.size(); i++) {
                addressArray[i] = addressList.get(i);
            }
            mimeMessage.setSubject(email.getSubject());
            mimeMessage.setContent(email.getMessage(), "text/html");
            Transport.send(mimeMessage, addressArray);
        } catch (AddressException ex) {
            logger.error("", ex);
        } catch (MessagingException ex) {
            logger.error("", ex);
        }
    }

    @Override
    public void sendAttachment(EmailBean email) {

        try {
            EmailApiAuthenticator apiAuthenticator = email.getApiAuthenticator();
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", falseLiteral);
            properties.put("mail.smtp.host", apiAuthenticator.getHostname());
            properties.put("mail.smtp.port", apiAuthenticator.getPort());
            properties.put("mail.debug", falseLiteral);

            Session session = Session.getInstance(properties,
                    new javax.mail.Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(apiAuthenticator.getUsername(), apiAuthenticator.getPassword());
                        }
                    });
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(apiAuthenticator.getFromTitle()));
            List<InternetAddress> addressList = new ArrayList<>();
            if (email.getRecipients() != null && email.getRecipients().length > 0) {
                for (String recipient : email.getRecipients()) {
                    addressList.add(new InternetAddress(recipient));
                }
            }
            InternetAddress[] addressArray = new InternetAddress[addressList.size()];
            for (int i = 0; i < addressList.size(); i++) {
                addressArray[i] = addressList.get(i);
            }
            message.setSubject(email.getSubject());
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(email.getMessage(), "text/html");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(email.getAttachment().getAbsolutePath());
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(email.getAttachment().getName());
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart);
            Transport.send(message, addressArray);
        } catch (AddressException ex) {
            logger.error("", ex);
        } catch (MessagingException ex) {
            logger.error("", ex);
        }
    }
    
}

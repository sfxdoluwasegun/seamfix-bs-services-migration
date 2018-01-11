/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.emailapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.stringtemplate.v4.ST;

import com.sf.biocapture.app.BsClazz;

/**
 *
 * @author Marcel
 * @since Jul 18, 2017 - 12:01:36 AM
 */
public class EmailBeanFactory extends BsClazz {

    public static List<EmailBean> emails = new ArrayList();

    private static final String DATE_PATTERN = "EEE, MMM d, yyyy";
    public static enum EmailResponse {

        SUCCESS, INCOMPLETE_PARAMETERS, IO_EXCEPTION, NAMING_EXCEPTION, HIBERNATE_EXCEPTION;
    }

   
    public static EmailBean newLicenceRequestEmailBean(EmailApiAuthenticator apiAuthenticator, String macAddress, String tagId, String agentName,
            String agentEmail, String[] adminEmails
            ) {
        String templateFileName = "license-request.stl";
        String message = "";
        String subject = "License Request";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        String formattedRequestDate =  sdf.format(new Timestamp(new Date().getTime()));
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("macAddress", macAddress);
            params.put("tagId", tagId);
            params.put("agentName", agentName);
            params.put("agentEmail",agentEmail);
            params.put("formattedDate", formattedRequestDate);

            message = generateMessage(templateFileName, params);
            EmailBean emailBean = new EmailBean(apiAuthenticator,adminEmails, message,agentName,subject);
            return emailBean;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public static EmailBean newLocationDeviationToDealer(EmailApiAuthenticator apiAuthenticator, String[] dealerEmail, String dealerName, String deviceTag, 
    		String defaultLocation, String currentLocation, String agentEmail, String agentName, String agentPhoneNumber, String outletName, String outletAddress,
    		String simropUrl) {
    	
    	String templateFileName = "location-deviation-dealer.stl";
    	String subject ="SIMROP: DEVICE LOCATION DEVIATION ALERT";
    	try {
    		Map<String, String> params = new HashMap<String, String>();
    		params.put("dealerName", dealerName);
    		params.put("deviceTag", deviceTag);
    		params.put("defaultLocation", defaultLocation);
    		params.put("currentLocation", currentLocation);
    		params.put("agentEmail", agentEmail);
    		params.put("agentName", agentName);
    		params.put("agentPhoneNumber", agentPhoneNumber);
    		params.put("outletName", outletName);
    		params.put("outletAddress", outletAddress);
    		params.put("title", "DEVICE LOCATION DEVIATION ALERT");
    		params.put("simropUrl", simropUrl);
    		
    		String message = getGeneralTemplate(generateMessage(templateFileName, params));
            EmailBean emailBean = new EmailBean(apiAuthenticator, dealerEmail, message, dealerName, subject);
            return emailBean;
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    	return null;
    }
    
    public static EmailBean newLocationDeviationToCAC(EmailApiAuthenticator apiAuthenticator, String[] dealerEmail, String dealerName, String dealerCode, 
    		String dealerRegion, String deviceTag, String defaultLocation, String currentLocation, String agentEmail, String agentName, String agentPhoneNumber, 
    		String outletName, String outletAddress, String simropUrl) {
    	
    	String templateFileName = "location-deviation-CAC.stl";
    	String subject ="SIMROP: DEVICE LOCATION DEVIATION ALERT";
    	try {
    		Map<String, String> params = new HashMap<String, String>();
    		params.put("dealerName", dealerName);
    		params.put("dealerCode", dealerCode);
    		params.put("dealerRegion", dealerRegion);
    		params.put("deviceTag", deviceTag);
    		params.put("defaultLocation", defaultLocation);
    		params.put("currentLocation", currentLocation);
    		params.put("AgentEmail", agentEmail);
    		params.put("agentName", agentName);
    		params.put("agentPhoneNumber", agentPhoneNumber);
    		params.put("outletName", outletName);
    		params.put("outletAddress", outletAddress);
    		params.put("title", "DEVICE LOCATION DEVIATION ALERT");
    		params.put("simropUrl", simropUrl);
    		
    		String message = getGeneralTemplate(generateMessage(templateFileName, params));
            EmailBean emailBean = new EmailBean(apiAuthenticator, dealerEmail, message, dealerName, subject);
            return emailBean;
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    	return null;
    }

    private static String generateMessage(String templateFileName, Map<String, String> params) throws IOException {
        String line = "";
        InputStream is = EmailBeanFactory.class.getClassLoader().getResourceAsStream("/mailtemplates/" + templateFileName);

        BufferedReader buffer = new BufferedReader(new InputStreamReader(is));

        StringBuilder builder = new StringBuilder();
        while ((line = buffer.readLine()) != null) {
            String newLine = "\n";
            builder.append(line).append(newLine);
        }

        ST template = new ST(builder.toString(), '$', '$');
        Set<String> keys = params.keySet();
        keys.stream().forEach((k) -> {
            template.add(k, params.get(k));
        });

        String renderEmail = template.render();
        return renderEmail;
    }
    
    private static String getGeneralTemplate(String message) {
        try {
            String line;
            BufferedReader buffer = new BufferedReader(new InputStreamReader(EmailBeanFactory.class.getClassLoader().getResourceAsStream("/mailtemplates/send_general_email.stl")));

            StringBuilder builder = new StringBuilder();
            while ((line = buffer.readLine()) != null) {
                String newLine = "\n";
                builder.append(line).append(newLine);
            }

            Map<String, String> params = new HashMap<String, String>();
            params.put("message", message);
            
            ST template = new ST(builder.toString(), '$', '$');
            Set<String> keys = params.keySet();
            keys.stream().forEach((k) -> {
                template.add(k, params.get(k));
            });

            return template.render();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return message;
    }
}

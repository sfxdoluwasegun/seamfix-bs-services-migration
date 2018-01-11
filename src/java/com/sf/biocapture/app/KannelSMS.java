package com.sf.biocapture.app;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;

import com.javacodegeeks.kannel.api.SMSManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * A class for sending sms via Kannel Gateway
 *
 * @author nuke/iyare/mujib
 *
 */
@Singleton
public class KannelSMS extends BsClazz {

    private String serverIp = "10.1.226.246";// "172.24.5.49";
    private int serverPort = 13013;
    private String defaultUsername = "seamfix";
    private String defaultPassword = "passwd";
    private String smsUrl = "http://10.1.226.246:13013/cgi-bin/sendsms";
    private final String SENDER = "MTN KYC";

    private SMSManager smsManager;// = SMSManager.getInstance();

    @PostConstruct
    public void init() {
        serverIp = appProps.getProperty("kannel.server", serverIp);
        serverPort = appProps.getInt("kannel.port", serverPort);
        smsUrl = appProps.getProperty("kannel.url", smsUrl);
        defaultUsername = appProps.getProperty("kannel.username", defaultUsername);
        defaultPassword = appProps.getProperty("kannel.pw", defaultPassword);
        smsManager = SMSManager.getInstance();
    }

    @PreDestroy
    public void cleanUp() {
        logger.debug("Cleaning up kannel sms");
    }

    /**
     *
     * @param msisdn
     * @param message
     * @param recv recipient mobile number
     * @param msg message to deliver
     * @param priority defaults to 0 (0 - 9)
     * @return true if sent successfully
     * @throws java.net.MalformedURLException
     */
//	public boolean sendSMS(String recv, String msg) {
//		try {
//                        if( (recv != null) && (recv.length() == 11) )
//                            recv = recv.substring(recv.length() - 10);
//                        
//			String g = smsManager.sendSMS(serverIp, String.valueOf(serverPort), username, password, SENDER, recv, msg);
//			logger.info("Kannel Reply for " + recv + " : " + g);
//			return g.indexOf("Sent") != -1;
//		} catch (Exception ex) {
//			logger.error("Exception ", ex);
//		}
//		return false;
//	}
    public boolean sendSMS(String msisdn, String message) throws MalformedURLException, IOException {
        boolean sent = false;
//            String defaultSmsUrl = "http://10.1.226.246:13013/cgi-bin/sendsms";
//            String defaultUsername = appProps.getProperty("sms-gate-way-username","seamfix");
//            String defaultPassword = appProps.getProperty("sms-gate-way-password","passwd");            
//            String smsUrl = appProps.getProperty("sms-gate-way-url", defaultSmsUrl);

        String msg = URLEncoder.encode(message, "UTF-8");

        if ((msisdn != null) && (msisdn.length() > 10)) {
            msisdn = msisdn.trim().substring(1);
        }

        System.out.println("The new phone number is : " + msisdn);
        String smsParams = "username=" + defaultUsername + "&password=" + defaultPassword + "&to=" + msisdn.trim() + "&text=" + msg;
        String urlString = smsUrl + "?" + smsParams;
        URL url = new URL(urlString);

        HttpURLConnection httpUrl = (HttpURLConnection) url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(httpUrl.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        inputLine = in.readLine();
        //        System.out.println("The returned message : " + inputLine );
        if ((inputLine != null) && (inputLine.equalsIgnoreCase("Sent."))) {
            sent = true;
        }

        in.close();
        return sent;
    }

    /**
     *
     * @param recv recipient mobile number
     * @param msg message to deliver
     * @param priority defaults to 0 (0 - 9)
     * @param sender
     * @return true if sent successfully
     */
    @SuppressWarnings("PMD")
    public boolean sendSMS(String recv, String msg, String sender) {
        try {
            recv = recv.substring(recv.length() - 10);
            String g = smsManager.sendSMS(serverIp, String.valueOf(serverPort), defaultUsername, defaultPassword, sender, recv, msg);
            logger.info("Kannel Reply for " + recv + " : " + g);
            return g.indexOf("Sent") != -1;
        } catch (Exception ex) {
            logger.error("Exception ", ex);
        }
        return false;
    }

    /**
     *
     * @param recvs list of msisdn (7084052248)
     * @param msg
     * @param priority
     * @return
     */
    @SuppressWarnings("PMD")
    public boolean sendSMS(List<String> recvs, String msg, Short priority) {

        try {
            smsManager.sendBulkSMS(serverIp, String.valueOf(serverPort), defaultUsername, defaultPassword, SENDER, recvs, msg, priority);
            return true;
        } catch (Exception e) {
            logger.error("Exception ", e);
        }
        return false;
    }

    /**
     *
     * @param recvs list of msisdn (7084052248)
     * @param msg
     * @param priority
     * @return
     */
    @SuppressWarnings("PMD")
    public boolean sendSMS(List<String> recvs, String msg, Short priority, String sender) {

        try {
            smsManager.sendBulkSMS(serverIp, String.valueOf(serverPort), defaultUsername, defaultPassword, sender, recvs, msg, priority);
            return true;
        } catch (Exception e) {
            logger.error("Exception ", e);
        }
        return false;
    }

}

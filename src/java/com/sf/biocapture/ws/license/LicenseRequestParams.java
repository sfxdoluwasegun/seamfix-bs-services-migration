
package com.sf.biocapture.ws.license;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author @wizzyclems
 */
public class LicenseRequestParams 
{        
     private String macAddress;
     private String tagId;
     private String agentName;
     private String agentEmail;
     private String formattedRequestTime;
     private String[] adminEmails;
     private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
     
     public LicenseRequestParams(){         
     }

   
    public LicenseRequestParams(String macAddress, String tagId, String agentName, String agentEmail, Timestamp requestDate, String[] adminEmails) {
        this.macAddress = macAddress;
        this.tagId = tagId;
        this.agentName = agentName;
        this.agentEmail = agentEmail;
        this.adminEmails = adminEmails;
        if( requestDate != null ){            
            this.formattedRequestTime = sdf.format( new Date(requestDate.getTime() ) );
        }
    }    

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentEmail() {
        return agentEmail;
    }

    public void setAgentEmail(String agentEmail) {
        this.agentEmail = agentEmail;
    }

    public String getFormattedRequestTime() {
        return formattedRequestTime;
    }

    public void setFormattedRequestTime(String formattedRequestTime) {
        this.formattedRequestTime = formattedRequestTime;
    }

    public String[] getAdminEmails() {
        return adminEmails;
    }

    public void setAdminEmails(String[] adminEmails) {
        this.adminEmails = adminEmails;
    }
    
}

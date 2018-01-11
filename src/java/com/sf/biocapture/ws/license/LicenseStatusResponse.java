
package com.sf.biocapture.ws.license;

import com.sf.biocapture.StatusResponse;

public class LicenseStatusResponse {
    
    private StatusResponse statusResponse;
    private String licenseCode;

    public String getLicenseCode(){
        return licenseCode;
    }

    public void setLicenseCode(String licenseCode){
        this.licenseCode = licenseCode;
    }

    public StatusResponse getStatusResponse() {
        return statusResponse;
    }

    public void setStatusResponse(StatusResponse statusResponse) {
        this.statusResponse = statusResponse;
    }
    
}
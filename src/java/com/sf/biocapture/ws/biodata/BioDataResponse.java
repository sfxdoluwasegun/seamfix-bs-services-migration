
package com.sf.biocapture.ws.biodata;

import com.sf.biocapture.StatusResponse;
import com.sf.biocapture.proxy.BioData;


public class BioDataResponse extends StatusResponse {
    
    private BioData bioData;
        
    public BioData getBioData() {
        return bioData;
    }

    public void setBioData(BioData bioData) {
        this.bioData = bioData;
    }
    
}
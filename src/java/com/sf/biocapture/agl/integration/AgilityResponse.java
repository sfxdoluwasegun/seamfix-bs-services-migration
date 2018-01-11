package com.sf.biocapture.agl.integration;

import com.sf.biocapture.proxy.BioData;
import java.sql.Timestamp;

public class AgilityResponse {
	private String code;
	private String description;
	private String puk;
        private Integer childMsisdnCount;
	private Boolean valid = false;
	private BioData bioData;
	private Timestamp activationDate; //This field is used for Activation Status service.
        /**
         * this value is used internally to track re-registration eligibility status
         */
        private Timestamp lastSIMRegDate;
        private String activationStatus;  //This field is used for Activation Status service.
        private String uniqueId; // This field was introduced to be used in the Activation status service.
        private String assetStatus;// This field stores the eligibility status of a msisdn for re-registration.
	public AgilityResponse(){
		
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BioData getBioData() {
		return bioData;
	}

	public void setBioData(BioData bioData) {
		this.bioData = bioData;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public String getPuk() {
		return puk;
	}

	public void setPuk(String puk) {
		this.puk = puk;
	}

        /**
         * This method returns the activation date of a msisdn or sim serial
         * It's used in the activation status service endpoint
         * @return 
         */
        public Timestamp getActivationDate() {
            return activationDate;
        }

        /** 
         * This allows you to set the timestamp for the activation date of a msisdn or sim serial. 
         * It's used in the activation status service endpoint
         * @param activationDate 
         */
        public void setActivationDate(Timestamp activationDate) {
            this.activationDate = activationDate;
        }

        /***
         * This method returns the activation status of a msisdn or sim serial.
         * It's used in the activation status service endpoint
         * @return 
         */
        public String getActivationStatus() {
            return activationStatus;
        }

        /**
         * This method allows you to set the activation status of a msisdn or sim serial.
         * It's used in the activation status service endpoint
         * @param activationStatus 
         */
        public void setActivationStatus(String activationStatus) {
            this.activationStatus = activationStatus;
        }   

        /**
         * returns the unique ID of a msisdn or sim serial.
         * @return 
         */
        public String getUniqueId() {
            return uniqueId;
        }

        
        public void setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
        }

        public Integer getChildMsisdnCount() {
            return childMsisdnCount;
        }

        public void setChildMsisdnCount(Integer childMsisdnCount) {
            this.childMsisdnCount = childMsisdnCount;
        }   

    public String getAssetStatus() {
        return assetStatus;
    }

    public void setAssetStatus(String assetStatus) {
        this.assetStatus = assetStatus;
    }

    public Timestamp getLastSIMRegDate() {
        return lastSIMRegDate;
    }

    public void setLastSIMRegDate(Timestamp lastSIMRegDate) {
        this.lastSIMRegDate = lastSIMRegDate;
    }
        
        
        
}

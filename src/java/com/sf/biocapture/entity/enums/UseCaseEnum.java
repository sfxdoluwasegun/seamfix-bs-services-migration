package com.sf.biocapture.entity.enums;

/**
 * 
 * @author Nnanna
 * @since 06/12/2016
 */
public enum UseCaseEnum {
	
	RE_REGISTRATION("REREGISTRATION"),
	PLATINUM("PLATINUM"),
	SME_CORPORATE("SME-CORPORATE"),
	MNP_SME_CORPORATE("MNP-SME-CORPORATE"),
	ADD_SIM("ADD-SIM"),
	SIM_SWAP("SIMSWAP");
	
	private UseCaseEnum(String name){
		this.name = name;
	}
	
	private String name;
	
	public String getName() {
        return name;
    }
}

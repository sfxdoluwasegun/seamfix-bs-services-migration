package com.sf.biocapture.sim.churn;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChurnMessage implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4322386488918747839L;

	private String refNumber;
	
	private List<String> list;
	
	private int size = 200;

	public String getRefNumber() {
		return refNumber;
	}

	public void setRefNumber(String refNumber) {
		this.refNumber = refNumber;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}
	
	public boolean addIfNotFull(String msisdn) {
		if(list == null){
			list = new ArrayList<String>();
		}
		if(list.size() < size){
			list.add(msisdn);
			return true;
		}
		return false;
	}

}

package com.sf.biocapture.ws.vtu;

import java.io.Serializable;

/**
 * Data Transfer Object for Bundle Types
 * @author Nnanna
 * @since 6 Nov 2017, 11:54:45
 */
public class BundleTypeDTO implements Serializable {
	/**
	 * bundle id
	 */
	private String bundleId;
	/**
	 * bundle name
	 */
	private String name;
	
	/**
	 * bundle type description
	 */
	private String description;
	
	/**
	 * bundle type amount
	 */
	private double amount;
	
	/**
	 * VTU category
	 */
	private String category;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBundleId() {
		return bundleId;
	}

	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}

}

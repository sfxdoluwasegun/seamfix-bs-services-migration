package com.sf.biocapture.app;


/**
 * @author Neme
 * 
 */
public class Constants {

	//CRUD CONSTANTS
	public static final String CREATE = "CREATE";
	public static final String UPDATE = "UPDATE";
	public static final String DELETE = "DELETE";
	public static final String SELECT = "SELECT";
	
	
	//ENTITY CONTANTS
	public static final String LICENSE	= "LICENSE";
	public static final String ENROLLMENT_REF = "ENROLLMENT_REF";
	
	//DEVICE TYPES
	public static final String DEVICE_TYPE_CAMERA = "Camera";
	public static final String DEVICE_TYPE_FINGER_PRINT_SCANNER = "Fingerprint_Scanner".trim();
	
	private final static String requestDelimeter = "#s#x#";
	
	public static String getRequestDelimeter(){
		return requestDelimeter;
	}
	
}

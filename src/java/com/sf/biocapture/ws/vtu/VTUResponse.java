package com.sf.biocapture.ws.vtu;

import com.sf.biocapture.ws.ResponseCodeEnum;
import com.sf.biocapture.ws.ResponseData;

/**
 * 
 * @author Nnanna
 * @since 9 Nov 2017, 14:16:53
 */
public class VTUResponse extends ResponseData {

	private static final long serialVersionUID = 1L;
	
	public VTUResponse(ResponseCodeEnum resp){
		super(resp);
	}
	
	public VTUResponse(){
		setCode(ResponseCodeEnum.ERROR);
		setDescription(ResponseCodeEnum.ERROR.getDescription());
	}
	
	public VTUResponse(ResponseCodeEnum code, String description){
		setCode(code);
		setDescription(description);
	}

}
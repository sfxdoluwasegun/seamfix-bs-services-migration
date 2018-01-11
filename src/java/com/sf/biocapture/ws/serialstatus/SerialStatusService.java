
package com.sf.biocapture.ws.serialstatus;

import com.google.gson.Gson;
import com.sf.biocapture.agl.integration.AgilityResponse;
import com.sf.biocapture.agl.integration.QuerySimSerial;
import com.sf.biocapture.app.BsClazz;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.inject.Inject;
import javax.ws.rs.Path;
import org.apache.commons.lang3.math.NumberUtils;

@Path("/serial")
public class SerialStatusService  extends BsClazz implements ISerialStatus
{

    @Inject 
    QuerySimSerial qss;
        
    @Override
    public SerialStatusResponse getStatus(String serialNumber, String puk) {
        logger.debug("Serial Number.: " + serialNumber + " | puk : " + puk); 
        
        //Status  0 - false, 1 - true, non-positive-value - error
        if( (serialNumber == null) || serialNumber.isEmpty() || ( !NumberUtils.isDigits(serialNumber)) || (serialNumber.length() != 20) ){
            return getResponse("The specified serial is invalid.Serial must be a digit of 20 numbers long.",0);
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        AgilityResponse ar = qss.validateSIMSerial(serialNumber,serialNumber + "-" + sdf.format(new Date()), puk);
        
        if( ( ar != null ) ){
            Gson g = new Gson();
            logger.debug("***** The agility response object for sim serial status is shown below...");
            logger.debug( g.toJson(ar) );
        }
        
        if( (ar != null) && (ar.getValid() != null) && ar.getValid() ){
            logger.debug("Agility response is : " + ar.getCode() + ":" + ar.getDescription());
            return getResponse("The specified serial is valid.",1);
        }
        
        return getResponse("The specified serial is invalid.",0);
    }

    private SerialStatusResponse getResponse(String msg, int code){
        SerialStatusResponse response = new SerialStatusResponse();
        response.setMessage(msg);
        response.setStatus(code);
        return response;
    }
    
    
}

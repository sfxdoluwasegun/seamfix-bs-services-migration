
package com.sf.biocapture.ws.serialstatus;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public interface ISerialStatus {

    @POST
    @Path("/status")
    @Produces({MediaType.APPLICATION_JSON})
    public SerialStatusResponse getStatus(@FormParam("serialNumber") String serialNo, @FormParam("puk") String puk);

}
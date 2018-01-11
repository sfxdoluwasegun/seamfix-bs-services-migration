/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.threshold;

import com.sf.biocapture.ws.threshold.pojo.RequestPojo;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Marcel
 * @since Jul 4, 2016, 6:04:30 PM
 */
public interface IThresholdService {

    @Deprecated
    @POST
    @Path("/")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getThresholdPojo(RequestPojo requestPojo);
    
    @Deprecated
    @POST
    @Path("/fingertypes")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getThresholdPojoWithFingerType(RequestPojo requestPojo);

    
    @POST
    @Path("/")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getThreshold(RequestPojo requestPojo);
    
    @POST
    @Path("/{targetEndpoint}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getThreshold(RequestPojo requestPojo, @PathParam("targetEndpoint") int targetEndpoint);

    
    @Deprecated
    @GET
    @Path("/requestpojo")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getRequestPojo();

    @GET
    @Path("/requestpojo/fingertypes")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getRequestPojoWithFingerType();
}

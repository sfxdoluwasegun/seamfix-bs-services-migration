package com.sf.biocapture.ws.tags;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.ws.access.LicenseResponse;
import javax.annotation.security.PermitAll;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

@Path("/tags")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TagsResource extends BsClazz {

    @Inject
    private TagsDS tagsDS;

    @Inject
    private TagsAction action;

    @POST
    @Path("/")
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed({"smartclient", "droid"})
    public ClientRefResponse tag(ClientRefRequest req, @Context HttpHeaders headers) {
        return tagsDS.tag(req, headers);
    }

    @POST
    @Path("/retag")
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed({"smartclient", "droid"})
    public ClientRefResponse retag(@Context HttpHeaders headers, ClientRefRequest req) {
        return action.retag(headers, req);
    }

    @POST
    @Path("/test")
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed({"smartclient", "droid"})
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ClientRefResponse testKit(ClientRefRequest req) {
        return action.testKit(req.getRef());
    }

    @POST
    @Path("/license")
    public Response getLicense(ClientRefRequest req) {
        List<LicenseResponse> lrs = new ArrayList<LicenseResponse>();
        LicenseResponse lr = new LicenseResponse();
        lrs.add(lr);
        return Response.status(Status.OK).entity(lr).build();
    }

    @POST
    @Path("/licensereq")
    public Response requestLicense(ClientRefRequest req) {
        return Response.status(Status.OK).entity(new ClientRefResponse()).build();
    }

    @POST
    @Path("/update/deviceid")
    @PermitAll
    public Response updateDeviceId(@Context HttpHeaders headers, TagRequestPojo tagRequestPojo) {
        return Response.status(Status.OK).entity(action.updateDeviceId(headers, tagRequestPojo)).build();
    }

    @POST
    @Path("/fetchTag")
    @PermitAll
    public Response confirmTag (TagRequestPojo tagRequestPojo) {
        return Response.status(Status.OK).entity(action.confirmDeviceTag(tagRequestPojo)).build();
    }
}

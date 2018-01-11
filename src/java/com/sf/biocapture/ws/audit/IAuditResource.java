/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.audit;

import com.sf.biocapture.ws.audit.pojo.ClientActivityLogPojo;
import java.util.List;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Marcel
 * @since Jun 29, 2016, 12:00:51 PM
 */
public interface IAuditResource {

    @POST
    @Path("/alog")
    @Produces({MediaType.APPLICATION_JSON})
    public Response logActivity(@Context HttpHeaders headers, List<ClientActivityLogPojo> clientActivityLogPojos);
}

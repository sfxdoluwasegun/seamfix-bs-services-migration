/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.audit;

import com.sf.biocapture.app.BsClazz;
import com.sf.biocapture.ws.audit.pojo.ClientActivityLogPojo;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 *
 * @author Marcel
 * @since Jun 29, 2016, 12:01:08 PM
 */
@Path("/audit")
public class AuditResource extends BsClazz implements IAuditResource {

    @Inject
    AuditService auditService;

    @Override
    public Response logActivity(HttpHeaders headers, List<ClientActivityLogPojo> clientActivityLogPojos) {
        return Response.status(200).entity(auditService.createClientActivityLog(clientActivityLogPojos, headers)).build();
    }
}
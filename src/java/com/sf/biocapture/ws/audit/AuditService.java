/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.audit;

import com.sf.biocapture.analyzer.IntrusionAnalyzer;
import com.sf.biocapture.ds.DataService;
import com.sf.biocapture.entity.audit.ClientActivityLog;
import com.sf.biocapture.ws.HeaderIdentifier;
import com.sf.biocapture.ws.ResponseCodeEnum;
import com.sf.biocapture.ws.audit.pojo.ClientActivityLogPojo;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import nw.orm.core.exception.NwormQueryException;

/**
 *
 * @author Marcel
 * @since Jun 30, 2016, 10:31:48 AM
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class AuditService extends DataService {
    @Inject
    IntrusionAnalyzer analyzer;
    
    public List<AuditResponse> createClientActivityLog(List<ClientActivityLogPojo> clientActivityLogPojos, HttpHeaders headers) {
        List<AuditResponse> auditResponses = new ArrayList<>();
        HeaderIdentifier headerIdentifier = analyzer.getIdentifier(headers);
        if (clientActivityLogPojos == null) {
            AuditResponse auditResponse = new AuditResponse(ResponseCodeEnum.ERROR);
            auditResponse = new AuditResponse(ResponseCodeEnum.ERROR);
            auditResponse.setDescription("Request parameters is required");
            auditResponses.add(auditResponse);
            return auditResponses;
        }

        for (ClientActivityLogPojo clientActivityLogPojo : clientActivityLogPojos) {
            AuditResponse auditResponse = new AuditResponse(ResponseCodeEnum.ERROR);
            try {
                if (clientActivityLogPojo.getMacAddress() == null || clientActivityLogPojo.getMacAddress().isEmpty() || clientActivityLogPojo.getKitTag() == null || clientActivityLogPojo.getKitTag().isEmpty()) {
                    auditResponse = new AuditResponse(ResponseCodeEnum.ERROR);
                    auditResponse.setId(clientActivityLogPojo.getId());
                    auditResponse.setDescription("Mac Address or Kit Tag is missing");
                    auditResponses.add(auditResponse);
                    continue;
                }
                ClientActivityLog clientActivityLog = new ClientActivityLog();
                clientActivityLog.setUniqueActivityCode(clientActivityLogPojo.getActivityCode());
                clientActivityLog.setActivity(clientActivityLogPojo.getActivity());
                if (clientActivityLogPojo.getActivityEndTime() != null) {
                    clientActivityLog.setActivityEndTime(new Date(clientActivityLogPojo.getActivityEndTime()));
                }
                if (clientActivityLogPojo.getActivityStartTime() != null) {
                    clientActivityLog.setActivityStartTime(new Date(clientActivityLogPojo.getActivityStartTime()));
                }
                clientActivityLog.setEnrollmentRef(clientActivityLogPojo.getEnrollmentRef());
                clientActivityLog.setFullName(clientActivityLogPojo.getFullName());
                clientActivityLog.setKitTag(clientActivityLogPojo.getKitTag());
                clientActivityLog.setMacAddress(clientActivityLogPojo.getMacAddress());
                clientActivityLog.setUsername(clientActivityLogPojo.getUsername());
                clientActivityLog.setDuration(clientActivityLogPojo.getDuration());
                clientActivityLog.setRefDeviceId(clientActivityLogPojo.getRefDeviceId());
                clientActivityLog.setRealTimeDeviceId(clientActivityLogPojo.getRealTimeDeviceId());
                
                dbService.create(clientActivityLog);

                auditResponse = new AuditResponse(ResponseCodeEnum.SUCCESS);
                auditResponse.setId(clientActivityLogPojo.getId());
                auditResponses.add(auditResponse);
            } catch (NwormQueryException e) {
                auditResponse = new AuditResponse(ResponseCodeEnum.ERROR);
                auditResponse.setId(clientActivityLogPojo.getId());
                auditResponses.add(auditResponse);
                logger.error("", e);
            }
        }
        return auditResponses;
    }
}
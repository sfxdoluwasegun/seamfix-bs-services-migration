/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.audit;

import com.sf.biocapture.ws.ResponseCodeEnum;
import com.sf.biocapture.ws.ResponseData;

/**
 *
 * @author Marcel
 * @since Jun 29, 2016, 12:00:17 PM
 */
public class AuditResponse extends ResponseData {

    private Long id;

    public AuditResponse(ResponseCodeEnum responseCodeEnum) {
        super(responseCodeEnum);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    
}

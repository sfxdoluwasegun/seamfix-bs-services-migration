/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.tags;

import com.sf.biocapture.ws.ResponseCodeEnum;
import com.sf.biocapture.ws.ResponseData;
import com.sf.biocapture.ws.tags.pojo.EnrollmentRefPojo;

/**
 *
 * @author Marcel
 * @since Sep 21, 2017 - 10:49:29 AM
 */
public class TagResponsePojo extends ResponseData {

    private EnrollmentRefPojo enrollmentRef;

    public TagResponsePojo() {
    }

    public TagResponsePojo(ResponseCodeEnum responseCodeEnum) {
        super(responseCodeEnum);
    }

    public EnrollmentRefPojo getEnrollmentRef() {
        return enrollmentRef;
    }

    public void setEnrollmentRef(EnrollmentRefPojo enrollmentRef) {
        this.enrollmentRef = enrollmentRef;
    }

}

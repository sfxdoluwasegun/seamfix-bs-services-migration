/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ilog;

import java.io.Serializable;
import nw.orm.core.IEntity;

/**
 *
 * @author Marcel
 * @since Aug 13, 2017 - 4:27:22 PM
 */
public interface ILog extends Serializable {

    public String getRequestType();

    public String getTransactionId();

    public IEntity getEntity();

    public String getRequestPayload();

    public String getResponsePayload();

}

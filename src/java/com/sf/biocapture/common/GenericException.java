/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sf.biocapture.common;

/**
 *
 * @author Marcel
 * @since 17-Oct-2016, 12:35:30
 */
public class GenericException extends Exception{

    public GenericException() {
    }

    public GenericException(String message) {
        super(message);
    }

    public GenericException(Throwable cause) {
        super(cause);
    }

    public GenericException(String message, Throwable cause) {
        super(message, cause);
    }

}

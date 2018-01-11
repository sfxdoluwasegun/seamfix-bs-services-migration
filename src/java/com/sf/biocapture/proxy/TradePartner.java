/*
 * This is a proxy TradePartner entity that removes the the state list field keeping
 * the TradePartner returned to the client simple.
 */
package com.sf.biocapture.proxy;

/**
 *
 * @author @wizzyclems
 */
public class TradePartner
{
    private String tpCode;
    private String tpName;

    public TradePartner() {
    }
    
    public String getTpCode() {
        return tpCode;
    }

    public void setTpCode(String tpCode) {
        this.tpCode = tpCode;
    }

    public String getTpName() {
        return tpName;
    }

    public void setTpName(String tpName) {
        this.tpName = tpName;
    }
}
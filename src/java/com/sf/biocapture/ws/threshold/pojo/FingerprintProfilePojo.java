/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.threshold.pojo;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Marcel
 * @since Jul 12, 2016, 2:23:45 PM
 */
@XmlRootElement(name = "profile")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class FingerprintProfilePojo implements Serializable {

    private int minNumberOfBlocks;
    private int minNumberOfGoodBlocks;
    private int maxNumberOfDarkBlocks;
    private int maxNumberOfLightBlocks;
    private int maxNumberOfPoorRidgeFlow;
    private int maxNotPartOfPrint;
    private int minAFIQ;
    private int minNFIQ;

    public int getMinNumberOfBlocks() {
        return minNumberOfBlocks;
    }

    public void setMinNumberOfBlocks(int minNumberOfBlocks) {
        this.minNumberOfBlocks = minNumberOfBlocks;
    }

    public int getMinNumberOfGoodBlocks() {
        return minNumberOfGoodBlocks;
    }

    public void setMinNumberOfGoodBlocks(int minNumberOfGoodBlocks) {
        this.minNumberOfGoodBlocks = minNumberOfGoodBlocks;
    }

    public int getMaxNumberOfDarkBlocks() {
        return maxNumberOfDarkBlocks;
    }

    public void setMaxNumberOfDarkBlocks(int maxNumberOfDarkBlocks) {
        this.maxNumberOfDarkBlocks = maxNumberOfDarkBlocks;
    }

    public int getMaxNumberOfLightBlocks() {
        return maxNumberOfLightBlocks;
    }

    public void setMaxNumberOfLightBlocks(int maxNumberOfLightBlocks) {
        this.maxNumberOfLightBlocks = maxNumberOfLightBlocks;
    }

    public int getMaxNumberOfPoorRidgeFlow() {
        return maxNumberOfPoorRidgeFlow;
    }

    public void setMaxNumberOfPoorRidgeFlow(int maxNumberOfPoorRidgeFlow) {
        this.maxNumberOfPoorRidgeFlow = maxNumberOfPoorRidgeFlow;
    }

    public int getMaxNotPartOfPrint() {
        return maxNotPartOfPrint;
    }

    public void setMaxNotPartOfPrint(int maxNotPartOfPrint) {
        this.maxNotPartOfPrint = maxNotPartOfPrint;
    }

    public int getMinAFIQ() {
        return minAFIQ;
    }

    public void setMinAFIQ(int minAFIQ) {
        this.minAFIQ = minAFIQ;
    }

    public int getMinNFIQ() {
        return minNFIQ;
    }

    public void setMinNFIQ(int minNFIQ) {
        this.minNFIQ = minNFIQ;
    }

}

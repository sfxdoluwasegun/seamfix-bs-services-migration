/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sf.biocapture.ws.threshold.pojo;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Marcel
 * @since 05-Aug-2016, 08:49:39
 */
@XmlRootElement(name = "property")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class DemographicPropertyInfo implements Serializable {

    @XmlElement(name = "minLength")
    private Integer minLength;
    @XmlElement(name = "maxLength")
    private Integer maxLength;
    @XmlElement(name = "maxAllowableContinousCharacters")
    private Integer maxAllowableContinousCharacters;
    @XmlElement(name = "maxAllowableSequentialCharacters")
    private Integer maxAllowableSequentialCharacters;
    @XmlElement(name = "maxAllowableSequentialKeyboardCharacters")
    private Integer maxAllowableSequentialKeyboardCharacters;
    @XmlElement(name = "maxSpecialCharacters")
    private Integer maxSpecialCharacters;
    @XmlElement(name = "maxAllowableDigits")
    private Integer maxAllowableDigits;
    @XmlElement(name = "maxAllowableNonDigits")
    private Integer maxAllowableNonDigits;
    @XmlElement(name = "maxConsecutiveConsonants")
    private Integer maxConsecutiveConsonants;
    @XmlElement(name = "fieldName")
    private String fieldName;
    @XmlElement(name = "checkVowels")
    private String checkVowels;
    @XmlElement(name = "regexExpression")
    private String regexExpression;
    @XmlElement(name = "regexDescription")
    private String regexDescription;

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public Integer getMaxAllowableContinousCharacters() {
        return maxAllowableContinousCharacters;
    }

    public void setMaxAllowableContinousCharacters(Integer maxAllowableContinousCharacters) {
        this.maxAllowableContinousCharacters = maxAllowableContinousCharacters;
    }

    public Integer getMaxAllowableSequentialCharacters() {
        return maxAllowableSequentialCharacters;
    }

    public void setMaxAllowableSequentialCharacters(Integer maxAllowableSequentialCharacters) {
        this.maxAllowableSequentialCharacters = maxAllowableSequentialCharacters;
    }

    public Integer getMaxAllowableSequentialKeyboardCharacters() {
        return maxAllowableSequentialKeyboardCharacters;
    }

    public void setMaxAllowableSequentialKeyboardCharacters(Integer maxAllowableSequentialKeyboardCharacters) {
        this.maxAllowableSequentialKeyboardCharacters = maxAllowableSequentialKeyboardCharacters;
    }

    public Integer getMaxSpecialCharacters() {
        return maxSpecialCharacters;
    }

    public void setMaxSpecialCharacters(Integer maxSpecialCharacters) {
        this.maxSpecialCharacters = maxSpecialCharacters;
    }

    public Integer getMaxAllowableDigits() {
        return maxAllowableDigits;
    }

    public void setMaxAllowableDigits(Integer maxAllowableDigits) {
        this.maxAllowableDigits = maxAllowableDigits;
    }

    public Integer getMaxConsecutiveConsonants() {
        return maxConsecutiveConsonants;
    }

    public void setMaxConsecutiveConsonants(Integer maxConsecutiveConsonants) {
        this.maxConsecutiveConsonants = maxConsecutiveConsonants;
    }

    public String getRegexExpression() {
        return regexExpression;
    }

    public void setRegexExpression(String regexExpression) {
        this.regexExpression = regexExpression;
    }

    public Integer getMaxAllowableNonDigits() {
        return maxAllowableNonDigits;
    }

    public void setMaxAllowableNonDigits(Integer maxAllowableNonDigits) {
        this.maxAllowableNonDigits = maxAllowableNonDigits;
    }

    public String getRegexDescription() {
        return regexDescription;
    }

    public void setRegexDescription(String regexDescription) {
        this.regexDescription = regexDescription;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getCheckVowels() {
        return checkVowels;
    }

    public void setCheckVowels(String checkVowels) {
        this.checkVowels = checkVowels;
    }

}

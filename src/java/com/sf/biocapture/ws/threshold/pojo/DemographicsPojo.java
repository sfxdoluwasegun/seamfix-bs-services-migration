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
 * @since 05-Aug-2016, 08:57:51
 */
@XmlRootElement(name = "demographic")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class DemographicsPojo implements Serializable {

    @XmlElement(name = "surname")
    private DemographicPropertyInfo surname;
    @XmlElement(name = "firstName")
    private DemographicPropertyInfo firstName;
    @XmlElement(name = "motherMaidenName")
    private DemographicPropertyInfo motherMaidenName;
    @XmlElement(name = "middleName")
    private DemographicPropertyInfo middleName;
    @XmlElement(name = "email")
    private DemographicPropertyInfo email;
    @XmlElement(name = "address")
    private DemographicPropertyInfo address;
    @XmlElement(name = "dateOfBirth")
    private DemographicPropertyInfo dateOfBirth;
    @XmlElement(name = "companyName")
    private DemographicPropertyInfo companyName;
    @XmlElement(name = "companyAddress")
    private DemographicPropertyInfo companyAddress;
    @XmlElement(name = "houseAddress")
    private DemographicPropertyInfo houseAddress;
    @XmlElement(name = "streetAddress")
    private DemographicPropertyInfo streetAddress;
    @XmlElement(name = "cityAddress")
    private DemographicPropertyInfo cityAddress;

    @XmlElement(name = "companyHouseAddress")
    private DemographicPropertyInfo companyHouseAddress;
    @XmlElement(name = "companyStreetAddress")
    private DemographicPropertyInfo companyStreetAddress;
    @XmlElement(name = "companyCityAddress")
    private DemographicPropertyInfo companyCityAddress;
    @XmlElement(name = "alternatePhoneNumber")
    private DemographicPropertyInfo alternatePhoneNumber;
    @XmlElement(name = "passportExpiryDate")
    private DemographicPropertyInfo passportExpiryDate;
    @XmlElement(name = "passportNumber")
    private DemographicPropertyInfo passportNumber;
    @XmlElement(name = "postalCode")
    private DemographicPropertyInfo postalCode;
    @XmlElement(name = "companyRegNumber")
    private DemographicPropertyInfo companyRegNumber;
    @XmlElement(name = "companyPostalCode")
    private DemographicPropertyInfo companyPostalCode;

    public DemographicPropertyInfo getSurname() {
        return surname;
    }

    public void setSurname(DemographicPropertyInfo surname) {
        this.surname = surname;
    }

    public DemographicPropertyInfo getFirstName() {
        return firstName;
    }

    public void setFirstName(DemographicPropertyInfo firstName) {
        this.firstName = firstName;
    }

    public DemographicPropertyInfo getMotherMaidenName() {
        return motherMaidenName;
    }

    public void setMotherMaidenName(DemographicPropertyInfo motherMaidenName) {
        this.motherMaidenName = motherMaidenName;
    }

    public DemographicPropertyInfo getMiddleName() {
        return middleName;
    }

    public void setMiddleName(DemographicPropertyInfo middleName) {
        this.middleName = middleName;
    }

    public DemographicPropertyInfo getEmail() {
        return email;
    }

    public void setEmail(DemographicPropertyInfo email) {
        this.email = email;
    }

    public DemographicPropertyInfo getAddress() {
        return address;
    }

    public void setAddress(DemographicPropertyInfo address) {
        this.address = address;
    }

    public DemographicPropertyInfo getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(DemographicPropertyInfo dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public DemographicPropertyInfo getCompanyName() {
        return companyName;
    }

    public void setCompanyName(DemographicPropertyInfo companyName) {
        this.companyName = companyName;
    }

    public DemographicPropertyInfo getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(DemographicPropertyInfo companyAddress) {
        this.companyAddress = companyAddress;
    }

    public DemographicPropertyInfo getHouseAddress() {
        return houseAddress;
    }

    public void setHouseAddress(DemographicPropertyInfo houseAddress) {
        this.houseAddress = houseAddress;
    }

    public DemographicPropertyInfo getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(DemographicPropertyInfo streetAddress) {
        this.streetAddress = streetAddress;
    }

    public DemographicPropertyInfo getCityAddress() {
        return cityAddress;
    }

    public void setCityAddress(DemographicPropertyInfo cityAddress) {
        this.cityAddress = cityAddress;
    }

    public DemographicPropertyInfo getCompanyHouseAddress() {
        return companyHouseAddress;
    }

    public void setCompanyHouseAddress(DemographicPropertyInfo companyHouseAddress) {
        this.companyHouseAddress = companyHouseAddress;
    }

    public DemographicPropertyInfo getCompanyStreetAddress() {
        return companyStreetAddress;
    }

    public void setCompanyStreetAddress(DemographicPropertyInfo companyStreetAddress) {
        this.companyStreetAddress = companyStreetAddress;
    }

    public DemographicPropertyInfo getCompanyCityAddress() {
        return companyCityAddress;
    }

    public void setCompanyCityAddress(DemographicPropertyInfo companyCityAddress) {
        this.companyCityAddress = companyCityAddress;
    }

    public void setAlternatePhoneNumber(DemographicPropertyInfo alternatePhoneNumber) {
        this.alternatePhoneNumber = alternatePhoneNumber;
    }

    public void setExpiryDate(DemographicPropertyInfo passportExpiryDate) {
        this.passportExpiryDate = passportExpiryDate;
    }

    public void setPassportNumber(DemographicPropertyInfo passportNumber) {
        this.passportNumber = passportNumber;
    }

    public void setPostalCode(DemographicPropertyInfo postalCode) {
        this.postalCode = postalCode;
    }

    public void setCompanyRegNumber(DemographicPropertyInfo companyRegNumber) {
        this.companyRegNumber = companyRegNumber;
    }

    public void setCompanyPostalCode(DemographicPropertyInfo companyPostalCode) {
        this.companyPostalCode = companyPostalCode;
    }

    public DemographicPropertyInfo getAlternatePhoneNumber() {
        return alternatePhoneNumber;
    }

    public DemographicPropertyInfo getPassportExpiryDate() {
        return passportExpiryDate;
    }

    public DemographicPropertyInfo getPassportNumber() {
        return passportNumber;
    }

    public DemographicPropertyInfo getPostalCode() {
        return postalCode;
    }

    public DemographicPropertyInfo getCompanyRegNumber() {
        return companyRegNumber;
    }

    public DemographicPropertyInfo getCompanyPostalCode() {
        return companyPostalCode;
    }

}

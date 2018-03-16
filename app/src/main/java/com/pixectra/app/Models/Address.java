package com.pixectra.app.Models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * Created by Prashu on 2/7/2018.
 * Used to store shipping address of the user
 */

public class Address implements Serializable {
    @Exclude
    String key;
    String name;
    String addNo;
    String street;
    String pincode;
    String city;
    String state;
    String mobile;
    String optional;

    public Address() {
    }

    public Address(String name, String addNo, String street, String pincode, String city, String state, String mobile, String optional) {
        this.name = name;
        this.addNo = addNo;
        this.street = street;
        this.pincode = pincode;
        this.city = city;
        this.state = state;
        this.mobile = mobile;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddNo() {
        return addNo;
    }

    public void setAddNo(String addNo) {
        this.addNo = addNo;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOptional() {
        return optional;
    }

    public void setOptional(String optional) {
        this.optional = optional;
    }

    @Override
    public String toString() {
        return "Address{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", addNo='" + addNo + '\'' +
                ", street='" + street + '\'' +
                ", pincode='" + pincode + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", mobile='" + mobile + '\'' +
                ", optional='" + optional + '\'' +
                '}';
    }
}

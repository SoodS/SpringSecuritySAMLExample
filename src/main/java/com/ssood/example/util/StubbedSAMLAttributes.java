package com.ssood.example.util;


import java.io.Serializable;
import java.util.List;

public class StubbedSAMLAttributes implements Serializable {
    private String firstName;
    private String lastName;
    private String mail;


    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
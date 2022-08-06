package com.example.meraki.model;

public class Monetize {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getAdmirer() {
        return admirer;
    }

    public void setAdmirer(String admirer) {
        this.admirer = admirer;
    }

    private String id;
    private String offer;
    private String admirer;

    public Monetize() {
    }

    public Monetize(String id, String offer, String admirer) {
        this.id = id;
        this.offer = offer;
        this.admirer = admirer;
    }


}

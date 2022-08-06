package com.example.meraki.model;

public class Post {

    private String description;
    private String imageurl;
    private String postid;
    private String publisher;

    public String getOfferAccepted() {
        return offerAccepted;
    }

    public void setOfferAccepted(String offerAccepted) {
        this.offerAccepted = offerAccepted;
    }

    private String offerAccepted;

    public String getStartAmt() {
        return startAmt;
    }

    public void setStartAmt(String startAmt) {
        this.startAmt = startAmt;
    }

    private String startAmt;


    private Boolean monetize;

    public Post(String description, String imageurl, String postid, String publisher, Boolean monetize,String startAmt,String offerAccepted) {
        this.description = description;
        this.imageurl = imageurl;
        this.postid = postid;
        this.publisher = publisher;
        this.monetize = monetize;
        this.startAmt=startAmt;
        this.offerAccepted=offerAccepted;
    }



    public Post() {
    }

    public Boolean getMonetize() {
        return monetize;
    }

    public void setMonetize(Boolean monetize) {
        this.monetize = monetize;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
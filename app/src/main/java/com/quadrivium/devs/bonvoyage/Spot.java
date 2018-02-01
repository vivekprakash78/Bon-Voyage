package com.quadrivium.devs.bonvoyage;

/**
 * Created by vivek on 02-02-2018.
 */

public class Spot {

    private String spotID;
    private String spotName;
    private String spotAddress;
    private Boolean spotOpenNow;
    private double spotRating;


    public Spot(String spotID, String spotName, String spotAddress,Boolean spotOpenNow,double spotRating)
    {
        this.spotID=spotID;
        this.spotName=spotName;
        this.spotAddress=spotAddress;
        this.spotOpenNow=spotOpenNow;
        this.spotRating=spotRating;
    }

    public String getSpotID() {
        return spotID;
    }
    public String getSpotname() {
        return spotName;
    }
    public String getSpotAddress(){
        return spotAddress;
    }
    public Boolean getSpotOpenNow(){
        return spotOpenNow;
    }
    public double getspotRating() {
        return spotRating;
    }

}

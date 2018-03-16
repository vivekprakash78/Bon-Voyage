package com.quadrivium.devs.bonvoyage;

/**
 * Created by vivek on 22-02-2018.
 */

public class City {
    private String cityID;
    private String cityName;


    public City(String cityID, String cityName)
    {
        this.cityID=cityID;
        this.cityName=cityName;
    }

    public String getCityID() {
        return cityID;
    }
    public String getCityName() {
        return cityName;
    }

}

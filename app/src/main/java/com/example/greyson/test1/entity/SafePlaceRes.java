package com.example.greyson.test1.entity;

/**
 * Created by greyson on 1/4/17.
 */

public class SafePlaceRes {

    /**
     * id : 86
     * establishment : McDonald's
     * address : 100 Waverley Rd
     * suburb : Malvern East
     * postcode : 3145
     * state : VIC
     * type : Restaurant
     * latitude : -37.8761
     * longtitude : 145.0478
     */

    private int id;
    private String establishment;
    private String address;
    private String suburb;
    private int postcode;
    private String state;
    private String type;
    private Double latitude;
    private Double longtitude;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEstablishment() {
        return establishment;
    }

    public void setEstablishment(String establishment) {
        this.establishment = establishment;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public int getPostcode() {
        return postcode;
    }

    public void setPostcode(int postcode) {
        this.postcode = postcode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(Double longtitude) {
        this.longtitude = longtitude;
    }
}

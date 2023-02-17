package com.example.lab6;

/**
 * This class defines a city.
 * Class implements Comparable interface for sorting in CityList
 */

public class City implements Comparable {
    private String city;
    private String province;

    City(String city, String province){
        this.city = city;
        this.province = province;
    }

    String getCityName() {
        return this.city;
    }

    String getProvince() {
        return this.province;
    }

    @Override
    public int compareTo(Object o){
        City city = (City) o;
        return this.city.compareTo(city.getCityName());
    }
}

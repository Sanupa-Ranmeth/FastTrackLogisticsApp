package models;

public class City {
    private int cityID;
    private String cityName;

    //Constructor
    public City(int cityID, String cityName) {
        this.cityID = cityID;
        this.cityName = cityName;
    }

    //Getters
    public int getCityID() {
        return cityID;
    }

    public String getCityName() {
        return cityName;
    }

    //Display City
    @Override
    public String toString() {
        return cityName;
    }
}

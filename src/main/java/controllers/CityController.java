package controllers;

import models.City;
import models.CityDAO;

import java.util.List;

public class CityController {
    private CityDAO cityDAO = new CityDAO();

    //Get all cities
    public List<City> getAllCities() {
        return cityDAO.getAllCities();
    }

    public String getCityNamebyCityID(int cityID) {
        return cityDAO.getCityNameByCityID(cityID);
    }

    public int getCityIDByCityName(String cityName) {
        return cityDAO.getCityIDByCityName(cityName);
    }
}

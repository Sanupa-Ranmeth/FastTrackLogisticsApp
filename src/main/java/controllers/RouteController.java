package controllers;

import models.City;
import models.Route;
import models.RouteDAO;

import java.util.List;

public class RouteController {
    private RouteDAO routeDAO = new RouteDAO();

    public boolean addRoute (Route route) {
        return routeDAO.addRoute(route);
    }

    public boolean removeRoute (int routeID) {
        return routeDAO.deleteRoute(routeID);
    }

    public boolean addCityToRoute (int routeID, int cityID, int sequence) {
        return routeDAO.addCitytoRoute(routeID, cityID, sequence);
    }

    public List<City> getCitiesbyRoute (int routeID) {
        return routeDAO.getCitiesbyRouteID(routeID);
    }

    public int getRouteIDFromCityName (String cityName) {
        return routeDAO.getRouteFromCityName(cityName);
    }
}

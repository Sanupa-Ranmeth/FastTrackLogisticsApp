package controllers;

import models.City;
import models.Route;
import models.RouteDAO;

import java.util.List;

public class RouteController {
    private final RouteDAO routeDAO = new RouteDAO();

    public boolean addRoute(Route route) {
        return routeDAO.addRoute(route);
    }

    public boolean removeRoute(int routeID) {
        return routeDAO.deleteRoute(routeID);
    }

    public boolean addCityToRoute(int routeID, int cityID, int sequence) {
        return routeDAO.addCitytoRoute(routeID, cityID, sequence);
    }

    // Alias for addCityToRoute to match RouteView usage
    public boolean addCitytoRoute(int routeID, int cityID, int sequence) {
        return addCityToRoute(routeID, cityID, sequence);
    }

    public List<City> getCitiesbyRoute(int routeID) {
        return routeDAO.getCitiesbyRouteID(routeID);
    }

    public int getRouteIDFromCityName(String cityName) {
        return routeDAO.getRouteFromCityName(cityName);
    }

    public List<Route> getAllRoutesWithCities() {
        return routeDAO.getAllRoutesWithCities();
    }

    public boolean updateRoute(int routeID, int startCityID, int endCityID) {
        return routeDAO.updateRoute(routeID, startCityID, endCityID);
    }

    public boolean removeAllCitiesFromRoute(int routeID) {
        return routeDAO.removeAllCitiesFromRoute(routeID);
    }

    public boolean removeCityFromRoute(int routeID, int cityID) {
        return routeDAO.removeCityFromRoute(routeID, cityID);
    }

    public int getCitySequence(int routeID, int cityID) {
        return routeDAO.getCitySequence(routeID, cityID);
    }

    public boolean addRoute(int startCityID, int endCityID) {
        Route route = new Route(startCityID, endCityID);
        return routeDAO.addRoute(route);
    }

    // Alias for deleteRoute to match RouteView usage
    public boolean deleteRoute(int routeID) {
        return removeRoute(routeID);
    }

    // Returns all routes as Object[][] for RouteView table
    public Object[][] getAllRoutes() {
        List<Route> routes = routeDAO.getAllRoutesWithCities();
        Object[][] data = new Object[routes.size()][];
        for (int i = 0; i < routes.size(); i++) {
            Route r = routes.get(i);
            data[i] = new Object[] {
                r.getRouteID(),
                r.getStartCityName(),
                r.getEndCityName(),
                r.getDistance(),
                r.getEstimatedTime()
            };
        }
        return data;
    }
}

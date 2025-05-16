package models;

public class Route {
    private int RouteID;
    private int StartCityID;
    private int EndCityID;

    //Full Constructor
    public Route(int routeID, int startCityID, int endCityID) {
        RouteID = routeID;
        StartCityID = startCityID;
        EndCityID = endCityID;
    }

    //Partial constructor for adding routes
    public Route(int startCityID, int endCityID) {
        StartCityID = startCityID;
        EndCityID = endCityID;
    }

    //getters and setters
    public int getRouteID() {
        return RouteID;
    }

    public void setRouteID(int routeID) {
        RouteID = routeID;
    }

    public int getStartCityID() {
        return StartCityID;
    }

    public void setStartCityID(int startCityID) {
        StartCityID = startCityID;
    }

    public int getEndCityID() {
        return EndCityID;
    }

    public void setEndCityID(int endCityID) {
        EndCityID = endCityID;
    }
}

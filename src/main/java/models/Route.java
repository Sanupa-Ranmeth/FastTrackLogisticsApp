package models;

public class Route {
    private int RouteID;
    private int StartCityID;
    private int EndCityID;
    private String startCityName;
    private String endCityName;
    private double distance;
    private String estimatedTime;

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

    // Getters and setters for new fields
    public String getStartCityName() {
        return startCityName;
    }
    public void setStartCityName(String startCityName) {
        this.startCityName = startCityName;
    }
    public String getEndCityName() {
        return endCityName;
    }
    public void setEndCityName(String endCityName) {
        this.endCityName = endCityName;
    }
    public double getDistance() {
        return distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }
    public String getEstimatedTime() {
        return estimatedTime;
    }
    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }
}

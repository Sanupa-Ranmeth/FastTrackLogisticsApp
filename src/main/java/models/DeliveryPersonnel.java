package models;

public class DeliveryPersonnel extends User {
    private int driverID;
    private String driverName;
    private String schedule;
    private int routeID;

    //Partial constructor - for adding new drivers, driverID is not necessary
    public DeliveryPersonnel(String username, String password, String email, String driverName, String schedule, int routeID) {
        super(username, password, email, "driver");
        this.driverName = driverName;
        this.schedule = schedule;
        this.routeID = routeID;
    }

    //Full constructor - for updating / retrieving drivers
    public DeliveryPersonnel(int driverID, String username, String password, String email, String driverName, String schedule, int routeID) {
        super(username, password, email, "driver");
        this.driverID = driverID;
        this.driverName = driverName;
        this.schedule = schedule;
        this.routeID = routeID;
    }

    //Getters
    public int getDriverID() {
        return driverID;
    }

    public String getDriverName() {
        return driverName;
    }

    public String getSchedule() {
        return schedule;
    }

    public int getRouteID() {
        return routeID;
    }

    //Setters
    public void setDriverID(int driverID) {
        this.driverID = driverID;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public void setRouteID(int routeID) {
        this.routeID = routeID;
    }
}

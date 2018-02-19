package naMsze.naMsze;

public class Location {
    private int LocationID;
    private String StreetAddress;
    private String City;
    private String State;
    private double X;
    private double Y;

    public Location() {}

    public Location(int LocationID, String StreetAddress, String City, String State,
                    double X, double Y) {
        this.LocationID = LocationID;
        this.StreetAddress = StreetAddress;
        this.City = City;
        this.State = State;
        this.X = X;
        this.Y = Y;
    }

    public Location(String StreetAddress, String City, String State,
                    double X, double Y) {
        this.StreetAddress = StreetAddress;
        this.City = City;
        this.State = State;
        this.X = X;
        this.Y = Y;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public int getLocationID() {
        return LocationID;
    }

    public void setLocationID(int locationID) {
        LocationID = locationID;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getStreetAddress() {
        return StreetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        StreetAddress = streetAddress;
    }

    public double getX() {
        return X;
    }

    public void setX(double x) {
        X = x;
    }

    public double getY() {
        return Y;
    }

    public void setY(double y) {
        Y = y;
    }
}

package naMsze.naMsze;

public class Church {
    private int ChurchID;
    private String Name;
    private int LocationID;

    public Church() {};

    public Church(int ChurchID, String Name, int LocationID) {
        this.ChurchID = ChurchID;
        this.Name = Name;
        this.LocationID = LocationID;
    }

    public Church(String Name, int LocationID) {
        this.Name = Name;
        this.LocationID = LocationID;
    }

    public int getChurchID() {
        return ChurchID;
    }

    public void setChurchID(int churchID) {
        ChurchID = churchID;
    }

    public int getLocationID() {
        return LocationID;
    }

    public void setLocationID(int locationID) {
        LocationID = locationID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}

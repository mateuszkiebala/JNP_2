package naMsze.naMsze;

public class MassType {
    private int MassTypeID;
    private String Name;

    public MassType() {};

    public MassType(int MassTypeID, String Name) {
        this.MassTypeID = MassTypeID;
        this.Name = Name;
    }

    public MassType(String Name) {
        this.Name = Name;
    }

    public int getMassTypeID() {
        return MassTypeID;
    }

    public void setMassTypeID(int massTypeID) {
        MassTypeID = massTypeID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}

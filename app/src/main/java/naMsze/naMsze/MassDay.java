package naMsze.naMsze;

public class MassDay {
    private int DayID;
    private String DayOfTheWeek;
    private int MassTypeID;

    public MassDay() {};

    public MassDay(int DayID, String DayOfTheWeek, int MassTypeID) {
        this.DayID = DayID;
        this.DayOfTheWeek = DayOfTheWeek;
        this.MassTypeID = MassTypeID;
    }

    public MassDay(String DayOfTheWeek, int MassTypeID) {
        this.DayOfTheWeek = DayOfTheWeek;
        this.MassTypeID = MassTypeID;
    }

    public int getDayID() {
        return DayID;
    }

    public void setDayID(int dayID) {
        DayID = dayID;
    }

    public String getDayOfTheWeek() {
        return DayOfTheWeek;
    }

    public void setDayOfTheWeek(String dayOfTheWeek) {
        DayOfTheWeek = dayOfTheWeek;
    }

    public int getMassTypeID() {
        return MassTypeID;
    }

    public void setMassTypeID(int massTypeID) {
        MassTypeID = massTypeID;
    }
}

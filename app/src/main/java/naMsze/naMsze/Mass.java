package naMsze.naMsze;

public class Mass {
    private int MassID;
    private String Hour;
    private String Target;
    private int PeriodID;
    private int MassTypeID;
    private int ChurchID;

    public Mass() {}

    public Mass(int MassID, String Hour, String Target, int PeriodID, int MassTypeID, int ChurchID) {
        this.MassID = MassID;
        this.Hour = Hour;
        this.Target = Target;
        this.PeriodID = PeriodID;
        this.MassTypeID = MassTypeID;
        this.ChurchID = ChurchID;
    }

    public Mass(String Hour, String Target, int PeriodID, int MassTypeID, int ChurchID) {
        this.Hour = Hour;
        this.Target = Target;
        this.PeriodID = PeriodID;
        this.MassTypeID = MassTypeID;
        this.ChurchID = ChurchID;
    }

    public int getChurchID() {
        return ChurchID;
    }

    public void setChurchID(int churchID) {
        ChurchID = churchID;
    }

    public String getHour() {
        return Hour;
    }

    public void setHour(String hour) {
        Hour = hour;
    }

    public int getMassID() {
        return MassID;
    }

    public void setMassID(int massID) {
        MassID = massID;
    }

    public int getPeriodID() {
        return PeriodID;
    }

    public void setPeriodID(int periodID) {
        PeriodID = periodID;
    }

    public int getMassTypeID() {
        return MassTypeID;
    }

    public void setMassTypeID(int massTypeID) {
        MassTypeID = massTypeID;
    }

    public String getTarget() {
        return Target;
    }

    public void setTarget(String target) {
        Target = target;
    }
}

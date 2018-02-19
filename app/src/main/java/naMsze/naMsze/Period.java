package naMsze.naMsze;

public class Period {
    private int PeriodID;
    private String Name;
    private String Start;
    private String End;

    public Period() {}

    public Period(int PeriodID, String Name, String Start, String End) {
        this.PeriodID = PeriodID;
        this.Name = Name;
        this.Start = Start;
        this.End = End;
    }

    public Period(String Name, String Start, String End) {
        this.Name = Name;
        this.Start = Start;
        this.End = End;
    }

    public String getEnd() {
        return End;
    }

    public void setEnd(String end) {
        End = end;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getPeriodID() {
        return PeriodID;
    }

    public void setPeriodID(int periodID) {
        PeriodID = periodID;
    }

    public String getStart() {
        return Start;
    }

    public void setStart(String start) {
        Start = start;
    }
}

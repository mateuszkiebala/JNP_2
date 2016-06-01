package naMsze.naMsze;

public class Feast {
    private int FeastID;
    private String Name;
    private String Date;

    public Feast() {
    }

    ;

    public Feast(int FeastID, String Name, String Date) {
        this.FeastID = FeastID;
        this.Name = Name;
        this.Date = Date;
    }

    public Feast(String Name, String Date) {
        this.Name = Name;
        this.Date = Date;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getFeastID() {
        return FeastID;
    }

    public void setFeastID(int feastID) {
        FeastID = feastID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public static String countDateOfEaster(int year) {
        String res;
        int a, b, c, d, e;
        a = year % 19;
        b = year % 4;
        c = year % 7;
        d = (a * 19 + 24) % 30;
        e = (2 * b + 4 * c + 6 * d + 5) % 7;
        if ((d == 29 && e == 6) || (d == 28 && e == 6)) {
            d -= 7;
        }
        int date = d + e + 22;
        if (date > 31) {
            res = Integer.toString(year) + "-04-" + Integer.toString(date % 31);
        } else {
            res = Integer.toString(year) + "-03-" + Integer.toString(date);
        }
        return res;
    }
}

package naMsze.naMsze;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.text.SimpleDateFormat;
import java.util.*;

public class MyDatabase extends SQLiteAssetHelper {
    private static final String DB_NAME = "mass.db";
    private static final int DB_VERSION = 1;
    private Context _context;
    private String allNameString;
    private String studentsNameString;
    private String childrenNameString;

    public MyDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        _context = context;
        allNameString = context.getResources().getString(R.string.all_string);
        studentsNameString = context.getResources().getString(R.string.students_string);
        childrenNameString = context.getResources().getString(R.string.children_string);
    }

    private String quoteString(String s) {
        return "\"" + s + "\"";
    }

    /** Getting PeriodID of selected in argument period. */
    public int getPeriodID(String Date) {
        int periodID = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery = "SELECT PeriodID FROM Period WHERE " + quoteString(Date)
                            + " >= Start AND End >= " + quoteString(Date)
                            + " ORDER BY PeriodID DESC";
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.moveToFirst()) {
            periodID = Integer.parseInt(cursor.getString(0));
        }
        cursor.close();
        return periodID;
    }

    /** Getting all churches in concrete City. */
    public List<Location> getChurches(String City, double R, double Xs, double Ys) {
        List<Location> churchesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery = "SELECT * FROM Location WHERE City == "
                            + quoteString(City);
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.moveToFirst()) {
            do {
                double X = cursor.getDouble(4);
                double Y = cursor.getDouble(5);
                if (Geolocation.distance(X, Y, Xs, Ys, 'K') <= R) {
                    Location loc = new Location();
                    loc.setLocationID(cursor.getInt(0));
                    loc.setStreetAddress(cursor.getString(1));
                    loc.setCity(cursor.getString(2));
                    loc.setState(cursor.getString(3));
                    loc.setX(cursor.getDouble(4));
                    loc.setY(cursor.getDouble(5));
                    churchesList.add(loc);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return churchesList;
    }

    /** Returns the massTypeID for selected dayOfTheWeek. */
    public int getMassTypeID(String DayOfTheWeek) {
        int MassTypeID = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery = "SELECT MassTypeID FROM MassDay WHERE DayOfTheWeek == "
                + quoteString(DayOfTheWeek);
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.moveToFirst()) {
            MassTypeID = Integer.parseInt(cursor.getString(0));
        }
        cursor.close();
        return MassTypeID;
    }

    /** Returns all masses of selected target after concrete hour. */
    public List<String> getAllMassesAfterHour(String Hour, String Target, int PeriodID,
                                        int MassTypeID, int ChurchID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery;
        List<String> result = new ArrayList<>();

        if (Target.equals(childrenNameString))
            Target = "children";
        else if (Target.equals(studentsNameString))
            Target = "students";

        if (Target.equals(allNameString)){
            sqlQuery = "SELECT Hour FROM Mass WHERE "
                    + quoteString(Hour) + " <= Hour AND ("
                    + PeriodID + " == PeriodID OR PeriodID == 1) AND " // 1 is yearlong
                    + MassTypeID + " == MassTypeID AND "
                    + ChurchID + " == ChurchID ORDER BY Hour";
        } else {
            sqlQuery = "SELECT Hour FROM Mass WHERE "
                    + quoteString(Hour) + " <= Hour AND "
                    + quoteString(Target) + " == Target AND ("
                    + PeriodID + " == PeriodID OR PeriodID == 1) AND " // 1 is yearlong
                    + MassTypeID + " == MassTypeID AND "
                    + ChurchID + " == ChurchID ORDER BY Hour";
        }

        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String hour = cursor.getString(0);
                result.add(hour);
            } while(cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    /** Checks whether there is a feast at this day. */
    public boolean isThereAFeast(String Date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery = "SELECT FeastID FROM Feast WHERE "
                + quoteString(Date) + " == Date";
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.moveToFirst()) {
            return true;
        }
        return false;
    }

    public int getChurchID(int LocationID) {
        int ChurchID = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery = "SELECT ChurchID FROM Church WHERE LocationID == "
                            + quoteString(Integer.toString(LocationID));
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.moveToFirst()) {
            ChurchID = cursor.getInt(0);
        }
        cursor.close();
        return ChurchID;
    }

    public String getChurchName(int ChurchID) {
        String churchName = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery = "SELECT Name FROM Church WHERE ChurchID == " + ChurchID;
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.moveToFirst()) {
            churchName = cursor.getString(0);
        }
        cursor.close();
        return churchName;
    }

    public String getEasterDate() {
        String date = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery = "SELECT Date FROM Feast WHERE Name == \"Wielkanoc\"";
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.moveToFirst()) {
            date = cursor.getString(0);
        }
        cursor.close();
        return date;
    }

    public void updateEasterDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery = "UPDATE Feast SET Date = "
                        + quoteString(date)
                        + " WHERE Name == \"Wielkanoc\"";
        db.execSQL(sqlQuery);
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
        String Wniebowstapienie_str = null;
        String BozeCialo_str = null;
        try {
            Wniebowstapienie_str = addDays(formatDate.parse(date), 42);
            BozeCialo_str = addDays(formatDate.parse(date), 60);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        // Update Wniebowstapienie.
        sqlQuery = "UPDATE Feast SET Date = "
                    + quoteString(Wniebowstapienie_str)
                    + " WHERE Name == \"Wniebowst¹pienie\"";
        db.execSQL(sqlQuery);

        // Update Boze Cia³o.
        sqlQuery = "UPDATE Feast SET Date = "
                + quoteString(BozeCialo_str)
                + " WHERE Name == \"Bo¿e Cia³o\"";
        db.execSQL(sqlQuery);
    }

    public void updateYearOfStaticFeasts() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery = "SELECT * FROM Feast WHERE (Name != 'Wielkanoc' AND "
                    + "Name != 'Wniebowst¹pienie' AND "
                    + "Name != 'Bo¿e Cia³o')";
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String oldDate = cursor.getString(2);
                int year = Integer.parseInt(oldDate.substring(0, 4));
                ++year;
                String newDate = Integer.toString(year) + oldDate.substring(4);
                sqlQuery = "UPDATE Feast SET Date = "
                        + quoteString(newDate)
                        + " WHERE Name == " + quoteString(cursor.getString(1));
                db.execSQL(sqlQuery);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private String addDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(java.util.Calendar.DATE, days);
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
        return formatDate.format(cal.getTime());
    }

    public List<Period> getPeriodList() {
        List<Period> res = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlQuery = "SELECT * FROM Period";
        Cursor cursor = db.rawQuery(sqlQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Period p = new Period();
                p.setPeriodID(cursor.getInt(0));
                p.setName(cursor.getString(1));
                p.setStart(cursor.getString(2));
                p.setEnd(cursor.getString(3));
                res.add(p);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    /** Updates year of period, +1 to current year. */
    public void updatePeriod(int periodID, String start, String end) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Generate new start date.
        String[] splited_start = start.split("-");
        int startYear = Integer.parseInt(splited_start[0]);
        String new_start = Integer.toString(startYear + 1) + "-"
                        + splited_start[1] + "-" + splited_start[2];

        // Generate new end date.
        String[] splited_end = end.split("-");
        int endYear = Integer.parseInt(splited_end[0]);
        String new_end = Integer.toString(endYear + 1) + "-"
                + splited_end[1] + "-" + splited_end[2];
        String sqlQuery = "UPDATE Period SET Start = "
                    + quoteString(new_start) + ", End = "
                    + quoteString(new_end) + " WHERE PeriodID == " + periodID;
        db.execSQL(sqlQuery);
    }
}

package naMsze.naMsze;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Calendar extends Activity implements AbsListView.OnScrollListener {

    private boolean dateChangedByUser = false; // Variable changes when user change date.
    private String selectedDate;
    private String selectedTarget;
    private double selectedRange;
    private String collectedDate;
    private String collectedTarget;
    private double collectedRange;
    private String time;
    private String date;
    private int hour;
    private int minutes;
    private ListView hoursListView;
    private CircularArrayAdapter<String> hoursListAdapter;
    private ListView minutesListView;
    private CircularArrayAdapter<String> minutesListAdapter;
    private int hoursListHeight;
    private int minutesListHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            selectedDate = extras.getString("date");
            selectedRange = extras.getDouble("range");
            selectedTarget = extras.getString("target");
            collectedDate = selectedDate;
            collectedRange = selectedRange;
            collectedTarget = selectedTarget;
        }

        SimpleDateFormat formatToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat formatHour = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        Date sDate = new Date();
        if (selectedDate == null) {
            // Set current date and time.
            date = formatDate.format(new Date());
            time = formatHour.format(new Date());
            hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
            minutes = calendar.get(java.util.Calendar.MINUTE);
        } else {
            // Set before selected date and time (what user sees on menu).
            try {
                sDate = formatToDate.parse(selectedDate);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
            date = formatDate.format(sDate);
            time = formatHour.format(sDate);
            hour = Integer.parseInt(time.substring(0, 2));
            minutes = Integer.parseInt(time.substring(3, 5));
        }

        initializeHours();
        initializeMinutes();
        initializeCalendar(sDate);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Need to set lists with middle element on middle, because when activity is finished
        // user sees wrong view for a few milliseconds.
        moveItemAtPosToMiddle(hoursListView, getMiddlePosition(hoursListView));
        moveItemAtPosToMiddle(minutesListView, getMiddlePosition(minutesListView));
    }

    /** Set hoursListView. Init of:
     *  Adapter with hours values in descending order
     *  Speed of scrolling
     *  Scroll listener
     *  Hide scrollbar
     *  Runnable for counting list view height
     */
    private void initializeHours() {
        hoursListView = (ListView) findViewById(R.id.hoursListView);
        String[] hourData = new String[24];
        // Hours in descending order.
        for (int i = 0; i <= 23; ++i)
            hourData[i] = String.format("%02d", 23 - i);

        hoursListAdapter = new CircularArrayAdapter<>(this, R.layout.calendar_list_view_item, hourData);
        hoursListView.setAdapter(hoursListAdapter);
        initializeListView(hoursListView);
        // We need to use it in new thread because there is no guarantee that in onCreate, listView
        // has been already drawn, but when we use new thread the listView is drawn and
        // we get correct height.
        hoursListView.post(new Runnable() {
            @Override
            public void run() {
                hoursListHeight = hoursListView.getHeight();
                TextView textView = (TextView) hoursListView.getChildAt(0);
                int center = (textView.getBottom() - textView.getTop()) / 2;
                hoursListView.setSelectionFromTop(hoursListAdapter.QUARTER + (23 - hour),
                        (hoursListHeight / 2) - center);
            }
        });
    }

    /** Set minutesListView. Init of:
     *  Adapter with minutes values in descending order
     *  Speed of scrolling
     *  Scroll listener
     *  Hide scrollbar
     *  Runnable for counting list view height
     */
    private void initializeMinutes() {
        minutesListView = (ListView) findViewById(R.id.minutesListView);
        String[] minutesData = new String[60];
        // Minutes in descending order.
        for (int i = 0; i <= 59; ++i)
            minutesData[i] = String.format("%02d", 59 - i);

        minutesListAdapter = new CircularArrayAdapter<>(this, R.layout.calendar_list_view_item, minutesData);
        minutesListView.setAdapter(minutesListAdapter);
        initializeListView(minutesListView);
        // We need to use it in new thread because there is no guarantee that in onCreate, listView
        // has been already drawn, but when we use new thread the listView is drawn and
        // we get correct height.
        hoursListView.post(new Runnable() {
            @Override
            public void run() {
                minutesListHeight = minutesListView.getHeight();
                TextView textView = (TextView) minutesListView.getChildAt(0);
                int center = (textView.getBottom() - textView.getTop()) / 2;
                minutesListView.setSelectionFromTop(minutesListAdapter.QUARTER + (59 - minutes),
                        (minutesListHeight / 2) - center);
            }
        });
    }

    /** Initialise common features for hours and minutes lists. */
    private void initializeListView(ListView listView) {
        listView.setOnScrollListener(this);
        listView.setVerticalScrollBarEnabled(false);
        // Making scrolling 3 times slower.
        listView.setFriction(ViewConfiguration.getScrollFriction() * 3);
    }

    /** Initialize calendar. */
    private void initializeCalendar(Date sDate) {
        CalendarView calendar = (CalendarView) findViewById(R.id.calendarView);
        calendar.setShowWeekNumber(false);
        // Sets colors.
        calendar.setSelectedWeekBackgroundColor(getResources().getColor(R.color.PowderBlue));
        calendar.setUnfocusedMonthDateColor(getResources().getColor(R.color.Teal));
        calendar.setFocusedMonthDateColor(getResources().getColor(R.color.Black));
        calendar.setWeekSeparatorLineColor(getResources().getColor(R.color.DarkMagenta));
        calendar.setBackgroundColor(getResources().getColor(R.color.LightSteelBlue));

        // Set date that user sees on menu.
        calendar.setDate(sDate.getTime());
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                date = Integer.toString(year) + "-" +
                        String.format("%02d", month + 1) + "-" +
                        String.format("%02d", dayOfMonth);
                dateChangedByUser = true;
            }
        });
    }

    /** SetMarks on hour/minute when activity starts. */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // When view is created, we can mark (color) current hour and minute.
        setMark(hoursListView, 1, getResources().getColor(R.color.Black));
        setMark(minutesListView, 1, getResources().getColor(R.color.Black));
    }

    /** Set time in right format. */
    private void setTime(int hour, int minutes) {
        time = String.format("%02d", hour) + ":" + String.format("%02d", minutes);
    }

    /** Set color on list item. */
    private void setMark(ListView listView, int pos, int color) {
        TextView text = (TextView) listView.getChildAt(pos);
        text.setTextColor(color);
    }

    /** Clears marks on hour/minute text. */
    private void clearMarks(ListView listView) {
        int first = listView.getFirstVisiblePosition();
        int last = listView.getLastVisiblePosition();
        for (int i = first; i <= last; ++i) {
            setMark(listView, last - i, getResources().getColor(R.color.DimGray));
        }
    }

    private void moveItemAtPosToMiddle(ListView listView, int pos) {
        TextView textView = (TextView) listView.getChildAt(pos - listView.getFirstVisiblePosition());
        int depthOfCenter = textView.getTop() + (textView.getBottom() - textView.getTop()) / 2;
        ObjectAnimator.ofInt(listView, "scrollY",
                            depthOfCenter - (hoursListHeight / 2)).setDuration(500).start();
    }

    private int getMiddlePosition(ListView listView) {
        int firstVisiblePos = listView.getFirstVisiblePosition();
        int lastVisiblePos = listView.getLastVisiblePosition();
        return (firstVisiblePos + lastVisiblePos) / 2;
    }

    /** When cancel button is pressed, we back to main menu without changes. */
    public void onCancelButton(View view) {
        Intent mainActivity = new Intent(this, MainActivity.class);
        mainActivity.putExtra("target", collectedTarget);
        mainActivity.putExtra("range", collectedRange);
        mainActivity.putExtra("date", collectedDate);
        mainActivity.putExtra("state", false);
        startActivity(mainActivity);
        moveItemAtPosToMiddle(hoursListView, getMiddlePosition(hoursListView));
        moveItemAtPosToMiddle(minutesListView, getMiddlePosition(minutesListView));
        finish();
    }

    /** Send recently selected data. */
    public void onAcceptButton(View view) {
        setTime(hour, minutes);
        // Prepare to send data.
        Intent mainActivity = new Intent(this, MainActivity.class);
        mainActivity.putExtra("target", selectedTarget);
        mainActivity.putExtra("range", selectedRange);
        mainActivity.putExtra("date", date + " " + time);
        mainActivity.putExtra("state", dateChangedByUser);
        startActivity(mainActivity);
        moveItemAtPosToMiddle(hoursListView, getMiddlePosition(hoursListView));
        moveItemAtPosToMiddle(minutesListView, getMiddlePosition(minutesListView));
        finish();
    }

    /** Back to main menu without changes. */
    @Override
    public void onBackPressed() {
        Intent mainActivity = new Intent(this, MainActivity.class);
        mainActivity.putExtra("target", collectedTarget);
        mainActivity.putExtra("range", collectedRange);
        mainActivity.putExtra("date", collectedDate);
        mainActivity.putExtra("state", false);
        startActivity(mainActivity);
        moveItemAtPosToMiddle(hoursListView, getMiddlePosition(hoursListView));
        moveItemAtPosToMiddle(minutesListView, getMiddlePosition(minutesListView));
        finish();
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    /** Action when scrolling timer. */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
            if (view.equals(hoursListView))
                clearMarks(hoursListView);
            else if (view.equals(minutesListView))
                clearMarks(minutesListView);
        } else if (scrollState == SCROLL_STATE_IDLE) {
            String touchedHour = Integer.toString(hour);
            String touchedMin = Integer.toString(minutes);
            dateChangedByUser = true;
            // Searching for first and last visible position, then mark the center item.
            if (view.equals(hoursListView)) {
                int pos = getMiddlePosition(hoursListView);
                touchedHour = (String) hoursListAdapter.getItem(pos);
                moveItemAtPosToMiddle(hoursListView, getMiddlePosition(hoursListView));
                setMark(hoursListView, pos - hoursListView.getFirstVisiblePosition(),
                        getResources().getColor(R.color.Black));
            } else if (view.equals(minutesListView)) {
                int pos = getMiddlePosition(minutesListView);
                touchedMin = (String) minutesListAdapter.getItem(pos);
                moveItemAtPosToMiddle(minutesListView, getMiddlePosition(minutesListView));
                setMark(minutesListView, pos - minutesListView.getFirstVisiblePosition(),
                        getResources().getColor(R.color.Black));
            }
            hour = Integer.parseInt(touchedHour);
            minutes = Integer.parseInt(touchedMin);
            setTime(Integer.parseInt(touchedHour), Integer.parseInt(touchedMin));
        }
    }
}

package naMsze.naMsze;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private boolean isDateChangedByUser = false; // If user changed date.
    private boolean isDataDefault = true; // Date wasn't selected by user but is not default = cancel in calendar
    private String childrenNameString;
    private String studentsNameString;
    private String allNameString;
    private String dateHeader;
    private String targetHeader;
    private String rangeHeader;
    private String selectedDate;
    private String selectedTarget;
    private String dateExtras = "date";
    private String targetExtras = "target";
    private String rangeExtras = "range";
    private String stateExtras = "state";
    private double selectedRange;
    private TextView dateHeaderResultView;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    ArrayList<HeaderInfo> listDataHeader;

    /** Runnable that updates time and date in real time, but only when app starts,
      * doesn't change time when user has set date or time. */
    Handler dateChangeHandler = new Handler();
    Runnable dateChangeRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isDateChangedByUser && isDataDefault && !selectedDate.equals(getToday())) {
                selectedDate = getToday();
                dateHeaderResultView.setText(selectedDate.substring(0, 10) + "\n" + selectedDate.substring(11));
            }
            dateChangeHandler.postDelayed(this, 1000);
        }
    };

    private OnGroupClickListener myGroupClicked = new OnGroupClickListener() {
        @Override
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            // Get the group info.
            HeaderInfo headerInfo = listDataHeader.get(groupPosition);
            if (headerInfo.getName().equals(dateHeader))
                startCalendar();
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeStrings();
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            // There are extras from user.
            getExtras(extras);
        }
        if (selectedDate == null) {
            // Date, target, range aren't initialised
            // (extras equal null don't mean that there are no extras)
            // Set default values.
            setDefaultData();
        }

        // Set expandable list.
        expListView = (ExpandableListView) findViewById(R.id.menuList);
        prepareListData();
        initializeExpList();
        expListView.setOnGroupClickListener(myGroupClicked);
        lookForDatabaseUpdates();
    }

    private void setDefaultData() {
        isDataDefault = true;
        isDateChangedByUser = false;
        selectedDate = getToday();
        selectedTarget = allNameString;
        selectedRange = 2;
    }

    private void getExtras(Bundle extras) {
        isDateChangedByUser = extras.getBoolean(stateExtras);
        selectedDate = extras.getString(dateExtras);
        selectedTarget = extras.getString(targetExtras);
        selectedRange = extras.getDouble(rangeExtras);
        isDataDefault = false;
    }

    private void updateEaster(MyDatabase db, int currentYear) {
        String newEasterDate = Feast.countDateOfEaster(currentYear);
        db.updateEasterDate(newEasterDate);
    }

    private void updatePeriods(MyDatabase db) {
        List<Period> periods = db.getPeriodList();
        for (Period p : periods) {
            db.updatePeriod(p.getPeriodID(), p.getStart(), p.getEnd());
        }
    }

    /** Checks if the year has changed, if yes then dates of feasts and periods change. */
    private void lookForDatabaseUpdates() {
        // Get current Easter year.
        MyDatabase db = new MyDatabase(this);
        String dateEaster = db.getEasterDate();
        String[] splited = dateEaster.split("-");
        int yearEaster = Integer.parseInt(splited[0]);

        // Get current year.
        Date currentDate = new Date();
        SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");
        int currentYear = Integer.parseInt(formatYear.format(currentDate));

        // Check if needs updates.
        if (currentYear != yearEaster) {
            updateEaster(db, currentYear);
            updatePeriods(db);
            db.updateYearOfStaticFeasts();
        }
    }

    /** Initialise expandableList (adapter, divider). */
    private void initializeExpList() {
        listAdapter = new ExpandableListAdapter(this, listDataHeader,
                selectedDate, selectedTarget, selectedRange);
        expListView.setAdapter(listAdapter);
        expListView.setDividerHeight(10);
    }

    /** Initialise all required strings. */
    private void initializeStrings() {
        dateHeader = getResources().getString(R.string.date_header);
        targetHeader = getResources().getString(R.string.target_header);
        rangeHeader = getResources().getString(R.string.range_header);
        allNameString = getResources().getString(R.string.all_string);
        studentsNameString = getResources().getString(R.string.students_string);
        childrenNameString = getResources().getString(R.string.children_string);
    }

    /** Prepare data for expandableList. Headers and children. */
    private void prepareListData() {
        listDataHeader = new ArrayList<>();

        // Adding child headers.
        HeaderInfo date = new HeaderInfo(dateHeader);
        HeaderInfo target = new HeaderInfo(targetHeader);
        HeaderInfo range = new HeaderInfo(rangeHeader);
        listDataHeader.add(date);
        listDataHeader.add(target);
        listDataHeader.add(range);

        // Adding child data.
        // Ranges.
        List<String> rangeChildItemList = new ArrayList<>();
        rangeChildItemList.add("0.5");
        rangeChildItemList.add("1");
        rangeChildItemList.add("2");
        rangeChildItemList.add("5");
        rangeChildItemList.add("10");
        range.getChildrenList().add(new ChildInfo(rangeChildItemList));

        // Targets.
        List<String> targetChildItemList = new ArrayList<>();
        targetChildItemList.add(childrenNameString);
        targetChildItemList.add(studentsNameString);
        targetChildItemList.add(allNameString);
        target.getChildrenList().add(new ChildInfo(targetChildItemList));
    }

    private String getToday() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        Date today = new Date();
        return dateFormatter.format(today);
    }

    /** Check for new changes in headerResults fields always before starting new activity. */
    private void checkForUsersChanges() {
        for (HeaderInfo headerInfo : listDataHeader) {
            TextView headerResultView = (TextView) listAdapter.getHeaderResultView(headerInfo);
            String result = headerResultView.getText().toString();
            // Save what was clicked by user.
            if (headerInfo.getName().equals(targetHeader)) {
                selectedTarget = result;
            } else if (headerInfo.getName().equals(rangeHeader)) {
                String[] splited = result.split("\\s+"); // We get number and km.
                selectedRange = Double.parseDouble(splited[0]);
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // At this moment we can get view of dateHeaderResult and start dateRunnable.
        HeaderInfo headerInfo = listDataHeader.get(0);
        dateHeaderResultView = (TextView) listAdapter.getHeaderResultView(headerInfo);
        dateChangeHandler.post(dateChangeRunnable);
    }

    public void startCalendar() {
        checkForUsersChanges();
        Intent calendar = new Intent(this, Calendar.class);
        calendar.putExtra("date", selectedDate);
        calendar.putExtra("target", selectedTarget);
        calendar.putExtra("range", selectedRange);
        startActivity(calendar);
        dateChangeHandler.removeCallbacks(dateChangeRunnable);
        finish();
    }

    public void startButton(View v) {
        checkForUsersChanges();
        Intent maps = new Intent(this, MapsActivity.class);
        maps.putExtra("date", selectedDate);
        maps.putExtra("target", selectedTarget);
        maps.putExtra("range", selectedRange);
        startActivity(maps);
    }

    /** Action when user click back button. */
    private long lastPressTime = 0;
    @Override
    public void onBackPressed() {
        Toast exitToast = Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.exit_message), Toast.LENGTH_SHORT);
        long currentTime = System.currentTimeMillis();
        // User has some time to press button again and exit.
        if (currentTime - lastPressTime > 2000) {
            exitToast.show();
            lastPressTime = currentTime;
        } else {
            exitToast.cancel();
            finish();
        }
    }
}

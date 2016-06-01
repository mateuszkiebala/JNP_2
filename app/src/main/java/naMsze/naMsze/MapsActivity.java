package naMsze.naMsze;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements GoogleMap.InfoWindowAdapter {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private String selectedDate;
    private String selectedTarget;
    private double selectedRange;
    private double myLongitude;
    private double myLatitude;
    private boolean mapsReady = false;
    private boolean locationShowed = false;

    /** Search database only when coordinates of my position are known. */
    Handler dbHandler = new Handler();
    Runnable dbRunnable = new Runnable() {
        @Override
        public void run() {
            if (mapsReady) {
                findMasses();
            } else {
                dbHandler.postDelayed(this, 500);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selectedDate = extras.getString("date");
            selectedTarget = extras.getString("target");
            selectedRange = extras.getDouble("range");
        }
        setMapSettings();
        dbHandler.postDelayed(dbRunnable, 0);
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void findMasses() {
        // Getting data and time selected by user.
        Date date = null;
        String Hour = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat formatHour = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        try {
            date = formatDate.parse(selectedDate);
            Date selDate = format.parse(selectedDate);
            Hour = formatHour.format(selDate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        DateFormat formatDayOfTheWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        String dayOfTheWeek = formatDayOfTheWeek.format(date);

        MyDatabase db = new MyDatabase(this);

        // Getting correct MassTypeId, depends from day.
        int MassTypeID_1 = 0;
        int MassTypeID_2 = 0;
        String date_str = formatDate.format(date);
        if (db.isThereAFeast(date_str)) {
            MassTypeID_1 = 3;
        } else {
            if (dayOfTheWeek.equals("Saturday")) {
                MassTypeID_1 = db.getMassTypeID("Saturday_ferial");
                MassTypeID_2 = db.getMassTypeID("Saturday_dominical");
            } else {
                MassTypeID_1 = db.getMassTypeID(dayOfTheWeek);
            }
        }

        // Getting all masses that are correct with our description.
        int PeriodID = db.getPeriodID(date_str);
        List<naMsze.naMsze.Location> churches = db.getChurches
                ("Warszawa", selectedRange, myLatitude, myLongitude);
        for (naMsze.naMsze.Location loc : churches) {
            int ChurchID = db.getChurchID(loc.getLocationID());
            List<String> resMasses = db.getAllMassesAfterHour(Hour, selectedTarget, PeriodID,
                    MassTypeID_1, ChurchID);
            if (MassTypeID_2 != 0) {
                resMasses.addAll(db.getAllMassesAfterHour(Hour, selectedTarget, PeriodID,
                        MassTypeID_2, ChurchID));
                // We have to remove redundancy, that can be created by saturday_dominical masses.
                for (int i = 0; i < resMasses.size() - 1; ++i) {
                    if (resMasses.get(i).equals(resMasses.get(i + 1)))
                        resMasses.remove(i);
                }
            }
            if (!resMasses.isEmpty()) {
                String resHour = resMasses.get(0);
                setChurchOnMap(resHour, loc, resMasses);
            }
        }
    }

    /** Sets mass on the map with all masses (in marker description)
        that are that day at that church. */
    private void setChurchOnMap(String hour, naMsze.naMsze.Location location, List<String> masses) {
        LatLng loc = new LatLng(location.getX(), location.getY());
        IconGenerator factory = new IconGenerator(this);
        factory.setTextAppearance(R.style.markerText);
        Bitmap icon = factory.makeIcon(hour);
        String hours = "";
        // We don't need first mass, it is displayed on marker.
        if (masses.size() <= 1) {
            hours = "brak";
        } else {
            for (int i = 1; i < masses.size(); ++i) {
                if (i == 1)
                    hours = masses.get(i);
                else
                    hours = hours + ", " + masses.get(i);
            }
        }

        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(icon))
                .position(loc).title(getResources().getString(R.string.marker_message)).snippet(hours));

        // Setting features for marker.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                Context context = getApplicationContext();
                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());
                snippet.setGravity(Gravity.CENTER);

                info.addView(title);
                info.addView(snippet);
                return info;
            }
        });
    }


    private void setMapSettings() {
        // Enable zoom.
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        // Enable compass.
        mMap.getUiSettings().setCompassEnabled(true);
        // Set Map type.
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Enable current location button.
        mMap.setMyLocationEnabled(true);
        // Set listener for changing my location.
        mMap.setOnMyLocationChangeListener(myLocationChangeListener);
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener =
        new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            myLongitude = location.getLongitude();
            myLatitude = location.getLatitude();

            if (mMap != null) {
                float zoom = 0;
                if (selectedRange == 0.5)
                    zoom = 15.0f;
                else if (selectedRange == 1)
                    zoom = 14.0f;
                else if (selectedRange == 2)
                    zoom = 13.5f;
                else if (selectedRange == 5)
                    zoom = 12.5f;
                else if (selectedRange == 10)
                    zoom = 12.0f;
                // Our location is showed only once at the beginning.
                if (!locationShowed) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, zoom));
                    locationShowed = true;
                }
            }
            mapsReady = true;
        }
    };

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        LatLng Warsaw = new LatLng(52.231821, 21.007219);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Warsaw, 8.0f));
    }
}

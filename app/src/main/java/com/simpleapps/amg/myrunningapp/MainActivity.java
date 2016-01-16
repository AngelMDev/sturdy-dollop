package com.simpleapps.amg.myrunningapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import java.util.Calendar;

public class MainActivity
        extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    Button beginButton;
    Button startButton;
    Button stopButton;
    ViewFlipper viewFlipper;
    msChronometer chronometer;
    boolean isRunning = false;
    boolean isPaused = false;
    long timeStopped = 0;
    boolean firstTime = true;
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    int LOCATION_PERMISSION_CODE=10;
    LocationRequest mLocationRequest;
    TextView speedTextView;
    TextView accuracyTextView;

    SQLiteDatabase historyDB = null;
    View topLevelLayout;
    //@TODO organize variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        createApiInstance();
        mGoogleApiClient.connect();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_draw);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeComponents();

        retrieveData(savedInstanceState);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (firstTime) {
            firstTime = false;
            createDatabase();

        }
    }

    private void createApiInstance() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }


    private void createDatabase() {
        try {
            historyDB = this.openOrCreateDatabase("HistoryDB", MODE_PRIVATE, null);
            historyDB.execSQL("CREATE TABLE IF NOT EXISTS history " + "(id integer primary key, date DATATIME,time NCHAR);");
            //File database=getApplicationContext().getDatabasePath("HistoryDB");
        } catch (Exception e) {

        }
    }

    //@TODO move this to other class
    private void addEntry(String time) {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        String hour;
        String minute;
        if (calendar.get(Calendar.HOUR_OF_DAY) < 10) {
            hour = "0" + calendar.get(Calendar.HOUR_OF_DAY);
        } else {
            hour = calendar.get(Calendar.HOUR_OF_DAY) + "";
        }
        if (calendar.get(Calendar.MINUTE) < 10) {
            minute = "0" + calendar.get(Calendar.MINUTE);
        } else {
            minute = "" + calendar.get(Calendar.MINUTE);
        }
        String dateOfRun = hour + ":" + minute + "" + "  " + calendar.get(Calendar.DAY_OF_MONTH) + "/" +
                month + "/" + calendar.get(Calendar.YEAR) + " ";
        time = time + " s";

        historyDB.execSQL("INSERT INTO history (date,time) VALUES ('" + dateOfRun + "','" + time + "');");
    }

    MenuItem actionLock = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        actionLock = menu.findItem(R.id.action_lock);
        return true;
    }

    boolean lockState = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_lock) {
            if (!lockState) {
                actionLock.setIcon(R.drawable.ic_lock_open_black_24dp);
                lockState = true;
                beginButton.setClickable(false);
                startButton.setClickable(false);
                stopButton.setClickable(false);
                topLevelLayout.setVisibility(View.VISIBLE);
            } else {
                actionLock.setIcon(R.drawable.ic_lock_black_24dp);
                beginButton.setClickable(true);
                startButton.setClickable(true);
                stopButton.setClickable(true);
                topLevelLayout.setVisibility(View.INVISIBLE);
                lockState = false;
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //Handles the flipview
    public void goRight(View view) {
// Next screen comes in from left.

            viewFlipper.setInAnimation(this, R.anim.slide_in_from_right);

            // Current screen goes out from right.

            viewFlipper.setOutAnimation(this, R.anim.slide_out_to_left);

            // Display next screen.
            viewFlipper.showNext();

    }

    //Handles Viewflip
    public void goBack() {
        viewFlipper.setInAnimation(this, R.anim.slide_in_from_left);

        viewFlipper.setOutAnimation(this, R.anim.slide_out_to_right);

        viewFlipper.showPrevious();
    }

    //user press back on running activity
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

//TODO move to msChronometer
    //handles when the user presses start
    public void startPauseRun(View view) {
        if (!isRunning) {

            if (!isPaused) {
                chronometer.setBase(SystemClock.elapsedRealtime());
                isPaused = false;
            } else if (isPaused) {
                chronometer.setBase(SystemClock.elapsedRealtime()+(chronometer.getBase()) - timeStopped);
                Log.d("Paused", "Paused");
            }
            chronometer.start();
            startButton.setBackgroundColor(ContextCompat.getColor(this, R.color.materialYellow));
            startButton.setText(R.string.pause_button);
            isRunning = true;

        } else if (isRunning) {
            chronometer.stop();
            timeStopped = SystemClock.elapsedRealtime();
            startButton.setBackgroundColor(ContextCompat.getColor(this, R.color.materialGreen));
            startButton.setText(R.string.start_button);
            isRunning = false;
            isPaused = true;
        }
    }
    //TODO move to msChronometer
    public void stopRun(View view) {
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        startButton.setBackgroundColor(ContextCompat.getColor(this, R.color.materialGreen));
        startButton.setText(R.string.start_button);
        isRunning = false;
        isPaused = false;
        addEntry(String.valueOf(SystemClock.elapsedRealtime() - chronometer.getBase()));
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_history) {
            Intent openHistory = new Intent(this, HistoryActivity.class);
            startActivity(openHistory);

        } else if (id == R.id.nav_manage) {

        }
        return true;
    }

    private void initializeComponents() {
        beginButton = (Button) findViewById(R.id.beginButton);
        startButton = (Button) findViewById(R.id.startPauseButton);
        stopButton = (Button) findViewById(R.id.stopButton);
        topLevelLayout = findViewById(R.id.top_layout);
        topLevelLayout.setVisibility(View.INVISIBLE);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        chronometer = (msChronometer) findViewById(R.id.dchronometer);
        speedTextView=(TextView) findViewById(R.id.speedTV);
        accuracyTextView=(TextView) findViewById(R.id.displayAccuracyTV);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("firstTime", firstTime);
        super.onSaveInstanceState(outState);
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);

    }

    @Override
    protected void onStop() {
        saveSettings();
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);

        super.onStop();
    }

    protected void saveSettings() {
        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putBoolean("firstTime", firstTime);

        spEditor.apply();
    }

    private void retrieveData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            firstTime = savedInstanceState.getBoolean("firstTime");
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_CODE);
            }
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.d("LOCATION", "Requesting");
        }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2500);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.d("LOCATION", "createLocationRequest");
    }



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation=location;
        Log.d("LOCATION", "onLocationChangedCalled");
        accuracyTextView.setText(String.valueOf(mLastLocation.getAccuracy()));
        float accuracy=mLastLocation.getAccuracy();
        if(accuracy<20.0) {
            speedTextView.setText(String.valueOf(mLastLocation.getSpeed()) + " m/s");
            if(accuracy<10){
                beginButton.setBackgroundColor(ContextCompat.getColor(this, R.color.materialGreen));
                beginButton.setClickable(true);
            }
        }
        else{
            speedTextView.setText("- m/s");
            Toast.makeText(this,"GPS accuracy not enough",Toast.LENGTH_SHORT).show();
            beginButton.setBackgroundColor(ContextCompat.getColor(this, R.color.materialGray));
            beginButton.setClickable(false);
        }


    }
}
//checkout https://github.com/mikepenz/MaterialDrawer
//http://blog.sqisland.com/2015/01/partial-slidingpanelayout.html
//https://github.com/michelelacorte/ScrollableAppBar
//https://github.com/daimajia/AndroidSwipeLayout
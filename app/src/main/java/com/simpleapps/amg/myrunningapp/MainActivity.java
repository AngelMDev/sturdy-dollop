package com.simpleapps.amg.myrunningapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.concurrent.TimeUnit;

public class MainActivity
        extends BaseActivity
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
    Location currentLocation;
    int LOCATION_PERMISSION_CODE = 10;
    LocationRequest mLocationRequest;
    TextView speedTextView;
    TextView distanceTextView;
    TextView runDateOV, runTimeOV, runDistanceOV, runAvgSpeedOV, runTopSpeedOV, runAltChangeOV;
    View topLevelLayout;
    double distance = 0;
    float maxSpeed = 0;
    double avgSpeed = 0;
    float speed;
    double tempDistance = 0;
    float accuracy;
    Handler mHandler;
    Runnable runnable;
    DBAdapter dbAdapter;
    ImageView logo;
    String speedF;
    String distanceF;
    protected DrawerLayout drawer;
    //@TODO organize variables. maxspeed needs to be reset.

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nav_draw);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initializeComponents();
        dbAdapter = new DBAdapter();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setLogoTint();

        if (firstTime) {
            firstTime = false;
            dbAdapter.createDatabase(this);

        }
    }

    private void setLogoTint() {
        TypedValue typedValue= new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int color=typedValue.data;
        logo.setColorFilter(color);
    }


    private void initializeComponents() {
        beginButton = (Button) findViewById(R.id.beginButton);
        startButton = (Button) findViewById(R.id.startPauseButton);
        stopButton = (Button) findViewById(R.id.stopButton);
    //    topLevelLayout = findViewById(R.id.top_layout);
    //    topLevelLayout.setVisibility(View.INVISIBLE);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        chronometer = (msChronometer) findViewById(R.id.dchronometer);
        speedTextView = (TextView) findViewById(R.id.speedTV);
        distanceTextView = (TextView) findViewById(R.id.distanceTV);
        runDateOV = (TextView) findViewById(R.id.runDateDetail);
        runTimeOV = (TextView) findViewById(R.id.runTimeDetail);
        runDistanceOV = (TextView) findViewById(R.id.runDistanceDetail);
        runAltChangeOV = (TextView) findViewById(R.id.runAltChangeDetail);
        runAvgSpeedOV = (TextView) findViewById(R.id.runAvgSpeedDetail);
        runTopSpeedOV = (TextView) findViewById(R.id.runTopSpeedDetail);
        logo = (ImageView) findViewById(R.id.imgLogo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        createApiInstance();
        mGoogleApiClient.connect();
        Log.d("ON START", "onStart called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        createLocationRequest();
        createApiInstance();
        mGoogleApiClient.connect();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    protected void onStop() {
        saveSettings();
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        if (runnable != null)
            mHandler.removeCallbacks(runnable);
        super.onStop();
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

    //@TODO move this to other class
    private void addEntry(String time) {
        Calendar calendar = Calendar.getInstance();
        int monthi=calendar.get(Calendar.MONTH)+1;
        String month = "" + monthi;
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
        dbAdapter.createDatabase(this);

        String maxspeedF = String.format("%.2f", maxSpeed);

        String avgSpeedF = String.format("%.2f", avgSpeed);
        showRunOverview(dateOfRun, time, distanceF, maxspeedF, avgSpeedF, 0);
    }

    private void showRunOverview(String dateOfrun, String time, String distance, String maxSpeed, String avgSpeed, double altChange) {
        runDateOV.setText(dateOfrun);
        runTimeOV.setText(time);
        runDistanceOV.setText(distance+"m");
        runTopSpeedOV.setText(maxSpeed+"m/s");
        runAvgSpeedOV.setText(avgSpeed+"m/s");
        runAltChangeOV.setText(String.valueOf(altChange)+"m");
        dbAdapter.addEntry("history2", dateOfrun, time, distance, maxSpeed, avgSpeed, 0); //TODO change altchange with real value

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

    public void goNext() {
        viewFlipper.setInAnimation(this, R.anim.slide_in_from_right);
        viewFlipper.setOutAnimation(this, R.anim.slide_out_to_left);
        viewFlipper.showNext();
    }

    //user press back on running activity
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (viewFlipper.getDisplayedChild() == 1) {
                goBack();
                return true;
            } else if (viewFlipper.getDisplayedChild() == 2) {
                viewFlipper.setDisplayedChild(0);
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    boolean runBeenStarted = false;
    boolean dialogPaused = false;

    //TODO move to msChronometer
    //handles when the user presses start
    public void startPauseRun(View view) {
        if (!isRunning) {
            if (!isPaused) {
                chronometer.setBase(SystemClock.elapsedRealtime());

            } else if (isPaused) {
                chronometer.setBase(SystemClock.elapsedRealtime() + (chronometer.getBase()) - timeStopped);
                isPaused = false;
            }
            runBeenStarted = true;
            chronometer.start();
            startButton.setBackgroundColor(ContextCompat.getColor(this, R.color.materialYellow));
            startButton.setText(R.string.pause_button);
            isRunning = true;
            trackDistance();


            if (accuracy < 20.0) {
                if (isRunning) {
                    //TODO move trackDistance here
                }
            }

        } else if (isRunning) {
            chronometer.stop();
            mHandler.removeCallbacks(runnable);
            timeStopped = SystemClock.elapsedRealtime();
            startButton.setBackgroundColor(ContextCompat.getColor(this, R.color.materialGreen));
            startButton.setText(R.string.start_button);
            isRunning = false;
            isPaused = true;

        }
    }

    //TODO move to msChronometer
    public void stopRun(final View view) {
        if (runBeenStarted) {
            // if(distance>0) { // TODO: 1/25/2016  uncomment this on release
            if (!isPaused) {
                startPauseRun(view);
                dialogPaused = true;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_stop_run)
                    .setPositiveButton(R.string.dialog_stop, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (chronometer.getTimeElapsed() > 0) {
                                avgSpeed = distance / chronometer.getTimeElapsed();
                                addEntry(formatChrono(chronometer.getTimeElapsed()));
                            }
                            chronometer.stop();
                            mHandler.removeCallbacks(runnable);
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            startButton.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.materialGreen));
                            startButton.setText(R.string.start_button);
                            isRunning = false;
                            isPaused = false;
                            maxSpeed = 0;
                            distance = 0;
                            runBeenStarted = false;
                            distanceTextView.setText("0m");
                            goNext();
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            if (dialogPaused) {
                                startPauseRun(view);
                                dialogPaused = false;
                            }
                        }
                    })
                    .show();
            //  }
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_homey) {
            recreate();
        }
        if (id == R.id.nav_history) {
            Intent openHistory = new Intent(this, HistoryActivity.class);
            startActivity(openHistory);
        }
        if (id == R.id.nav_settings) {
            Intent openSettings = new Intent(this, SettingsActivity.class);
            startActivity(openSettings);
        }
        return true;
    }

    private String formatChrono(long time) {

        long hours = TimeUnit.MILLISECONDS.toHours(time);
        time -= TimeUnit.HOURS.toMillis(TimeUnit.MILLISECONDS.toHours(time));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
        time -= TimeUnit.MINUTES.toMillis(TimeUnit.MILLISECONDS.toMinutes(time));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time);
        time -= TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(time));
        long milli = time;

        return String.format("%02dh:%02dm:%02ds:%02dms", hours, minutes, seconds, milli);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("firstTime", firstTime);
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        if (runnable != null)
            mHandler.removeCallbacks(runnable);
        Log.d("ONSAVEINSTANCE", "onSaveInstanceState called");
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
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            }
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            createLocationRequest();
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Log.d("LOCATION", "Requesting");
        }


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
//TODO move to another class and make it have its own thread?

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        accuracy = currentLocation.getAccuracy();
        speed = currentLocation.getSpeed();
        calcMaxSpeed(speed);
        speedF = String.format("%.2f", speed);
        speedTextView.setText(String.valueOf(speedF) + " m/s");
        if (accuracy < 10) {
            beginButton.setBackgroundColor(ContextCompat.getColor(this, R.color.materialGreen));
            beginButton.setClickable(true);
        }
        if (accuracy > 20) {
            speedTextView.setText("- m/s");
            Toast.makeText(this, "Obteniendo señal GPS...", Toast.LENGTH_SHORT).show();
            beginButton.setBackgroundColor(ContextCompat.getColor(this, R.color.materialGray));
            beginButton.setClickable(true);//TODO: set to false on release
        }
    }


    private void trackDistance() {
        mHandler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                calcDistance();
                mHandler.postDelayed(this, 1000);

            }
        };
        mHandler.postDelayed(runnable, 1000);
    }


    private void calcDistance() {
        if (currentLocation.hasSpeed()&isPaused==false&isRunning==true) {
            tempDistance = currentLocation.distanceTo(mLastLocation);
            mLastLocation = currentLocation;
            if (speed > 0) { //@TODO check on this
                distance += tempDistance;

            } else {

            }
            distanceF = String.format("%.2f", distance);
            distanceTextView.setText(distanceF + "m");
        }
    }

    private void calcMaxSpeed(float speed) {
        if (speed > maxSpeed) {
            maxSpeed = speed;
        }
    }


    private double calculateDistance(Location current, Location last) {
        int R = 6371; // km
        double x = (current.getLongitude() - last.getLongitude()) * Math.cos((current.getLatitude() + last.getLatitude()) / 2);
        double y = (current.getLatitude() - last.getLatitude());
        return Math.sqrt(x * x + y * y) * R;
    }
}
//checkout https://github.com/mikepenz/MaterialDrawer
//http://blog.sqisland.com/2015/01/partial-slidingpanelayout.html
//https://github.com/michelelacorte/ScrollableAppBar
//https://github.com/daimajia/AndroidSwipeLayout
package com.simpleapps.amg.myrunningapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Button beginButton;
    Button startButton;
    Button stopButton;
    ViewFlipper viewFlipper;
    msChronometer chronometer;
    TextView chronoTextView=null;
    boolean isRunning = false;
    boolean isPaused = false;
    long timeStopped = 0;
    boolean firstTime = true;
    SQLiteDatabase historyDB = null;
    Handler Chronohandler;
    View topLevelLayout;
    //@TODO organize variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_draw);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeComponents();

        retrieveData(savedInstanceState);
        Chronohandler=new Handler();




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

     MenuItem actionLock=null;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        actionLock=menu.findItem(R.id.action_lock);
        return true;
    }
    boolean lockState=false;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();



        if (id == R.id.action_lock) {
            if (!lockState) {
                actionLock.setIcon(R.drawable.ic_lock_open_black_24dp);
                lockState=true;
                beginButton.setClickable(false);
                startButton.setClickable(false);
                stopButton.setClickable(false);
                topLevelLayout.setVisibility(View.VISIBLE);
            }else{
                actionLock.setIcon(R.drawable.ic_lock_black_24dp);
                beginButton.setClickable(true);
                startButton.setClickable(true);
                stopButton.setClickable(true);
                topLevelLayout.setVisibility(View.INVISIBLE);
                lockState=false;
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
    long chronoBase=0;
    long chronoCurrent=0;
    long ms=0;
    int s=0;
    int m=0;
    int h=0;
    int cms=0;
    final Runnable updateChrono=new Runnable() {
        @Override
        public void run() {
            //android.os.Process.setThreadPriority(-15);
            chronoCurrent=SystemClock.elapsedRealtime();
            ms=chronoCurrent-chronoBase;
            if(ms>999){
                s++;
                chronoBase=SystemClock.elapsedRealtime();
            }
            if(s>59){
                m++;
                s=0;
            }
            if(m>59){
                h++;
                m=0;
            }
            //chronoTextView.setText(h+":"+m+":"+s+":"+ms);
            Chronohandler.postDelayed(this, 25);

        }
    };
    //handles when the user presses start
    public void startPauseRun(View view) {
        if (!isRunning) {

            if (!isPaused) {
                chronometer.setBase(SystemClock.elapsedRealtime());
            } else if (isPaused) {
                chronometer.setBase(chronometer.getBase() + SystemClock.elapsedRealtime() - timeStopped);
            }
            chronometer.start();
            startButton.setBackgroundColor(ContextCompat.getColor(this, R.color.materialYellow));
            startButton.setText(R.string.pause_button);
            isRunning = true;
            isPaused = false;
            //---------------
            chronoBase=SystemClock.elapsedRealtime();
            Chronohandler.post(updateChrono);
        } else if (isRunning) {
            chronometer.stop();
            timeStopped = SystemClock.elapsedRealtime();
            startButton.setBackgroundColor(ContextCompat.getColor(this, R.color.materialGreen));
            startButton.setText(R.string.start_button);
            isRunning = false;
            isPaused = true;
        }
    }

    public void stopRun(View view) {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.stop();
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

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("firstTime", firstTime);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        saveSettings();
        super.onStop();
    }

    protected void saveSettings() {
        SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putBoolean("firstTime", firstTime);

        spEditor.commit();
    }

    private void retrieveData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            firstTime = savedInstanceState.getBoolean("firstTime");

        }

    }
}
//checkout https://github.com/mikepenz/MaterialDrawer
//http://blog.sqisland.com/2015/01/partial-slidingpanelayout.html
//https://github.com/michelelacorte/ScrollableAppBar
//https://github.com/daimajia/AndroidSwipeLayout
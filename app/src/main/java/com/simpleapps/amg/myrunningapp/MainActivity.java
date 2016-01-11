package com.simpleapps.amg.myrunningapp;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ViewFlipper;

public class MainActivity extends AppCompatActivity {
    Button beginButton;
    Button startButton;
    Button stopButton;
    ViewFlipper viewFlipper;
    Chronometer chronometer;
    boolean isRunning=false;
    boolean isPaused=false;
    long timeStopped=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        beginButton=(Button)findViewById(R.id.beginButton);
        startButton=(Button)findViewById(R.id.startPauseButton);
        stopButton=(Button)findViewById(R.id.stopButton);

        viewFlipper=(ViewFlipper)findViewById(R.id.viewFlipper);
        chronometer=(Chronometer)findViewById(R.id.dchronometer);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {
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
    public void goBack(View view) {
        viewFlipper.setInAnimation(this, R.anim.slide_in_from_left);

        viewFlipper.setOutAnimation(this, R.anim.slide_out_to_right);

        viewFlipper.showPrevious();
    }
    //handles when the user presses start
    public void startPauseRun(View view) {
        if(!isRunning)
        {

            if(!isPaused){
                chronometer.setBase(SystemClock.elapsedRealtime());
            }else if(isPaused){
                chronometer.setBase(chronometer.getBase()+SystemClock.elapsedRealtime()-timeStopped);
            }
            chronometer.start();
            startButton.setBackgroundColor(ContextCompat.getColor(this,R.color.materialYellow));
            startButton.setText(R.string.pause_button);
            isRunning=true;
            isPaused=false;
        }
        else if(isRunning)
        {
            chronometer.stop();
            timeStopped=SystemClock.elapsedRealtime();
            startButton.setBackgroundColor(ContextCompat.getColor(this, R.color.materialGreen));
            startButton.setText(R.string.start_button);
            isRunning=false;
            isPaused=true;
        }
    }

    public void stopRun(View view) {
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.stop();
        startButton.setBackgroundColor(ContextCompat.getColor(this, R.color.materialGreen));
        startButton.setText(R.string.start_button);
        isRunning=false;
        isPaused=false;
    }
}

package com.simpleapps.amg.myrunningapp;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

/**
 * Created by Angel on 5/26/2017.
 */

public class BaseActivity extends AppCompatActivity {
    private final static int THEME_ORANGE = 1;
    private final static int THEME_BLUE = 2;
    private final static int THEME_GREEN = 4;
    private final static int THEME_GREENALT = 5;
    private final static int THEME_RED = 3;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateTheme();
    }
    public void updateTheme() {
        if (Utility.getTheme(getApplicationContext()) <= THEME_ORANGE) {
            setTheme(R.style.AppTheme);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
        } else if (Utility.getTheme(getApplicationContext()) == THEME_RED) {
            setTheme(R.style.RedBlack);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
        }
        else if (Utility.getTheme(getApplicationContext()) == THEME_BLUE) {
            setTheme(R.style.BlueWhite);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
        }
        else if (Utility.getTheme(getApplicationContext()) == THEME_GREEN) {
            setTheme(R.style.GreenWhite);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
        }
        else if (Utility.getTheme(getApplicationContext()) == THEME_GREENALT) {
            setTheme(R.style.GreenBlack);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
        }
    }
}


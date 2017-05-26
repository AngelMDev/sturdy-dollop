package com.simpleapps.amg.myrunningapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingsActivity extends BaseActivity {

    RadioGroup radioGroup;
    Button button;
    RadioButton radioButton;
    int selectedID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        radioGroup=(RadioGroup) findViewById(R.id.radio_group);
        button=(Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedID=radioGroup.getCheckedRadioButtonId();
                radioButton=(RadioButton)findViewById(selectedID);
                //CHANGE THEME
                switchTheme();
            }
        });
    }

    private void switchTheme() {
        switch (selectedID)
        {
            case R.id.radioButton:
                Utility.setTheme(getApplicationContext(), 1);
                recreateActivity();
                break;
            case R.id.radioButton1:
                Utility.setTheme(getApplicationContext(), 2);
                recreateActivity();
                break;
            case R.id.radioButton2:
                Utility.setTheme(getApplicationContext(), 3);
                recreateActivity();
                break;
            case R.id.radioButton3:
                Utility.setTheme(getApplicationContext(), 4);
                recreateActivity();
                break;
            case R.id.radioButton4:
                Utility.setTheme(getApplicationContext(), 5);
                recreateActivity();
                break;
        }
    }

    public void recreateActivity() {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }



}

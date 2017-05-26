package com.simpleapps.amg.myrunningapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
        Toolbar myToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(myToolbar);
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

    MenuItem actionGoHome;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);

        actionGoHome = menu.findItem(R.id.go_home);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.go_home){
            finish();
        }
        return super.onOptionsItemSelected(item);
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

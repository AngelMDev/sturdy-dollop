package com.simpleapps.amg.myrunningapp;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

public class HistoryActivity extends AppCompatActivity {
    SQLiteDatabase historyDB = null;
    DBAdapter dbAdapter;
    String ALL="*";
    ListView historyListView;
    ListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        dbAdapter=new DBAdapter();
        initializeUI();
        //historyListView=(ListView)findViewById(R.id.historyListView);
        getEntries(ALL);
    }

    private void initializeUI() {
        historyListView=(ListView)findViewById(R.id.historyListView);
        //TODO dude initialize everything on the history row layout, change the layout to history row layout in the DBadapter, get rid of this listview
    }

    //@TODO show entries more elegantly
    private void getEntries(String parameter) {
        dbAdapter.createDatabase(this);
        listAdapter= dbAdapter.getEntries(this,parameter);
        historyListView.setAdapter(listAdapter);

    }

    private void populateListView(){

    }
    public void deleteEntries(View view) {
        dbAdapter.createDatabase(this);
        dbAdapter.deleteEntries();

    }
}

package com.simpleapps.amg.myrunningapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class HistoryActivity extends AppCompatActivity {
    SQLiteDatabase historyDB = null;
    ListView historyListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        //historyListView=(ListView)findViewById(R.id.historyListView);
        try {
            historyDB = this.openOrCreateDatabase("HistoryDB", MODE_PRIVATE, null);
        } catch (Exception e) {

        }
        getEntries();
    }
//@TODO show entries more elegantly
    private void getEntries() {
        Cursor cursor = historyDB.rawQuery("SELECT * FROM history", null);
        int idColumn = cursor.getColumnIndex("id");
        int dateColumn = cursor.getColumnIndex("date");
        int timeColumn = cursor.getColumnIndex("time");
        int distanceColumn=cursor.getColumnIndex("distance");
        int maxSpeedColumn=cursor.getColumnIndex("max_speed");

        cursor.moveToFirst();
        String entryList = "";
        if (cursor != null && cursor.getCount() > 0) {
            do {
                String id = cursor.getString(idColumn);
                String date = cursor.getString(dateColumn);
                String time = cursor.getString(timeColumn);
                String distance = cursor.getString(distanceColumn);
                String maxSpeed= cursor.getString(maxSpeedColumn);

                entryList = entryList +"ID:"+ id + " |Date: " + date + " |Time: " + time + " |Distance: " +distance+" |Max Speed: "+maxSpeed+"\n--------------\n";
            }
            while (cursor.moveToNext());
            TextView historyTV = (TextView) findViewById(R.id.historyTextView);
            historyTV.setText(entryList);
           // populateListView();
            cursor.close();
        } else {
            TextView historyTV = (TextView) findViewById(R.id.historyTextView);
            historyTV.setText("No entries.");
        }

    }

    private void populateListView(){
        Cursor cursor = historyDB.rawQuery("SELECT * FROM history", null);
        String[] myArray=new String[]{"date"};
        int[] myArray2=new int[]{R.id.idRow};
        SimpleCursorAdapter adapter=new SimpleCursorAdapter(this,R.layout.history_row_layout,cursor,myArray,myArray2);
    }
    public void deleteEntries(View view) {
        historyDB.execSQL("DELETE FROM history");

    }
}

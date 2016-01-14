package com.simpleapps.amg.myrunningapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HistoryActivity extends AppCompatActivity {
    SQLiteDatabase historyDB = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
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

        cursor.moveToFirst();
        String entryList = "";
        if (cursor != null && cursor.getCount() > 0) {
            do {
                String id = cursor.getString(idColumn);
                String date = cursor.getString(dateColumn);
                String time = cursor.getString(timeColumn);

                entryList = entryList + id + " | " + date + " | " + time + "\n";
            }
            while (cursor.moveToNext());
            TextView historyTV = (TextView) findViewById(R.id.historyTextView);
            historyTV.setText(entryList);
        } else {
            TextView historyTV = (TextView) findViewById(R.id.historyTextView);
            historyTV.setText("No entries.");
        }
    }

    public void deleteEntries(View view) {
        historyDB.execSQL("DELETE FROM history");

    }
}

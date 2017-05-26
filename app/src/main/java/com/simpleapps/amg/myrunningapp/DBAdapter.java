package com.simpleapps.amg.myrunningapp;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;


public class DBAdapter {
    SQLiteDatabase historyDB = null;
    HistoryAdapter historyAdapter;

    public void createDatabase(Context context) {
        try {
            historyDB = context.openOrCreateDatabase("HistoryDB", Context.MODE_PRIVATE, null);
            createTable("history2");
            //File database=getApplicationContext().getDatabasePath("HistoryDB");
        } catch (Exception ignored) {

        }

    }

    //TODO rename to openOrCreate and make it so the other methods accept context as a parameter so that this method doesnt have to be called in
    //...other classes
    public void openDatabase(Context context) {
        try {
            // historyDB = context.openDatabase("HistoryDB", null, 0);
        } catch (Exception e) {
            Log.d("DATABASE", "Database couldn't open. ERROR: " + e);
        }
    }

    public void closeDatabase() {
        try {
            historyDB.close();
        } catch (Exception ignored) {

        }

    }

    public void createTable(String tableName) {
        historyDB.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + " (id integer primary key, date DATATIME,time NCHAR,distance DOUBLE,max_speed FLOAT,avg_speed DOUBLE,alt_change FLOAT);");
    }

    public void insertColumn(String tableName, String rowName, String dataType) {
        historyDB.execSQL("ALTER TABLE " + tableName + " ADD COLUMN " + rowName + " dataType");
        closeDatabase();
    }

    public void addEntry(String tableName, String dateOfRun, String time, String distance, String maxSpeed, String avgSpeed, float altChange) {
        historyDB.execSQL("INSERT INTO " + tableName + " (date,time,distance,max_speed,avg_speed,alt_change) VALUES "
                + "('"
                + dateOfRun
                + "','"
                + time
                + "','"
                + distance
                + "','"
                + maxSpeed
                + "','"
                + avgSpeed
                + "','"
                + altChange
                + "');");
        closeDatabase();
    }

    public ListAdapter getEntries(Context context, String parameter) {
        Cursor cursor = historyDB.rawQuery("SELECT * FROM history2" + parameter, null);
        ListAdapter listAdapter = null;
        int idColumn = cursor.getColumnIndex("id");
        int dateColumn = cursor.getColumnIndex("date");
        int timeColumn = cursor.getColumnIndex("time");
        int distanceColumn = cursor.getColumnIndex("distance");
        int maxSpeedColumn = cursor.getColumnIndex("max_speed");
        int avgSpeedColumn = cursor.getColumnIndex("avg_speed");
        int altChangeColumn = cursor.getColumnIndex("alt_change");

        cursor.moveToFirst();
        List<String> idArray = new ArrayList<>();
        List<String> dateArray = new ArrayList<>();
        List<String> timeArray = new ArrayList<>();
        List<String> distanceArray = new ArrayList<>();
        List<String> maxSpeedArray = new ArrayList<>();
        List<String> avgSpeedArray = new ArrayList<>();
        List<String> altChangeArray = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            do {
                String id = cursor.getString(idColumn);
                String date = cursor.getString(dateColumn);
                String time = cursor.getString(timeColumn);
                String distance = cursor.getString(distanceColumn);
                String maxSpeed = cursor.getString(maxSpeedColumn);
                String avgSpeed = cursor.getString(avgSpeedColumn);
                String altChange = cursor.getString(altChangeColumn);
                idArray.add(id);
                dateArray.add(date);
                timeArray.add(time);
                distanceArray.add(distance);
                maxSpeedArray.add(maxSpeed);
                avgSpeedArray.add(avgSpeed);
                altChangeArray.add(altChange);
            }
            while (cursor.moveToNext());
            cursor.close();
            listAdapter = new HistoryAdapter(idArray, dateArray, timeArray, distanceArray, context);
        }
        closeDatabase();
        return listAdapter;
    }

    public List<String> getDetail(long idValue) {
        Cursor cursor = historyDB.rawQuery("SELECT * FROM history2 WHERE id= " + idValue, null);
        Log.d("DATABASE", "idValue: " + idValue);
        int idColumn = cursor.getColumnIndex("id");
        int dateColumn = cursor.getColumnIndex("date");
        int timeColumn = cursor.getColumnIndex("time");
        int distanceColumn = cursor.getColumnIndex("distance");
        int maxSpeedColumn = cursor.getColumnIndex("max_speed");
        int avgSpeedColumn = cursor.getColumnIndex("avg_speed");
        int altChangeColumn = cursor.getColumnIndex("alt_change");
        List<String> detailArray = new ArrayList<>();

        if (cursor.getCount()==1) {
            cursor.moveToFirst();
            String id = cursor.getString(idColumn);
            String date = cursor.getString(dateColumn);
            String time = cursor.getString(timeColumn);
            String distance = cursor.getString(distanceColumn);
            String maxSpeed = cursor.getString(maxSpeedColumn);
            String avgSpeed = cursor.getString(avgSpeedColumn);
            String altChange = cursor.getString(altChangeColumn);
            detailArray.add(id);
            detailArray.add(date);
            detailArray.add(time);
            detailArray.add(distance);
            detailArray.add(maxSpeed);
            detailArray.add(avgSpeed);
            detailArray.add(altChange);
        }
        else{
            Log.d("DATABASE","getCount="+cursor.getCount());
        }
        cursor.close();
        closeDatabase();
        return detailArray;
    }


    public void deleteEntries(String parameter) {
        historyDB.execSQL("DELETE FROM history2 "+parameter);
    }
    public void reassignID(Context context){
        createDatabase(context);
        Cursor cursor = historyDB.rawQuery("SELECT id FROM history2",null);
        int idColumn = cursor.getColumnIndex("id");
        cursor.moveToFirst();
        for(int i=1;i<=cursor.getCount();i++){
            historyDB.execSQL("UPDATE history2 SET id="+i+" WHERE id="+cursor.getLong(idColumn));
            cursor.moveToNext();
        }
        closeDatabase();
    }
}

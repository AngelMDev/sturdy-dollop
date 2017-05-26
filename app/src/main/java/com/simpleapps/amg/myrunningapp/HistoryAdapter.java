package com.simpleapps.amg.myrunningapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;


public class HistoryAdapter extends BaseAdapter {
    List<String> idRow, dateRow, timeRow, distanceRow;
    Context myContext;

    HistoryAdapter() {
        idRow = null;
        dateRow = null;
    }

    public HistoryAdapter(List<String> idRowText, List<String> dateRowText, List<String> timeArray, List<String> distanceArray, Context context) {
        idRow = idRowText;
        dateRow = dateRowText;
        timeRow = timeArray;
        distanceRow = distanceArray;
        myContext = context;
    }

    @Override
    public int getCount() {
        return idRow.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(myContext);
        View row = convertView;
        if (convertView == null)
            row = inflater.inflate(R.layout.history_row_layout, parent, false);
        TextView id, date,time,distance;
        id = (TextView) row.findViewById(R.id.idRow);
        date = (TextView) row.findViewById(R.id.dateRow);
        time=(TextView) row.findViewById(R.id.timeRow);
        distance=(TextView) row.findViewById(R.id.distanceRow);
        id.setText(idRow.get(position));
        date.setText(dateRow.get(position));
        time.setText(timeRow.get(position));
        distance.setText(distanceRow.get(position));
        return row;
    }
}

package com.simpleapps.amg.myrunningapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    SQLiteDatabase historyDB = null;
    DBAdapter dbAdapter;
    ListView historyListView;
    ListAdapter listAdapter;
    ViewFlipper viewFlipper;
    TextView runId, runDate, runTime, runDistance, runMaxSpeed, runAvgSpeed, runAltChange;

    String GET_ALL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        dbAdapter = new DBAdapter();
        initializeUI();
        getEntries(GET_ALL);
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long n=Long.parseLong(((TextView) view.findViewById(R.id.idRow)).getText().toString());
                openDatabase();
                List<String> list = dbAdapter.getDetail(n);
                runId.setText(list.get(0));
                runDate.setText(list.get(1));
                runTime.setText(list.get(2));
                runDistance.setText(list.get(3));
                runMaxSpeed.setText(list.get(4));
                runAvgSpeed.setText(list.get(5));
                runAltChange.setText(list.get(6));
                showNext();
            }
        });
    }

    public void showNext() {
        viewFlipper.setInAnimation(this, R.anim.slide_in_from_right);
        viewFlipper.setOutAnimation(this, R.anim.slide_out_to_left);
        viewFlipper.showNext();
        actionDeleteAll.setVisible(false);
        actionDeleteEntry.setVisible(true);
        actionDeleteEntry.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    public void showPrev() {
        viewFlipper.setInAnimation(this, R.anim.slide_in_from_left);
        viewFlipper.setOutAnimation(this, R.anim.slide_out_to_right);
        viewFlipper.showPrevious();
        actionDeleteAll.setVisible(true);
        actionDeleteEntry.setVisible(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (viewFlipper.getDisplayedChild() == 1) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                showPrev();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    MenuItem actionDeleteAll = null;
    MenuItem actionDeleteEntry=null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_history, menu);
        actionDeleteAll = menu.findItem(R.id.action_delete_all);
        actionDeleteEntry=menu.findItem(R.id.action_delete_detail);
        actionDeleteEntry.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        actionDeleteAll.setVisible(true);
        actionDeleteEntry.setVisible(false);
        return true;
    }

    public void openDatabase() {
        dbAdapter.createDatabase(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete_all) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.dialog_are_you_sure_delete_entries)
                    .setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deleteEntries(GET_ALL);
                            getEntries(GET_ALL);
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .show();


        }else if(id==R.id.action_delete_detail){
            deleteEntries("WHERE id=" + runId.getText());
            dbAdapter.reassignID(this);
            getEntries(GET_ALL);
            showPrev();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeUI() {
        historyListView = (ListView) findViewById(R.id.historyListView);
        viewFlipper = (ViewFlipper) findViewById(R.id.historyViewFlipper);
        runId = (TextView) findViewById(R.id.runNumberDetail);
        runDate = (TextView) findViewById(R.id.runDateDetail);
        runTime = (TextView) findViewById(R.id.runTimeDetail);
        runDistance = (TextView) findViewById(R.id.runDistanceDetail);
        runMaxSpeed = (TextView) findViewById(R.id.runTopSpeedDetail);
        runAvgSpeed = (TextView) findViewById(R.id.runAvgSpeedDetail);
        runAltChange = (TextView) findViewById(R.id.runAltChangeDetail);
    }
/*If you want the listview to ever show the Run# sequentially even if an entry has been deleted, restructure de table in the
database so that new IDs are applied in each row with a loop(i.e i=0;i<(number rows);i++...)
*/

    //@TODO show entries more elegantly
    //@TODO convert to recyclerview, research how to add an onclick listener
    private void getEntries(String parameter) {
        dbAdapter.createDatabase(this);
        listAdapter = dbAdapter.getEntries(this, parameter);
        historyListView.setAdapter(listAdapter);

    }


    public void deleteEntries(String parameter) {
        dbAdapter.createDatabase(this);
        dbAdapter.deleteEntries(parameter);

    }

}




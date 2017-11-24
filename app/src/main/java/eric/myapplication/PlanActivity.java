package eric.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import eric.myapplication.misc.Attraction;
import eric.myapplication.misc.CustomListAdapter;
import eric.myapplication.misc.AttractionDBHelper;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

import static eric.myapplication.misc.AttractionContract.AttractionEntry.*;

public class PlanActivity extends AppCompatActivity {
    public final static String SELECTED_KEY = "SELECTED";
    public final static String LIST_KEY = "LIST";

    private SQLiteDatabase attractionDB;
    private CustomListAdapter adapter;

    private ArrayList<String> availableAttractionNames;
    private ArrayList<Attraction> selectedAttractions;
    private SpinnerDialog spinnerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        AttractionDBHelper attractionDBHelper = new AttractionDBHelper(this);
        attractionDB = attractionDBHelper.getWritableDatabase();
        selectedAttractions = getAttractionList(SELECTED_TABLE_NAME);
        availableAttractionNames = getAttractionNameList(AVAILABLE_TABLE_NAME);

        adapter = new CustomListAdapter(this, selectedAttractions);
        ListView attrListView = findViewById(R.id.list_view);
        TextView emptyView = findViewById(R.id.empty_list);

        attrListView.setAdapter(adapter);
        attrListView.setEmptyView(emptyView);

        // Click on ListView item to remove attraction
        attrListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    final int position, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(PlanActivity.this);
                final Attraction attr = selectedAttractions.get(position);

                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete " + attr.getName());
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        attractionDB.beginTransaction();
                        String attrName = attr.getName();

                        // Remove from list of selected attractions
                        selectedAttractions.remove(attr);
                        removeFromTable(SELECTED_TABLE_NAME, attrName);

                        // Add to list of available attractions
                        availableAttractionNames.add(attrName);
                        addToTable(AVAILABLE_TABLE_NAME, attrName);

                        Collections.sort(availableAttractionNames);
                        attractionDB.endTransaction();
                        adapter.notifyDataSetChanged();

                        Toast.makeText(PlanActivity.this, attrName + " removed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                adb.show();
            }
        });

        spinnerInit();
    }

    // Show list of available attractions to choose from
    public void addAttrOnClick(View view) {
        spinnerDialog.showSpinerDialog();
    }

    // Proceed to next activity
    public void doneOnClick(View view) {
        Intent intent = new Intent(view.getContext(), PlanMapsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(SELECTED_KEY, selectedAttractions);
        intent.putExtra(LIST_KEY, bundle);
        startActivity(intent);
    }

    @Override
    // Inflate menu on top-right
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.plan_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            // Remove all attractions from selected list
            case R.id.remove_all:
                attractionDB.beginTransaction();
                for (Iterator<Attraction> iter = selectedAttractions.iterator(); iter.hasNext(); ) {
                    Attraction attr = iter.next();
                    String attrName = attr.getName();

                    // Add to list of available attractions
                    availableAttractionNames.add(attrName);
                    addToTable(AVAILABLE_TABLE_NAME, attrName);

                    // Remove from list of selected attractions
                    iter.remove();
                    removeFromTable(SELECTED_TABLE_NAME, attrName);
                }

                Collections.sort(availableAttractionNames);
                attractionDB.endTransaction();
                adapter.notifyDataSetChanged();

                Toast.makeText(this, "All selected attractions removed.", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.settings:
                Toast.makeText(this, "Not implemented.", Toast.LENGTH_SHORT).show();
                return true;
        }

        return true;
    }

    // Initialize the Spinner popup and set click listener
    private void spinnerInit() {
        spinnerDialog = new SpinnerDialog(PlanActivity.this, availableAttractionNames,
                "Select or Search Attractions", R.style.DialogAnimations_SmileWindow);

        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                Attraction attr = getAttraction(AVAILABLE_TABLE_NAME, item);
                if (attr == null) {
                    Toast.makeText(PlanActivity.this, "WHY IS IT HERE?", Toast.LENGTH_SHORT).show();
                    return;
                }

                attractionDB.beginTransaction();

                // Add to list of selected attractions
                selectedAttractions.add(attr);
                addToTable(SELECTED_TABLE_NAME, attr.getName());
                Collections.sort(selectedAttractions);

                // Remove from list of available attractions
                availableAttractionNames.remove(position);
                removeFromTable(AVAILABLE_TABLE_NAME, item);

                attractionDB.endTransaction();
                adapter.notifyDataSetChanged();

                Toast.makeText(PlanActivity.this, item + " added.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Add attraction with the respective name to the database table
    // In actual, only reset the COL_REMOVED field to 0
    private void addToTable(String tableName, String attrName) {
        ContentValues values = new ContentValues();
        values.put(COL_REMOVED, 0);

        String whereClause = COL_NAME + "=?";
        String[] whereArgs = {attrName};
        attractionDB.update(tableName, values, whereClause, whereArgs);
    }

    // Remove attraction with the respective name from the database table
    // In actual, only set the COL_REMOVED field to 1
    private void removeFromTable(String tableName, String attrName) {
        ContentValues values = new ContentValues();
        values.put(COL_REMOVED, 1);
        String whereClause = COL_NAME + "=?";
        String[] whereArgs = {attrName};
        attractionDB.update(tableName, values, whereClause, whereArgs);
    }

    // Get the attraction with the corresponding name
    private Attraction getAttraction(String tableName, String attrName) {
        String selection = COL_REMOVED + "=? AND " + COL_NAME + "=?";
        String[] selectionArgs = {"0", attrName};
        Cursor cursor = attractionDB.query(tableName,
                null, selection, selectionArgs, null, null, COL_NAME);

        int nameIndex = cursor.getColumnIndex(COL_NAME);
        int infoIndex = cursor.getColumnIndex(COL_INFO);
        int imageIndex = cursor.getColumnIndex(COL_IMAGE);

        while (cursor.moveToNext()) {
            String name = cursor.getString(nameIndex);
            String info = cursor.getString(infoIndex);
            int image = cursor.getInt(imageIndex);

            if (name.equals(attrName)) {
                cursor.close();
                return new Attraction(name, info, image);
            }
        }

        // Should not get here
        return null;
    }

    // Get a list of attractions for the ListView
    private ArrayList<Attraction> getAttractionList(String tableName) {
        ArrayList<Attraction> output = new ArrayList<>();
        String selection = COL_REMOVED + "=?";
        String[] selectionArgs = {"0"};
        Cursor cursor = attractionDB.query(tableName,
                null, selection, selectionArgs, null, null, COL_NAME);

        int nameIndex = cursor.getColumnIndex(COL_NAME);
        int infoIndex = cursor.getColumnIndex(COL_INFO);
        int imageIndex = cursor.getColumnIndex(COL_IMAGE);

        while (cursor.moveToNext()) {
            String name = cursor.getString(nameIndex);
            String info = cursor.getString(infoIndex);
            int image = cursor.getInt(imageIndex);

            Attraction attr = new Attraction(name, info, image);
            output.add(attr);
        }

        cursor.close();
        return output;
    }

    // Get a list of attraction names for the Spinner
    private ArrayList<String> getAttractionNameList(String tableName) {
        ArrayList<String> output = new ArrayList<>();
        String selection = COL_REMOVED + "=?";
        String[] selectionArgs = {"0"};
        Cursor cursor = attractionDB.query(tableName,
                null, selection, selectionArgs, null, null, COL_NAME);

        int nameIndex = cursor.getColumnIndex(COL_NAME);
        while (cursor.moveToNext()) {
            String name = cursor.getString(nameIndex);
            output.add(name);
        }

        cursor.close();
        return output;
    }
}

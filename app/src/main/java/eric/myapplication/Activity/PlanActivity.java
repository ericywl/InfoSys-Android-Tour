package eric.myapplication.Activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import eric.myapplication.Database.AttractionDBHelper;
import eric.myapplication.InfoActivity;
import eric.myapplication.R;
import eric.myapplication.Misc.Attraction;
import eric.myapplication.Adapter.CustomListAdapter;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

import static eric.myapplication.Database.AttractionContract.AttractionEntry.*;

public class PlanActivity extends AppCompatActivity {
    public static final String SELECTED_KEY = "SELECTED";
    public static final String LIST_KEY = "LIST";
    public static final String INFO_KEY = "INFO";
    public static final String IMAGE_KEY = "IMAGE";

    private SQLiteDatabase attractionDB;
    private CustomListAdapter adapter;

    public static ArrayList<String> availableAttractionNames;
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
        final ListView attrListView = findViewById(R.id.list_view);
        TextView emptyView = findViewById(R.id.empty_list);

        attrListView.setAdapter(adapter);
        attrListView.setEmptyView(emptyView);

        // Click on ListView item to view more info
        attrListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                Attraction attr = selectedAttractions.get(position);
                Intent intent = new Intent(view.getContext(), InfoActivity.class);
                intent.putExtra(INFO_KEY, attr.getDescription());
                intent.putExtra(IMAGE_KEY, attr.getLargeImage());
                startActivity(intent);
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
                    addToTable(AVAILABLE_TABLE_NAME, attr);

                    // Remove from list of selected attractions
                    iter.remove();
                    removeFromTable(SELECTED_TABLE_NAME, attrName);
                }

                Collections.sort(availableAttractionNames);
                attractionDB.setTransactionSuccessful();
                attractionDB.endTransaction();
                adapter.notifyDataSetChanged();

                Toast.makeText(this, "All selected attractions removed.", Toast.LENGTH_SHORT).show();
                return true;

            // Not yet implemented
            case R.id.settings:
                Toast.makeText(this, "Not implemented.", Toast.LENGTH_SHORT).show();
                return true;

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
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
                addToTable(SELECTED_TABLE_NAME, attr);
                Collections.sort(selectedAttractions);

                // Remove from list of available attractions
                availableAttractionNames.remove(position);
                removeFromTable(AVAILABLE_TABLE_NAME, item);

                attractionDB.setTransactionSuccessful();
                attractionDB.endTransaction();
                adapter.notifyDataSetChanged();

                Toast.makeText(PlanActivity.this, item + " added.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* SQLITE DATABASE RELATED METHODS*/

    // Add attraction to the database table
    private void addToTable(String tableName, Attraction attr) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, attr.getName());
        values.put(COL_ADDR, attr.getAddress());
        values.put(COL_IMAGE, attr.getImage());
        values.put(COL_INFO, attr.getDescription());
        values.put(COL_LARGE_IMAGE, attr.getLargeImage());

        long id = attractionDB.insert(tableName, null, values);
        Log.i("eric1", "" + id);
    }

    // Remove attraction with the respective name from the database table
    private void removeFromTable(String tableName, String attrName) {
        String selection = COL_NAME + "=?";
        String[] selectionArgs = {attrName};
        attractionDB.delete(tableName, selection, selectionArgs);
    }

    // Get the attraction with the corresponding name
    private Attraction getAttraction(String tableName, String attrName) {
        String selection = COL_NAME + "=?";
        String[] selectionArgs = {attrName};
        Cursor cursor = attractionDB.query(tableName,
                null, selection, selectionArgs, null, null, COL_NAME);

        int nameIndex = cursor.getColumnIndex(COL_NAME);
        int addrIndex = cursor.getColumnIndex(COL_ADDR);
        int imageIndex = cursor.getColumnIndex(COL_IMAGE);
        int largeImageIndex = cursor.getColumnIndex(COL_LARGE_IMAGE);
        int infoIndex = cursor.getColumnIndex(COL_INFO);

        while (cursor.moveToNext()) {
            String name = cursor.getString(nameIndex);
            String addr = cursor.getString(addrIndex);
            String info = cursor.getString(infoIndex);
            int image = cursor.getInt(imageIndex);
            int largeImage = cursor.getInt(largeImageIndex);

            if (name.equals(attrName)) {
                cursor.close();
                return new Attraction(name, addr, image, largeImage, info);
            }
        }

        // Should not get here
        return null;
    }

    // Get a list of attractions for the ListView
    private ArrayList<Attraction> getAttractionList(String tableName) {
        ArrayList<Attraction> output = new ArrayList<>();
        Cursor cursor = attractionDB.query(tableName,
                null, null, null, null, null, COL_NAME);

        int nameIndex = cursor.getColumnIndex(COL_NAME);
        int addrIndex = cursor.getColumnIndex(COL_ADDR);
        int imageIndex = cursor.getColumnIndex(COL_IMAGE);
        int largeImageIndex = cursor.getColumnIndex(COL_LARGE_IMAGE);
        int infoIndex = cursor.getColumnIndex(COL_INFO);

        while (cursor.moveToNext()) {
            String name = cursor.getString(nameIndex);
            String addr = cursor.getString(addrIndex);
            String info = cursor.getString(infoIndex);
            int image = cursor.getInt(imageIndex);
            int largeImage = cursor.getInt(largeImageIndex);

            Attraction attr = new Attraction(name, addr, image, largeImage, info);
            output.add(attr);
        }

        cursor.close();
        return output;
    }

    // Get a list of attraction names for the Spinner
    private ArrayList<String> getAttractionNameList(String tableName) {
        ArrayList<String> output = new ArrayList<>();
        Cursor cursor = attractionDB.query(tableName,
                null, null, null, null, null, COL_NAME);

        int nameIndex = cursor.getColumnIndex(COL_NAME);
        while (cursor.moveToNext()) {
            String name = cursor.getString(nameIndex);
            Log.i("eric1", name);
            output.add(name);
        }

        cursor.close();
        Log.i("eric1", "get name list");
        return output;
    }
}

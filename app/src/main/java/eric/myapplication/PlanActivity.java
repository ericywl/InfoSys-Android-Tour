package eric.myapplication;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

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

    private ArrayList<String> availableAttractionNames;
    private ArrayList<Attraction> selectedAttractions;
    private SpinnerDialog spinnerDialog;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        AttractionDBHelper attractionDBHelper = new AttractionDBHelper(this);
        attractionDB = attractionDBHelper.getWritableDatabase();
        selectedAttractions = getAttractionList(SELECTED_TABLE_NAME);
        availableAttractionNames = getAttractionNameList(AVAILABLE_TABLE_NAME);

        final CustomListAdapter adapter = new CustomListAdapter(this, selectedAttractions);
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
                        // Remove from list of selected attractions
                        selectedAttractions.remove(attr);
                        removeFromTable(SELECTED_TABLE_NAME, attr.getName());

                        // Add to list of available attractions
                        availableAttractionNames.add(attr.getName());
                        addToTable(AVAILABLE_TABLE_NAME, attr.getName());
                        Collections.sort(availableAttractionNames);

                        adapter.notifyDataSetChanged();
                        Toast.makeText(PlanActivity.this, attr.getName() + " deleted.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                adb.show();
            }
        });

        spinnerInit(adapter);
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

    private void spinnerInit(final CustomListAdapter adapter) {
        spinnerDialog = new SpinnerDialog(PlanActivity.this, availableAttractionNames,
                "Select or Search Attractions", R.style.DialogAnimations_SmileWindow);

        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                // Add to list of selected attractions
                Attraction attr = getAttraction(AVAILABLE_TABLE_NAME, item);
                if (attr != null) {
                    selectedAttractions.add(attr);
                    addToTable(SELECTED_TABLE_NAME, attr.getName());
                    Collections.sort(selectedAttractions);

                    // Remove from list of available attractions
                    availableAttractionNames.remove(position);
                    removeFromTable(AVAILABLE_TABLE_NAME, item);

                    adapter.notifyDataSetChanged();
                    Toast.makeText(PlanActivity.this, item + " added.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addToTable(String tableName, String attrName) {
        ContentValues values = new ContentValues();
        values.put(COL_REMOVED, 0);

        String whereClause = COL_NAME + "=?";
        String[] whereArgs = {attrName};
        attractionDB.update(tableName, values, whereClause, whereArgs);
    }

    private void removeFromTable(String tableName, String attrName) {
        ContentValues values = new ContentValues();
        values.put(COL_REMOVED, 1);
        String whereClause = COL_NAME + "=?";
        String[] whereArgs = {attrName};
        attractionDB.update(tableName, values, whereClause, whereArgs);
    }

    private Attraction getAttraction(String tableName, String attrName) {
        Log.i("eric1", "start getAttraction");
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

        return null;
    }

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

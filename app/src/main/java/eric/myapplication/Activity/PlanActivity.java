package eric.myapplication.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import eric.myapplication.Adapter.CustomListAdapter;
import eric.myapplication.Database.AttractionDBHelper;
import eric.myapplication.Misc.Attraction;
import eric.myapplication.R;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

import static eric.myapplication.Database.AttractionContract.AttractionEntry.AVAILABLE_TABLE_NAME;
import static eric.myapplication.Database.AttractionContract.AttractionEntry.SELECTED_TABLE_NAME;
import static eric.myapplication.Database.AttractionDBHelper.addToTable;
import static eric.myapplication.Database.AttractionDBHelper.getAttraction;
import static eric.myapplication.Database.AttractionDBHelper.getAttractionList;
import static eric.myapplication.Database.AttractionDBHelper.getAttractionNameList;
import static eric.myapplication.Database.AttractionDBHelper.removeFromTable;

public class PlanActivity extends AppCompatActivity {
    public static final String SELECTED_KEY = "SELECTED";
    public static final String LIST_KEY = "LIST";
    public static final String INFO_KEY = "INFO";
    public static final String IMAGE_KEY = "IMAGE";
    public static final String NAME_KEY = "NAME";
    public static final String BUDGET_KEY = "BUDGET";

    private SQLiteDatabase attractionDB;
    private CustomListAdapter adapter;

    private ArrayList<String> availableAttractionNames;
    private ArrayList<Attraction> selectedAttractions;
    private SpinnerDialog spinnerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        AttractionDBHelper attractionDBHelper = AttractionDBHelper.getInstance(this);
        attractionDB = attractionDBHelper.getWritableDatabase();
        selectedAttractions = getAttractionList(attractionDB, SELECTED_TABLE_NAME);
        availableAttractionNames = getAttractionNameList(attractionDB,
                AVAILABLE_TABLE_NAME);

        adapter = new CustomListAdapter(this, selectedAttractions, availableAttractionNames);
        final ListView attrListView = findViewById(R.id.list_view);
        TextView emptyView = findViewById(R.id.empty_list);

        attrListView.setAdapter(adapter);
        attrListView.setEmptyView(emptyView);

        // Click on ListView item to view more info
        attrListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                Log.i("eric1", "Clicked");
                Attraction attr = selectedAttractions.get(position);
                Intent intent = new Intent(view.getContext(), InfoActivity.class);
                intent.putExtra(NAME_KEY, attr.getName());
                intent.putExtra(INFO_KEY, attr.getDescription());
                intent.putExtra(IMAGE_KEY, attr.getLargeImage());
                startActivity(intent);
            }
        });

        spinnerInit();
    }

    // Show list of available attractions to choose from
    public void addAttrOnClick(View view) {
        adapter.updateAvailableList(availableAttractionNames);
        spinnerDialog.showSpinerDialog();
    }

    // Proceed to next activity
    public void doneOnClick(View view) {
        final Intent intent = new Intent(view.getContext(), PlanMapsActivity.class);
        final Bundle bundle = new Bundle();
        final ArrayList<String> selectedAttrNames = new ArrayList<>();
        for (Attraction attr : selectedAttractions) {
            selectedAttrNames.add(attr.getName());
        }

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Budget");
        adb.setMessage("Key in your budget.");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(90, 0, 100, 0);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(input, params);

        adb.setView(layout);
        adb.setNegativeButton("Cancel", null);
        adb.setPositiveButton("Enter", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                bundle.putSerializable(SELECTED_KEY, selectedAttrNames);
                intent.putExtra(LIST_KEY, bundle);
                intent.putExtra(BUDGET_KEY, input.getText().toString());
                attractionDB.close();
                startActivity(intent);
            }
        });

        adb.show();
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
            case R.id.add_all:
                attractionDB.beginTransaction();

                for (Iterator<String> iter = availableAttractionNames.iterator(); iter.hasNext(); ) {
                    String attrName = iter.next();

                    // Add to list of selected attractions
                    Attraction attr = getAttraction(attractionDB, AVAILABLE_TABLE_NAME, attrName);
                    selectedAttractions.add(attr);
                    addToTable(attractionDB, SELECTED_TABLE_NAME, attr);

                    // Remove from list of selected attractions
                    iter.remove();
                    removeFromTable(attractionDB, AVAILABLE_TABLE_NAME, attrName);
                }

                attractionDB.setTransactionSuccessful();
                attractionDB.endTransaction();
                adapter.notifyDataSetChanged();

                Toast.makeText(this, "All available attractions added.",
                        Toast.LENGTH_SHORT).show();
                return true;

            // Remove all attractions from selected list
            case R.id.remove_all:
                attractionDB.beginTransaction();
                for (Iterator<Attraction> iter = selectedAttractions.iterator(); iter.hasNext(); ) {
                    Attraction attr = iter.next();
                    String attrName = attr.getName();

                    // Add to list of available attractions
                    availableAttractionNames.add(attrName);
                    addToTable(attractionDB, AVAILABLE_TABLE_NAME, attr);

                    // Remove from list of selected attractions
                    iter.remove();
                    removeFromTable(attractionDB, SELECTED_TABLE_NAME, attrName);
                }

                Collections.sort(availableAttractionNames);
                attractionDB.setTransactionSuccessful();
                attractionDB.endTransaction();
                adapter.notifyDataSetChanged();

                Toast.makeText(this, "All selected attractions removed.",
                        Toast.LENGTH_SHORT).show();
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
                Attraction attr = getAttraction(attractionDB, AVAILABLE_TABLE_NAME, item);
                if (attr == null) {
                    Toast.makeText(PlanActivity.this, "WHY IS IT HERE?", Toast.LENGTH_SHORT).show();
                    return;
                }

                attractionDB.beginTransaction();

                // Add to list of selected attractions
                selectedAttractions.add(attr);
                addToTable(attractionDB, SELECTED_TABLE_NAME, attr);

                // Remove from list of available attractions
                availableAttractionNames.remove(position);
                removeFromTable(attractionDB, AVAILABLE_TABLE_NAME, item);

                attractionDB.setTransactionSuccessful();
                attractionDB.endTransaction();
                adapter.notifyDataSetChanged();

                Toast.makeText(PlanActivity.this, item + " added.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package eric.myapplication;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class MainActivity extends AppCompatActivity {
    static Map<String, Attraction> attractionMap = new HashMap<>();
    static ArrayList<Attraction> selectedAttractions = new ArrayList<>();

    static {
        attractionMap.put("Changi City Point",
                new Attraction("Changi City Point", "Lorem Ipsum", R.drawable.octopus));
        attractionMap.put("ION",
                new Attraction("ION", "Lorem Ipsum", R.drawable.octopus));
        attractionMap.put("SUTD",
                new Attraction("SUTD", "Lorem Ipsum", R.drawable.octopus));
        attractionMap.put("Singapore Flyer",
                new Attraction("Singapore Flyer", "Lorem Ipsum", R.drawable.octopus));
        attractionMap.put("Night Safari",
                new Attraction("Night Safari", "Lorem Ipsum", R.drawable.octopus));

        selectedAttractions.add(new Attraction("Marina Bay Sands", "Lorem Ipsum", R.drawable.octopus));
        selectedAttractions.add(new Attraction("Vivo City", "Lorem Ipsum", R.drawable.octopus));
        selectedAttractions.add(new Attraction("Resorts World Sentosa", "Lorem Ipsum",
                R.drawable.octopus));
        selectedAttractions.add(new Attraction("Budda Tooth Relic Temple", "Lorem Ipsum",
                R.drawable.octopus));
        selectedAttractions.add(new Attraction("Singapore Zoo", "Lorem Ipsum", R.drawable.octopus));
        selectedAttractions.add(new Attraction("Jurong Bird Park", "Lorem Ipsum", R.drawable.octopus));
    }

    SpinnerDialog spinnerDialog;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final CustomListAdapter adapter = new CustomListAdapter(this, selectedAttractions);
        final ArrayList<String> attractionStrList = new ArrayList<>(attractionMap.keySet());
        Collections.sort(attractionStrList);
        Collections.sort(selectedAttractions);

        listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position,
                                    long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
                final Attraction attr = selectedAttractions.get(position);

                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete " + attr.getName());
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        selectedAttractions.remove(attr);
                        attractionMap.put(attr.getName(), attr);
                        attractionStrList.add(attr.getName());
                        Collections.sort(attractionStrList);
                        adapter.notifyDataSetChanged();

                        Toast.makeText(MainActivity.this, attr.getName() + " deleted.",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                adb.show();
            }
        });

        spinnerDialog = new SpinnerDialog(MainActivity.this, attractionStrList,
                "Select or Search Attractions", R.style.DialogAnimations_SmileWindow);

        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                String name = attractionStrList.get(position);
                selectedAttractions.add(attractionMap.get(name));
                Collections.sort(selectedAttractions);
                attractionMap.remove(name);
                attractionStrList.remove(position);
                adapter.notifyDataSetChanged();

                Toast.makeText(MainActivity.this, item + " added.", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialog.showSpinerDialog();
            }
        });
    }
}

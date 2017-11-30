package eric.myapplication.Activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eric.myapplication.Database.TravelDBHelper;
import eric.myapplication.Misc.TSPBruteForce;
import eric.myapplication.Misc.TSPRoute;
import eric.myapplication.Misc.TSPFastSolver;
import eric.myapplication.R;

import static eric.myapplication.Database.TravelContract.TravelEntry.*;
import static eric.myapplication.Database.TravelDBHelper.getAttractionNameList;

// Class to test the solver, does not have anything to do with the project
public class TravelActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);

        TextView dbtext = findViewById(R.id.dbtext);

        TravelDBHelper travelDBHelper = new TravelDBHelper(this);
        SQLiteDatabase travelDB = travelDBHelper.getReadableDatabase();
        List<String> attrNameList = getAttractionNameList(travelDB, TAXI_TIME);

        List<String> placesToVisit = new ArrayList<>(Arrays.asList(
                BURMESE,
                KONG_MENG,
                KWAN_IM,
                SAKYA_MUNI,
                WAT_ANANDA
        ));

        // TSPFastSolver tspSolver = new TSPFastSolver(travelDB);
        TSPBruteForce tspSolver = new TSPBruteForce(travelDB);
        long startTime = System.nanoTime();
        TSPRoute bestTSPRoute = tspSolver.findBestRoute(MBS, placesToVisit, 20);
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        Log.i("eric1time", "" + (double) elapsedTime / 1000000000.0);

        dbtext.setText(bestTSPRoute.toString());
    }
}

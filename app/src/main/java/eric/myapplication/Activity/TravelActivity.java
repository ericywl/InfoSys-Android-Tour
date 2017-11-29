package eric.myapplication.Activity;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eric.myapplication.Database.TravelDBHelper;
import eric.myapplication.Misc.Route;
import eric.myapplication.Misc.TSPBruteForce;
import eric.myapplication.R;

import static eric.myapplication.Database.TravelContract.TravelEntry.*;
import static eric.myapplication.Database.TravelDBHelper.*;

public class TravelActivity extends AppCompatActivity {
    private List<String> attrNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);

        TextView dbtext = findViewById(R.id.dbtext);

        TravelDBHelper travelDBHelper = new TravelDBHelper(this);
        SQLiteDatabase travelDB = travelDBHelper.getReadableDatabase();
        attrNameList = getAttractionNameList(travelDB, TAXI_TIME);

        List<String> placesToVisit = new ArrayList<>(Arrays.asList(
                SIONG_LIM,
                WAT_ANANDA,
                THIAN_HOCK,
                BURMESE,
                KONG_MENG
        ));

        TSPBruteForce tspBruteForce = new TSPBruteForce(travelDB);
        Route bestRoute = tspBruteForce.findBestRoute(MBS, placesToVisit, 20);

        dbtext.setText(bestRoute.toString());
    }
}

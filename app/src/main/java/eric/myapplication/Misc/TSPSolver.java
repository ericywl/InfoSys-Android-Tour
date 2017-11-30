package eric.myapplication.Misc;


import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import static eric.myapplication.Database.TravelContract.TravelEntry.*;
import static eric.myapplication.Database.TravelDBHelper.getEntry;

public class TSPSolver {
    public List<TSPPath> initPaths(SQLiteDatabase sqLiteDatabase, List<String> places) {
        List<TSPPath> TSPPaths = new ArrayList<>();

        for (int i = 0; i < places.size() - 1; i++) {
            String from = places.get(i);
            String to = places.get(i + 1);

            // Get time and cost for all transport modes
            double taxiTime = getEntry(sqLiteDatabase, TAXI_TIME, from, to);
            double taxiCost = getEntry(sqLiteDatabase, TAXI_COST, from, to);
            double busTime = getEntry(sqLiteDatabase, PT_TIME, from, to);
            double busCost = getEntry(sqLiteDatabase, PT_COST, from, to);
            double walkTime = getEntry(sqLiteDatabase, WALK_TIME, from, to);

            double busTimeCost = (busTime - taxiTime) / (taxiCost - busCost);
            double walkTimeCost = (walkTime - taxiTime) / taxiCost;

            String altTransportMode = "PT";
            double altTime = busTime;
            double altCost = busCost;
            double timeIncreasePerCostSaving = busTimeCost;

            // Get most efficient alternative transport mode
            if (busTimeCost < 0 || busTimeCost > walkTimeCost) {
                altTransportMode = "WALK";
                altTime = walkTime;
                altCost = 0;
                timeIncreasePerCostSaving = walkTimeCost;
            }

            // Add path to list of paths
            TSPPath path = new TSPPath(from, to, taxiTime, taxiCost, altTime, altCost,
                    altTransportMode, timeIncreasePerCostSaving);
            TSPPaths.add(path);
        }

        return TSPPaths;
    }
}

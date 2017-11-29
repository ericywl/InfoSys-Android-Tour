package eric.myapplication.Misc;


import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static eric.myapplication.Database.TravelContract.TravelEntry.*;
import static eric.myapplication.Database.TravelDBHelper.*;

public class TSPBruteForce {
    private String originName;
    private SQLiteDatabase travelDB;
    private List<Route> allRoutes = new ArrayList<>();

    public TSPBruteForce(SQLiteDatabase sqLiteDatabase) {
        this.travelDB = sqLiteDatabase;
    }

    public Route findBestRoute(String originName, List<String> placesToVisit, int budget) {
        Route tempRoute = new Route();
        this.originName = originName;
        tempRoute.addPlace(originName, 0, 0);

        travelDB.beginTransaction();
        findAllRoutes(tempRoute, placesToVisit);

        Collections.sort(allRoutes);
        travelDB.setTransactionSuccessful();
        travelDB.endTransaction();

        return allRoutes.get(0);
    }

    private void findAllRoutes(Route tempRoute, List<String> unvisitedAttractions) {
        if (unvisitedAttractions.isEmpty()) {
            allRoutes.add(tempRoute);
            return;
        }

        for (String attrName : unvisitedAttractions) {
            Route newRoute = tempRoute.clone();
            List<String> newUnvisitedAttractions = new ArrayList<>(unvisitedAttractions);
            String from = newRoute.getLast();

            // Get time and cost for all transport modes
            double taxiTime = getEntry(travelDB, TAXI_TIME, from, attrName);
            double taxiCost = getEntry(travelDB, TAXI_COST, from, attrName);
            double busTime = getEntry(travelDB, PT_TIME, from, attrName);
            double busCost = getEntry(travelDB, PT_COST, from, attrName);
            double walkTime = getEntry(travelDB, WALK_TIME, from, attrName);

            double busTimeCost = (busTime - taxiTime) / (taxiCost - busCost);
            double walkTimeCost = (walkTime - taxiTime) / taxiCost;

            String altTransportMode = "BUS";
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

            // Add path to route
            Path path = new Path(from, attrName, altTime, altCost, altTransportMode,
                    timeIncreasePerCostSaving);

            newRoute.addPlace(attrName, taxiTime, taxiCost);
            newRoute.addPath(path);
            newUnvisitedAttractions.remove(attrName);
            findAllRoutes(newRoute, newUnvisitedAttractions);
        }
    }

    /*
    private int routeWeight(String tableName, List<String> route) {
        int routeWeight = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            String from = route.get(i);
            String to = route.get(i + 1);
            routeWeight += getEntry(travelDB, tableName, from, to);
        }

        return routeWeight;
    }
    */
}

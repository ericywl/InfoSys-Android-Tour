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
        List<String> tempRoute = new ArrayList<>();
        this.originName = originName;
        tempRoute.add(originName);

        travelDB.beginTransaction();
        findAllRoutes(tempRoute, placesToVisit);

        Collections.sort(allRoutes);
        Route tempBestRoute = allRoutes.get(0);
        tempBestRoute.setPaths(initPaths(tempBestRoute.getPlaces()));
        if (tempBestRoute.getCostWeight() <= budget) {
            return tempBestRoute;
        }

        // replace paths

        travelDB.setTransactionSuccessful();
        travelDB.endTransaction();

        return null;
    }

    private void findAllRoutes(List<String> tempRoute, List<String> unvisitedAttractions) {
        if (unvisitedAttractions.isEmpty()) {
            tempRoute.add(originName);
            double totalTime = routeWeight(TAXI_TIME, tempRoute);
            double totalCost = routeWeight(TAXI_COST, tempRoute);
            Route route = new Route(tempRoute, totalTime, totalCost);
            allRoutes.add(route);
            return;
        }

        for (String attrName : unvisitedAttractions) {
            List<String> newRoute = new ArrayList<>(tempRoute);
            List<String> newUnvisitedAttractions = new ArrayList<>(unvisitedAttractions);

            newRoute.add(attrName);
            newUnvisitedAttractions.remove(attrName);
            findAllRoutes(newRoute, newUnvisitedAttractions);
        }
    }

    private double routeWeight(String tableName, List<String> route) {
        double routeWeight = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            String from = route.get(i);
            String to = route.get(i + 1);
            routeWeight += getEntry(travelDB, tableName, from, to);
        }

        return routeWeight;
    }

    private List<Path> initPaths(List<String> places) {
        List<Path> paths = new ArrayList<>();

        for (int i = 0; i < places.size(); i++) {
            String from = places.get(i);
            String to = places.get(i + 1);

            // Get time and cost for all transport modes
            double taxiTime = getEntry(travelDB, TAXI_TIME, from, to);
            double taxiCost = getEntry(travelDB, TAXI_COST, from, to);
            double busTime = getEntry(travelDB, PT_TIME, from, to);
            double busCost = getEntry(travelDB, PT_COST, from, to);
            double walkTime = getEntry(travelDB, WALK_TIME, from, to);

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

            // Add path to list of paths
            Path path = new Path(from, to, taxiTime, taxiCost, altTime, altCost,
                    altTransportMode, timeIncreasePerCostSaving);
            paths.add(path);
        }

        return paths;
    }

}

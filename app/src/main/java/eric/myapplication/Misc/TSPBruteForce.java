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
    private List<TSPRoute> allTSPRoutes = new ArrayList<>();

    public TSPBruteForce(SQLiteDatabase sqLiteDatabase) {
        this.travelDB = sqLiteDatabase;
    }

    public TSPRoute findBestRoute(String originName, List<String> placesToVisit, int budget) {
        List<String> tempRoute = new ArrayList<>();
        this.originName = originName;
        tempRoute.add(originName);

        travelDB.beginTransaction();
        findAllRoutes(tempRoute, placesToVisit);

        Collections.sort(allTSPRoutes);
        TSPRoute tempBestTSPRoute = allTSPRoutes.get(0);
        tempBestTSPRoute.setPaths(initPaths(tempBestTSPRoute.getPlaces()));
        if (tempBestTSPRoute.getCostWeight() <= budget) {
            return tempBestTSPRoute;
        }

        // Replace transport modes on paths that are the least efficient
        List<TSPPath> replacedTSPPaths = tempBestTSPRoute.getPaths();
        List<TSPPath> sortedTSPPaths = new ArrayList<>(replacedTSPPaths);
        Collections.sort(sortedTSPPaths);

        int index = 0;
        while (tempBestTSPRoute.getCostWeight() > budget) {
            TSPPath sTSPPath = sortedTSPPaths.get(index);
            if (sTSPPath.getTimeIncreasePerCostSaving() > 0) {
                for (TSPPath oTSPPath : replacedTSPPaths)
                    if (sTSPPath.equals(oTSPPath)) {
                        oTSPPath.setToAltTransportMode();

                        double timeIncrease = sTSPPath.getAltTime() - sTSPPath.getTaxiTime();
                        double costDecrease = sTSPPath.getTaxiCost() - sTSPPath.getAltCost();
                        tempBestTSPRoute.addTimeWeight(timeIncrease);
                        tempBestTSPRoute.reduceCostWeight(costDecrease);
                    }
            }

            index++;
        }

        tempBestTSPRoute.setPaths(replacedTSPPaths);
        travelDB.setTransactionSuccessful();
        travelDB.endTransaction();

        return tempBestTSPRoute;
    }

    private void findAllRoutes(List<String> tempRoute, List<String> unvisitedAttractions) {
        if (unvisitedAttractions.isEmpty()) {
            tempRoute.add(originName);
            double totalTime = routeWeight(TAXI_TIME, tempRoute);
            double totalCost = routeWeight(TAXI_COST, tempRoute);
            TSPRoute TSPRoute = new TSPRoute(tempRoute, totalTime, totalCost);
            allTSPRoutes.add(TSPRoute);
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

    private List<TSPPath> initPaths(List<String> places) {
        List<TSPPath> TSPPaths = new ArrayList<>();

        for (int i = 0; i < places.size() - 1; i++) {
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

            // Add TSPPath to list of TSPPaths
            TSPPath TSPPath = new TSPPath(from, to, taxiTime, taxiCost, altTime, altCost,
                    altTransportMode, timeIncreasePerCostSaving);
            TSPPaths.add(TSPPath);
        }

        return TSPPaths;
    }
}

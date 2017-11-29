package eric.myapplication.Misc;


import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static eric.myapplication.Database.TravelContract.TravelEntry.*;
import static eric.myapplication.Database.TravelDBHelper.getEntry;

public class TSPFastSolver {
    private String originName;
    private SQLiteDatabase travelDB;
    private TSPRoute bestApproxRoute;

    public TSPFastSolver(SQLiteDatabase sqLiteDatabase) {
        this.travelDB = sqLiteDatabase;
    }

    public TSPRoute findBestRoute(String originName, List<String> placesToVisit, int budget) {
        List<String> tempRoute = new ArrayList<>();
        this.originName = originName;
        tempRoute.add(originName);

        travelDB.beginTransaction();
        findOneRoute(tempRoute, placesToVisit);

        bestApproxRoute.setPaths(initPaths(bestApproxRoute.getPlaces()));
        if (bestApproxRoute.getCostWeight() <= budget) {
            return bestApproxRoute;
        }

        // Best route with all TAXI did not meet the budget
        // Replace transport modes on paths that are the least efficient
        List<TSPPath> replacedPaths = bestApproxRoute.getPaths();
        List<TSPPath> sortedTSPPaths = new ArrayList<>(replacedPaths);
        Collections.sort(sortedTSPPaths);

        int index = 0;
        while (bestApproxRoute.getCostWeight() > budget) {
            TSPPath sTSPPath = sortedTSPPaths.get(index);
            if (sTSPPath.getTimeIncreasePerCostSaving() > 0) {
                for (TSPPath oTSPPath : replacedPaths)
                    if (sTSPPath.equals(oTSPPath)) {
                        oTSPPath.setToAltTransportMode();

                        double timeIncrease = sTSPPath.getAltTime() - sTSPPath.getTaxiTime();
                        double costDecrease = sTSPPath.getTaxiCost() - sTSPPath.getAltCost();
                        bestApproxRoute.addTimeWeight(timeIncrease);
                        bestApproxRoute.reduceCostWeight(costDecrease);
                    }
            }

            index++;
        }

        bestApproxRoute.setPaths(replacedPaths);
        travelDB.setTransactionSuccessful();
        travelDB.endTransaction();
        travelDB.close();

        return bestApproxRoute;
    }

    // Find route approximation using Nearest Neighbor heuristic
    private void findOneRoute(List<String> tempRoute, List<String> unvisitedAttractions) {
        if (unvisitedAttractions.isEmpty()) {
            tempRoute.add(originName);
            double totalTime = routeWeight(TAXI_TIME, tempRoute);
            double totalCost = routeWeight(TAXI_COST, tempRoute);
            bestApproxRoute = new TSPRoute(tempRoute, totalTime, totalCost);
            return;
        }

        String currentAttr = tempRoute.get(tempRoute.size() - 1);
        String nearestAttr = null;
        double leastTime = 0;

        for (String attrName : unvisitedAttractions) {
            if (nearestAttr == null) {
                nearestAttr = attrName;
                leastTime = getEntry(travelDB, TAXI_TIME, currentAttr, nearestAttr);
            }

            double currentTime = getEntry(travelDB, TAXI_TIME, currentAttr, attrName);
            if (currentTime < leastTime) {
                nearestAttr = attrName;
                leastTime = currentTime;
            }
        }

        tempRoute.add(nearestAttr);
        unvisitedAttractions.remove(nearestAttr);
        findOneRoute(tempRoute, unvisitedAttractions);
    }

    // Calculate total weight of route
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

            // Add path to list of paths
            TSPPath path = new TSPPath(from, to, taxiTime, taxiCost, altTime, altCost,
                    altTransportMode, timeIncreasePerCostSaving);
            TSPPaths.add(path);
        }

        return TSPPaths;
    }
}

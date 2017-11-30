package eric.myapplication.Misc;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static eric.myapplication.Database.TravelContract.TravelEntry.PT_COST;
import static eric.myapplication.Database.TravelContract.TravelEntry.PT_TIME;
import static eric.myapplication.Database.TravelContract.TravelEntry.TAXI_COST;
import static eric.myapplication.Database.TravelContract.TravelEntry.TAXI_TIME;
import static eric.myapplication.Database.TravelContract.TravelEntry.WALK_TIME;
import static eric.myapplication.Database.TravelDBHelper.getEntry;

public class TSPFastSolver extends TSPSolver {
    private String originName;
    private SQLiteDatabase travelDB;
    private TSPRoute bestApproxRoute;

    public TSPFastSolver(SQLiteDatabase sqLiteDatabase) {
        this.travelDB = sqLiteDatabase;
    }

    public TSPRoute findBestRoute(String originName, List<String> placesToVisit, double budget) {
        List<String> tempRoute = new ArrayList<>();
        this.originName = originName;
        tempRoute.add(originName);

        travelDB.beginTransaction();
        findOneRoute(tempRoute, placesToVisit);

        bestApproxRoute.setPaths(initPaths(travelDB, bestApproxRoute.getPlaces()));
        if (bestApproxRoute.getCostWeight() <= budget) {
            return bestApproxRoute;
        }

        // Best route with all TAXI did not meet the budget
        // Replace transport modes on paths that are the least efficient
        List<TSPPath> replacedPaths = bestApproxRoute.getPaths();
        List<TSPPath> sortedPaths = new ArrayList<>(replacedPaths);
        Collections.sort(sortedPaths);
        Log.i("eric1", sortedPaths.toString());

        int index = 0;
        while (bestApproxRoute.getCostWeight() > budget) {
            TSPPath sPath = sortedPaths.get(index);
            // Include cases where alternative time might be lower than taxi time
            if (sPath.getTimeIncreasePerCostSaving() > -2) {
                for (TSPPath rPath : replacedPaths)
                    if (sPath.equals(rPath) && !rPath.isSwitched()) {
                        rPath.setToAltTransportMode();

                        double timeIncrease = sPath.getAltTime() - sPath.getTaxiTime();
                        double costDecrease = sPath.getTaxiCost() - sPath.getAltCost();
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
}

package eric.myapplication.Misc;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static eric.myapplication.Database.TravelContract.TravelEntry.*;
import static eric.myapplication.Database.TravelDBHelper.*;

public class TSPBruteForce extends TSPSolver {
    private String originName;
    private SQLiteDatabase travelDB;
    private List<TSPRoute> allTSPRoutes = new ArrayList<>();

    public TSPBruteForce(SQLiteDatabase sqLiteDatabase) {
        this.travelDB = sqLiteDatabase;
    }

    public TSPRoute findBestRoute(String originName, List<String> placesToVisit, double budget) {
        List<String> tempRoute = new ArrayList<>();
        this.originName = originName;
        tempRoute.add(originName);

        travelDB.beginTransaction();
        findAllRoutes(tempRoute, placesToVisit);

        Collections.sort(allTSPRoutes);
        List<TSPRoute> bestThreeRoutes = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            if (i >= allTSPRoutes.size()) break;

            TSPRoute route = allTSPRoutes.get(i);
            route.setPaths(initPaths(travelDB, route.getPlaces()));
            if (route.getCostWeight() <= budget) {
                return route;
            }

            // Replace transport modes on paths that are the least efficient
            List<TSPPath> replacedPaths = route.getPaths();
            List<TSPPath> sortedPaths = new ArrayList<>(replacedPaths);
            Collections.sort(sortedPaths);
            Log.i("eric1", sortedPaths.toString());

            int index = 0;
            while (route.getCostWeight() > budget) {
                TSPPath sPath = sortedPaths.get(index);
                // Include cases where alternative time might be lower than taxi time
                if (sPath.getTimeIncreasePerCostSaving() > -2) {
                    for (TSPPath rPath : replacedPaths)
                        if (sPath.equals(rPath) && !rPath.isSwitched()) {
                            rPath.setToAltTransportMode();

                            double timeIncrease = sPath.getAltTime() - sPath.getTaxiTime();
                            double costDecrease = sPath.getTaxiCost() - sPath.getAltCost();
                            route.addTimeWeight(timeIncrease);
                            route.reduceCostWeight(costDecrease);
                        }
                }

                index++;
            }

            route.setPaths(replacedPaths);
            bestThreeRoutes.add(route);
        }

        travelDB.setTransactionSuccessful();
        travelDB.endTransaction();

        Collections.sort(bestThreeRoutes);
        return bestThreeRoutes.get(0);
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
}

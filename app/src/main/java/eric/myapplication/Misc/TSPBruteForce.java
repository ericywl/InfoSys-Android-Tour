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

        findAllRoutes(tempRoute, placesToVisit);
        Collections.sort(allRoutes);

        for (Route route : allRoutes) {
            if (route.getCostWeight() <= budget)
                return route;
        }

        return null;
    }

    private void findAllRoutes(List<String> tempRoute, List<String> unvisitedAttractions) {
        if (unvisitedAttractions.isEmpty()) {
            tempRoute.add(originName);
            int timeWeight = routeWeight(TAXI_TIME, tempRoute);
            int costWeight = routeWeight(TAXI_COST, tempRoute);
            allRoutes.add(new Route(tempRoute, timeWeight, costWeight));
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

    private int routeWeight(String tableName, List<String> route) {
        int routeWeight = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            String from = route.get(i);
            String to = route.get(i + 1);
            routeWeight += getEntry(travelDB, tableName, from, to);
        }

        return routeWeight;
    }
}

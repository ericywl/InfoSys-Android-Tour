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

    public Route findBestRoute(String originName, List<String> placesToVisit) {
        List<String> tempRoute = new ArrayList<>();
        this.originName = originName;
        tempRoute.add(originName);

        findAllRoutes(tempRoute, placesToVisit);
        Collections.sort(allRoutes);
        return allRoutes.get(0);
    }

    private void findAllRoutes(List<String> tempRoute, List<String> unvisitedAttractions) {
        if (unvisitedAttractions.isEmpty()) {
            tempRoute.add(originName);
            allRoutes.add(new Route(tempRoute, routeWeight(tempRoute)));
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

    private int routeWeight(List<String> route) {
        int routeWeight = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            String from = route.get(i);
            String to = route.get(i + 1);
            routeWeight += getEntry(travelDB, TAXI_TIME, from, to);
        }

        return routeWeight;
    }
}

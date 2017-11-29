package eric.myapplication.Algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TSPBruteForce {
    private static Map<List<TSPPlace>, Integer> allRoutes = new HashMap<>();
    private static TSPPlace origin = new TSPPlace("Start");
    static {
        origin.addWeight("First", 7);
        origin.addWeight("Second", 2);
        origin.addWeight("Third", 3);
    }

    public static void main(String[] args) {
        TSPPlace first = new TSPPlace("First");
        first.addWeight("Start", 3);
        first.addWeight("Second", 2);
        first.addWeight("Third", 5);

        TSPPlace second = new TSPPlace("Second");
        second.addWeight("Start", 5);
        second.addWeight("First", 8);
        second.addWeight("Third", 2);

        TSPPlace third = new TSPPlace("Third");
        third.addWeight("Start", 10);
        third.addWeight("First", 11);
        third.addWeight("Second", 7);

        List<TSPPlace> places = new ArrayList<>();
        places.add(first);
        places.add(second);
        places.add(third);

        List<TSPPlace> currentRoute = new ArrayList<>();
        currentRoute.add(origin);
        bruteForce(currentRoute, places);

        System.out.println(allRoutes);
    }

    private static void bruteForce(List<TSPPlace> currentRoute, List<TSPPlace> places) {
        if (places.isEmpty()) {
            currentRoute.add(origin);
            int routeWeight = routeWeight(currentRoute);
            allRoutes.put(currentRoute, routeWeight);
            return;
        }

        for (TSPPlace place : places) {
            List<TSPPlace> newRoute = new ArrayList<>(currentRoute);
            List<TSPPlace> newPlaces = new ArrayList<>(places);
            newRoute.add(place);
            newPlaces.remove(place);
            bruteForce(newRoute, newPlaces);
        }
    }

    private static int routeWeight(List<TSPPlace> route) {
        int routeWeight = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            TSPPlace place = route.get(i);
            TSPPlace nextPlace = route.get(i + 1);
            routeWeight += place.getWeights().getOrDefault(nextPlace.getName(), 1000);
        }

        return routeWeight;
    }
}

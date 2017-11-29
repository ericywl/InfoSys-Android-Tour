package eric.myapplication.Misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TSPBruteForceExample {
    private static Map<List<TSPPlaceExample>, Integer> allRoutes = new HashMap<>();
    private static TSPPlaceExample origin = new TSPPlaceExample("Start");
    static {
        origin.addWeight("First", 7);
        origin.addWeight("Second", 2);
        origin.addWeight("Third", 3);
    }

    public static void main(String[] args) {
        TSPPlaceExample first = new TSPPlaceExample("First");
        first.addWeight("Start", 3);
        first.addWeight("Second", 2);
        first.addWeight("Third", 5);

        TSPPlaceExample second = new TSPPlaceExample("Second");
        second.addWeight("Start", 5);
        second.addWeight("First", 8);
        second.addWeight("Third", 2);

        TSPPlaceExample third = new TSPPlaceExample("Third");
        third.addWeight("Start", 10);
        third.addWeight("First", 11);
        third.addWeight("Second", 7);

        List<TSPPlaceExample> places = new ArrayList<>();
        places.add(first);
        places.add(second);
        places.add(third);

        List<TSPPlaceExample> currentRoute = new ArrayList<>();
        currentRoute.add(origin);
        bruteForce(currentRoute, places);

        System.out.println(allRoutes);
    }

    private static void bruteForce(List<TSPPlaceExample> currentRoute, List<TSPPlaceExample> places) {
        if (places.isEmpty()) {
            currentRoute.add(origin);
            int routeWeight = routeWeight(currentRoute);
            allRoutes.put(currentRoute, routeWeight);
            return;
        }

        for (TSPPlaceExample place : places) {
            List<TSPPlaceExample> newRoute = new ArrayList<>(currentRoute);
            List<TSPPlaceExample> newPlaces = new ArrayList<>(places);
            newRoute.add(place);
            newPlaces.remove(place);
            bruteForce(newRoute, newPlaces);
        }
    }

    private static int routeWeight(List<TSPPlaceExample> route) {
        int routeWeight = 0;
        for (int i = 0; i < route.size() - 1; i++) {
            TSPPlaceExample place = route.get(i);
            TSPPlaceExample nextPlace = route.get(i + 1);
            routeWeight += place.getWeights().getOrDefault(nextPlace.getName(), 1000);
        }

        return routeWeight;
    }
}

package eric.myapplication.Misc;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Route implements Comparable<Route> {
    private List<Path> paths = new ArrayList<>();
    private List<String> places = new ArrayList<>();
    private int size = 0;
    private double timeWeight = 0;
    private double costWeight = 0;

    public void addPlace(String place, double time, double cost) {
        places.add(place);
        size++;

        timeWeight += time;
        costWeight += cost;
    }

    public void addPath(Path path) {
        paths.add(path);
    }

    @Override
    public Route clone() {
        Route routeClone;

        try {
            routeClone = (Route) super.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
            routeClone = new Route();
        }

        return routeClone;
    }

    @Override
    public int compareTo(@NonNull Route comparedRoute) {
        if (this.getTimeWeight() < comparedRoute.getTimeWeight()) {
            return -1;
        }

        if (this.getTimeWeight() > comparedRoute.getTimeWeight()) {
            return 1;
        }

        return 0;
    }

    @Override
    public String toString() {
        return places.toString() + "\n\nTotal time: " + timeWeight
                + "\nTotal cost: " + costWeight;
    }

    public List<String> getPlaces() {
        return places;
    }

    public List<Path> getPaths() {
        return paths;
    }

    public String getLast() {
        return places.get(size - 1);
    }

    public double getTimeWeight() {
        return timeWeight;
    }

    public double getCostWeight() {
        return costWeight;
    }
}

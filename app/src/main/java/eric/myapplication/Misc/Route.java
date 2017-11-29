package eric.myapplication.Misc;


import android.support.annotation.NonNull;

import java.util.List;

public class Route implements Comparable<Route> {
    private List<String> places;
    private List<Path> paths;
    private double timeWeight;
    private double costWeight;

    public Route(List<String> places, double totalTime, double totalCost) {
        this.places = places;
        this.timeWeight = totalTime;
        this.costWeight = totalCost;
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

    public void setPaths(List<Path> paths) {
        this.paths = paths;
    }

    public double getTimeWeight() {
        return timeWeight;
    }

    public double getCostWeight() {
        return costWeight;
    }
}

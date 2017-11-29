package eric.myapplication.Misc;


import android.support.annotation.NonNull;

import java.util.List;

public class Route implements Comparable<Route> {
    private List<String> places;
    private int timeWeight;
    private int costWeight;

    public Route(List<String> places, int timeWeight, int costWeight) {
        this.places = places;
        this.timeWeight = timeWeight;
        this.costWeight = costWeight;
    }

    public List<String> getPlaces() {
        return places;
    }

    public int getTimeWeight() {
        return timeWeight;
    }

    public int getCostWeight() {
        return costWeight;
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
}

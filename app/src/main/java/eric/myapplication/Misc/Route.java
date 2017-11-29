package eric.myapplication.Misc;


import android.support.annotation.NonNull;

import java.util.List;

public class Route implements Comparable<Route> {
    private List<String> places;
    private int totalWeight;

    public Route(List<String> places, int totalWeight) {
        this.places = places;
        this.totalWeight = totalWeight;
    }

    public List<String> getPlaces() {
        return places;
    }

    public int getWeight() {
        return totalWeight;
    }

    @Override
    public int compareTo(@NonNull Route comparedRoute) {
        if (this.getWeight() < comparedRoute.getWeight()) {
            return -1;
        }

        if (this.getWeight() > comparedRoute.getWeight()) {
            return 1;
        }

        return 0;
    }

    @Override
    public String toString() {
        return places.toString() + "\n\nTotal Weight: " + totalWeight;
    }
}

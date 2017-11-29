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

    public void addTimeWeight(double amount) {
        if (amount > 0) {
            timeWeight += amount;
        }
    }

    public void reduceCostWeight(double amount) {
        if (amount > 0) {
            costWeight -= amount;
        }
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
        StringBuilder outputBld = new StringBuilder();

        for (Path path : paths) {
            outputBld.append(path.toString());
            outputBld.append("\n");
        }

        outputBld.append("\n\n");
        outputBld.append("Total time: " + timeWeight);
        outputBld.append("\n");
        outputBld.append("Total cost: " + Math.round(costWeight * 1000.0) / 1000.0);

        return outputBld.toString();
    }

    public List<String> getPlaces() {
        return places;
    }

    public List<Path> getPaths() {
        return paths;
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

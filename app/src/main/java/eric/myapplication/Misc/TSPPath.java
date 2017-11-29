package eric.myapplication.Misc;


import android.support.annotation.NonNull;

public class TSPPath implements Comparable<TSPPath>{
    private String from;
    private String to;
    private String altTransportMode;
    private String transportMode = "TAXI";

    private double taxiTime;
    private double taxiCost;
    private double altTime;
    private double altCost;
    private double timeIncreasePerCostSaving;

    public TSPPath(String from, String to, double taxiTime, double taxiCost , double altTime, double altCost,
                   String altTransportMode, double timeIncreasePerCostSaving) {
        this.from = from;
        this.to = to;
        this.altTransportMode = altTransportMode;

        this.taxiTime = taxiTime;
        this.taxiCost = taxiCost;
        this.altTime = altTime;
        this.altCost = altCost;
        this.timeIncreasePerCostSaving = timeIncreasePerCostSaving;
    }

    public void setToAltTransportMode() {
        transportMode = altTransportMode;
    }

    @Override
    public String toString() {
        double time = taxiTime;
        double cost = taxiCost;

        if (!transportMode.equals("TAXI")) {
            time = altTime;
            cost = altCost;
        }

        return from + " => " + to + ", Time: " + time + " Cost: " + cost + " Mode: " + transportMode;
    }

    @Override
    public int compareTo(@NonNull TSPPath comparedTSPPath) {
        if (this.getTimeIncreasePerCostSaving() < comparedTSPPath.getTimeIncreasePerCostSaving()) {
            return -1;
        }

        if (this.getTimeIncreasePerCostSaving() > comparedTSPPath.getTimeIncreasePerCostSaving()) {
            return 1;
        }

        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass())
            return false;

        TSPPath comparedTSPPath = (TSPPath) obj;
        if (!comparedTSPPath.getTransportMode().equals("TAXI"))
            return false;

        if (!comparedTSPPath.getFrom().equals(this.getFrom()))
            return false;

        if (!comparedTSPPath.getTo().equals(this.getTo()))
            return false;

        return true;
    }

    public String getTransportMode() {
        return transportMode;
    }

    public String getAltTransportMode() {
        return altTransportMode;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public double getTaxiTime() {
        return taxiTime;
    }

    public double getTaxiCost() {
        return taxiCost;
    }

    public double getAltTime() {
        return altTime;
    }

    public double getAltCost() {
        return altCost;
    }

    public double getTimeIncreasePerCostSaving() {
        return timeIncreasePerCostSaving;
    }
}

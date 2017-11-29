package eric.myapplication.Misc;


import android.support.annotation.NonNull;

public class Path implements Comparable<Path>{
    private String from;
    private String to;
    private String transportMode;
    private String altTransportMode;
    private boolean setToAltTransportMode;
    private double altTime;
    private double altCost;
    private double timeIncreasePerCostSaving;

    public Path(String from, String to, double altTime, double altCost,
                String altTransportMode, double timeIncreasePerCostSaving) {
        this.from = from;
        this.to = to;
        this.transportMode = "TAXI";
        this.altTransportMode = altTransportMode;
        this.altTime = altTime;
        this.altCost = altCost;
        this.timeIncreasePerCostSaving = timeIncreasePerCostSaving;
    }

    public void setToAltTransportMode(boolean b) {
        transportMode = b ? altTransportMode : "TAXI";
    }

    public String getTransportMode() {
        return transportMode;
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

    @Override
    public int compareTo(@NonNull Path path) {
        return 0;
    }
}

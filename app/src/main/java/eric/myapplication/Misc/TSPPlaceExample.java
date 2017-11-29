package eric.myapplication.Misc;

import java.util.HashMap;
import java.util.Map;

public class TSPPlaceExample {
    private String name;
    private Map<String, Integer> weights = new HashMap<>();

    public TSPPlaceExample(String name) {
        this.name = name;
    }

    public void addWeight(String place, Integer weight) {
        weights.put(place, weight);
    }

    public String getName() {
        return name;
    }

    public Map<String, Integer> getWeights() {
        return weights;
    }

    @Override
    public String toString() {
        return getName();
    }
}

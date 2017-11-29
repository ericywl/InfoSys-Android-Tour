package eric.myapplication.Algorithm;

import java.util.HashMap;
import java.util.Map;

public class TSPPlace {
    private String name;
    private Map<String, Integer> weights = new HashMap<>();

    public TSPPlace(String name) {
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

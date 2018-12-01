package com.soerboe.gjeter;

/**
 * Represents a predator observation.
 */
public class PredatorObservation extends Observation {
    private String type;
    private int count;

    public PredatorObservation(Observation o) {
        super(o);
        this.setObservationType(Constants.OBS_TYPE.PREDATOR);
    }

    public String getType() {
        return type;
    }

    public int getCount() {
        return count;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

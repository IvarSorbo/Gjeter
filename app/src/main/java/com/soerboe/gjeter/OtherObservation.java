package com.soerboe.gjeter;


public class OtherObservation extends Observation {
    private String type;
    private int count;

    public OtherObservation(Observation o) {
        super(o);
        this.setObservationType(Constants.OBS_TYPE.OTHER);
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

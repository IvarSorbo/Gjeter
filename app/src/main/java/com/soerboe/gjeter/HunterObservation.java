package com.soerboe.gjeter;

public class HunterObservation extends Observation {
    private int hunterCount;
    private int dogCount;

    public HunterObservation(Observation o) {
        super(o);
        this.setObservationType(Constants.OBS_TYPE.HUNTER);
    }

    public int getHunterCount() {
        return hunterCount;
    }

    public int getDogCount() {
        return dogCount;
    }

    public void setHunterCount(int hunterCount) {
        this.hunterCount = hunterCount;
    }

    public void setDogCount(int dogCount) {
        this.dogCount = dogCount;
    }
}

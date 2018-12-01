package com.soerboe.gjeter;

import org.osmdroid.util.GeoPoint;

import java.util.Date;

/**
 * A Sheep herd observation.
 */
public class SheepHerdObservation extends Observation {
    // A count for each of the different colored sheep
    private int whiteSheepCount;
    private int blackSheepCount;
    //private int brownSheepCount;
    private int otherSheepCount;

    public SheepHerdObservation(GeoPoint obsPosition, GeoPoint myPosition, Date time) {
        super(obsPosition, myPosition, time);
        this.setObservationType(Constants.OBS_TYPE.SHEEP_HERD);
    }

    public SheepHerdObservation(SheepHerdObservation sheepHerdObservation){
        super(sheepHerdObservation.getObsPosition(), sheepHerdObservation.getMyPosition(), sheepHerdObservation.getTime());
        this.whiteSheepCount = sheepHerdObservation.getWhiteSheepCount();
        this.blackSheepCount = sheepHerdObservation.getBlackSheepCount();
        //this.brownSheepCount = sheepHerdObservation.brownSheepCount;
        this.otherSheepCount = sheepHerdObservation.getOtherSheepCount();
        this.setObservationType(Constants.OBS_TYPE.SHEEP_HERD);
    }

    public SheepHerdObservation(Observation o){
        super(o);
        this.setObservationType(Constants.OBS_TYPE.SHEEP_HERD);
    }

    public void setWhiteSheepCount(int whiteSheepCount) {
        this.whiteSheepCount = whiteSheepCount;
    }

    public void setBlackSheepCount(int blackSheepCount) {
        this.blackSheepCount = blackSheepCount;
    }
    /*
    public void setBrownSheepCount(int brownSheepCount) {
        this.brownSheepCount = brownSheepCount;
        updateCount();
    }*/

    public void setOtherSheepCount(int otherSheepCount) {
        this.otherSheepCount = otherSheepCount;
    }

    public int getWhiteSheepCount() {
        return whiteSheepCount;
    }

    public int getBlackSheepCount() {
        return blackSheepCount;
    }
    /*
    public int getBrownSheepCount() {
        return brownSheepCount;
    }*/

    public int getOtherSheepCount() {
        return otherSheepCount;
    }
}

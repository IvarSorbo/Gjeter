package com.soerboe.gjeter;

import org.osmdroid.util.GeoPoint;

public class SheepHerdObservation extends Observation {
    // A count for each of the different colored sheep
    private int whiteSheepCount;
    private int blackSheepCount;
    private int brownSheepCount;
    private int otherSheepCount;

    public SheepHerdObservation(Waypoint waypoint, GeoPoint myPosition) {
        super(waypoint, myPosition);
        this.setObservationType("Saueflokk");
    }

    // Setters and getters
    private void updateCount(){
        this.setTotalCount(whiteSheepCount + blackSheepCount + brownSheepCount + otherSheepCount);
    }

    public void setWhiteSheepCount(int whiteSheepCount) {
        this.whiteSheepCount = whiteSheepCount;
        updateCount();
    }

    public void setBlackSheepCount(int blackSheepCount) {
        this.blackSheepCount = blackSheepCount;
        updateCount();
    }

    public void setBrownSheepCount(int brownSheepCount) {
        this.brownSheepCount = brownSheepCount;
        updateCount();
    }

    public void setOtherSheepCount(int otherSheepCount) {
        this.otherSheepCount = otherSheepCount;
        updateCount();
    }

    public int getWhiteSheepCount() {
        return whiteSheepCount;
    }

    public int getBlackSheepCount() {
        return blackSheepCount;
    }

    public int getBrownSheepCount() {
        return brownSheepCount;
    }

    public int getOtherSheepCount() {
        return otherSheepCount;
    }

    // Return a JSON representation of the object
    public String toJSON(){
        //TODO
        return "{\"TODO\":\"this\"}";
    }
}

package com.soerboe.gjeter;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.util.Date;

public class SheepHerdObservation extends Observation {
    // A count for each of the different colored sheep
    private int whiteSheepCount;
    private int blackSheepCount;
    //private int brownSheepCount;
    private int otherSheepCount;

    public SheepHerdObservation(GeoPoint obsPosition, GeoPoint myPosition, Date time) {
        super(obsPosition, myPosition, time);
        this.setObservationType("Saueflokk");//TODO: this should not be hardcoded
    }

    public SheepHerdObservation(Observation o){
        super(o);
        this.setObservationType("Saueflokk");//TODO: this should not be hardcoded
    }

    // Setters and getters
    private void updateCount(){
        this.setTotalCount(whiteSheepCount + blackSheepCount /*+ brownSheepCount*/ + otherSheepCount);
    }

    public void setWhiteSheepCount(int whiteSheepCount) {
        this.whiteSheepCount = whiteSheepCount;
        updateCount();
    }

    public void setBlackSheepCount(int blackSheepCount) {
        this.blackSheepCount = blackSheepCount;
        updateCount();
    }
    /*
    public void setBrownSheepCount(int brownSheepCount) {
        this.brownSheepCount = brownSheepCount;
        updateCount();
    }*/

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
    /*
    public int getBrownSheepCount() {
        return brownSheepCount;
    }*/

    public int getOtherSheepCount() {
        return otherSheepCount;
    }

    // Return a JSON representation of the object
    public String toJSON(){
        JSONObject json = new JSONObject();
        try {
            json.put("obsPosition", this.getObsPosition());
            json.put("myPosition", this.getMyPosition());
            json.put("totalCount", this.getTotalCount());
            json.put("time", this.getTime().getTime());
            json.put("observationType", this.getObservationType());
            json.put("whiteSheepCount",this.whiteSheepCount);
            json.put("blackSheepCount",this.blackSheepCount);
            //json.put("brownSheepCount",this.brownSheepCount);
            json.put("otherSheepCount",this.otherSheepCount);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}

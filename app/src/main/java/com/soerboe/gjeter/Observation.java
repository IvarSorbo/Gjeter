package com.soerboe.gjeter;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;

import java.util.Date;

/**
 * This is the superclass in which all the specific observation classes inherit from
 */
public class Observation {
    private String observationType;
    private int totalCount;
    private GeoPoint obsPosition;
    private GeoPoint myPosition;
    private Date time;


    public Observation(final GeoPoint obsPosition, final GeoPoint myPosition, final Date time){
        this.obsPosition = obsPosition;
        this.myPosition = myPosition;
        this.time = time;
    }

    public Observation(final Observation o){
        this.obsPosition = o.obsPosition;
        this.myPosition = o.myPosition;
        this.time = o.time;
    }

    // Getters and setters:
    public void setObservationType(String observationType) {
        this.observationType = observationType;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public Date getTime(){ return time; }

    public String getObservationType() {
        return observationType;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public GeoPoint getObsPosition() {
        return obsPosition;
    }

    public GeoPoint getMyPosition() {
        return myPosition;
    }

    /**
     * Return the distance between obsPosition and myPosition
     */
    public double getDistance(){
        return obsPosition.distanceToAsDouble(myPosition);
    }

    public String toJSON(){
        JSONObject json = new JSONObject();
        try {
            json.put("obsPosition", this.obsPosition);
            json.put("myPosition", this.myPosition);
            json.put("totalCount", this.totalCount);
            json.put("time", this.time.getTime());
            json.put("observationType", this.observationType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}


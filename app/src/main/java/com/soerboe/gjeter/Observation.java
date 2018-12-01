package com.soerboe.gjeter;

import org.osmdroid.util.GeoPoint;

import java.util.Date;

/**
 * This is the superclass in which all the specific observation classes inherit from
 */
public class Observation {
    private Constants.OBS_TYPE observationType;
    private GeoPoint obsPosition;
    private GeoPoint myPosition;
    private Date time;
    private String notes;

    public Observation(final GeoPoint obsPosition, final GeoPoint myPosition, final Date time){
        this.obsPosition = obsPosition;
        this.myPosition = myPosition;
        this.time = time;
        this.observationType = Constants.OBS_TYPE.DEFAULT_OBSERVATION;
        this.notes = "";
    }

    public Observation(final Observation o){
        this.obsPosition = o.obsPosition;
        this.myPosition = o.myPosition;
        this.time = o.time;
    }

    // Getters and setters:
    public void setObservationType(Constants.OBS_TYPE observationType) {
        this.observationType = observationType;
    }

    public Date getTime(){ return time; }

    public Constants.OBS_TYPE getObservationType() {
        return observationType;
    }

    public GeoPoint getObsPosition() {
        return obsPosition;
    }

    public GeoPoint getMyPosition() {
        return myPosition;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Return the distance between obsPosition and myPosition
     */
    public double getDistance(){
        return obsPosition.distanceToAsDouble(myPosition);
    }
}
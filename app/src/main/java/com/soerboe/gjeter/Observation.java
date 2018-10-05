package com.soerboe.gjeter;

import org.osmdroid.util.GeoPoint;

/**
 * This is the superclass in which all the specific observation classes inherit from
 */
public class Observation {
    private String observationType;
    private int totalCount;
    private Waypoint waypoint;
    private GeoPoint myPosition;

    public Observation(Waypoint waypoint, GeoPoint myPosition){
        this.waypoint = waypoint;
        this.myPosition = myPosition;
    }

    // Getters and setters:
    public void setObservationType(String observationType) {
        this.observationType = observationType;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public String getObservationType() {
        return observationType;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public Waypoint getWaypoint() {
        return waypoint;
    }

    public GeoPoint getMyPosition() {
        return myPosition;
    }

    //String toJSON();
}


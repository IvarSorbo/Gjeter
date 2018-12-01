package com.soerboe.gjeter;

import java.util.ArrayList;
import java.util.Date;

/**
 * Stores all the information gathered from a trip.
 * This info will be saved to permanent memory when the app closes
 */
public class Trip {
    private ArrayList<Waypoint> track;
    private ArrayList<String> observations;
    private Date startTime;
    private Date endTime;

    public Trip(){
        startTime = new Date(System.currentTimeMillis());
        track = new ArrayList<>();
        observations = new ArrayList<>();
    }

    public String getFilename() {
        return "trip_" + startTime.getTime();
    }

    public void addWaypoint(Waypoint newWaypoint){
        track.add(newWaypoint);
    }

    public Waypoint getCurrentGeoPoint(){
        if(track.size() > 0){
            return track.get(track.size()-1);
        }
        return null;
    }

    public void addObservation(String JSONrepresentation){
        observations.add(JSONrepresentation);
    }

    public void finish(){
        endTime = new Date(System.currentTimeMillis());
    }
}

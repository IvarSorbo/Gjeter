package com.soerboe.gjeter;

import java.util.ArrayList;
import java.util.Date;

/**
 * Stores all the information gathered from a trip that is to be stored to permanent memory
 */
public class Trip {
    private ArrayList<Waypoint> track;
    private ArrayList<String> observations; // I could store them as (id, String) for easier update/delete
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

    /*
    * NOTE:
    * For some reason, calling gson.toJson(this) causes the observation-activity's finish() method
    * to throw an android.os.TransactionTooLargeException. Therefore I have to encode this object
    * to JSON directly from the main activity instead of just having a toJSON method in this class.
    * */
}

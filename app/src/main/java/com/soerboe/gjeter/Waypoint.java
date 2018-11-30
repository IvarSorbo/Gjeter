package com.soerboe.gjeter;

import org.osmdroid.util.GeoPoint;

import java.util.Date;

/**
 * A GeoPoint and a timestamp
 */
public class Waypoint extends GeoPoint{
    private Date time;

    public Waypoint(GeoPoint geoPoint, Date time){
        super(geoPoint);
        this.time = time;
    }

    public Date getTime(){
        return time;
    }
}

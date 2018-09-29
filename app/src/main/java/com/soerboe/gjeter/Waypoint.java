package com.soerboe.gjeter;

import android.location.Location;

import org.osmdroid.util.GeoPoint;

import java.util.Date;

/**
 * A GeoPoint and a timestamp
 */
public class Waypoint extends GeoPoint{
    private Date time;

    public Waypoint(Location location, Date time){
        //this.geoPoint = geopoint;
        super(location);
        this.time = time;
    }

    public Date getTime(){
        return time;
    }

    public String toGeoJSONPoint(){
        /* Example:
        {"type": "Point", "coordinates": [10.401765, 63.419780]}*/
        return "{\"type\":\"Point\", \"coordinates\":[" + super.getLongitude() + "," + super.getLatitude() + "]}";
    }

    public String toGeoJSONFeature(){
        /* Example:
        { "type": "Feature",
        "geometry": {"type": "Point", "coordinates": [10.401765, 63.419780]},
        "properties": {"time": 1538164297}
        }*/
        return "{\"type\":\"Feature\", \"geometry\":" + this.toGeoJSONPoint()
                + ",\"properties\":{\"time\":" + time.getTime() + "}}";
    }

}

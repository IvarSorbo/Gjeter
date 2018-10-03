package com.soerboe.gjeter;

/**
 * TODO: make this into an interface that the specific observation-classes can inherit from.
 * Need:
 * - A type field
 * - A total number field
 * - A toString field which returns all the info in a neat format
 * - A (double) distance which should be sent along if the user switches between observation-type
 *
 * - Also need to define some constants (this should be defined somewhere else though)
 * -- LONG_DISTANCE
 * -- The different types
 */
public class Observation {
    private Waypoint waypoint;
    private int count;

    public Observation(Waypoint waypoint, int count){
        this.waypoint = waypoint;
        this.count = count;
    }

}


package com.soerboe.gjeter;

/**
 * Either make this an interface for observation-classes
 * or make this the "superclass" of more specific observatino-classes
 * e.g., SheepObservation, PredatorObservation, HunterObservation, ...
 */
public class Observation {
    private Waypoint waypoint;
    private int count;

    public Observation(Waypoint waypoint, int count){
        this.waypoint = waypoint;
        this.count = count;
    }

}


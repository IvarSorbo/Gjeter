package com.soerboe.gjeter;

/**
 * Class for storing global constants
 */
public class Constants {
    public static final String MY_LAT = "my_lat";
    public static final String MY_LNG = "my_lng";
    public static final String OBS_LAT = "obs_lat";
    public static final String OBS_LNG = "obs_lng";

    public static final int LONG_DISTANCE = 200;

    public enum OBS_TYPE {DEFAULT_OBSERVATION, SHEEP_HERD, SHEEP_HERD_DETAILED, DEAD_SHEEP, PREDATOR, HUNTER, OTHER}
}

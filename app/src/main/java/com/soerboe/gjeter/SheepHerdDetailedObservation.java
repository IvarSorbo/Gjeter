package com.soerboe.gjeter;

import android.support.annotation.NonNull;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Date;

public class SheepHerdDetailedObservation extends SheepHerdObservation {
    private int lambCount;
    private int lambOriginalCount;
    private ArrayList<Tuple> owners;

    public SheepHerdDetailedObservation(GeoPoint obsPosition, GeoPoint myPosition, Date time) {
        super(obsPosition, myPosition, time);
    }

    public SheepHerdDetailedObservation(Observation o) {
        super(o);
    }

    public int getLambCount() {
        return lambCount;
    }

    public int getLambOriginalCount() {
        return lambOriginalCount;
    }

    public ArrayList<Tuple> getOwners() {
        return owners;
    }

    public void setLambCount(int lambCount) {
        this.lambCount = lambCount;
    }

    public void setLambOriginalCount(int lambOriginalCount) {
        this.lambOriginalCount = lambOriginalCount;
    }

    public void addOwner(Tuple owner) {
        this.owners.add(owner);
    }
    // TODO: need more methods to manipulate the arraylist of owners


    @Override
    public String toJSON() {
        return "placeholder for detailed sheep herd observation object";//TODO create the JSON object
    }

    /**
     * A tuple of ear tag color - count
     */
    public class Tuple{
        private String earTagColor;
        private int count;
        public Tuple(String earTagColor, int count){
            this.earTagColor = earTagColor;
            this.count = count;
        }

        public String getEarTagColor() {
            return earTagColor;
        }

        public int getCount() {
            return count;
        }

        public void setEarTagColor(String earTagColor) {
            this.earTagColor = earTagColor;
        }

        public void setCount(int count) {
            this.count = count;
        }

        @NonNull
        @Override
        public String toString() {
            return this.toJSON();
        }

        public String toJSON(){
            // Ex.: {"gul":8}
            return "{\""+ earTagColor +"\":" + this.count + "}";
        }
    }
}

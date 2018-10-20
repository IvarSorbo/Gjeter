package com.soerboe.gjeter;

import com.google.gson.Gson;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Date;

public class SheepHerdDetailedObservation extends SheepHerdObservation {
    private int lambCount;
    private int lambOriginalCount;
    private ArrayList<EarTag> earTags = new ArrayList<>();

    public SheepHerdDetailedObservation(GeoPoint obsPosition, GeoPoint myPosition, Date time) {
        super(obsPosition, myPosition, time);
        setObservationType(Constants.OBS_TYPE.SHEEP_HERD_DETAILED);
    }

    public SheepHerdDetailedObservation(SheepHerdObservation sheepHerdObservation){
        super(sheepHerdObservation);
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

    public void setLambCount(int lambCount) {
        this.lambCount = lambCount;
    }

    public void setLambOriginalCount(int lambOriginalCount) {
        this.lambOriginalCount = lambOriginalCount;
    }

    public void updateEarTags(EarTag newEarTag){
        // Check if the earTag is already in the list, if it is, update it
        for(EarTag et: earTags){
            if(et.getColor().equals(newEarTag.getColor())){
                et.setCount(newEarTag.getCount());
                return;
            }
        }
        earTags.add(newEarTag);
    }

    public void removeEarTag(EarTag inputEarTag){
        for(EarTag et: earTags){
            if(et.getColor().equals(inputEarTag.getColor())){
                earTags.remove(et);
                return;
            }
        }
    }

    @Override
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

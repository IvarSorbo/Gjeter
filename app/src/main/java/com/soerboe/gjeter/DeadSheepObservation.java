package com.soerboe.gjeter;

import android.net.Uri;

import java.util.ArrayList;

public class DeadSheepObservation extends Observation {
    private String owner;
    private int number;
    private ArrayList<Uri> photos = new ArrayList<>();

    public DeadSheepObservation(Observation o) {
        super(o);
        super.setObservationType(Constants.OBS_TYPE.DEAD_SHEEP);
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void addPhoto(Uri uri) {
        photos.add(uri);
    }

    public ArrayList<Uri> getPhotos() {
        return photos;
    }
}

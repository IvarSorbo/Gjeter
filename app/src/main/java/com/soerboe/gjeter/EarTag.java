package com.soerboe.gjeter;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class EarTag {
    private String color;
    private int count;

    public EarTag(String color, int count) {
        this.color = color;
        this.count = count;
    }

    public EarTag(){
        this.color = "";
        this.count = 0;
    }

    public String getColor() {
        return color;
    }

    public int getCount() {
        return count;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @NonNull
    @Override
    public String toString(){
        return this.toJSON();
    }

    public String toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("color", this.color);
            json.put("count", this.count);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}


package com.soerboe.gjeter;

/**
 * Represents a sheep's ear tag.
 */
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
}


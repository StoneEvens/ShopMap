package com.shopmap.shopmap.map;

public class MapElement {
    protected int row, col;
    protected String name;
    protected int[] distanceMultiplier;

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String getName() {
        return name;
    }

    public int[] getDistanceMultiplier() {
        return distanceMultiplier;
    }

    public void setDistanceMultiplier(int[] values) {
        this.distanceMultiplier = values;
    }
}

package com.shopmap.shopmap.map;

public class Shelf extends MapElement{
    private Walkway nearestWkwy;
    private int[] wkwyLocation;

    public Shelf() {
        this.row = -1;
        this.col = -1;
        this.name = "DNE";
        this.nearestWkwy = new Walkway();
        wkwyLocation = new int[2];
    }

    public Shelf(String name, int row, int col, Walkway wkwy, int[] wkwyLocation) {
        this.row = row;
        this.col = col;
        this.name = name;
        this.nearestWkwy = wkwy;
        this.wkwyLocation = wkwyLocation;
    }

    public void setNearestWkwy(Walkway wkwy, int[] wkwyLocation) {
        this.nearestWkwy = wkwy;
        this.wkwyLocation = wkwyLocation;
    }

    public Walkway getWkwy() {
        return nearestWkwy;
    }

    public int[] getWkwyLocation() {
        return wkwyLocation;
    }
}

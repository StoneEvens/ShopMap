package com.shopmap.shopmap.mapanddistance;

public class Walkway extends MapElement{
    private Aisle inAisle;

    public Walkway() {
        this.row = -1;
        this.col = -1;
        this.name = "DNE";
        this.inAisle = new Aisle();
    }

    public Walkway(String name, int row, int col, Aisle aisle) {
        this.row = row;
        this.col = col;
        this.name = name;
        this.inAisle = aisle;
    }

    public void setAisle(Aisle aisle) {
        this.inAisle = aisle;
    }

    public Aisle getAisle() {
        return inAisle;
    }
}
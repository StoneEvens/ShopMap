package com.shopmap.shopmap.mapanddistance;

public class Intersection extends MapElement{
    public Intersection() {
        this.row = -1;
        this.col = -1;
        this.name = "DNE";
    }

    public Intersection(String name, int row, int col) {
        this.row = row;
        this.col = col;
        this.name = name;
    }
}

package com.shopmap.shopmap.map;

public class Aisle {
    int aisleRow, aisleCol;
    private Intersection itsc1, itsc2;

    public Aisle() {
        this.aisleRow = -1;
        this.aisleCol = -1;
        this.itsc1 = new Intersection();
        this.itsc2 = new Intersection();
    }

    public Aisle(int aisleRow, int aisleCol, Intersection itsc1, Intersection itsc2) {
        this.aisleRow = aisleRow;
        this.aisleCol = aisleCol;
        this.itsc1 = itsc1;
        this.itsc2 = itsc2;
    }

    public int getAisleRow() {
        return aisleRow;
    }

    public int getAisleCol() {
        return aisleCol;
    }

    public Intersection getItsc1() {
        return itsc1;
    }

    public Intersection getItsc2() {
        return itsc2;
    }
}

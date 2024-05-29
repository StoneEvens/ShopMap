package com.shopmap.shopmap;

import com.shopmap.shopmap.mapanddistance.Intersection;
import com.shopmap.shopmap.mapanddistance.MapElement;
import com.shopmap.shopmap.mapanddistance.Shelf;

import java.util.ArrayList;

public class Route {
    private final ArrayList<Shelf> targetShelves;
    private final Intersection entrance, exit;
    private final boolean[][] isRoute;

    public Route(int totalRow, int totalCol, Intersection entrance, Intersection exit, ArrayList<Shelf> targetShelves) {
        this.entrance = entrance;
        this.exit = exit;
        this.targetShelves = targetShelves;
        isRoute = new boolean[totalRow][totalCol];

        for (Shelf s: targetShelves) {
            isRoute[s.getWkwy().getRow()][s.getWkwy().getCol()] = true;
        }

        calculateRoute();
    }

    public void calculateRoute() {
        for (int i = 0; i < targetShelves.size() - 1; i++) {
            Shelf start = targetShelves.get(i);
            Shelf end = targetShelves.get(i + 1);

            findPath(start, end);
        }
    }

    private void findPath(Shelf start, Shelf end) {
        ArrayList<Intersection> startItscs = new ArrayList<>();
        startItscs.add(start.getWkwy().getAisle().getItsc1());
        startItscs.add(start.getWkwy().getAisle().getItsc2());

        ArrayList<Intersection> endItscs = new ArrayList<>();
        endItscs.add(end.getWkwy().getAisle().getItsc1());
        endItscs.add(end.getWkwy().getAisle().getItsc2());

        int tempD = 1000;
        Intersection[] closestItscs = new Intersection[2];

        for (Intersection sI: startItscs) {
            for (Intersection eI: endItscs) {
                int d = Math.abs(sI.getRow() - eI.getRow()) + Math.abs(sI.getCol() - eI.getCol());

                if (!start.getWkwy().getAisle().equals(end.getWkwy().getAisle())) {
                    d += Math.abs(sI.getRow() - start.getRow()) * start.getWkwy().getDistanceMultiplier()[0] + Math.abs(sI.getCol() - start.getCol()) * start.getWkwy().getDistanceMultiplier()[1];
                    d += Math.abs(end.getRow() - eI.getRow()) * end.getWkwy().getDistanceMultiplier()[0] + Math.abs(end.getCol() - eI.getCol()) * end.getWkwy().getDistanceMultiplier()[1];
                } else {
                    d += Math.abs(start.getRow() - end.getRow()) * start.getWkwy().getDistanceMultiplier()[0] + Math.abs(start.getCol() - end.getCol()) * start.getWkwy().getDistanceMultiplier()[1];
                }

                if (d <= tempD) {
                    tempD = d;
                    closestItscs[0] = sI;
                    closestItscs[1] = eI;
                }
            }
        }

        setRoute(start.getWkwy(), closestItscs[0]);

        setRoute(closestItscs[0], closestItscs[1]);

        setRoute(closestItscs[1], end.getWkwy());
    }

    private void setRoute(MapElement start, MapElement end) {
        int startRow = start.getRow();
        int startCol = start.getCol();

        isRoute[startRow][startCol] = true;

        int rowSteps = start.getRow() - end.getRow();

        if (rowSteps > 0) {
            while (rowSteps > 0) {
                startRow --;
                rowSteps--;
                isRoute[startRow][startCol] = true;
            }
        } else if (rowSteps < 0) {
            while (rowSteps < 0) {
                startRow++;
                rowSteps++;
                isRoute[startRow][startCol] = true;
            }
        }

        int colSteps = start.getCol() - end.getCol();

        if (colSteps > 0) {
            while (colSteps > 0) {
                startCol--;
                colSteps--;
                isRoute[startRow][startCol] = true;
            }
        } else if (colSteps < 0) {
            while (colSteps < 0) {
                startCol++;
                colSteps++;
                isRoute[startRow][startCol] = true;
            }
        }
    }

    public boolean[][] getIsRoute() {
        return isRoute;
    }
}

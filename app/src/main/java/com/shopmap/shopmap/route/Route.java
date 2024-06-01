package com.shopmap.shopmap.route;

import com.shopmap.shopmap.map.Intersection;
import com.shopmap.shopmap.map.MapElement;
import com.shopmap.shopmap.map.Shelf;

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
        findPath(entrance, targetShelves.get(0));

        for (int i = 0; i < targetShelves.size() - 1; i++) {
            Shelf start = targetShelves.get(i);
            Shelf end = targetShelves.get(i + 1);

            findPath(start, end);
        }

        findPath(exit, targetShelves.get(targetShelves.size() - 1));
    }

    private void findPath(Intersection itsc, Shelf shelf) {
        ArrayList<Intersection> shelfIntersections = new ArrayList<>();
        shelfIntersections.add(shelf.getWkwy().getAisle().getItsc1());
        shelfIntersections.add(shelf.getWkwy().getAisle().getItsc2());

        int tempD = 1000;
        Intersection closestItsc = new Intersection();

        for (Intersection eI: shelfIntersections) {
            int d = Math.abs(itsc.getRow() - eI.getRow()) * shelf.getWkwy().getDistanceMultiplier()[0] + Math.abs(itsc.getCol() - eI.getCol()) * shelf.getWkwy().getDistanceMultiplier()[1];

            d += Math.abs(shelf.getRow() - eI.getRow()) * shelf.getWkwy().getDistanceMultiplier()[0] + Math.abs(shelf.getCol() - eI.getCol()) * shelf.getWkwy().getDistanceMultiplier()[1];

            if (d < tempD) {
                tempD = d;
                closestItsc = eI;
            }
        }

        setRoute(itsc, closestItsc);

        setRoute(closestItsc, shelf);
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
                int d = 0;

                if (!start.getWkwy().getAisle().equals(end.getWkwy().getAisle())) {
                    d += Math.abs(sI.getRow() - eI.getRow()) * start.getWkwy().getDistanceMultiplier()[0] + Math.abs(sI.getCol() - eI.getCol()) * end.getWkwy().getDistanceMultiplier()[1];
                }

                d += Math.abs(sI.getRow() - start.getRow()) * start.getWkwy().getDistanceMultiplier()[0] + Math.abs(sI.getCol() - start.getCol()) * start.getWkwy().getDistanceMultiplier()[1];
                d += Math.abs(end.getRow() - eI.getRow()) * end.getWkwy().getDistanceMultiplier()[0] + Math.abs(end.getCol() - eI.getCol()) * end.getWkwy().getDistanceMultiplier()[1];

                if (d < tempD) {
                    tempD = d;
                    closestItscs[0] = sI;
                    closestItscs[1] = eI;
                }
            }
        }

        if (!start.getWkwy().getAisle().equals(end.getWkwy().getAisle())) {
            setRoute(start.getWkwy(), closestItscs[0]);

            setRoute(closestItscs[0], closestItscs[1]);

            setRoute(closestItscs[1], end.getWkwy());
        } else {
            setRoute(start.getWkwy(), end.getWkwy());
        }
    }

    private void setRoute(MapElement start, MapElement end) {
        if (start instanceof Shelf) {
            start = ((Shelf) start).getWkwy();
        }

        if (end instanceof  Shelf) {
            end = ((Shelf) end).getWkwy();
        }

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

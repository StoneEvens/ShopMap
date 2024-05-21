package com.shopmap.shopmap;

import java.util.ArrayList;

public class Map {
    private ArrayList<Aisle> aisles;
    private String[] storeInfo;
    private ArrayList<Shelf> shelves;
    private MapElement[][] mapElements;
    private Client client;

    public Map(Client client) {
        this.client = client;
    }

    //This should be public void constructMap(String values);
    public void constructMap(String storeID) {
        aisles = new ArrayList<Aisle>();
        shelves = new ArrayList<Shelf>();
        //This should be storeInfo = ts.getStoreInfo(values);
        //storeInfo = ;

        getData(client.getStoreInfo(storeID));
    }

    //This should be public void getData(String values)
    private void getData(String[] info) {
        this.storeInfo = info[0].split("/ADD/");


        //Instantiate mapElements
        mapElements = new MapElement[Integer.parseInt(storeInfo[0])][Integer.parseInt(storeInfo[1])];

        //Always Create Intersection First
        //The itsc id need to changed after the sql is done
        int cnt = 0;

        String[] itscLoc = info[1].split("/ADD/");

        for (String s : itscLoc) {
            String[] pos = s.split("/,/");
            int posX = Integer.parseInt(pos[0]);
            int posY = Integer.parseInt(pos[1]);

            Intersection i = new Intersection("Itsc" + cnt, posX, posY);

            mapElements[posX][posY] = i;
            cnt++;
        }

        //Create Walkways
        String[] wkwyLoc = info[2].split("/ADD/");

        for (String s : wkwyLoc) {
            String[] pos = s.split("/,/");
            int posX = Integer.parseInt(pos[0]);
            int posY = Integer.parseInt(pos[1]);

            Walkway w = new Walkway("Wkwy ", posX, posY, new Aisle());

            mapElements[posX][posY] = w;
        }

        //Create Shelves
        int k = 0;

        String[] shelfLoc = info[3].split("/ADD/");

        //This should be for (int[] c: ts.getShelves(values))
        for (String str : shelfLoc) {
            String[] pos = str.split("/,/");
            int posX = Integer.parseInt(pos[0]);
            int posY = Integer.parseInt(pos[1]);
            int wkwyLocX = Integer.parseInt(pos[2]);
            int wkwyLocY = Integer.parseInt(pos[3]);

            Walkway w = (Walkway) mapElements[posX + wkwyLocX][posY + wkwyLocY];
            Shelf s = new Shelf("S" + k, posX, posY, w, new int[]{wkwyLocX, wkwyLocY});

            mapElements[posX][posY] = s;
            shelves.add(s);

            k++;
        }

        //Create Aisle
        int r = 0;
        int c = -1;

        for (MapElement[] li : mapElements) {
            c = -1;
            int indexA = -1;
            int indexB = -1;

            for (int i = 0; i < li.length; i++) {
                if (li[i] instanceof Intersection) {
                    indexA = indexB;
                    indexB = i;

                    if (indexA != -1) {
                        c++;

                        Aisle a = new Aisle(r, c, (Intersection) li[indexA], (Intersection) li[indexB]);
                        aisles.add(a);

                        for (int j = indexA + 1; j < indexB; j++) {
                            if (li[j] instanceof Walkway) {
                                ((Walkway) li[j]).setAisle(a);
                            }
                        }
                    }
                }
            }

            if (c != -1) {
                r++;
            }
        }

        //Create Distance Multiplier
        for (MapElement[] li : mapElements) {
            for (MapElement mE : li) {
                if (mE instanceof Walkway) {
                    if (((Walkway) mE).getAisle().getAisleRow() >= 0) {
                        mE.setDistanceMultiplier(new int[]{1, 2});
                    } else {
                        mE.setDistanceMultiplier(new int[]{1, 1});
                    }
                } else if (mE instanceof Intersection) {
                    mE.setDistanceMultiplier(new int[]{1, 1});
                }
            }
        }
    }

    //Very Important, has all the important information in it
    //The walkways in aisle (-1,-1) is in the "imaginary" aisle that holds everything that are not in the horizontal aisles (which are vertical aisles)
    //Those that are in the imaginary aisle are the perpendicular aisles to the shelves, so they would be VerticalWkwys in the app
    //And the rest of them would be the HorizonalWkwys in the app
    //Finding the shelves where the product is saved can also be achieved by using this list
    public MapElement[][] getMapElements() {
        return mapElements;
    }

    //The aisles are critical to finding the shortest distance between any to product, that is the shelf that holds the product, and is the walkway where the customer would stand in and grab the item
    public ArrayList<Aisle> getAisles() {
        return aisles;
    }

    //Here the customer would send a list of products first, then through SQL, we would know which shelf each product is on, then we can know the name of the shelf (Or ID, could be changed)
    //If designed properly, there should be no imaginary shelves returned, but handling the exception would be just fine.
    public MapElement getMapElement(String name) {
        MapElement mapElement = new MapElement();

        for (MapElement[] mE : mapElements) {
            for (MapElement e : mE) {
                if (e.getName().equals(name)) {
                    mapElement = e;
                }
            }
        }

        return mapElement;
    }

    public ArrayList<Shelf> getShelves() {
        return shelves;
    }

    public Intersection getStart() {
        return (Intersection) getMapElement(storeInfo[2]);
    }

    public Intersection getEnd() {
        return (Intersection) getMapElement(storeInfo[3]);
    }
}
package com.shopmap.shopmap;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.shopmap.shopmap.client.Client;
import com.shopmap.shopmap.client.ReconnectDialog;
import com.shopmap.shopmap.route.Route;
import com.shopmap.shopmap.map.Intersection;
import com.shopmap.shopmap.map.Map;
import com.shopmap.shopmap.map.MapElement;
import com.shopmap.shopmap.map.Shelf;
import com.shopmap.shopmap.map.Walkway;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    Client client;
    Map map;
    GridLayout gridLayout;
    TextView debugText;
    Button testButton, resetButton;
    ReconnectDialog dialog;
    ArrayList<String> targetShelves;
    int total_Row, total_Col;

    //Initiate client and map (server must be on)
    public MainActivity() throws InterruptedException {
        client = new Client();

        while (!client.getConnected()) {
            TimeUnit.MILLISECONDS.sleep(100);
        }

        map = new Map(client);
        dialog = new ReconnectDialog(client);
        map.constructMap("1");

        targetShelves = new ArrayList<>();

        check();
    }

    //This part constantly checks if the client is still connected to the server
    public void check() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    while (true) {
                        try {
                            if (!client.getConnected()) {
                                if (!dialog.isAdded()) {
                                    dialog.show(MainActivity.this.getFragmentManager(), "");
                                    //client.setIp(ipInput.getText().toString());
                                }
                            }
                            Log.i("Client Status", String.format("%s", client.getConnected()));
                            this.wait(10000 * dialog.getMultiplier());
                        } catch (InterruptedException e) {
                            Log.i("Debug", "Wait Interrupted");
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        total_Row = Integer.parseInt(map.getStoreInfo()[0]);
        total_Col = Integer.parseInt(map.getStoreInfo()[1]);

        debugText = findViewById(R.id.DebugText);

        gridLayout = findViewById(R.id.GridLayout);
        testButton = findViewById(R.id.pathButton);
        testButton.setOnClickListener(new CalculatePathListener());

        //Reset map when button is clicked
        resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetMap();
            }
        });

        gridLayout.setRowCount(total_Row);
        gridLayout.setColumnCount(total_Col);

        //Construct the map
        constructMap();
    }

    //The function that constructs the map
    private void constructMap() {
        int i = 0;
        for (MapElement[] li: map.getMapElements()) {
            int j = 0;
            for (MapElement mE: li) {
                View v = new View(getBaseContext());

                if (mE instanceof Intersection) {
                    v = getLayoutInflater().inflate(R.layout.intersection, null);
                } else if (mE instanceof Shelf) {
                    v = getLayoutInflater().inflate(R.layout.shelf, null);

                    CardView cv = v.findViewById(R.id.ShelfCard);

                    View finalV = v;
                    cv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.i("Debug", "Triggered");
                            //debugText.setText("Shelf Selected");
                            int index = gridLayout.indexOfChild(finalV);
                            int row = (int) Math.floor(index / total_Col);
                            int col = index%total_Col;
                            MapElement element = map.getMapElement(row, col);
                            client.getProduct(element.getName());

                            cv.setCardBackgroundColor(Color.RED);

                            //Change after this
                            if (targetShelves.size() < 20 && !targetShelves.contains(element.getName())) {
                                targetShelves.add(element.getName());
                            }
                        }
                    });
                } else if (mE instanceof Walkway) {
                    if (((Walkway) mE).getAisle().getAisleRow() != -1) {
                        v = getLayoutInflater().inflate(R.layout.horizontalwalkway, null);
                    } else {
                        v = getLayoutInflater().inflate(R.layout.vertcalwalkway, null);
                    }
                }

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;

                params.columnSpec = GridLayout.spec(j, 1, 1);
                params.rowSpec = GridLayout.spec(i, 1, 1);

                gridLayout.addView(v, params);

                j++;
            }

            i++;
        }
    }

    //The function that resets the map
    private void resetMap() {
        for (MapElement[] li: map.getMapElements()) {
            for (MapElement mE: li) {
                View view = gridLayout.getChildAt(mE.getRow() * total_Col + mE.getCol());
                CardView cv = view.findViewById(R.id.HorizontalWalkwayCard);

                if (cv != null) {
                    cv.setCardBackgroundColor(Color.GRAY);
                } else {
                    cv = view.findViewById(R.id.VerticalWalkwayCard);

                    if (cv != null) {
                        cv.setCardBackgroundColor(Color.GRAY);
                    } else {
                        cv = view.findViewById(R.id.IntersectionCard);

                        if (cv != null) {
                            cv.setCardBackgroundColor(Color.GRAY);
                        } else {
                            cv = view.findViewById(R.id.ShelfCard);
                            cv.setCardBackgroundColor(Color.WHITE);
                        }
                    }
                }
            }
        }

        targetShelves.clear();
    }

    private class CalculatePathListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (!targetShelves.isEmpty()) {
                ArrayList<Shelf> order = new ArrayList<Shelf>();

                try {
                    ArrayList<String> temp = client.getPath(targetShelves);

                    for (String s: temp) {
                        MapElement mE = map.getMapElement(s);
                        order.add((Shelf) mE);
                    }
                } catch (Exception e) {
                    if (!dialog.isAdded()) {
                        dialog.show(MainActivity.this.getFragmentManager(), "Reconnect");
                    }
                }

                resetMap();
                targetShelves.clear();

                String testOutput = "";
                for (Shelf s: order) {
                    testOutput += (s.getName() + "->");
                }
                for (Shelf s: order) {
                    int index = s.getRow() * total_Col + s.getCol();

                    View view = gridLayout.getChildAt(index);
                    CardView cv = view.findViewById(R.id.ShelfCard);
                    cv.setCardBackgroundColor(Color.RED);
                }
                //Send product list to back end to save it in the database
                Route route = new Route(total_Row, total_Col, map.getStart(), map.getEnd(), order);

                boolean[][] isRoute = route.getIsRoute();

                for (int i = 0; i < total_Row; i++) {
                    for (int j = 0; j < total_Col; j++) {
                        if (isRoute[i][j]) {
                            View view = gridLayout.getChildAt(i * total_Col + j);
                            CardView cv = view.findViewById(R.id.HorizontalWalkwayCard);

                            if (cv != null) {
                                cv.setCardBackgroundColor(Color.YELLOW);
                            } else {
                                cv = view.findViewById(R.id.VerticalWalkwayCard);

                                if (cv != null) {
                                    cv.setCardBackgroundColor(Color.YELLOW);
                                } else {
                                    cv = view.findViewById(R.id.IntersectionCard);

                                    if (cv != null) {
                                        cv.setCardBackgroundColor(Color.YELLOW);
                                    }
                                }
                            }
                        }
                    }
                }

                debugText.setText(testOutput);
            }
        }
    }
}
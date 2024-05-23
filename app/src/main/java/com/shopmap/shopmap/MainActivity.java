package com.shopmap.shopmap;

import androidx.annotation.ColorRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    Client client;
    Map map;
    GridLayout gridLayout;
    TextView debugText;
    Button testButton, resetButton;
    ReconnectDialog dialog;
    int total_Row, total_Col;

    public MainActivity() throws InterruptedException {
        client = new Client();
        map = new Map(client);

        TimeUnit.MILLISECONDS.sleep(100);

        dialog = new ReconnectDialog(client);
        map.constructMap("1");

        check();
    }

    public void check() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    while (true) {
                        try {
                            if (client.getConnected() == false) {
                                if (!dialog.isAdded()) {
                                    dialog.show(MainActivity.this.getFragmentManager(), "");
                                    //client.setIp(ipInput.getText().toString());
                                }
                            }
                            Log.i("Client Status", String.format("%s", client.getConnected()));
                            this.wait(10000 * dialog.getMultiplier());
                        } catch (InterruptedException e) {

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
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetMap();

                ArrayList<String> order = new ArrayList<String>();
                
                try {
                    order = client.getPath(new ArrayList<String>());
                } catch (Exception e) {
                    if (!dialog.isAdded()) {
                        dialog.show(MainActivity.this.getFragmentManager(), "Reconnect");
                    }
                }

                String testOutput = "";
                for (String s: order) {
                    testOutput += (s + "->");
                }
                for (String s: order) {
                    MapElement mE = map.getMapElement(s);
                    int index = mE.getRow() * total_Col + mE.getCol();

                    View view = gridLayout.getChildAt(index);
                    CardView cv = view.findViewById(R.id.ShelfCard);
                    cv.setCardBackgroundColor(Color.RED);
                }

                debugText.setText(testOutput);
            }
        });

        resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetMap();
            }
        });

        gridLayout.setRowCount(total_Row);
        gridLayout.setColumnCount(total_Col);
        /*
        int i = 0;

        for (ArrayList<String> li: client.getMap("1")) {
            int j = 0;

            for (String s: li) {
                View v = new View(getBaseContext());

                if (s.equals("Itsc")) {
                    v = getLayoutInflater().inflate(R.layout.intersection, null);
                } else if (s.equals("Shelf")) {
                    v = getLayoutInflater().inflate(R.layout.shelf, null);

                    CardView cv = v.findViewById(R.id.ShelfCard);

                    cv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("Debug", "Triggered");
                            debugText.setText("Shelf Selected");
                        }
                    });
                } else if (s.equals("HWalkway")) {
                    v = getLayoutInflater().inflate(R.layout.horizontalwalkway, null);
                } else if (s.equals("VWalkway")) {
                    v = getLayoutInflater().inflate(R.layout.vertcalwalkway, null);
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

         */


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

                    cv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("Debug", "Triggered");
                            debugText.setText("Shelf Selected");
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



        /*
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                View v = new View(getBaseContext());

                if (i%3 == 0 || j%3 == 0) {
                    if (i%3 == 0 && j%3 == 0) {
                        v = getLayoutInflater().inflate(R.layout.intersection, null);
                    } else if (i%3 == 0) {
                        v = getLayoutInflater().inflate(R.layout.vertcalwalkway, null);
                    } else {
                        v = getLayoutInflater().inflate(R.layout.horizontalwalkway, null);
                    }
                } else {
                    v = getLayoutInflater().inflate(R.layout.shelf, null);

                    CardView cv = v.findViewById(R.id.ShelfCard);

                    cv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("Debug", "Triggered");
                            debugText.setText("Shelf Selected");
                        }
                    });
                }

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;

                params.columnSpec = GridLayout.spec(i, 1, 1);
                params.rowSpec = GridLayout.spec(j, 1, 1);

                gridLayout.addView(v, params);
            }
        }
         */
    }

    private void resetMap() {
        for (Shelf s: map.getShelves()) {
            int index = s.getRow() * total_Col + s.getCol();

            View view = gridLayout.getChildAt(index);
            CardView cv = view.findViewById(R.id.ShelfCard);
            cv.setCardBackgroundColor(Color.WHITE);
        }
    }
}
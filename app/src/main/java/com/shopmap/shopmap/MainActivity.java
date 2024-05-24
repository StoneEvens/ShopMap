package com.shopmap.shopmap;

import androidx.annotation.ColorRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
    ArrayList<Shelf> targetShelves;
    int total_Row, total_Col;

    public MainActivity() throws InterruptedException {
        client = new Client();

        while (!client.getConnected()) {
            TimeUnit.MILLISECONDS.sleep(100);
        }

        map = new Map(client);
        dialog = new ReconnectDialog(client);
        map.constructMap("1");

        targetShelves = new ArrayList<Shelf>();

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

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

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

                            if (targetShelves.size() <= 20) {
                                targetShelves.add((Shelf) element);
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

    private void resetMap() {
        for (Shelf s: map.getShelves()) {
            int index = s.getRow() * total_Col + s.getCol();

            View view = gridLayout.getChildAt(index);
            CardView cv = view.findViewById(R.id.ShelfCard);
            cv.setCardBackgroundColor(Color.WHITE);
        }
    }
}
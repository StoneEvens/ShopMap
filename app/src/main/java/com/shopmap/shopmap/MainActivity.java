package com.shopmap.shopmap;

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

import java.util.LinkedList;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {
    GridLayout gridLayout;
    TextView debugText;
    int[] blockRowType1, blockRowType2;
    Queue<int[]> routeList;
    Queue<int[]> targetShelf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        debugText = findViewById(R.id.DebugText);

        gridLayout = findViewById(R.id.GridLayout);
        gridLayout.setRowCount(10);
        gridLayout.setColumnCount(7);

        //Client.getMapData();
        blockRowType1 = new int[]{0, 1, 1, 0, 1, 1, 0};
        blockRowType2 = new int[]{2, 3, 3, 2, 3, 3, 2};

        //Client.getTargetShelf();
        targetShelf = new LinkedList<int[]>();
        targetShelf.add(new int[] {1, 4});
        targetShelf.add(new int[] {1, 5});
        targetShelf.add(new int[] {2, 1});
        targetShelf.add(new int[] {2, 2});

        //Client.getRoute();
        routeList = new LinkedList<int[]>();
        routeList.add(new int[] {0, 0});
        routeList.add(new int[] {0, 3});
        routeList.add(new int[] {0, 4});
        routeList.add(new int[] {0, 5});
        routeList.add(new int[] {0, 6});
        routeList.add(new int[] {1, 0});
        routeList.add(new int[] {1, 3});
        routeList.add(new int[] {2, 0});
        routeList.add(new int[] {2, 3});
        routeList.add(new int[] {3, 0});
        routeList.add(new int[] {3, 1});
        routeList.add(new int[] {3, 2});
        routeList.add(new int[] {3, 3});

        /*
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                View v;

                if (i%2 == 0 && j%2 == 1) {
                    v = getLayoutInflater().inflate(R.layout.vertcalwalkway, null);
                } else if (i%2 == 1 && j%2 == 0) {
                    v = getLayoutInflater().inflate(R.layout.horizontalwalkway, null);
                } else if ((i == 1 || i == 3 || j == 1 || j == 3) && i%2 == 1) {
                    v = getLayoutInflater().inflate(R.layout.shelf, null);

                    CardView cv = v.findViewById(R.id.ShelfCard);

                    cv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("Debug", "Triggered");
                            debugText.setText("Shelf Selected");
                        }
                    });
                } else {
                    v = getLayoutInflater().inflate(R.layout.intersection, null);
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
        try {
            for (int i = 0; i < gridLayout.getRowCount(); i++) {
                for (int j = 0; j < gridLayout.getColumnCount(); j++) {
                    View v;

                    if (i%3 == 0) {
                        if (blockRowType1[j] == 0) {
                            v = getLayoutInflater().inflate(R.layout.intersection, null);

                            if (!routeList.isEmpty() && routeList.peek()[0] == i && routeList.peek()[1] == j) {
                                v.findViewById(R.id.IntersectionCard).setBackgroundColor(Color.RED);
                                routeList.poll();
                            }
                        } else {
                            v = getLayoutInflater().inflate(R.layout.horizontalwalkway, null);

                            if (!routeList.isEmpty() && routeList.peek()[0] == i && routeList.peek()[1] == j) {
                                v.findViewById(R.id.HorizontalWalkwayCard).setBackgroundColor(Color.RED);
                                routeList.poll();
                            }
                        }
                    } else {
                        if (blockRowType2[j] == 2) {
                            v = getLayoutInflater().inflate(R.layout.vertcalwalkway, null);

                            if (!routeList.isEmpty() && routeList.peek()[0] == i && routeList.peek()[1] == j) {
                                v.findViewById(R.id.VerticalWalkwayCard).setBackgroundColor(Color.RED);
                                routeList.poll();
                            }
                        } else {
                            v = getLayoutInflater().inflate(R.layout.shelf, null);

                            if (!targetShelf.isEmpty() && targetShelf.peek()[0] == i && targetShelf.peek()[1] == j) {
                                v.findViewById(R.id.ShelfCard).setBackgroundColor(Color.YELLOW);
                                targetShelf.poll();
                            }

                            int finalI = i;
                            int finalJ = j;
                            v.findViewById(R.id.ShelfCard).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.i("Debug", "Triggered");
                                    debugText.setText(String.format("Shelf %d%d Selected", finalI, finalJ));
                                }
                            });
                        }
                    }

                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    params.width = ViewGroup.LayoutParams.WRAP_CONTENT;

                    params.columnSpec = GridLayout.spec(j, 1, 1);
                    params.rowSpec = GridLayout.spec(i, 1, 1);

                    gridLayout.addView(v, params);
                }
            }
        } catch (NullPointerException e1) {
            debugText.setText(e1.toString());
        }
    }
}
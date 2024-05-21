package com.shopmap.shopmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    GridLayout gridLayout;
    TextView debugText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        debugText = findViewById(R.id.DebugText);

        gridLayout = findViewById(R.id.GridLayout);

        gridLayout.setRowCount(7);
        gridLayout.setColumnCount(7);

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
    }
}
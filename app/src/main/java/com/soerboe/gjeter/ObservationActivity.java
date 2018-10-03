package com.soerboe.gjeter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ObservationActivity extends AppCompatActivity {
    private Intent data = new Intent();
    private int LONG_DISTANCE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation);

        LONG_DISTANCE  = getResources().getInteger(R.integer.LONG_DISTANCE);

        // Getting the distance that was set in MainActivity
        Intent i = getIntent();
        double distance = i.getDoubleExtra("distance", LONG_DISTANCE + 2);

        data.putExtra("result", "The input distance: " + distance);

        Button bt_back = findViewById(R.id.bt_obs_back);
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch back to the main activity
                goBack();
            }
        });
    }

    private void goBack(){
        // Return resultCode 0 if successful
        setResult(0, data);
        finish();
    }
}

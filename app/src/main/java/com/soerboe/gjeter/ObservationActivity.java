package com.soerboe.gjeter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ObservationActivity extends AppCompatActivity {
    // Tag used in debug messages
    private static final String TAG = MainActivity.class.getSimpleName();

    private Intent data = new Intent();
    private int LONG_DISTANCE;

    private boolean success = false;

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
        Log.d(TAG, "goBack()");
        setResult(10, data);
        success = true;
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
        if (!success){
            setResult(0, data);
        }
    }
}

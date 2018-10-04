package com.soerboe.gjeter;

import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class ObservationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    // Tag used in debug messages
    private static final String TAG = MainActivity.class.getSimpleName();

    private Intent data = new Intent();
    private int LONG_DISTANCE;

    private boolean success = false;

    private Toolbar toolbar;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation);
        Resources res = getResources();

        LONG_DISTANCE  = res.getInteger(R.integer.LONG_DISTANCE);

        /*
        // Getting the list of observation-types
        TypedArray res_obs_types = res.obtainTypedArray(R.array.observation_types_nb);
        int number_of_types = res_obs_types.length();
        String[] obs_types = new String[number_of_types];
        for (int i = 0; i < number_of_types; i++){
            obs_types[i] = res_obs_types.getString(i);
        }
        res_obs_types.recycle();*/

        // Changing ActionBar
        toolbar = findViewById(R.id.toolbar_obs);
        spinner = findViewById(R.id.spinner_nav_obs);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.observation_types_nb, R.layout.spinner_item);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);

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

    //OnItemSelectedListener classes:
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected = parent.getItemAtPosition(position).toString();
        Log.d(TAG, "User selected: " + selected);
        switch (selected){
            case "Saueflokk":{
                changeFragment(new SheepHerdFragment());
                break;
            }
            case "Død sau":{
                changeFragment(new DeadSheepFragment());
                break;
            }
            case "Rovdyr":{
                changeFragment(new PredatorFragment());
                break;
            }
            case "Jeger":{
                changeFragment(new HunterFragment());
                break;
            }
            case "Annet":{
                changeFragment(new OtherFragment());
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {  }

    private void changeFragment(Fragment newFragment){
        // Create new transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace the content of fragment_container with newFragment
        transaction.replace(R.id.fragment_container, newFragment);
        //transaction.addToBackStack(null); // No need to add the fragment to the back stack

        // Commit the transaction
        transaction.commit();
    }
}

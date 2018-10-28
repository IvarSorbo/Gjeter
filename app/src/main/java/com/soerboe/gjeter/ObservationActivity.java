package com.soerboe.gjeter;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.osmdroid.util.GeoPoint;

import java.util.Date;

public class ObservationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, SheepHerdFragment.MoreDetailsListener {
    // Tag used in debug messages
    private static final String TAG = ObservationActivity.class.getSimpleName();

    private Intent data = new Intent();

    private boolean success = false;

    private Toolbar toolbar;
    private Spinner spinner;

    private Observation observation;

    private MyFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation);
        //Resources res = getResources();

        /*
        // Getting the list of observation-types
        TypedArray res_obs_types = res.obtainTypedArray(R.array.observation_types_nb);
        int number_of_types = res_obs_types.length();
        String[] obs_types = new String[number_of_types];
        for (int i = 0; i < number_of_types; i++){
            obs_types[i] = res_obs_types.getString(i);
        }
        res_obs_types.recycle();*/

        // Getting the distance that was set in MainActivity
        observation = getObservationFromIntent();

        // Changing ActionBar
        toolbar = findViewById(R.id.toolbar_obs);
        spinner = findViewById(R.id.spinner_nav_obs);
        spinner.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.observation_types_nb, R.layout.spinner_item);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);

        // Setting up the "save"-button:
        Button bt_save = findViewById(R.id.bt_obs_save);
        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch back to the main activity
                sendBackResult();
            }
        });
    }

    /**
     * Fetches data from the Intent.
     * MainActivity sends the positions of the observation and of the user.
     * @return Observation
     */
    private Observation getObservationFromIntent(){
        Intent i = getIntent();
        double obs_lat = i.getDoubleExtra(Constants.OBS_LAT, 0.0);
        double obs_lng = i.getDoubleExtra(Constants.OBS_LNG, 0.0);
        double my_lat = i.getDoubleExtra(Constants.MY_LAT, 0.0);
        double my_lng = i.getDoubleExtra(Constants.MY_LNG, 0.0);
        return new Observation(
                new GeoPoint(obs_lat, obs_lng),
                new GeoPoint(my_lat, my_lng),
                new Date(System.currentTimeMillis())
        );
    }

    /**
     * This can be called from the fragments to get the initial Observation-object
     * @return Observation
     */
    protected Observation getObservation() {
        if (observation == null){
            observation = getObservationFromIntent();
        }
        return observation;
    }

    /**
     * Gets the result from the current fragment, adds it to the returned intent and
     * finishes the activity.
     */
    private void sendBackResult(){
        String result = fragment.toJSON();

        Log.d(TAG, "\nsendBackResult() is returning:\n" + result);

        data.putExtra("result", result);
        setResult(Activity.RESULT_OK, data);
        success = true;
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!success){
            sendBackResult();
            Log.d(TAG, "onPause called the sendBackResult method");
        }
    }

    //OnItemSelectedListener methods:
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selected = parent.getItemAtPosition(position).toString();
        Log.d(TAG, "User selected: " + selected);
        switch (selected){
            case "Saueflokk":{
                // Show different fragments based on the distance to the observation:
                // (if user's position is unknown it will be set to 0.0,0.0 which will be far away
                // from any observation-location in Norway. Thus, the long-distance fragment will
                // be shown if the user's position is unknown)
                if (observation.getDistance() > Constants.LONG_DISTANCE){
                    changeFragment(new SheepHerdFragment());
                }else{
                    changeFragment(new SheepHerdDetailedFragment());
                }
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
            default: {
                changeFragment(new SheepHerdFragment());
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {  }

    /**
     * Replaces the current fragment and updates the "fragment" variable
     * @param newFragment the fragment that is to replace the current fragment
     */
    private void changeFragment(MyFragment newFragment){
        Log.d(TAG, "Switching fragment to " + newFragment.getClass().getSimpleName());

        // Update the local variable
        fragment = newFragment;

        // Create new transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace the content of fragment_container with newFragment
        transaction.replace(R.id.fragment_container, newFragment);
        //transaction.addToBackStack(null); // No need to add the fragment to the back stack

        // Commit the transaction
        transaction.commit();
    }

    // SheepHerdFragment method:
    @Override
    public void onMoreDetailsClicked(SheepHerdObservation sheepHerdObs) {
        //This is a listener that must handle the event that "more details" were clicked
        observation = sheepHerdObs;
        //Log.d(TAG, "Class of observation is now: "+observation.getClass().getCanonicalName());
        changeFragment(new SheepHerdDetailedFragment());
    }
}

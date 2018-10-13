package com.soerboe.gjeter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

public class SheepHerdDetailedFragment extends MyFragment{
    private SheepHerdDetailedObservation observation;
    private ArrayList<EarTagFragment> earTagFragments;
    private Activity activity;
    private TextInputEditText whiteSheep, blackSheep, otherSheep, lamb, lambOrg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sheep_herd_detailed, container, false);
        earTagFragments = new ArrayList<>();
        addEarTagFragment(); // Inflate the first earTagFragment

        // The input boxes
        whiteSheep = view.findViewById(R.id.whiteSheep_detSheepHerdFrag);
        blackSheep = view.findViewById(R.id.blackSheep_detSheepHerdFrag);
        otherSheep = view.findViewById(R.id.otherSheep_detSheepHerdFrag);
        lamb = view.findViewById(R.id.lamb_detSheepHerdFrag);
        lambOrg = view.findViewById(R.id.lamb_org_detSheepHerdFrag);

        // Check for changes to the original lamb count and alert the user if some lambs are missing
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(!lambsAccountedFor()){
                    //Alert the user
                    lambOrg.setError("Mangler noen lam?");
                }
            }

            private boolean lambsAccountedFor(){
                return InputChecker.getInt(lamb, 0) >= InputChecker.getInt(lambOrg, 0);
            }
        };
        lambOrg.addTextChangedListener(textWatcher);

        // Setup button to add more EarTagFragments
        Button bt_addEarTag = view.findViewById(R.id.bt_add_eartag_fragment);
        bt_addEarTag.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addEarTagFragment();
                    }
                }
        );

        // Assumes that the parent activity is an ObservationActivity
        observation = new SheepHerdDetailedObservation(new SheepHerdObservation(((ObservationActivity) activity).getObservation()));
        fillInExisitingInfo();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = getActivity();
    }

    private void addEarTagFragment(){
        // Add another EarTagFragment to the container
        EarTagFragment newFragment = new EarTagFragment();
        earTagFragments.add(newFragment);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        transaction.add(R.id.eartag_fragment_container, newFragment);

        transaction.commit();
    }

    /**
     * This should be called after the Observation object has been fetched from the parent activity.
     */
    private void fillInExisitingInfo(){
        // Check if the observation object contains any useful info
        if(observation.getWhiteSheepCount() > 0) {whiteSheep.setText(String.valueOf(observation.getWhiteSheepCount()));}
        if(observation.getBlackSheepCount() > 0) {blackSheep.setText(String.valueOf(observation.getBlackSheepCount()));}
        if(observation.getOtherSheepCount() > 0) {otherSheep.setText(String.valueOf(observation.getOtherSheepCount()));}
        if(observation.getLambCount() > 0) {lamb.setText(String.valueOf(observation.getLambCount()));}
        if(observation.getLambOriginalCount() > 0) {lambOrg.setText(String.valueOf(observation.getLambOriginalCount()));}
    }

    private void update(){
        // Get the earTags
        for(EarTagFragment etf: earTagFragments){
            EarTag et = etf.getEarTag();
            observation.updateEarTags(et);
        }
        // update the other stuff
        observation.setWhiteSheepCount(InputChecker.getInt(whiteSheep, 0));
        observation.setBlackSheepCount(InputChecker.getInt(blackSheep, 0));
        observation.setOtherSheepCount(InputChecker.getInt(otherSheep, 0));
        observation.setLambCount(InputChecker.getInt(lamb, 0));
        observation.setLambOriginalCount(InputChecker.getInt(lambOrg, 0));
    }


    @Override
    public String toJSON() {
        update();
        return observation.toJSON();
    }
}

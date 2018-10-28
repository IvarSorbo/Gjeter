package com.soerboe.gjeter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

public class HunterFragment extends MyFragment {

    private TextInputEditText hunterCount, dogCount;
    private Activity activity;
    private HunterObservation hunterObservation;
    private Gson gson = new Gson();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hunter, container, false);
        hunterCount = view.findViewById(R.id.hunters_hunterFragment);
        dogCount = view.findViewById(R.id.dogs_hunterFragment);

        // Assumes that the parent activity is an ObservationActivity
        Observation observation = ((ObservationActivity) activity).getObservation();
        hunterObservation = new HunterObservation(observation);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = getActivity();
    }

    private void update(){
        hunterObservation.setHunterCount(InputChecker.getInt(hunterCount,0 ));
        hunterObservation.setDogCount(InputChecker.getInt(dogCount, 0));
    }

    @Override
    public String toJSON() {
        update();
        return gson.toJson(hunterObservation);
    }
}

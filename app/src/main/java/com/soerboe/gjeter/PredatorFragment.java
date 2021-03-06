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

/**
 * A fragment for filling in information about a predator observation.
 */
public class PredatorFragment extends MyFragment {
    private TextInputEditText type, count, notes;
    private Activity activity;
    private PredatorObservation predatorObservation;
    private Gson gson = new Gson();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_predator, container, false);

        type = view.findViewById(R.id.type_predatorFragment);
        count = view.findViewById(R.id.count_predatorFragment);
        notes = view.findViewById(R.id.notes_predatorFragment);

        // Assumes that the parent activity is an ObservationActivity
        Observation observation = ((ObservationActivity) activity).getObservation();
        predatorObservation = new PredatorObservation(observation);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = getActivity();
    }

    private void update(){
        predatorObservation.setType(InputChecker.getString(type));
        predatorObservation.setCount(InputChecker.getInt(count, 0));
        predatorObservation.setNotes(InputChecker.getString(notes));
    }

    @Override
    public String toJSON() {
        update();
        return gson.toJson(predatorObservation);
    }
}
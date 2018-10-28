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

import java.io.InputStreamReader;

public class OtherFragment extends MyFragment {

    private TextInputEditText type, count;
    private Activity activity;
    private OtherObservation otherObservation;
    private Gson gson = new Gson();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_other, container, false);

        type = view.findViewById(R.id.type_otherFragment);
        count = view.findViewById(R.id.count_otherFragment);

        // Assumes that the parent activity is an ObservationActivity
        Observation observation = ((ObservationActivity) activity).getObservation();
        otherObservation = new OtherObservation(observation);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = getActivity();
    }

    private void update(){
        otherObservation.setType(InputChecker.getString(type));
        otherObservation.setCount(InputChecker.getInt(count, 0));
    }

    @Override
    public String toJSON() {
        update();
        return gson.toJson(otherObservation);
    }
}

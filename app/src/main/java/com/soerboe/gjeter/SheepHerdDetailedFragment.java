package com.soerboe.gjeter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SheepHerdDetailedFragment extends MyFragment {
    private SheepHerdDetailedObservation observation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sheep_herd_detailed, container, false);
        return view;
    }

    @Override
    public String toJSON() {
        //return observation.toJSON();
        return "";
    }

    //TODO: if the number of lambs observed is lower than the original number, the user should be notified
}

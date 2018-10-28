package com.soerboe.gjeter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PredatorFragment extends MyFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_predator, container, false);
        return view;
    }

    @Override
    public String toJSON() {
        return "Hello from PredatorFragment";
    }
}

// Also add the option to add tracks/excreta observations
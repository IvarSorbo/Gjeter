package com.soerboe.gjeter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DeadSheepFragment extends MyFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dead_sheep, container, false);
        return view;
    }

    @Override
    public String toJSON() {
        return "Hello from DeadSheepFragment";
    }
}

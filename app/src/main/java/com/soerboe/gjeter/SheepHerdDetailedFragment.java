package com.soerboe.gjeter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

public class SheepHerdDetailedFragment extends MyFragment{
    private SheepHerdDetailedObservation observation;
    private ArrayList<MyFragment> earTagFragments;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sheep_herd_detailed, container, false);
        earTagFragments = new ArrayList<MyFragment>();
        addEarTagFragment(); // Inflate the first earTagFragment

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
        return view;
    }

    private void addEarTagFragment(){
        // Add another EarTagFragment to the container
        EarTagFragment newFragment = new EarTagFragment();
        earTagFragments.add(newFragment);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        transaction.add(R.id.eartag_fragment_container, newFragment);

        transaction.commit();
    }


    @Override
    public String toJSON() {
        //return observation.toJSON();
        return "";
    }

    //TODO: if the number of lambs observed is lower than the original number, the user should be notified
}

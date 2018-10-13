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
import android.widget.Button;

public class SheepHerdFragment extends MyFragment {
    private TextInputEditText whiteCount, blackCount, otherCount;

    private Activity activity;

    private SheepHerdObservation sheepHerdObservation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sheep_herd, container, false);
        whiteCount = view.findViewById(R.id.whiteSheep_sheepHerdFragment);
        blackCount = view.findViewById(R.id.blackSheep_sheepHerdFragment);
        otherCount = view.findViewById(R.id.otherSheep_sheepHerdFragment);

        // "More details"-button
        Button btDetailedObs = view.findViewById(R.id.bt_detailed_obs);
        btDetailedObs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    update();
                    ((MoreDetailsListener) activity).onMoreDetailsClicked(sheepHerdObservation);
                }catch (ClassCastException cce){
                    //TODO: DoSomething
                }
            }
        });

        // Assumes that the parent activity is an ObservationActivity
        Observation observation = ((ObservationActivity) activity).getObservation();
        sheepHerdObservation = new SheepHerdObservation(observation);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = getActivity();
    }


    /**
     * Update the SheepHerdObservation object based on the inputs.
     */
    private void update(){
        sheepHerdObservation.setBlackSheepCount(InputChecker.getInt(blackCount,0));
        sheepHerdObservation.setWhiteSheepCount(InputChecker.getInt(whiteCount,0));
        sheepHerdObservation.setOtherSheepCount(InputChecker.getInt(otherCount,0));
    }

    /**
     * This should be called by the parent activity to get the JSON representation of the resulting
     * SheepHerdObservation object.
     * @return A JSON representation of the sheepHerdObservation object.
     */
    @Override
    public String toJSON() {
        update();
        return sheepHerdObservation.toJSON();
    }

    /**
     * An interface which the parent activity should implement in order
     */
    public interface MoreDetailsListener{
        void onMoreDetailsClicked(SheepHerdObservation sheepHerdObs);// When "more details" is clicked -> switch to detailed fragment
    }
}


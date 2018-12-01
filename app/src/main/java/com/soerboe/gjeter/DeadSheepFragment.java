package com.soerboe.gjeter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;

/**
 * A fragment for filling information about a dead sheep observation.
 */
public class DeadSheepFragment extends MyFragment {
    private TextInputEditText owner, number, notes;
    private TextView output;
    private Activity activity;
    private DeadSheepObservation deadSheepObservation;
    private Gson gson = new Gson();
    private Uri uri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dead_sheep, container, false);

        owner = view.findViewById(R.id.owner_deadSheepFragment);
        number = view.findViewById(R.id.earTagNumber_deadSheepFragment);
        notes = view.findViewById(R.id.notes_deadSheepFragment);
        output = view.findViewById(R.id.textView_output);

        // Assumes that the parent activity is an ObservationActivity
        Observation observation = ((ObservationActivity) activity).getObservation();
        deadSheepObservation = new DeadSheepObservation(observation);

        Button bt_take_photo = view.findViewById(R.id.bt_take_photo);
        bt_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String filename = "sheep" + String.valueOf(System.currentTimeMillis()) + ".jpg";
                File imageFile = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        , filename);
                uri = Uri.fromFile(imageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                startActivityForResult(intent, 0);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = getActivity();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            switch (resultCode){
                case Activity.RESULT_OK: {
                    output.append(uri.toString() + "\n");
                    deadSheepObservation.addPhoto(uri);
                    break;
                }
                case Activity.RESULT_CANCELED: {
                    Toast.makeText(activity, R.string.canceled, Toast.LENGTH_SHORT).show();
                    break;
                }
                default: break;
            }
        }
    }

    private void update(){
        deadSheepObservation.setOwner(InputChecker.getString(owner));
        deadSheepObservation.setNumber(InputChecker.getInt(number, 0));
        deadSheepObservation.setNotes(InputChecker.getString(notes));
    }

    @Override
    public String toJSON() {
        update();
        return gson.toJson(deadSheepObservation);
    }
}

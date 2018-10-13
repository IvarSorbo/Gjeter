package com.soerboe.gjeter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EarTagFragment extends MyFragment {
    private EarTag earTag;
    private TextInputEditText color, count;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ear_tag, container, false);

        earTag = new EarTag();
        color = view.findViewById(R.id.eartag_color);
        count = view.findViewById(R.id.eartag_count);

        return view;
    }

    private void update(){
        earTag.setColor(InputChecker.getString(color));
        earTag.setCount(InputChecker.getInt(count, 0));
    }

    public EarTag getEarTag(){
        update();
        return earTag;
    }

    @Override
    public String toJSON() {
        update();
        return earTag.toJSON();
    }
}

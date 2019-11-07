package com.autipro.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.autipro.DataActivity;
import com.autipro.R;
import com.autipro.helpers.DataType;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment implements View.OnClickListener {

    CardView running, drowning, loudVoices;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        running = view.findViewById(R.id.running);
        drowning = view.findViewById(R.id.drowning);
        loudVoices = view.findViewById(R.id.loudVoices);

        running.setOnClickListener(this);
        drowning.setOnClickListener(this);
        loudVoices.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), DataActivity.class);
        DataType.Sensor type = null;
        if(view == running){
            type = DataType.Sensor.RUNNING;
        } else if (view == drowning) {
            type = DataType.Sensor.DROWNING;
        } else if (view == loudVoices){
            type = DataType.Sensor.LOUD_VOICES;
        }
        intent.putExtra("type", type);
        startActivity(intent);
    }
}

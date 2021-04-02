package com.example.tinyhousemonitoring.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tinyhousemonitoring.R;
import com.example.tinyhousemonitoring.iot_environment.ApplicationEnvironment;
import com.example.tinyhousemonitoring.iot_environment.Patient;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;

/**
 * A simple {@link Fragment} subclass.
 */
public class HealthFragment extends Fragment {

    CardView CVHeartrate;
    TextView TVHeartRate;
    ImageView IVHeartrate;
    CardView CVEmergency;
    ImageView IVEmergency;

    Patient patient;

    public HealthFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health, container, false);
        CVHeartrate=view.findViewById(R.id.card_view_heartrate);
        System.out.println("CVHeartrate: "+CVHeartrate);
        TVHeartRate=view.findViewById(R.id.text_view_heartrate);
        IVHeartrate=view.findViewById(R.id.image_view_heartrate);
        CVEmergency=view.findViewById(R.id.card_view_emergency);
        IVEmergency=view.findViewById(R.id.image_view_emergency);

        monitorPatient();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        patient= ApplicationEnvironment.getPatient();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unmonitorPatient();
    }


    private void monitorPatient() {
        if (patient.hasHeartrateSensor()){
            patient.monitorHeartrate(new OnEventOccurred<Integer>() {
                @Override
                public void onUpdate(Integer response) {
                    TVHeartRate.setText(response + " bpm");
                }
            });
        }
        else {
            CVHeartrate.setAlpha((float) .5);
        }

        if (patient.hasEmergencyButton()){
            patient.monitorEmergency(new OnEventOccurred<Boolean>() {
                @Override
                public void onUpdate(Boolean response) {
                    if(response) IVEmergency.setImageResource(R.drawable.ic_notifications_active_black_24dp);
                }
            });

            CVEmergency.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    IVEmergency.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                    return true;
                }
            });
        }
        else {
            CVEmergency.setAlpha((float) .5);
        }
    }

    private void unmonitorPatient() {
    }

}

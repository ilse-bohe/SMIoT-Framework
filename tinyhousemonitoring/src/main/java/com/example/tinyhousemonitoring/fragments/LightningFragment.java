package com.example.tinyhousemonitoring.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.example.tinyhousemonitoring.R;
import com.example.tinyhousemonitoring.iot_environment.ApplicationEnvironment;
import com.madrapps.pikolo.HSLColorPicker;
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener;

import androidx.fragment.app.Fragment;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Lamp;

public class LightningFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, Switch.OnCheckedChangeListener {
    final String TAG = "ActuateFragment";

    LampExpandableListAdapter listAdapter;
    Activity activity;

    private static ToggleButton lightToggle;

    public static LightningFragment newInstance() {
        LightningFragment fragment = new LightningFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lightning, container, false);
        lightToggle = (ToggleButton) view.findViewById(R.id.toggleButton_light);
        lightToggle.setOnClickListener(this);

        final ImageView colorView = (ImageView) view.findViewById(R.id.imageView_color);
        final CountDownTimer c = new CountDownTimer(1500, 1000) {

            public void onTick(long millisUntilFinished) {
                System.out.println("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                ApplicationEnvironment.getRoom().changeColor(ApplicationEnvironment.getRoom().getColor(), new OnRequestCompleted() {
                    @Override
                    public void onSuccess(Object response) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listAdapter.notifyDataSetChanged();
                            }
                        });
                        Log.i(TAG, "Light color in room changed to " + ApplicationEnvironment.getRoom().getColor());
                    }
                });
            }
        };
        final HSLColorPicker colorPicker = (HSLColorPicker) view.findViewById(R.id.colorPicker);
        colorPicker.setColorSelectionListener(new SimpleColorSelectionListener() {
            @Override
            public void onColorSelected(final int color) {
                // Do whatever you want with the color
                c.cancel();
                c.start();
                ApplicationEnvironment.getRoom().setColor(String.format("%06X", (0xFFFFFF & color)));
                colorView.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            }
        });
        colorView.getBackground().setColorFilter(Color.parseColor("#43C0C0"), PorterDuff.Mode.MULTIPLY);
        colorPicker.setColor(Color.parseColor("#43C0C0"));
        System.out.println("COLOR " + colorPicker.getSolidColor());
        colorView.setColorFilter(colorPicker.getSolidColor());


        SeekBar lightBrightness = view.findViewById(R.id.seekBar_brightness);
        lightBrightness.setProgress(50);
        lightBrightness.setOnSeekBarChangeListener(this);

        ExpandableListView lampsExpandableListView = (ExpandableListView) view.findViewById(R.id.expandableListView_lamps);
        listAdapter = new LampExpandableListAdapter(getActivity(), R.layout.lamp_list_item, R.layout.lamp_settings_list_item, ApplicationEnvironment.getRoom().getLamps(), lampsExpandableListView);
        lampsExpandableListView.setAdapter(listAdapter);



        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toggleButton_light:
                boolean isChecked2 = ((ToggleButton) view).isChecked();
                if (isChecked2)
                    ApplicationEnvironment.getRoom().lightsOn(new OnRequestCompleted<Boolean>() {
                        @Override
                        public void onSuccess(Boolean response) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    listAdapter.notifyDataSetChanged();
                                }
                            });
                            Log.i(TAG, "Light in room turned on");
                        }
                    });
                else ApplicationEnvironment.getRoom().lightsOff(new OnRequestCompleted<Boolean>() {
                    @Override
                    public void onSuccess(Boolean response) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listAdapter.notifyDataSetChanged();
                            }
                        });
                        Log.i(TAG, "Light in room turned off");
                    }
                });
                break;
            default:
                break;
        }

    }

    int brightness;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.seekBar_brightness:
                brightness = progress;
                //TODO textview change value
                break;
            default:
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.seekBar_brightness:
                ApplicationEnvironment.getRoom().changeBrightness(brightness, new OnRequestCompleted() {
                    @Override
                    public void onSuccess(Object response) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listAdapter.notifyDataSetChanged();
                            }
                        });
                        Log.i(TAG, "Light brightness in room changed to " + brightness);
                    }
                });
                break;
            default:
                break;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.switch_light:
                final Lamp lamp = (Lamp) compoundButton.getTag();
                if (isChecked) lamp.turnOn(new OnRequestCompleted<Boolean>() {
                    @Override
                    public void onSuccess(Boolean response) {
                        Log.i(TAG, "Lamp " + lamp.getSystemID() + " in room turned on");
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
                else lamp.turnOff(new OnRequestCompleted<Boolean>() {
                    @Override
                    public void onSuccess(Boolean response) {
                        Log.i(TAG, "Lamp " + lamp.getSystemID() + " in room turned off");
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
                break;
            default:
                break;
        }
    }

    public static void minLampOn(boolean minLampOn) {
        lightToggle.setChecked(minLampOn);
    }
}

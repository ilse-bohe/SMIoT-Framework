package com.example.tinyhousemonitoring.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tinyhousemonitoring.R;
import com.example.tinyhousemonitoring.iot_environment.ApplicationEnvironment;
import com.example.tinyhousemonitoring.iot_environment.Room;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    CardView CVLock;
    ImageView IVLock;
    CardView CVLightning;
    ImageView IVLightning;
    CardView CVFireplace;
    ImageView IVFireplace;
    CardView CVCurtains;
    ImageView IVCurtains;

    CardView CVTemperature;
    TextView TVTemperature;
    CardView CVHumidity;
    TextView TVHumidity;
    CardView CVLightIntensity;
    TextView TVLightIntensity;
    CardView CVCo2;
    TextView TVCo2;
    CardView CVColor;
    TextView TVColor;
    FrameLayout FLColor;

    Room room;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_home, container, false);
       CVLock=view.findViewById(R.id.card_view_lock);
       IVLock=view.findViewById(R.id.image_view_lock);
       CVLightning=view.findViewById(R.id.card_view_lightning);
       IVLightning=view.findViewById(R.id.image_view_lightning);
       CVFireplace=view.findViewById(R.id.card_view_fireplace);
       IVFireplace=view.findViewById(R.id.image_view_fireplace);
       CVCurtains = view.findViewById(R.id.card_view_curtains);
       IVCurtains=view.findViewById(R.id.image_view_curtains);

       CVTemperature=view.findViewById(R.id.card_view_temperature);
       TVTemperature = view.findViewById(R.id.text_view_temperature);
       CVHumidity=view.findViewById(R.id.card_view_humidity);
       TVHumidity= view.findViewById(R.id.text_view_humidity);
       CVLightIntensity=view.findViewById(R.id.card_view_lightintensity);
       TVLightIntensity= view.findViewById(R.id.text_view_light_intensity);
       CVCo2=view.findViewById(R.id.card_view_co2value);
       TVCo2 = view.findViewById(R.id.text_view_co2);
       CVColor=view.findViewById(R.id.card_view_colorvalue);
       TVColor= view.findViewById(R.id.text_view_color);
       FLColor=view.findViewById(R.id.frame_layout_color_preview);

       monitorRoom();

       return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        room= ApplicationEnvironment.getRoom();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unmonitorRoom();
    }

    private void monitorRoom() {
        if(room.hasLock()){
            room.getLockStatus(new OnRequestCompleted<Boolean>() {
                @Override
                public void onSuccess(Boolean response) {
                    if (response) IVLock.setImageResource(R.drawable.ic_lock_open_black_24dp);
                    else IVLock.setImageResource(R.drawable.ic_lock_closed_24dp);
                }
            });

            CVLock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    room.getLockStatus(new OnRequestCompleted<Boolean>() {
                        @Override
                        public void onSuccess(Boolean response) {
                            if (response) {
                                room.closeLock(new OnRequestCompleted() {
                                    @Override
                                    public void onSuccess(Object response) {
                                        IVLock.setImageResource(R.drawable.ic_lock_closed_24dp);
                                    }
                                });

                            }
                            else {
                                room.openLock(new OnRequestCompleted() {
                                    @Override
                                    public void onSuccess(Object response) {
                                        IVLock.setImageResource(R.drawable.ic_lock_open_black_24dp);
                                    }
                                });

                            }
                        }
                    });
                }
            });
        }
        else {
            CVLock.setClickable(false);
            CVLock.setAlpha((float) .5);
        }

        if(room.hasLamps()){
            room.getLightningStatus(new OnRequestCompleted<Boolean>() {
                @Override
                public void onSuccess(Boolean response) {
                    if (response) IVLightning.setImageResource(R.drawable.ic_lightbulb_on);
                    else IVLightning.setImageResource(R.drawable.ic_lightbulb_off);
                }
            });

            CVLightning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    room.getLightningStatus(new OnRequestCompleted<Boolean>() {
                        @Override
                        public void onSuccess(Boolean response) {
                            System.out.println("LIGHTSTATUS: "+response);
                            if (response) {
                               room.lightsOff(new OnRequestCompleted() {
                                    @Override
                                    public void onSuccess(Object response) {
                                        IVLightning.setImageResource(R.drawable.ic_lightbulb_off);
                                    }
                                });

                            }
                            else {
                                room.lightsOn(new OnRequestCompleted() {
                                    @Override
                                    public void onSuccess(Object response) {
                                        IVLightning.setImageResource(R.drawable.ic_lightbulb_on);
                                    }
                                });

                            }
                        }
                    });
                }
            });

            CVLightning.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    getFragmentManager().beginTransaction().replace(R.id.frame_layout, new LightningFragment()).commit();
                    return true;
                }
            });
        }
        else {
            CVLightning.setClickable(false);
            CVLightning.setAlpha((float) .5);
        }

        if(room.hasFireplace()){
            room.getFireplaceStatus(new OnRequestCompleted<Boolean>() {
                @Override
                public void onSuccess(Boolean response) {
                    if (response) IVFireplace.setImageResource(R.drawable.ic_fire_burning);
                    else IVFireplace.setImageResource(R.drawable.ic_fire_out);
                }
            });

            CVFireplace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    room.getFireplaceStatus(new OnRequestCompleted<Boolean>() {
                        @Override
                        public void onSuccess(Boolean response) {
                            if (response) {
                                room.extinguishFireplace(new OnRequestCompleted() {
                                    @Override
                                    public void onSuccess(Object response) {
                                        IVFireplace.setImageResource(R.drawable.ic_fire_out);
                                    }
                                });

                            }
                            else {
                                room.ligthenFireplace(new OnRequestCompleted() {
                                    @Override
                                    public void onSuccess(Object response) {
                                        IVFireplace.setImageResource(R.drawable.ic_fire_burning);
                                    }
                                });

                            }
                        }
                    });
                }
            });
        }
        else {
            CVFireplace.setClickable(false);
            CVFireplace.setAlpha((float) .5);
        }

        if(room.hasCurtains()){
            room.getCurtainStatus(new OnRequestCompleted<Boolean>() {
                @Override
                public void onSuccess(Boolean response) {
                    if (response) IVCurtains.setImageResource(R.drawable.ic_blinds_open);
                    else IVCurtains.setImageResource(R.drawable.ic_blinds_closed);
                }
            });

            CVCurtains.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    room.getCurtainStatus(new OnRequestCompleted<Boolean>() {
                        @Override
                        public void onSuccess(Boolean response) {
                            if (response) {
                                room.closeCurtains(new OnRequestCompleted() {
                                    @Override
                                    public void onSuccess(Object response) {
                                        IVCurtains.setImageResource(R.drawable.ic_blinds_closed);
                                    }
                                });

                            }
                            else {
                                room.openCurtains(new OnRequestCompleted() {
                                    @Override
                                    public void onSuccess(Object response) {
                                        IVCurtains.setImageResource(R.drawable.ic_blinds_open);
                                    }
                                });

                            }
                        }
                    });
                }
            });
        }
        else {
            CVCurtains.setClickable(false);
            CVCurtains.setAlpha((float) .5);
        }

        if (room.hasTemperatureSensor()){
            room.monitorTemperature(new OnEventOccurred<Double>() {
                @Override
                public void onUpdate(Double response) {
                    TVTemperature.setText(String.format("%.2f", response)+ " °C");
                }
            });
        }
        else {
            CVTemperature.setAlpha((float) .5);
        }

        if (room.hasHumiditySensor()){
            room.monitorHumidity(new OnEventOccurred<Double>() {
                @Override
                public void onUpdate(Double response) {
                    TVHumidity.setText(String.format("%.2f", response)+" %");
                }
            });
        }
        else {
            CVHumidity.setAlpha((float) .5);
        }

        if (room.hasLightSensor()){
            room.monitorLightIntensity(new OnEventOccurred<Double>() {
                @Override
                public void onUpdate(Double response) {
                    TVLightIntensity.setText(String.format("%.2f", response)+" lux");
                }
            });
        }
        else {
            CVLightIntensity.setAlpha((float) .5);
        }

        if (room.hasCo2Sensor()){
            room.monitorCo2(new OnEventOccurred<Double>() {
                @Override
                public void onUpdate(Double response) {
                    TVCo2.setText(String.format("%.2f", response)+" ppm");
                }
            });
        }
        else {
            CVCo2.setAlpha((float) .5);
        }

        if (room.hasColorSensor()){
            room.monitorColorValue(new OnEventOccurred<String>() {
                @Override
                public void onUpdate(String response) {
                    TVColor.setText(response);
                    FLColor.setBackgroundColor(Color.parseColor(response));
                }
            });
        }
        else {
            CVColor.setAlpha((float) .5);
        }
    }

    private void unmonitorRoom() {
        if (room.hasTemperatureSensor()) room.unmonitorTemperature();
        if (room.hasHumiditySensor()) room.unmonitorHumidity();
        if (room.hasLightSensor()) room.unmonitorLightIntensity();
        if (room.hasCo2Sensor()) room.unmonitorCo2();
        if (room.hasColorSensor()) room.unmonitorColorValue();
    }
}

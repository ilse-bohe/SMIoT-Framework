package be.kuleuven.msec.devicelist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.HumiditySensor;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Lamp;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.LightSensor;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.TemperatureSensor;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.VirtualIoTDevice;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Device_constants;

public class CustomListAdapter extends BaseAdapter {

    final String TAG = "CustomListAdapter";
    final CustomListAdapter thisAdapter;

    private Context context;
    private ArrayList<VirtualIoTDevice> devices;

    public CustomListAdapter(@NonNull Context context, ArrayList<VirtualIoTDevice> devices) {
        this.context = context;
        this.devices = devices;
        this.thisAdapter=this;
    }


    @Override
    public int getViewTypeCount() {
        // we got two types of views: a sensor view and a lamp view
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        switch (devices.get(position).getType()) {
            case Device_constants.TYPE_LAMP:
                return 0;
            default:
                return 1;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        int type = getItemViewType(position);

        VirtualIoTDevice viotd = devices.get(position);


        if (v == null) {
            // Inflate the layout according to the view type
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (type == 0) {
                // Inflate the lamp layout
                v = inflater.inflate(R.layout.lamp_list_item, parent, false);

            } else {
                v = inflater.inflate(R.layout.sensor_list_item, parent, false);
            }
        }
        //

        TextView name = (TextView) v.findViewById(R.id.parameter_name);
        name.setText(viotd.getSystemID());

        TextView value = null;
        ImageView image = (ImageView) v.findViewById(R.id.parameter_image);

        if(type ==1){
            value = v.findViewById(R.id.parameter_value);
        }

        
        switch (viotd.getType()) {
            case Device_constants.TYPE_LAMP:
                image.setImageResource(R.drawable.ic_lamp_60dp);

                final Lamp l = (Lamp) getItem(position);
                final Switch lightSwitch = v.findViewById(R.id.switch_light);
                final SeekBar lightBrightness = v.findViewById(R.id.seekBar_brightness_lamp);
                final ImageButton lampColor = v.findViewById(R.id.imageButton_color);

                l.requestColor(new OnRequestCompleted<String>() {
                    @Override
                    public void onSuccess(String response) {
                        lampColor.getBackground().setColorFilter(Color.parseColor("#" + response), PorterDuff.Mode.MULTIPLY);
                    }
                });

                lampColor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        l.requestStatus(new OnRequestCompleted<Boolean>() {
                            @Override
                            public void onSuccess(Boolean isOn) {
                                if (!isOn) {
                                    turnLampOnDialog(l);
                                } else {
                                    new ColorPickerDialog(context, l, thisAdapter).show();
                                }
                            }

                            private void turnLampOnDialog(final Lamp lamp) {
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                                dialogBuilder.setTitle("Lamp is off");
                                dialogBuilder.setMessage("Do you want to turn on the light?");
                                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                                dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        lamp.turnOn(new OnRequestCompleted<Boolean>() {
                                            @Override
                                            public void onSuccess(Boolean response) {
                                                lightSwitch.setChecked(true);
                                                new ColorPickerDialog(context, lamp, thisAdapter).show();
                                            }
                                        });

                                    }
                                });
                                dialogBuilder.show();
                            }
                        });
                    }
                });

                l.requestBrightness(new OnRequestCompleted<Integer>() {
                    @Override
                    public void onSuccess(Integer response) {
                        lightBrightness.setProgress(response);
                    }
                });

                lightBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    int brightness;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        brightness = progress;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        l.changeBrightness(brightness, new OnRequestCompleted<Boolean>() {
                            @Override
                            public void onSuccess(Boolean response) {
                                Log.i(TAG, "Brightness of lamp " + l.getSystemID() + " changed to " + brightness + ".");
                            }
                        });
                    }
                });

                l.requestStatus(new OnRequestCompleted<Boolean>() {
                    @Override
                    public void onSuccess(Boolean response) {
                        lightSwitch.setChecked(response);
                    }
                });

                lightSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (lightSwitch.isChecked()) {
                            l.turnOn(new OnRequestCompleted<Boolean>() {
                                @Override
                                public void onSuccess(Boolean response) {
                                    Log.i(TAG, "Lamp " + l.getSystemID() + " turned on.");
                                }
                            });
                        } else {
                            l.turnOff(new OnRequestCompleted<Boolean>() {
                                @Override
                                public void onSuccess(Boolean response) {

                                    Log.i(TAG, "Lamp " + l.getSystemID() + " turned off.");

                                }
                            });
                        }
                    }
                });

                break;
            case Device_constants.TYPE_TEMPERATURE_SENSOR:
                image.setImageResource(R.drawable.ic_temperature_sensor_60dp);
                
                final TemperatureSensor t = (TemperatureSensor) getItem(position);
                final TextView temperatureValue = value;
                t.monitorTemperature(new OnEventOccurred<Double>() {
                    @Override
                    public void onUpdate(Double response) {
                        temperatureValue.setText(response+" °C");
                    }
                });
                
                
                break;
            case Device_constants.TYPE_HUMIDITY_SENSOR:
                image.setImageResource(R.drawable.ic_humidity_sensor_60dp);

                final HumiditySensor h = (HumiditySensor) getItem(position);
                final TextView humidityValue = value;
                h.monitorHumidity(new OnEventOccurred<Double>() {
                    @Override
                    public void onUpdate(Double response) {
                        humidityValue.setText(response+" %");
                    }
                });

                break;
            case Device_constants.TYPE_LIGHT_SENSOR:
                image.setImageResource(R.drawable.ic_light_sensor_60dp);

                final LightSensor ls = (LightSensor) getItem(position);
                final TextView lightSensorValue = value;
                ls.monitorLightIntensity(new OnEventOccurred<Double>() {
                    @Override
                    public void onUpdate(Double response) {
                        lightSensorValue.setText(response+" Lux");
                    }
                });

                break;

        }


        

        return v;
    }


    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}

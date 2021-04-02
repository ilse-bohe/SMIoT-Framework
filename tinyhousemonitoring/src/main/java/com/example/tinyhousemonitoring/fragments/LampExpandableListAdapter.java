package com.example.tinyhousemonitoring.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.tinyhousemonitoring.R;
import com.example.tinyhousemonitoring.iot_environment.ApplicationEnvironment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Lamp;

/**
 * Created by ilsebohe on 18/01/2018.
 */

public class LampExpandableListAdapter extends BaseExpandableListAdapter implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    final String TAG = "LampExpandableListAdapt";
    boolean minLampOn = false;
    int lastExpandedPosition = -1;
    int brightness;
    private LampExpandableListAdapter thisAdapter;
    private Context context;
    private List<Lamp> lamps_parent; // header titles
    private int parentLayoutResourceId;
    private int childLayoutResourceId;
    private ExpandableListView listView;
    // child data in format of header title, child title
    ///private HashMap<String, List<String>> _listDataChild;

    public LampExpandableListAdapter(@NonNull Context context, int parentLayoutResourceId, int childLayoutResourceId, ArrayList<Lamp> lamps, ExpandableListView listView) {
        this.context = context;
        this.parentLayoutResourceId = parentLayoutResourceId;
        this.childLayoutResourceId = childLayoutResourceId;
        this.lamps_parent = lamps;
        this.listView = listView;
        thisAdapter = this;
        dataSetChanged();

    }

    @Override
    public int getGroupCount() {
        return this.lamps_parent.size();
    }

    @Override
    public int getChildrenCount(int i) {
        //always 1 child
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.lamps_parent.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        //We wil return Parent, Lamp on which we want to do actuations
        //   return this.lamps_parent.get(groupPosition);
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int position, boolean isExpanded, View convertView, ViewGroup parent) {
        View row = convertView;
        LampHolder holder = null;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(parentLayoutResourceId, parent, false);
        holder = new LampHolder();
        holder.lamp = lamps_parent.get(position);
        holder.lightSwitch = (Switch) row.findViewById(R.id.switch_light);
        holder.lightSwitch.setTag(holder.lamp);
        holder.lightSwitch.setOnClickListener(this);
        holder.name = (TextView) row.findViewById(R.id.textView_lamp_name);
        row.setTag(holder);
        setupParent(holder);
        return row;
    }

    private void setupParent(final LampHolder holder) {
        holder.name.setText(holder.lamp.getSystemID());
        holder.lamp.requestStatus(new OnRequestCompleted<Boolean>() {
            @Override
            public void onSuccess(Boolean response) {
                holder.lightSwitch.setChecked(response);
            }
        });

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View row = convertView;
        LampSettingsHolder holder = null;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(childLayoutResourceId, parent, false);
        holder = new LampSettingsHolder();
        holder.lamp = lamps_parent.get(groupPosition);
        holder.brightnessSeekBar = (SeekBar) row.findViewById(R.id.seekBar_brightness_lamp);
        holder.brightnessSeekBar.setTag(holder.lamp);
        holder.brightnessSeekBar.setOnSeekBarChangeListener(this);
        holder.colorImageButton = (ImageButton) row.findViewById(R.id.imageButton_color);
        holder.colorImageButton.setTag(holder.lamp);
        holder.colorImageButton.setOnClickListener(this);
        row.setTag(holder);
        setupChild(holder);
        return row;
    }

    public void setupChild(final LampSettingsHolder holder) {
        holder.lamp.requestBrightness(new OnRequestCompleted<Integer>() {
            @Override
            public void onSuccess(Integer brightness) {
                holder.brightnessSeekBar.setProgress(brightness);
            }
        });
        holder.lamp.requestColor(new OnRequestCompleted<String>() {
            @Override
            public void onSuccess(String color) {
                if (color != null) {
                    holder.colorImageButton.getBackground().setColorFilter(Color.parseColor("#" + color), PorterDuff.Mode.MULTIPLY);

                }

            }
        });

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
        if (lastExpandedPosition != -1
                && groupPosition != lastExpandedPosition) {
            listView.collapseGroup(lastExpandedPosition);
        }
        lastExpandedPosition = groupPosition;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        switch (seekBar.getId()) {
            case R.id.seekBar_brightness_lamp:
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
            case R.id.seekBar_brightness_lamp:
                final Lamp lamp = (Lamp) seekBar.getTag();
                ApplicationEnvironment.getRoom().getLamp(lamp.getSystemID()).changeBrightness(brightness, new OnRequestCompleted() {
                    @Override
                    public void onSuccess(Object response) {
                        dataSetChanged();
                        Log.i(TAG, "Light brightness of lamp " + lamp.getSystemID() + " changed to " + brightness);
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageButton_color:
                final Lamp lamp = (Lamp) view.getTag();
                lamp.requestStatus(new OnRequestCompleted<Boolean>() {
                    @Override
                    public void onSuccess(Boolean isOn) {
                        if (!isOn) {
                            turnLampOnDialog(lamp);
                        } else {
                            new ColorPickerDialog(context, lamp, thisAdapter).show();
                        }
                    }
                });
                break;
            case R.id.switch_light:
                final Lamp lamp2 = (Lamp) view.getTag();
                boolean isChecked = ((Switch) view).isChecked();
                if (isChecked) lamp2.turnOn(new OnRequestCompleted<Boolean>() {
                    @Override
                    public void onSuccess(Boolean response) {
                        dataSetChanged();
                        Log.i(TAG, "Lamp " + lamp2.getSystemID() + " in room turned on");
                    }
                });
                else lamp2.turnOff(new OnRequestCompleted<Boolean>() {
                    @Override
                    public void onSuccess(Boolean response) {
                        dataSetChanged();
                        Log.i(TAG, "Lamp " + lamp2.getSystemID() + " in room turned off");
                    }
                });
                break;
            default:
                break;
        }
    }

    private void dataSetChanged() {
        thisAdapter.notifyDataSetChanged();
        minLampOn = false;
        for (Lamp lamp : lamps_parent) {
            lamp.requestStatus(new OnRequestCompleted<Boolean>() {
                @Override
                public void onSuccess(Boolean isOn) {
                    if (isOn) minLampOn = true;
                }
            });

        }
        LightningFragment.minLampOn(minLampOn);

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
                        dataSetChanged();
                        new ColorPickerDialog(context, lamp, thisAdapter).show();
                    }
                });

            }
        });
        dialogBuilder.show();
    }

    private static class LampSettingsHolder {
        Lamp lamp;
        SeekBar brightnessSeekBar;
        ImageButton colorImageButton;

    }

    private static class LampHolder {
        Lamp lamp;
        TextView name;
        Switch lightSwitch;
    }
}


package be.kuleuven.msec.iot.iotframework.generic.devicelayer;

import android.bluetooth.BluetoothClass;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Device_constants;

/**
 * Created by Thomas on 8/05/2018.
 */

public abstract class Gyroscope extends VirtualIoTDevice {
    private String unit; // in rad/s

    public Gyroscope(String systemID, String unit) {
        super(Device_constants.TYPE_GYROSCOPE,systemID);
        this.unit = unit;
    }

    public abstract void requestRateOfRotation(OnRequestCompleted<float[]> orc);

    public abstract void monitorRateOfRotation(OnEventOccurred<float[]> oeo);

    public abstract void unmonitorRateOfRotation();

    public String getUnit() {
        return unit;
    }
}

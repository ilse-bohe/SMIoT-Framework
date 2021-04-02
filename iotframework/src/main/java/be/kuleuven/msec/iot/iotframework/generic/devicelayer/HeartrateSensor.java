package be.kuleuven.msec.iot.iotframework.generic.devicelayer;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Device_constants;

/**
 * Created by Thomas on 12/12/2017.
 */

public abstract class HeartrateSensor extends VirtualIoTDevice {

    private String unit;
    //private int samplingRate;

    public HeartrateSensor(String systemID, String unit) {
        super(Device_constants.TYPE_HEARTRATE_SENSOR,systemID);
        this.unit = unit;
    }

    public abstract void requestHeartrate(OnRequestCompleted<Integer> orc);

    public abstract void monitorHeartrate(OnEventOccurred<Integer> oeo);

    public abstract void unmonitorHeartrate();

    // Trigger event when heart rate exceeds given value
    public abstract void exceeds(int value, OnEventOccurred<Integer> oeo);

    //public abstract void changeSamplingRate(int samplingRate, OnRequestCompleted<Boolean> orc);

    public String getUnit() {
        return unit;
    }

/*    public void setUnit(String unit) {
        this.unit = unit;
    }*/

/*    public int getSamplingRate() {
        return samplingRate;
    }*/
}

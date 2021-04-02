package be.kuleuven.msec.iot.iotframework.generic.devicelayer;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Device_constants;

/**
 * Created by ilsebohe on 16/11/2017.
 */

public abstract class Potentiometer extends VirtualIoTDevice {
    int voltage; //0-1023
    int samplingRate;

    public Potentiometer(String systemID) {
        super(Device_constants.TYPE_POTENTIOMETER,systemID);
    }


    protected int getVoltage() {
        return voltage;
    }

    protected void setVoltage(int voltage) {
        this.voltage = voltage;
    }

    protected int getSamplingRate() {
        return samplingRate;
    }

    protected void setSamplingRate(int samplingRate) {
        this.samplingRate = samplingRate;
    }

    public abstract void requestVoltage(OnRequestCompleted<Integer> orc);

    public abstract void monitorVoltage(OnEventOccurred<Integer> oeo);

    public abstract void unmonitorVoltage();

    public abstract void changeSamplingRate(int samplingRate, OnRequestCompleted<Boolean> orc);

}

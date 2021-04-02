package be.kuleuven.msec.iot.iotframework.generic.devicelayer;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Device_constants;

public abstract class LoudnessSensor extends VirtualIoTDevice {

    private double loudness;
    private int samplingRate;

    public LoudnessSensor(String systemID) {
        super(Device_constants.TYPE_LOUDNESS_SENSOR, systemID);
    }

    protected double getLoudness() {
        return loudness;
    }

    protected void setLoudness(double loudness) {
        this.loudness  = loudness;
    }

    protected int getSamplingRate() {
        return samplingRate;
    }

    protected void setSamplingRate(int samplingRate) {
        this.samplingRate = samplingRate;
    }

    public abstract void requestLoudness(OnRequestCompleted<Double> orc);

    public abstract void monitorLoudness(OnEventOccurred<Double> oeo);

    public abstract void unmonitorLoudness();

    public abstract void changeSamplingRate(int samplingRate, OnRequestCompleted<Boolean> orc);
}
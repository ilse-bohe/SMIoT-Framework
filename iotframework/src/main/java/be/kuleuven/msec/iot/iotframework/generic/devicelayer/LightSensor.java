package be.kuleuven.msec.iot.iotframework.generic.devicelayer;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.componentlayer.Component;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Device_constants;

/**
 * Created by ilsebohe on 24/10/2017.
 */

public abstract class LightSensor extends VirtualIoTDevice {
    double intensity;
    int samplingRate;

    public LightSensor(String systemID) {
        super(Device_constants.TYPE_LIGHT_SENSOR,systemID);
    }

    protected int getSamplingRate() {
        return samplingRate;
    }

    protected void setSamplingRate(int samplingRate) {
        this.samplingRate = samplingRate;
    }

    protected double getIntensity() {
        return intensity;
    }

    protected void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public abstract void requestLightIntensity(OnRequestCompleted<Double> orc);

    public abstract void monitorLightIntensity(OnEventOccurred<Double> oeo);

    public abstract void unmonitorLightIntensity();

    public abstract void changeSamplingRate(int samplingRate, OnRequestCompleted<Boolean> orc);

}

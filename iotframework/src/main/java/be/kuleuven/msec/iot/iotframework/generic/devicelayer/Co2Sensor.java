package be.kuleuven.msec.iot.iotframework.generic.devicelayer;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Device_constants;

public abstract class Co2Sensor extends VirtualIoTDevice {
    double co2Value;
    int samplingRate;

    public Co2Sensor(String systemID){
        super(Device_constants.TYPE_CO2_SENSOR,systemID);
    }

    public double getCo2Value() {
        return co2Value;
    }

    public void setCo2Value(double co2Value) {
        this.co2Value = co2Value;
    }

    public int getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(int samplingRate) {
        this.samplingRate = samplingRate;
    }

    public abstract void requestCo2Value(OnRequestCompleted<Double> orc);

    public abstract void monitorCo2Value(OnEventOccurred<Double> oeo);

    public abstract void unmonitorCo2Value();

    public abstract void changeSamplingRate(int samplingRate, OnRequestCompleted<Boolean> orc);

}

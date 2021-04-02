package be.kuleuven.msec.iot.iotframework.generic.devicelayer;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Device_constants;

public abstract class ColorSensor extends VirtualIoTDevice{
    String color;
    int samplingRate;

    public ColorSensor(String systemID){
        super(Device_constants.TYPE_COLOR_SENSOR, systemID);
    }

    public abstract void requestColor(OnRequestCompleted<String> orc);

    public abstract void monitorColor(OnEventOccurred<String> oeo);

    public abstract void unmonitorColor();

    public abstract void changeSamplingRate(int samplingRate, OnRequestCompleted<Boolean> orc);


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(int samplingRate) {
        this.samplingRate = samplingRate;
    }
}

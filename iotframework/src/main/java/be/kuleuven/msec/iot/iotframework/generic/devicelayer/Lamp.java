package be.kuleuven.msec.iot.iotframework.generic.devicelayer;


import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Device_constants;

/**
 * Created by michielwillocx on 11/09/17.
 */

public abstract class Lamp extends VirtualIoTDevice {

    String color;
    int brightness;
    boolean on;
    boolean online;
    double hue;
    int saturation;
    int temperature;

    public Lamp(String systemID) {
        super(Device_constants.TYPE_LAMP,systemID);
    }


    public Lamp(String systemID, String color, int brightness, boolean on, boolean online, double hue, int saturation, int temperature) {
        super(Device_constants.TYPE_LAMP,systemID);
        this.color = color;
        this.brightness = brightness;
        this.on = on;
        this.online = online;
        this.hue = hue;
        this.saturation = saturation;
        this.temperature = temperature;
    }

    protected String getColor() {
        return color;
    }

    protected void setColor(String color) {
        this.color = color;
    }

    protected int getBrightness() {
        return brightness;
    }

    protected void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    protected boolean isOn() {
        return on;
    }

    protected void setOn(boolean on) {
        this.on = on;
    }

    protected boolean isOnline() {
        return online;
    }

    protected void setOnline(boolean online) {
        this.online = online;
    }

    protected double getHue() {
        return hue;
    }

    protected void setHue(double hue) {
        this.hue = hue;
    }

    protected int getSaturation() {
        return saturation;
    }

    protected void setSaturation(int saturation) {
        this.saturation = saturation;
    }

    protected int getTemperature() {
        return temperature;
    }

    protected void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public abstract void turnOn(OnRequestCompleted<Boolean> orc);

    public abstract void turnOff(OnRequestCompleted<Boolean> orc);

    public abstract void requestStatus (OnRequestCompleted<Boolean> orc);

    public abstract void changeColor(String RGBcolor, OnRequestCompleted<Boolean> orc);

    public abstract void requestColor (OnRequestCompleted<String> orc);

    public abstract void changeBrightness(int brightness, OnRequestCompleted<Boolean> orc);

    public abstract void requestBrightness (OnRequestCompleted<Integer> orc);

    public abstract void changeHue(double hue, OnRequestCompleted<Boolean> orc);

    public abstract void requestHue (OnRequestCompleted<Double> orc);

    public abstract void changeSaturation(int saturation, OnRequestCompleted<Boolean> orc);

    public abstract void requestSaturation (OnRequestCompleted<Integer> orc);

    public abstract void changeTemperature(int temperature, OnRequestCompleted<Boolean> orc);

    public abstract void requestTemperature (OnRequestCompleted<Integer> orc);

    public abstract void updateLampInfo(OnRequestCompleted<Boolean> orc);

    public abstract void monitorLampInfo(OnEventOccurred<Lamp> oeo);

}

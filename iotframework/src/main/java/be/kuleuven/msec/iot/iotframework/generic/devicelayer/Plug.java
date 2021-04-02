package be.kuleuven.msec.iot.iotframework.generic.devicelayer;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;

public abstract class Plug extends  VirtualIoTDevice{


    public Plug(String systemID) {
        super("SMART_PLUG", systemID);
    }

    public abstract void turnOn(OnRequestCompleted<Boolean> orc);

    public abstract void turnOff(OnRequestCompleted<Boolean> orc);

    public abstract void getCurrent(OnRequestCompleted<Double> orc);

    public abstract void getVoltage(OnRequestCompleted<Double> orc);

    public abstract void getCurrentPowerConsumption(OnRequestCompleted<Double> orc);

    public abstract void getTotalPowerConsumption(OnRequestCompleted<Double> orc);



}

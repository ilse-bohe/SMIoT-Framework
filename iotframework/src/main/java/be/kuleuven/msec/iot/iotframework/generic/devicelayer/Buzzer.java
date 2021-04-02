package be.kuleuven.msec.iot.iotframework.generic.devicelayer;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Device_constants;

/**
 * Created by ilsebohe on 10/11/2017.
 */

public abstract class Buzzer extends VirtualIoTDevice {
    boolean on;

    public Buzzer(String systemID) {
        super(Device_constants.TYPE_BUZZER,systemID);
    }

    private boolean isOn() {
        return on;
    }

    private void setOn(boolean on) {
        this.on = on;
    }

    public abstract void turnOn(OnRequestCompleted<Boolean> orc);

    public abstract void turnOff(OnRequestCompleted<Boolean> orc);
}

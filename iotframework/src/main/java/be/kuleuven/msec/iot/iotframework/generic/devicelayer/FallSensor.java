package be.kuleuven.msec.iot.iotframework.generic.devicelayer;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Device_constants;

/**
 * Created by Thomas on 7/05/2018.
 */

public abstract class FallSensor extends VirtualIoTDevice {
    public FallSensor(String systemID) {
        super(Device_constants.TYPE_FALL_SENSOR, systemID);
    }

    public abstract void detectFall(OnEventOccurred<Boolean> orc);
}

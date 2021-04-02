package be.kuleuven.msec.iot.iotframework.implementations.SMIoT.smiotjsonmodel;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.VirtualIoTDevice;

/**
 * Created by ilsebohe on 10/01/2018.
 */

public class SMIoTDeviceJSONModel extends VirtualIoTDevice {
    public SMIoTDeviceJSONModel() {
    }

    @Override
    public void isReachable(OnRequestCompleted<Boolean> orc) {
    }

    @Override
    public void monitorReachability(OnEventOccurred<Boolean> oeo) {
    }

    @Override
    public void connect(OnRequestCompleted<Boolean> orc) {
    }

    @Override
    public void disconnect(OnRequestCompleted<Boolean> orc) {
    }
}

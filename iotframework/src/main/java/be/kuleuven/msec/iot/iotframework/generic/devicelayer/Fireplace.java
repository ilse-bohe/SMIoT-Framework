package be.kuleuven.msec.iot.iotframework.generic.devicelayer;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.VirtualIoTDevice;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Device_constants;

public abstract class Fireplace extends VirtualIoTDevice {

    boolean burning;

    public Fireplace(String systemID) {
        super(Device_constants.TYPE_FIREPLACE,systemID);
    }


    public abstract void light(OnRequestCompleted<Boolean> orc);

    public abstract void extinguish(OnRequestCompleted<Boolean> orc);

    public abstract void requestState(OnRequestCompleted<Boolean> orc);

    protected boolean isBurning() { return burning; }

    protected void setBurning(boolean burning) {
        this.burning = burning;
    }
}

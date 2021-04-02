package be.kuleuven.msec.iot.iotframework.generic.devicelayer;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Device_constants;

/**
 * Created by ilsebohe on 21/12/2017.
 */

public abstract class Lock extends VirtualIoTDevice {

    boolean locked;

    public Lock(String systemID) {
        super(Device_constants.TYPE_LOCK,systemID);
    }


    public abstract void open(OnRequestCompleted<Boolean> orc);

    public abstract void close(OnRequestCompleted<Boolean> orc);

    public abstract void requestState(OnRequestCompleted<Boolean> orc);

    protected boolean isLocked() {
        return locked;
    }

    protected void setLocked(boolean locked) {
        this.locked = locked;
    }
}

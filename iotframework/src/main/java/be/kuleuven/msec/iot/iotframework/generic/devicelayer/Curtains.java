package be.kuleuven.msec.iot.iotframework.generic.devicelayer;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Device_constants;

public abstract class Curtains extends VirtualIoTDevice {
    boolean open;

    public Curtains(String systemID) {
        super(Device_constants.TYPE_CURTAINS,systemID);
    }

    public abstract void open(OnRequestCompleted<Boolean> orc);

    public abstract void close(OnRequestCompleted<Boolean> orc);

    public abstract void requestState(OnRequestCompleted<Boolean> orc);

    protected boolean areOpen() {
        return open;
    }

    protected void setOpen(boolean open) {
        this.open = open;
    }

}

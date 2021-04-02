package be.kuleuven.msec.iot.iotframework.generic.devicelayer;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;


import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by michielwillocx on 18/09/17.
 */

public abstract class VirtualIoTDevice {

    protected String systemID;
    protected transient Scheduler observeScheduler;
    protected String type;

    public VirtualIoTDevice() {
    }

    public VirtualIoTDevice(String type, String systemID) {
        this.observeScheduler = AndroidSchedulers.mainThread();
        this.systemID = systemID;
        this.type=type;
    }
    public String getSystemID() {
        return systemID;
    }

    public void setSystemID(String systemID) {
        this.systemID = systemID;
    }

    public VirtualIoTDevice observeOn(Scheduler observeOnScheduler) {
        this.observeScheduler = observeOnScheduler;
        return this;
    }

    public abstract void isReachable(OnRequestCompleted<Boolean> orc);

    public abstract void monitorReachability(OnEventOccurred<Boolean> oeo);

    public abstract void connect(OnRequestCompleted<Boolean> orc);

    public abstract void disconnect(OnRequestCompleted<Boolean> orc);

    // other methods

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
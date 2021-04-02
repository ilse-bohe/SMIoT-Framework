package be.kuleuven.msec.iot.iotframework.generic.devicelayer;


import java.util.ArrayList;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.exceptions.VirtualIoTDeviceNotFoundException;
import be.kuleuven.msec.iot.iotframework.systemmanagement.jsonmodel.JSMDevice;

/**
 * Created by michielwillocx on 20/09/17.
 */

public abstract class VirtualIoTConnector {

    String systemID;
    protected ArrayList<VirtualIoTDevice> connectedDevices;

    public VirtualIoTConnector(String systemID) {
        this.systemID = systemID;
        this.connectedDevices= new ArrayList<>();
    }

    public String getSystemID() {
        return systemID;
    }

    public void setSystemID(String systemID) {
        this.systemID = systemID;
    }

    public VirtualIoTDevice getConnectedDeviceBasedOnSystemID(String systemID){
        for (VirtualIoTDevice device :
                connectedDevices) {
            System.out.println(device.getSystemID()+" " + device.getType() + " " + systemID);
            if(device.getSystemID().equals(systemID)) return device;
        }
        throw new VirtualIoTDeviceNotFoundException(this.getClass().getName(), this.getSystemID(), systemID);
    }

    public ArrayList<VirtualIoTDevice> getConnectedDevices() {
        return connectedDevices;
    }

    public abstract void updateConnectedDeviceList(OnRequestCompleted<Boolean> orc, ArrayList<JSMDevice> devices);

    public abstract void initialize(OnRequestCompleted orc);

    public abstract void isReachable(OnRequestCompleted<Boolean> orc);

    public abstract void monitorReachability(OnEventOccurred<Boolean> oeo);

    public abstract void connect(OnRequestCompleted<Boolean> orc);

    public abstract void disconnect(OnRequestCompleted<Boolean> orc);
}

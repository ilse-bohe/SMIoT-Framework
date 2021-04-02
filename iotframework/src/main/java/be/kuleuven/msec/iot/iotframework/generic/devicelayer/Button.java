package be.kuleuven.msec.iot.iotframework.generic.devicelayer;


import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Device_constants;

/**
 * Created by michielwillocx on 12/09/17.
 */

public abstract class Button extends VirtualIoTDevice {

    public Button(String systemID){
        super(Device_constants.TYPE_BUTTON,systemID);
    }


    public abstract void monitorButton(OnEventOccurred<Boolean> oeo);
    public abstract void unmonitorButton();

}

package be.kuleuven.msec.iot.iotframework.exceptions;

import be.kuleuven.msec.iot.iotframework.generic.devicelayer.HumiditySensor;

/**
 * Created by ilsebohe on 30/11/2017.
 */

public class VirtualIoTDeviceNotFoundException extends RuntimeException {

    public VirtualIoTDeviceNotFoundException(String connectorClassName, String connectorSystemID, String deviceID) {

        super("Could not find virtual IoT device '" + deviceID + "' in "+connectorClassName + " '" + connectorSystemID +"'");

    }
}

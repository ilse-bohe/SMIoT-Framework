package be.kuleuven.msec.iot.iotframework.implementations.sensorkits.xiaomi;

import java.util.ArrayList;
import java.util.Map;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.VirtualIoTConnector;
import be.kuleuven.msec.iot.iotframework.implementations.humiditysensors.XiaomiHumiditySensor;
import be.kuleuven.msec.iot.iotframework.implementations.temperaturesensors.XiaomiTemperatureSensor;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Device_constants;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Model_constants;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.VersaSense_constants;
import be.kuleuven.msec.iot.iotframework.systemmanagement.jsonmodel.JSMDevice;

public class XiaomiGateway extends VirtualIoTConnector {

    private String macAddress;

    private  XiaomiGateway thisGateway;
    private XiaomiBluetoothLeService bluetoothLeService;

    public XiaomiBluetoothLeService getBluetoothLeService() {
        return bluetoothLeService;
    }

    public XiaomiGateway(String systemID, Map<String, String> settings){
        super(systemID);
        this.macAddress=settings.get("mac");

        thisGateway=this;
    }



    @Override
    public void initialize(OnRequestCompleted orc) {
        bluetoothLeService= new XiaomiBluetoothLeService(macAddress);
        orc.onSuccess(true);
    }

    @Override
    public void updateConnectedDeviceList(OnRequestCompleted<Boolean> orc, ArrayList<JSMDevice> devices) {
        String SystemID = getSystemIDofMac_type(devices, macAddress, "Temperature");

        XiaomiTemperatureSensor temp = new XiaomiTemperatureSensor(getSystemIDofMac_type(devices, macAddress,"Temperature" ),thisGateway);
        connectedDevices.add(temp);
        XiaomiHumiditySensor hum = new XiaomiHumiditySensor(getSystemIDofMac_type(devices, macAddress,"Humidity" ),thisGateway);
        connectedDevices.add(hum);

        orc.onSuccess(true);


    }

    private String getSystemIDofMac_type(ArrayList<JSMDevice> devices, String macAddress, String type) {
        for (JSMDevice dev :devices) {
            if (dev.getModel().equals(Model_constants.MODEL_XIAOMI)){
                if(dev.getConnector().equals(thisGateway.getSystemID())) {
                    if(dev.getType().equals(Device_constants.TYPE_TEMPERATURE_SENSOR) && type.equals("Temperature")) return dev.getSystemID();
                    if(dev.getType().equals(Device_constants.TYPE_HUMIDITY_SENSOR) && type.equals("Humidity") ) return dev.getSystemID();
                }
            }
        }
        System.out.println("no match");
        return null;
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

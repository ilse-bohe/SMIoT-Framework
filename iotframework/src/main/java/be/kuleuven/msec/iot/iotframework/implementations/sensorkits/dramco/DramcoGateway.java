package be.kuleuven.msec.iot.iotframework.implementations.sensorkits.dramco;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.VirtualIoTConnector;
import be.kuleuven.msec.iot.iotframework.implementations.buttons.DramcoButton;
import be.kuleuven.msec.iot.iotframework.implementations.co2sensors.DramcoCo2Sensor;
import be.kuleuven.msec.iot.iotframework.implementations.colorsensors.DramcoColorSensor;
import be.kuleuven.msec.iot.iotframework.implementations.curtains.DramcoCurtains;
import be.kuleuven.msec.iot.iotframework.implementations.fireplaces.DramcoFireplace;
import be.kuleuven.msec.iot.iotframework.implementations.lightsensors.DramcoLightSensor;
import be.kuleuven.msec.iot.iotframework.implementations.locks.DramcoLock;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense.VersaSenseMonitoringService;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Device_constants;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Dramco_constants;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Model_constants;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.VersaSense_constants;
import be.kuleuven.msec.iot.iotframework.systemmanagement.jsonmodel.JSMConfiguration;
import be.kuleuven.msec.iot.iotframework.systemmanagement.jsonmodel.JSMDevice;

public class DramcoGateway extends VirtualIoTConnector {

    private String address;

    private DramcoGateway thisGateway;
    private DramcoMonitoringService monitoringService;

    public DramcoGateway(String systemID, Map<String, String> settings) {
        super(systemID);
        this.address=settings.get("address");
        thisGateway=this;
    }

    @Override
    public void initialize(OnRequestCompleted orc) {
        // nothing to do here
        orc.onSuccess(true);
    }

    @Override
    public void updateConnectedDeviceList(OnRequestCompleted<Boolean> orc, ArrayList<JSMDevice> devices) {
        //TODO now hardcoded from https://github.com/DRAMCO/DRAMCO-mini-house/wiki/MQTT-Format

        List<DramcoSensor> sensors = new ArrayList<DramcoSensor>();
        sensors.add(new DramcoSensor("co2sensor", "co2sensor-0c02"));
        sensors.add(new DramcoSensor("doorlock", "doorlock-10cb"));
        sensors.add(new DramcoSensor("colorsensor", "colorsensor-8421"));
        sensors.add(new DramcoSensor("luxsensor", "luxsensor-4f23"));
        sensors.add(new DramcoSensor("fireplace", "fireplace-f17e"));
        sensors.add(new DramcoSensor("curtains", "curtains-09e4"));
        sensors.add(new DramcoSensor("button", "button-9b65"));


        for (DramcoSensor s:sensors) {
            String deviceSystemID = getSystemIDofSensorID(devices, s.getId());
            if(deviceSystemID!=null) {
                switch(s.getType()){
                    case Dramco_constants.LOCK: connectedDevices.add(new DramcoLock(deviceSystemID, s.getId(), thisGateway)); break;
                    case Dramco_constants.CO2: connectedDevices.add(new DramcoCo2Sensor(deviceSystemID, s.getId(), thisGateway)); break;
                    case Dramco_constants.CURTAIN: connectedDevices.add(new DramcoCurtains(deviceSystemID, s.getId(), thisGateway)); break;
                    case Dramco_constants.COLOR: connectedDevices.add(new DramcoColorSensor(deviceSystemID, s.getId(), thisGateway)); break;
                    case Dramco_constants.LUX: connectedDevices.add(new DramcoLightSensor(deviceSystemID, s.getId(), thisGateway)); break;
                    case Dramco_constants.BUTTON: connectedDevices.add(new DramcoButton(deviceSystemID, s.getId(), thisGateway)); break;
                    case Dramco_constants.FIREPLACE: connectedDevices.add(new DramcoFireplace(deviceSystemID, s.getId(), thisGateway)); break;


                }
            }
        }

        orc.onSuccess(true);
    }

    private String getSystemIDofSensorID(ArrayList<JSMDevice> devices, String id) {
        for (JSMDevice dev :devices) {
            if (dev.getModel().equals(Model_constants.MODEL_DRAMCO)){

                if(dev.getSettings().get("id").equals(id)) {
                    System.out.println("match");

                    return dev.getSystemID();
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

    public DramcoMonitoringService getMonitoringService() {
        if (monitoringService == null) {
            monitoringService = new DramcoMonitoringService(address);
        }
        return monitoringService;
    }


    private class DramcoSensor {
        String type;
        String id;

        public DramcoSensor(String type, String id) {
            this.type=type;
            this.id=id;
        }

        public String getType() {
            return type;
        }

        public String getId() {
            return id;
        }
    }
}

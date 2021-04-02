package be.kuleuven.msec.iot.iotframework.implementations.colorsensors;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.ColorSensor;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.VirtualIoTDevice;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.dramco.DramcoGateway;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Dramco_constants;

public class DramcoColorSensor extends ColorSensor {

    private DramcoGateway gateway;
    private String sensorID;

    public DramcoColorSensor(String systemID, String sensorID, DramcoGateway gateway) {
        super(systemID);
        this.gateway = gateway;
        this.sensorID = sensorID;
    }

    @Override
    public void requestColor(OnRequestCompleted<String> orc) {
        monitorColor(new OnEventOccurred<String>() {
            @Override
            public void onUpdate(String response) {
                orc.onSuccess(response);
                unmonitorColor();
            }
        });
    }

    @Override
    public void monitorColor(OnEventOccurred<String> oeo) {
        gateway.getMonitoringService().subscribe(Dramco_constants.COLOR, sensorID, oeo);
    }

    @Override
    public void unmonitorColor() {
        gateway.getMonitoringService().unsubscribe(Dramco_constants.COLOR, sensorID);

    }

    @Override
    public void changeSamplingRate(int samplingRate, OnRequestCompleted<Boolean> orc) {

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

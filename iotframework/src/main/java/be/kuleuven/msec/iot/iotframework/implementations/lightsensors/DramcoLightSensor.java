package be.kuleuven.msec.iot.iotframework.implementations.lightsensors;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.LightSensor;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.VirtualIoTDevice;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.dramco.DramcoGateway;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Dramco_constants;

public class DramcoLightSensor extends LightSensor {
    private DramcoGateway gateway;
    private String sensorID;



    public DramcoLightSensor(String systemID, String sensorID, DramcoGateway gateway) {
        super(systemID);
        this.gateway=gateway;
        this.sensorID=sensorID;
    }

    @Override
    public void requestLightIntensity(OnRequestCompleted<Double> orc) {
        monitorLightIntensity(new OnEventOccurred<Double>() {
            @Override
            public void onUpdate(Double response) {
                orc.onSuccess(response);
                unmonitorLightIntensity();
            }
        });
    }

    @Override
    public void monitorLightIntensity(OnEventOccurred<Double> oeo) {
        gateway.getMonitoringService().subscribe(Dramco_constants.LUX, sensorID, new OnEventOccurred<String>() {
            @Override
            public void onUpdate(String response) {
                System.out.println(sensorID+ ": "+response);
                oeo.onUpdate(Double.parseDouble(response));
            }
        });
    }

    @Override
    public void unmonitorLightIntensity() {
        gateway.getMonitoringService().unsubscribe(Dramco_constants.LUX, sensorID);

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

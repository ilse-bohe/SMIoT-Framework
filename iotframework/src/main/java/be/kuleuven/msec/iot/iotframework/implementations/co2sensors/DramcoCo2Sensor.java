package be.kuleuven.msec.iot.iotframework.implementations.co2sensors;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Co2Sensor;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.dramco.DramcoGateway;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Dramco_constants;

public class DramcoCo2Sensor extends Co2Sensor {
    private DramcoGateway gateway;
    private String sensorID;



    public DramcoCo2Sensor(String systemID, String sensorID, DramcoGateway gateway) {
        super(systemID);
        this.gateway=gateway;
        this.sensorID=sensorID;
    }

    @Override
    public void requestCo2Value(OnRequestCompleted<Double> orc) {
        monitorCo2Value(new OnEventOccurred<Double>() {
            @Override
            public void onUpdate(Double response) {
                orc.onSuccess(response);
                unmonitorCo2Value();
            }
        });
    }

    @Override
    public void monitorCo2Value(OnEventOccurred<Double> oeo) {
        gateway.getMonitoringService().subscribe(Dramco_constants.CO2, sensorID, new OnEventOccurred<String>() {
            @Override
            public void onUpdate(String response) {
                System.out.println(sensorID+ ": "+response);
                oeo.onUpdate(Double.parseDouble(response));
            }
        });
    }

    @Override
    public void unmonitorCo2Value() {
        gateway.getMonitoringService().unsubscribe(Dramco_constants.CO2, sensorID);
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

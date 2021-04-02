package be.kuleuven.msec.iot.iotframework.implementations.fireplaces;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Fireplace;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.dramco.DramcoGateway;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Dramco_constants;

public class DramcoFireplace extends Fireplace {
    private DramcoGateway gateway;
    private String sensorID;

    public DramcoFireplace(String systemID, String sensorID, DramcoGateway gateway) {
        super(systemID);
        this.gateway = gateway;
        this.sensorID = sensorID;
    }

    @Override
    public void light(OnRequestCompleted<Boolean> orc) {
        gateway.getMonitoringService().subscribe(Dramco_constants.FIREPLACE, sensorID, new OnEventOccurred<String>() {
            @Override
            public void onUpdate(String response) {
                if (!response.equals("#000000")) {
                    gateway.getMonitoringService().unsubscribe(Dramco_constants.FIREPLACE, sensorID);
                    orc.onSuccess(true);
                }
            }
        });
        gateway.getMonitoringService().publish(Dramco_constants.FIREPLACE, sensorID, "#ff5500");

    }

    @Override
    public void extinguish(OnRequestCompleted<Boolean> orc) {
        gateway.getMonitoringService().subscribe(Dramco_constants.FIREPLACE, sensorID, new OnEventOccurred<String>() {
            @Override
            public void onUpdate(String response) {
                if (response.equals("#000000")) {
                    gateway.getMonitoringService().unsubscribe(Dramco_constants.FIREPLACE, sensorID);
                    orc.onSuccess(true);
                }
            }
        });
        gateway.getMonitoringService().publish(Dramco_constants.FIREPLACE, sensorID, "#000000");
    }

    @Override
    public void requestState(OnRequestCompleted<Boolean> orc) {
        gateway.getMonitoringService().subscribe(Dramco_constants.FIREPLACE, sensorID, new OnEventOccurred<String>() {
            @Override
            public void onUpdate(String response) {
                gateway.getMonitoringService().unsubscribe(Dramco_constants.FIREPLACE, sensorID);
                if (!response.equals("#000000")) {
                    orc.onSuccess(true);
                } else orc.onSuccess(false);
            }
        });
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

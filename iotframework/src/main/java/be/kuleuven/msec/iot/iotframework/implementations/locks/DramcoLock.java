package be.kuleuven.msec.iot.iotframework.implementations.locks;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Lock;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.dramco.DramcoGateway;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Dramco_constants;

public class DramcoLock extends Lock {

    private DramcoGateway gateway;
    private String sensorID;

    public DramcoLock(String systemID, String sensorID, DramcoGateway gateway) {
        super(systemID);
        this.gateway = gateway;
        this.sensorID = sensorID;
    }

    @Override
    public void open(OnRequestCompleted<Boolean> orc) {
        gateway.getMonitoringService().subscribe(Dramco_constants.LOCK, sensorID, new OnEventOccurred<String>() {
            @Override
            public void onUpdate(String response) {
                if (response.equals("open")) {
                    gateway.getMonitoringService().unsubscribe(Dramco_constants.LOCK, sensorID);
                    orc.onSuccess(true);
                }
            }
        });
        gateway.getMonitoringService().publish(Dramco_constants.LOCK, sensorID, "open");
    }

    @Override
    public void close(OnRequestCompleted<Boolean> orc) {
        gateway.getMonitoringService().subscribe(Dramco_constants.LOCK, sensorID, new OnEventOccurred<String>() {
            @Override
            public void onUpdate(String response) {
                if (response.equals("closed")) {
                    gateway.getMonitoringService().unsubscribe(Dramco_constants.LOCK, sensorID);
                    orc.onSuccess(true);
                }
            }
        });
        gateway.getMonitoringService().publish(Dramco_constants.LOCK, sensorID, "closed");
    }

    @Override
    public void requestState(OnRequestCompleted<Boolean> orc) {
        gateway.getMonitoringService().subscribe(Dramco_constants.LOCK, sensorID, new OnEventOccurred<String>() {
            @Override
            public void onUpdate(String response) {
                gateway.getMonitoringService().unsubscribe(Dramco_constants.LOCK, sensorID);
                if (response.equals("closed")) {
                    orc.onSuccess(false);
                } else orc.onSuccess(true);
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

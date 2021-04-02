package be.kuleuven.msec.iot.iotframework.implementations.curtains;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Curtains;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.dramco.DramcoGateway;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Dramco_constants;

public class DramcoCurtains extends Curtains {


    private DramcoGateway gateway;
    private String sensorID;

    public DramcoCurtains(String systemID, String sensorID, DramcoGateway gateway) {
        super(systemID);
        this.gateway = gateway;
        this.sensorID = sensorID;
    }

    @Override
    public void open(OnRequestCompleted<Boolean> orc) {
        gateway.getMonitoringService().subscribe(Dramco_constants.CURTAIN, sensorID, new OnEventOccurred<String>() {
            @Override
            public void onUpdate(String response) {
                if (response.equals("up")) {
                    gateway.getMonitoringService().unsubscribe(Dramco_constants.CURTAIN, sensorID);
                    orc.onSuccess(true);
                }
            }
        });
        gateway.getMonitoringService().publish(Dramco_constants.CURTAIN, sensorID, "up");
    }

    @Override
    public void close(OnRequestCompleted<Boolean> orc) {
        gateway.getMonitoringService().subscribe(Dramco_constants.CURTAIN, sensorID, new OnEventOccurred<String>() {
            @Override
            public void onUpdate(String response) {
                if (response.equals("down")) {
                    gateway.getMonitoringService().unsubscribe(Dramco_constants.CURTAIN, sensorID);
                    orc.onSuccess(true);
                }
            }
        });
        gateway.getMonitoringService().publish(Dramco_constants.CURTAIN, sensorID, "down");

    }

    @Override
    public void requestState(OnRequestCompleted<Boolean> orc) {
        gateway.getMonitoringService().subscribe(Dramco_constants.CURTAIN, sensorID, new OnEventOccurred<String>() {
            @Override
            public void onUpdate(String response) {
                if (response.equals("down")) {
                    gateway.getMonitoringService().unsubscribe(Dramco_constants.CURTAIN, sensorID);
                    orc.onSuccess(false);
                }else  orc.onSuccess(true);
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

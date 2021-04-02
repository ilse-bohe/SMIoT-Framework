package be.kuleuven.msec.iot.iotframework.implementations.buttons;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Button;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.VirtualIoTDevice;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.dramco.DramcoGateway;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Dramco_constants;

public class DramcoButton extends Button {
    private DramcoGateway gateway;
    private String sensorID;

    public DramcoButton(String systemID, String sensorID, DramcoGateway gateway) {
        super(systemID);
        this.gateway=gateway;
        this.sensorID=sensorID;
    }

    @Override
    public void monitorButton(OnEventOccurred<Boolean> oeo) {
        gateway.getMonitoringService().subscribe(Dramco_constants.BUTTON, sensorID, new OnEventOccurred<String>() {
            @Override
            public void onUpdate(String response) {
                if (response.equals("pressed")) oeo.onUpdate(true);
                else oeo.onUpdate(false);
            }
        });
    }

    @Override
    public void unmonitorButton() {
        gateway.getMonitoringService().unsubscribe(Dramco_constants.BUTTON, sensorID);

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

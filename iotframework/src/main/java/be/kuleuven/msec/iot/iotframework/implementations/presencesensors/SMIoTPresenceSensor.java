package be.kuleuven.msec.iot.iotframework.implementations.presencesensors;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.PresenceSensor;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.PressureSensor;
import be.kuleuven.msec.iot.iotframework.implementations.SMIoT.SMIoTGateway;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ilsebohe on 11/01/2018.
 */

public class SMIoTPresenceSensor extends PresenceSensor {

    SMIoTGateway gateway;

    public SMIoTPresenceSensor(String systemID, SMIoTGateway gateway) {
        super(systemID);
        this.gateway=gateway;

    }

    @Override
    public void requestPresence(OnRequestCompleted<Boolean> orc) {
        gateway.getRestService().getValue(type, systemID).subscribeOn(Schedulers.io()).observeOn(observeScheduler).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                boolean result = s.equals("true");
                setPresence(result);
                orc.onSuccess(result);
            }
        });
    }

    @Override
    public void monitorPresence(OnEventOccurred<Boolean> oeo) {
        gateway.getMonitoringService().subscribe(type, systemID, new OnEventOccurred<String>() {
            @Override
            public void onUpdate(String response) {
                boolean value = response.equals("true");
                setPresence(value);
                oeo.onUpdate(value);
            }
        });
    }

    @Override
    public void unmonitorPresence() {
        gateway.getMonitoringService().unsubscribe(type, systemID);
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

package be.kuleuven.msec.iot.iotframework.implementations.temperaturesensors;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.TemperatureSensor;
import be.kuleuven.msec.iot.iotframework.implementations.SMIoT.SMIoTGateway;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ilsebohe on 11/01/2018.
 */

public class SMIotTemperatureSensor extends TemperatureSensor {
    SMIoTGateway gateway;

    public SMIotTemperatureSensor(String systemID, SMIoTGateway gateway) {
        super(systemID);
        this.gateway = gateway;
    }

    @Override
    public void requestTemperature(OnRequestCompleted<Double> orc) {
        gateway.getRestService().getValue(type, systemID).subscribeOn(Schedulers.io()).observeOn(observeScheduler).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                double value = Double.parseDouble(s);
                setTemperature(value);
                orc.onSuccess(value);
            }
        });
    }

    @Override
    public void monitorTemperature(OnEventOccurred<Double> oeo) {
        gateway.getMonitoringService().subscribe(type, systemID, new OnEventOccurred<String>() {
            @Override
            public void onUpdate(String response) {
                double value = Double.parseDouble(response);
                setTemperature(value);
                oeo.onUpdate(value);
            }
        });
    }

    @Override
    public void unmonitorTemperature() {
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

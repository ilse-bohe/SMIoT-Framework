package be.kuleuven.msec.iot.iotframework.implementations.lamps.smiotlamp;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Lamp;
import be.kuleuven.msec.iot.iotframework.implementations.SMIoT.SMIoTGateway;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Device_constants;
import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ilsebohe on 09/01/2018.
 */

public class SMIoTLamp extends Lamp {

    SMIoTGateway gateway;

    public SMIoTLamp(String systemID, SMIoTGateway gateway) {
        super(systemID);
        this.gateway = gateway;
    }

    @Override
    public void turnOn(OnRequestCompleted<Boolean> orc) {
        gateway.getRestService().turnLampOn(systemID).subscribeOn(Schedulers.io()).observeOn(observeScheduler).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                setOn(true);
                orc.onSuccess(true);
            }
        });

    }

    @Override
    public void turnOff(OnRequestCompleted<Boolean> orc) {
        gateway.getRestService().turnLampOff(systemID).subscribeOn(Schedulers.io()).observeOn(observeScheduler).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                setOn(false);
                orc.onSuccess(true);
            }
        });
    }

    @Override
    public void requestStatus(OnRequestCompleted<Boolean> orc) {
        //TODO request from lamps
        orc.onSuccess(isOn());
    }

    @Override
    public void changeColor(String RGBcolor, OnRequestCompleted<Boolean> orc) {
        gateway.getRestService().changeLampColor(systemID, RGBcolor).subscribeOn(Schedulers.io()).observeOn(observeScheduler).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                setColor(RGBcolor);
                orc.onSuccess(true);
            }
        });
    }

    @Override
    public void requestColor(OnRequestCompleted<String> orc) {
        //TODO request from lamps
        orc.onSuccess(getColor());
    }

    @Override
    public void changeBrightness(int brightness, OnRequestCompleted<Boolean> orc) {
        gateway.getRestService().changeLampBrightness(systemID, brightness).subscribeOn(Schedulers.io()).observeOn(observeScheduler).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                setBrightness(brightness);
                orc.onSuccess(true);
            }
        });
    }

    @Override
    public void requestBrightness(OnRequestCompleted<Integer> orc) {
        //TODO request from lamps
        orc.onSuccess(getBrightness());
    }

    @Override
    public void changeHue(double hue, OnRequestCompleted<Boolean> orc) {
        gateway.getRestService().changeLampHue(systemID, hue).subscribeOn(Schedulers.io()).observeOn(observeScheduler).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                setHue(hue);
                orc.onSuccess(true);
            }
        });
    }

    @Override
    public void requestHue(OnRequestCompleted<Double> orc) {
        //TODO request from lamps
        orc.onSuccess(getHue());
    }

    @Override
    public void changeSaturation(int saturation, OnRequestCompleted<Boolean> orc) {
        gateway.getRestService().changeLampSaturation(systemID, saturation).subscribeOn(Schedulers.io()).observeOn(observeScheduler).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                setSaturation(saturation);
                orc.onSuccess(true);
            }
        });
    }

    @Override
    public void requestSaturation(OnRequestCompleted<Integer> orc) {
        //TODO request from lamps
        orc.onSuccess(getSaturation());
    }

    @Override
    public void changeTemperature(int temperature, OnRequestCompleted<Boolean> orc) {
        gateway.getRestService().changeLampTemperature(systemID, temperature).subscribeOn(Schedulers.io()).observeOn(observeScheduler).subscribe(new Action() {
            @Override
            public void run() throws Exception {
                setTemperature(temperature);
                orc.onSuccess(true);
            }
        });
    }

    @Override
    public void requestTemperature(OnRequestCompleted<Integer> orc) {
        //TODO request from lamps
        orc.onSuccess(getTemperature());
    }

    @Override
    public void updateLampInfo(OnRequestCompleted<Boolean> orc) {
    }

    @Override
    public void monitorLampInfo(OnEventOccurred<Lamp> oeo) {
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

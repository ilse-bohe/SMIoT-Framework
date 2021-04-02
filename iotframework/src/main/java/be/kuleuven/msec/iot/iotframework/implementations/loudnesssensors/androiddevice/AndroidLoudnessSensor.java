package be.kuleuven.msec.iot.iotframework.implementations.loudnesssensors.androiddevice;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.LoudnessSensor;
import io.reactivex.schedulers.Schedulers;

public class AndroidLoudnessSensor extends LoudnessSensor {
    private AndroidLoudnessConnector gateway;

    AndroidLoudnessSensor(String systemID, AndroidLoudnessConnector ac){
        super(systemID);
        this.gateway=ac;
    }

    @Override
    public void requestLoudness(OnRequestCompleted<Double> orc) {
        Observable.fromCallable(new Callable<Double>() {

            @Override
            public Double call()  {
                return gateway.requestLoudness(orc);
            }
        }).subscribeOn(Schedulers.io()).observeOn(observeScheduler).subscribe(new io.reactivex.functions.Consumer<Double>(){
            @Override
            public void accept(Double response) {
                orc.onSuccess(response);
            }
        });
    }



    @Override
    public void monitorLoudness(OnEventOccurred<Double> oeo) {
        gateway.monitorLoudness().subscribeOn(Schedulers.io()).observeOn(observeScheduler).subscribe(new io.reactivex.functions.Consumer<Double>(){
            @Override
            public void accept(Double response) {
                oeo.onUpdate(response);
            }
        });
    }

    @Override
    public void unmonitorLoudness() {
        gateway.unmonitorLoudness();

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

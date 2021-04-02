package be.kuleuven.msec.iot.iotframework.implementations.heartratesensors.polarH7;

import android.util.Log;

import java.util.concurrent.Callable;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.HeartrateSensor;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Thomas on 6/03/2018.
 */

public class PolarH7HeartrateSensor extends HeartrateSensor {
    private final String TAG="PolarH7HeartRateSensor";

    private PolarH7Device gateway;

    PolarH7HeartrateSensor(String systemID, PolarH7Device gateway) {
        super(systemID, "BPM");
        Log.i(TAG, "new PolarH7HeartRateSensor "+systemID+ " "+gateway);
        this.gateway = gateway;
    }

    @Override
    public void requestHeartrate(OnRequestCompleted<Integer> orc) {
        /*Observable.fromCallable(new Callable<Double>() {

            @Override
            public Double call() throws Exception {
                return gateway.requestHeartRate(orc);
            }
        }).subscribeOn(Schedulers.io()).observeOn(observeScheduler).subscribe(new io.reactivex.functions.Consumer<Double>(){
            @Override
            public void accept(Double response) throws Exception {
                orc.onSuccess(response);
            }
        });*/



        gateway.requestHeartRate().subscribeOn(Schedulers.io()).observeOn(observeScheduler).subscribe(new io.reactivex.functions.Consumer<Integer>(){
            @Override
            public void accept(Integer response) {
                orc.onSuccess(response);
            }
        });



        //gateway.requestHeartRate(orc);
    }

    @Override
    public void monitorHeartrate(OnEventOccurred<Integer> oeo) {
        gateway.monitorHeartRate().subscribeOn(Schedulers.io()).observeOn(observeScheduler).subscribe(new io.reactivex.functions.Consumer<Integer>(){
            @Override
            public void accept(Integer response) {
                Log.i(TAG, Integer.toString(response));
                oeo.onUpdate(response);
            }
        });
    }

    @Override
    public void unmonitorHeartrate() {
        gateway.unmonitor();
    }

    @Override
    public void exceeds(int value, OnEventOccurred<Integer> oeo) {
        gateway.exceeds(value, oeo);
    }

/*    @Override
    public void changeSamplingRate(int samplingRate, OnRequestCompleted<Boolean> orc) {

    }*/

    @Override
    public void isReachable(OnRequestCompleted<Boolean> orc) {
        gateway.isReachable(orc);
    }

    @Override
    public void monitorReachability(OnEventOccurred<Boolean> oeo) {

    }

    @Override
    public void connect(OnRequestCompleted<Boolean> orc) {
        gateway.connect(orc);
    }

    @Override
    public void disconnect(OnRequestCompleted<Boolean> orc) {
        gateway.disconnect(orc);
    }
}

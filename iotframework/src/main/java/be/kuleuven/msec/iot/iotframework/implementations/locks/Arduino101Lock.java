package be.kuleuven.msec.iot.iotframework.implementations.locks;

import android.util.Log;

import java.util.UUID;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Lock;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.arduino101.Arduino101Gateway;
import be.kuleuven.msec.iot.iotframework.implementations.temperaturesensors.Arduino101TemperatureSensor;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;

/**
 * Created by ilsebohe on 21/12/2017.
 */

public class Arduino101Lock extends Lock {
    final String TAG = "Arduino101Lock";

    UUID characteristicUUID;

    Arduino101Gateway gateway;
    Arduino101Lock thisLock;

    public Arduino101Lock(Arduino101Gateway gateway, UUID characteristicUUID) {
        super(characteristicUUID.toString());
        this.gateway = gateway;
        this.characteristicUUID = characteristicUUID;
        this.thisLock = this;
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

    @Override
    public void open(OnRequestCompleted<Boolean> orc) {
        gateway.getBluetoothLeService().writeCharacteristic(characteristicUUID, 1).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG, "onSubscribe");
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete");
                orc.onSuccess(true);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError");
                orc.onFailure(new Exception(e));
            }
        });

    }

    @Override
    public void close(OnRequestCompleted<Boolean> orc) {
        gateway.getBluetoothLeService().writeCharacteristic(characteristicUUID, 0).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG, "onSubscribe");
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete");
                orc.onSuccess(true);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError");
                orc.onFailure(new Exception(e));
            }
        });

    }

    @Override
    public void requestState(OnRequestCompleted<Boolean> orc) {
        //TODO
    }
}

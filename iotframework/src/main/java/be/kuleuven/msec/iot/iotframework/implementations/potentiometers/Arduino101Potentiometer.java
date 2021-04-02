package be.kuleuven.msec.iot.iotframework.implementations.potentiometers;

import java.util.UUID;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Potentiometer;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.arduino101.Arduino101Gateway;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ilsebohe on 16/11/2017.
 */

public class Arduino101Potentiometer extends Potentiometer {
    Arduino101Gateway gateway;
    UUID characteristicUUID;
    Arduino101Potentiometer thisPotentiometer;


    public Arduino101Potentiometer(Arduino101Gateway gateway, UUID characteristicUUID) {
        super(characteristicUUID.toString());
        this.gateway = gateway;
        this.characteristicUUID = characteristicUUID;
        this.thisPotentiometer=this;
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
    public void requestVoltage(OnRequestCompleted<Integer> orc) {
        gateway.getBluetoothLeService().readCharacteristic(characteristicUUID).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            int value;
            @Override
            public void accept(String result) throws Exception {
                value =Integer.parseInt(result);
                thisPotentiometer.setVoltage(value);
                orc.onSuccess(value);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                orc.onFailure(new Exception(throwable));
            }
        });
    }

    @Override
    public void monitorVoltage(OnEventOccurred<Integer> oeo)  {
        gateway.getBluetoothLeService().monitorCharacteristic(characteristicUUID).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            int value;
            @Override
            public void accept(String result) throws Exception {
                value =Integer.parseInt(result);
                thisPotentiometer.setVoltage(value);
                oeo.onUpdate(value);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                oeo.onErrorOccurred(new Exception(throwable));
            }
        });

    }

    public UUID getCharacteristicUUID() {
        return characteristicUUID;
    }

    @Override
    public void unmonitorVoltage() {
        gateway.getBluetoothLeService().unmonitorCharacteristic(characteristicUUID);

    }

    @Override
    public void changeSamplingRate(int samplingRate, OnRequestCompleted<Boolean> orc) {
        orc.onFailure(new UnsupportedOperationException(getClass().getSimpleName() +" does not support changeSamplingRate()") );
    }
}

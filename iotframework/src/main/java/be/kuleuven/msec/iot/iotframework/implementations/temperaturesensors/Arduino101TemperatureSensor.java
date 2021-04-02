package be.kuleuven.msec.iot.iotframework.implementations.temperaturesensors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.componentlayer.Component;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.TemperatureSensor;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.arduino101.Arduino101Gateway;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ilsebohe on 25/10/2017.
 */

public class Arduino101TemperatureSensor extends TemperatureSensor {
    UUID characteristicUUID;

    Arduino101Gateway gateway;
    Arduino101TemperatureSensor thisSensor;

    public Arduino101TemperatureSensor(Arduino101Gateway gateway, UUID characteristicUUID) {
        super(characteristicUUID.toString());
        this.gateway = gateway;
        this.characteristicUUID = characteristicUUID;
        this.thisSensor = this;
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
    public void requestTemperature(OnRequestCompleted<Double> orc) {
        //gateway.readCharacteristic(characteristicUUID, orc);
        gateway.getBluetoothLeService().readCharacteristic(characteristicUUID).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {

            double value;

            @Override
            public void accept(String result) throws Exception {
                value = Double.parseDouble(result);
                thisSensor.setTemperature(value);
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
    public void monitorTemperature(OnEventOccurred<Double> oeo) {
        try {
            gateway.getBluetoothLeService().monitorCharacteristic(characteristicUUID).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                double value;

                @Override
                public void accept(String result) throws Exception {
                    value = Double.parseDouble(result);
                    thisSensor.setTemperature(value);
                    oeo.onUpdate(value);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    oeo.onErrorOccurred(new Exception(throwable));
                }
            });
        } catch (Exception e) {
            oeo.onErrorOccurred(e);
        }
    }

    @Override
    public void unmonitorTemperature() {
        try {
            gateway.getBluetoothLeService().unmonitorCharacteristic(characteristicUUID);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void changeSamplingRate(int samplingRate, OnRequestCompleted<Boolean> orc) {
        orc.onFailure(new UnsupportedOperationException(getClass().getSimpleName() +" does not support changeSamplingRate()") );
    }

    public UUID getCharacteristicUUID() {
        return characteristicUUID;
    }
}

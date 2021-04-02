package be.kuleuven.msec.iot.iotframework.implementations.temperaturesensors;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.TemperatureSensor;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.xiaomi.XiaomiGateway;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class XiaomiTemperatureSensor extends TemperatureSensor {


    XiaomiGateway xiaomiGateway;

    public XiaomiTemperatureSensor(String systemID, XiaomiGateway gateway) {
        super(systemID);
        xiaomiGateway =gateway;
    }

    @Override
    public void requestTemperature(OnRequestCompleted<Double> orc) {
        xiaomiGateway.getBluetoothLeService().monitorTemperature().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {

            double value;
            @Override
            public void accept(String s) throws Exception {
                value = Double.parseDouble(s);
                orc.onSuccess(value);
                xiaomiGateway.getBluetoothLeService().unmonitorTemperature();
            }
        });
    }

    @Override
    public void monitorTemperature(OnEventOccurred<Double> oeo) {
        xiaomiGateway.getBluetoothLeService().monitorTemperature().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {

            double value;
            @Override
            public void accept(String s) throws Exception {
                value = Double.parseDouble(s);
                oeo.onUpdate(value);
            }
        });
    }

    @Override
    public void unmonitorTemperature() {
        xiaomiGateway.getBluetoothLeService().unmonitorTemperature();
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

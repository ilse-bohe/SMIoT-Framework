package be.kuleuven.msec.iot.iotframework.implementations.humiditysensors;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.HumiditySensor;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.xiaomi.XiaomiGateway;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class XiaomiHumiditySensor extends HumiditySensor {


    XiaomiGateway xiaomiGateway;

    public XiaomiHumiditySensor(String systemID, XiaomiGateway gateway) {
        super(systemID);
        xiaomiGateway =gateway;
    }


    @Override
    public void requestHumidity(OnRequestCompleted<Double> orc) {
        xiaomiGateway.getBluetoothLeService().monitorHumidity().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {

            double value;
            @Override
            public void accept(String s) throws Exception {
                value = Double.parseDouble(s);
                orc.onSuccess(value);
                xiaomiGateway.getBluetoothLeService().unmonitorHumidity();
            }
        });
    }

    @Override
    public void monitorHumidity(OnEventOccurred<Double> oeo) {
        xiaomiGateway.getBluetoothLeService().monitorHumidity().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {

            double value;
            @Override
            public void accept(String s) throws Exception {
                value = Double.parseDouble(s);
                oeo.onUpdate(value);
            }
        });
    }

    @Override
    public void unmonitorHumidity() {
        xiaomiGateway.getBluetoothLeService().unmonitorHumidity();

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

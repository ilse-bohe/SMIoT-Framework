package be.kuleuven.msec.iot.iotframework.implementations.pressuresensors;

import java.text.DecimalFormat;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.componentlayer.Component;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.PressureSensor;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.allthingstalk.AllThingsTalkGateway;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.allthingstalk.AllThingsTalkMonitoringService;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.allthingstalk.allthingstalkjsonmodel.ATTAssetJSONModel;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ilsebohe on 10/10/2017.
 */

public class ATTPressureSensor extends PressureSensor {

    String id;

    AllThingsTalkGateway gateway;
    ATTPressureSensor thisSensor;

    public ATTPressureSensor(String id, String deviceSystemID, AllThingsTalkGateway gateway) {
        super(deviceSystemID);
        this.id = id;
        this.gateway = gateway;
        this.thisSensor=this;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
    public void requestPressure(final OnRequestCompleted<Double> orc) {
        Single<ATTAssetJSONModel> result = gateway.getRestService().getAsset(id);

        Disposable disposable = result.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ATTAssetJSONModel>() {
                    double value;

                    @Override
                    public void accept(@NonNull ATTAssetJSONModel attAssetJSONModel) throws Exception {
                        value = Double.parseDouble(attAssetJSONModel.getState().getValue().toString());
                        thisSensor.setPressure(value);
                        orc.onSuccess(value);
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        orc.onFailure(new Exception(throwable));
                    }
                });



    }

    @Override
    public void monitorPressure(final OnEventOccurred<Double> oeo) {
        gateway.getMonitoringService().subscribe(this.getId(),new OnEventOccurred<String>() {
            @Override
            public void onUpdate(String arg) {
                thisSensor.setPressure(Double.parseDouble(arg));
                oeo.onUpdate(Double.parseDouble(arg));
            }
            @Override
            public void onErrorOccurred(Exception arg) {
                oeo.onErrorOccurred(arg);
            }
        });

    }

    @Override
    public void unmonitorPressure() {
        gateway.getMonitoringService().unsubscribe(this.getId());
    }

    @Override
    public void changeSamplingRate(int samplingRate, OnRequestCompleted<Boolean> orc) {
        orc.onFailure(new UnsupportedOperationException(getClass().getSimpleName() +" does not support changeSamplingRate()") );
    }
}

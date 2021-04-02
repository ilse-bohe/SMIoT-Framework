package be.kuleuven.msec.iot.iotframework.implementations.temperaturesensors;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.componentlayer.Component;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.TemperatureSensor;
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
 * Created by ilsebohe on 06/10/2017.
 */

public class ATTTemperatureSensor extends TemperatureSensor {

    String id;

    AllThingsTalkGateway gateway;
    AllThingsTalkMonitoringService monitoringService;
    ATTTemperatureSensor thisSensor;

    public ATTTemperatureSensor(String id, String deviceSystemID, AllThingsTalkGateway gateway) {
        super(deviceSystemID);
        this.id = id;
        this.gateway = gateway;
        this.thisSensor = this;
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
    public void requestTemperature(final OnRequestCompleted<Double> orc) {
        gateway.getRestService().getAsset(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ATTAssetJSONModel>() {
                    @Override
                    public void accept(@NonNull ATTAssetJSONModel asset) throws Exception {
                        orc.onSuccess(Double.parseDouble(asset.getState().getValue().toString()));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        orc.onFailure(new Exception(throwable));
                    }
                });
    }

    @Override
    public void monitorTemperature(final OnEventOccurred<Double> oeo) {
        this.requestTemperature(new OnRequestCompleted<Double>() {
            @Override
            public void onSuccess(Double response) {
                thisSensor.setTemperature(response);
                oeo.onUpdate(response);
            }
        });
        monitoringService = gateway.getMonitoringService();
        monitoringService.subscribe(this.getId(), new OnEventOccurred<String>() {
            @Override
            public void onUpdate(String arg) {
                thisSensor.setTemperature(Double.parseDouble(arg));
                oeo.onUpdate(Double.parseDouble(arg));
            }

            @Override
            public void onErrorOccurred(Exception exception) {
                oeo.onErrorOccurred(exception);
            }
        });
    }

    @Override
    public void unmonitorTemperature() {
        monitoringService = gateway.getMonitoringService();
        System.out.println("ATT THIS.GETID " +this.getId());
        monitoringService.unsubscribe(this.getId());
    }

    @Override
    public void changeSamplingRate(int samplingRate, OnRequestCompleted<Boolean> orc) {
        orc.onFailure(new UnsupportedOperationException(getClass().getSimpleName() +" does not support changeSamplingRate()") );
    }

    @Override
    public String toString() {
        return "ATTTemperatureSensor{" +
                "id='" + id + '\'' +
                '}';
    }
}

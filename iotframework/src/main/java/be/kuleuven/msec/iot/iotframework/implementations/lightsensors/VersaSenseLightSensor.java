package be.kuleuven.msec.iot.iotframework.implementations.lightsensors;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.LightSensor;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense.VersaSenseGateway;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense.retrofitrestbodies.SamplingRateBody;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense.versasensejsonmodel.VersaSenseDataJSONModel;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense.versasensejsonmodel.VersaSensePeripheralJSONModel;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense.versasensejsonmodel.VersaSensePeripheralSampleJSONModel;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.VersaSense_constants;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ilsebohe on 24/10/2017.
 */

public class VersaSenseLightSensor extends LightSensor {
    String id;
    String deviceMac;

    VersaSenseGateway gateway;
    VersaSenseLightSensor thisSensor;

    public VersaSenseLightSensor(String mac, String identifier, String deviceSystemID, VersaSenseGateway gateway) {
        super(deviceSystemID);
        this.id=identifier;
        this.deviceMac=mac;


        this.gateway = gateway;
        thisSensor = this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    @Override
    public void isReachable(OnRequestCompleted<Boolean> orc) {
        gateway.getRestService().getPeripheral(deviceMac, id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<VersaSensePeripheralJSONModel>() {
            @Override
            public void accept(VersaSensePeripheralJSONModel versaSensePeripheralJSONModel) throws Exception {
                orc.onSuccess(true);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                orc.onFailure(new Exception(throwable));
            }
        });

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
    public void requestLightIntensity(final OnRequestCompleted<Double> orc) {
        gateway.getRestService().getPeripheralSample(deviceMac, id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<VersaSensePeripheralSampleJSONModel>() {
                    @Override
                    public void accept(@NonNull VersaSensePeripheralSampleJSONModel versaSensePeripheralSampleJSONModel) throws Exception {
                        for(VersaSenseDataJSONModel data: versaSensePeripheralSampleJSONModel.getData()){
                            if(data.getMeasurement().equals(VersaSense_constants.LIGHT)){
                                double value;
                                value=Double.parseDouble(data.getValue());
                                thisSensor.setIntensity(value);
                                orc.onSuccess(value);
                            }
                        }
                    }


                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        orc.onFailure(new Exception(throwable));
                    }
                });
    }

    @Override
    public void monitorLightIntensity(final OnEventOccurred<Double> oeo) {
        gateway.getMonitoringService().subscribe(this.deviceMac, VersaSense_constants.LIGHT, new OnEventOccurred<String>() {
            @Override
            public void onUpdate(String arg) {
                System.out.println(arg);
                thisSensor.setIntensity(Double.parseDouble(arg));
                oeo.onUpdate(Double.parseDouble(arg));
            }

            @Override
            public void onErrorOccurred(Exception exception) {
                oeo.onErrorOccurred(exception);
            }
        });
    }

    @Override
    public void unmonitorLightIntensity() {
        gateway.getMonitoringService().unsubscribe(this.deviceMac, VersaSense_constants.LIGHT);
    }

    @Override
    public void changeSamplingRate(int samplingRate, OnRequestCompleted<Boolean> orc) {
        gateway.getRestService().changeSamplingRate(deviceMac, id, new SamplingRateBody(samplingRate))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        setSamplingRate(samplingRate);
                        orc.onSuccess(true);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        orc.onFailure(new Exception(throwable));
                    }
                });
    }
}

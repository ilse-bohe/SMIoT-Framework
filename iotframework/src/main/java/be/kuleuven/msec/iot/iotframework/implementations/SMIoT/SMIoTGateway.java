package be.kuleuven.msec.iot.iotframework.implementations.SMIoT;

import java.util.ArrayList;
import java.util.Iterator;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.VirtualIoTConnector;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.VirtualIoTDevice;
import be.kuleuven.msec.iot.iotframework.implementations.SMIoT.smiotjsonmodel.SMIoTDeviceJSONModel;
import be.kuleuven.msec.iot.iotframework.implementations.humiditysensors.SMIoTHumiditySensor;
import be.kuleuven.msec.iot.iotframework.implementations.lamps.smiotlamp.SMIoTLamp;
import be.kuleuven.msec.iot.iotframework.implementations.lightsensors.SMIoTLightSensor;
import be.kuleuven.msec.iot.iotframework.implementations.presencesensors.SMIoTPresenceSensor;
import be.kuleuven.msec.iot.iotframework.implementations.pressuresensors.SMIoTPressureSensor;
import be.kuleuven.msec.iot.iotframework.implementations.temperaturesensors.SMIotTemperatureSensor;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Device_constants;
import be.kuleuven.msec.iot.iotframework.systemmanagement.jsonmodel.JSMDevice;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ilsebohe on 08/01/2018.
 */

public class SMIoTGateway extends VirtualIoTConnector {

    String ip_address;
    String restPort;
    String mqttPort;

    SMIoTGateway thisGateway;
    SMIoTRestService restService;
    SMIoTMonitoringService monitoringService;

    public SMIoTGateway(String systemID, String ip_address, String restPort, String mqttPort) {
        super(systemID);
        this.ip_address = ip_address;
        this.restPort = restPort;
        this.mqttPort= mqttPort;
        thisGateway = this;
    }

    @Override
    public void initialize(OnRequestCompleted orc) {
        restService = new Retrofit.Builder()
                .baseUrl("http://" + ip_address + ":" + restPort)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                // uncommment .client(...) to log
                //.client(new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)).build())
                .build()
                .create(SMIoTRestService.class);
        orc.onSuccess(true);
    }

    @Override
    public void updateConnectedDeviceList(OnRequestCompleted<Boolean> orc, ArrayList<JSMDevice> devices) {
        restService.getAllDevices().subscribeOn(Schedulers.io())
                .subscribe(new Consumer<ArrayList<SMIoTDeviceJSONModel>>() {
                    @Override
                    public void accept(ArrayList<SMIoTDeviceJSONModel> devices) throws Exception {
                        final Iterator it = devices.iterator();
                        while (it.hasNext()) {
                            final SMIoTDeviceJSONModel device = (SMIoTDeviceJSONModel) it.next();
                            System.out.println(device);
                            System.out.println(device.getType());
                            VirtualIoTDevice temporary = null;
                            switch (device.getType()) {
                                case Device_constants.TYPE_LAMP:
                                    temporary = new SMIoTLamp(device.getSystemID(), thisGateway);
                                    break;
                                case Device_constants.TYPE_TEMPERATURE_SENSOR:
                                    temporary = new SMIotTemperatureSensor(device.getSystemID(), thisGateway);
                                    break;
                                case Device_constants.TYPE_HUMIDITY_SENSOR:
                                    temporary = new SMIoTHumiditySensor(device.getSystemID(), thisGateway);
                                    break;
                                case Device_constants.TYPE_LIGHT_SENSOR:
                                    temporary = new SMIoTLightSensor(device.getSystemID(), thisGateway);
                                    break;
                                case Device_constants.TYPE_PRESENCE_SENSOR:
                                    temporary = new SMIoTPresenceSensor(device.getSystemID(), thisGateway);
                                    break;
                                case Device_constants.TYPE_PRESSURE_SENSOR:
                                    temporary = new SMIoTPressureSensor(device.getSystemID(), thisGateway);
                                    break;
                            }
                            if(temporary!=null){
                                connectedDevices.add(temporary);
                            }

                            it.remove();
                        }
                        orc.onSuccess(true);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        orc.onFailure(new Exception(throwable));
                    }
                });

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

    public SMIoTMonitoringService getMonitoringService() {
        if(monitoringService==null) monitoringService = new SMIoTMonitoringService(ip_address, mqttPort);
        return monitoringService;
    }

    public SMIoTRestService getRestService() {
        return restService;
    }
}

package be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense;

import android.provider.SyncStateContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.VirtualIoTConnector;
import be.kuleuven.msec.iot.iotframework.implementations.humiditysensors.VersaSenseHumiditySensor;
import be.kuleuven.msec.iot.iotframework.implementations.lightsensors.VersaSenseLightSensor;
import be.kuleuven.msec.iot.iotframework.implementations.presencesensors.VersaSensePresenceSensor;
import be.kuleuven.msec.iot.iotframework.implementations.pressuresensors.VersaSensePressureSensor;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense.versasensejsonmodel.VersaSenseMeasurementJSONModel;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense.versasensejsonmodel.VersaSensePeripheralJSONModel;
import be.kuleuven.msec.iot.iotframework.implementations.temperaturesensors.VersaSenseTemperatureSensor;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense.versasensejsonmodel.VersaSenseDeviceJSONModel;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Device_constants;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Model_constants;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.VersaSense_constants;
import be.kuleuven.msec.iot.iotframework.systemmanagement.jsonmodel.JSMDevice;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ilsebohe on 13/10/2017.
 */

public class VersaSenseGateway extends VirtualIoTConnector {
/*

    ArrayList<VersaSenseTemperatureSensor> temperatureSensors;
    ArrayList<VersaSensePressureSensor> pressureSensors;
    ArrayList<VersaSenseLightSensor> lightSensors;
    ArrayList<VersaSenseHumiditySensor> humiditySensors;
    ArrayList<VersaSensePresenceSensor> presenceSensors;
*/

    private String address;

    private VersaSenseGateway thisGateway;
    private VersaSenseRestService restService;

    //TODO monitoring of sensors
    private VersaSenseMonitoringService monitoringService;

    public VersaSenseGateway(String systemID, Map<String, String> settings) {
        super(systemID);
        this.address = settings.get("address");

    /*    temperatureSensors = new ArrayList<>();
        pressureSensors = new ArrayList<>();
        lightSensors = new ArrayList<>();
        humiditySensors = new ArrayList<>();
        presenceSensors = new ArrayList<>();*/

        thisGateway = this;
    }

    @Override
    public void initialize(OnRequestCompleted orc) {

        restService = new Retrofit.Builder()
                .baseUrl(address)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                // uncommment .client(...) to log
                //.client(new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)).build())
                .build()
                .create(VersaSenseRestService.class);

        orc.onSuccess(true);
    }

    @Override
    public void updateConnectedDeviceList(final OnRequestCompleted<Boolean> orc, ArrayList<JSMDevice> devices) {
        restService.getAllDevices()
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<ArrayList<VersaSenseDeviceJSONModel>>() {

                    @Override
                    public void accept(@NonNull ArrayList<VersaSenseDeviceJSONModel> versaSenseDeviceJSONModels) throws Exception {
                        final Iterator it = versaSenseDeviceJSONModels.iterator();
                        while (it.hasNext()) {
                            final VersaSenseDeviceJSONModel device = (VersaSenseDeviceJSONModel) it.next();

                            for (VersaSensePeripheralJSONModel peripheral : device.getPeripherals()) {
                                final Iterator it2 = peripheral.getMeasurements().iterator();
                                while (it2.hasNext()) {
                                    final VersaSenseMeasurementJSONModel measurement = (VersaSenseMeasurementJSONModel) it2.next();
                                    Log.i("VSSGateway", measurement.getName());
                                    String deviceSystemID = getSystemIDofMac_Id_type(devices, device.getMac(), peripheral.getIdentifier(), measurement.getName());
                                    if (deviceSystemID!=null){
                                        if (measurement.getName().equals(VersaSense_constants.TEMPERATURE)) {
                                            VersaSenseTemperatureSensor temp = new VersaSenseTemperatureSensor(device.getMac(),peripheral.getIdentifier(), deviceSystemID, thisGateway);
//                                        temperatureSensors.add(temp);
                                            connectedDevices.add(temp);
                                        }
                                        if (measurement.getName().equals(VersaSense_constants.PRESSURE)) {
                                            VersaSensePressureSensor temp = new VersaSensePressureSensor(device.getMac(),peripheral.getIdentifier(),deviceSystemID, thisGateway);
//                                        pressureSensors.add(temp);
                                            connectedDevices.add(temp);

                                        }
                                        if (measurement.getName().equals(VersaSense_constants.HUMIDITY)) {
                                            VersaSenseHumiditySensor temp = new VersaSenseHumiditySensor(device.getMac(),peripheral.getIdentifier(),  deviceSystemID,thisGateway);
//                                        humiditySensors.add(temp);
                                            connectedDevices.add(temp);

                                        }
                                        if (measurement.getName().equals(VersaSense_constants.LIGHT)) {
                                            VersaSenseLightSensor temp = new VersaSenseLightSensor(device.getMac(),peripheral.getIdentifier(), deviceSystemID, thisGateway);
//                                        lightSensors.add(temp);
                                            connectedDevices.add(temp);

                                        }
                                        if (measurement.getName().equals(VersaSense_constants.MOTION)) {
                                            VersaSensePresenceSensor temp = new VersaSensePresenceSensor(device.getMac(),peripheral.getIdentifier(),deviceSystemID,  thisGateway);
//                                        presenceSensors.add(temp);
                                            connectedDevices.add(temp);

                                        }
                                    }


                                    it2.remove();
                                }
                            }
                            it.remove();
                        }
                        Log.i("VSSConnector", connectedDevices.toString());
                        orc.onSuccess(true);


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) {
                        orc.onFailure(new Exception(throwable));
                    }
                });

    }

    private String getSystemIDofMac_Id_type(ArrayList<JSMDevice> devices, String mac, String identifier, String type) {
        for (JSMDevice dev :devices) {
            if (dev.getModel().equals(Model_constants.MODEL_VERSASENSE)){

                if(dev.getSettings().get("mac").equals(mac) && dev.getSettings().get("identifier").equals(identifier)) {
                    System.out.println(dev.getType()+" type:"+type);
                    if(dev.getType().equals(Device_constants.TYPE_PRESSURE_SENSOR) && type.equals(VersaSense_constants.PRESSURE)) return dev.getSystemID();
                    if(dev.getType().equals(Device_constants.TYPE_TEMPERATURE_SENSOR) && type.equals(VersaSense_constants.TEMPERATURE)) return dev.getSystemID();
                    if(dev.getType().equals(Device_constants.TYPE_HUMIDITY_SENSOR) && type.equals(VersaSense_constants.HUMIDITY)) return dev.getSystemID();
                    if(dev.getType().equals(Device_constants.TYPE_LIGHT_SENSOR) && type.equals(VersaSense_constants.LIGHT)) return dev.getSystemID();
                    if(dev.getType().equals(Device_constants.TYPE_PRESENCE_SENSOR) && type.equals(VersaSense_constants.MOTION)) return dev.getSystemID();
                }
            }


        }
        System.out.println("no match");
        return null;
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

/*
    public TemperatureSensor getTemperatureSensorBasedOnUniqueID(String uniqueID) throws Exception {
        for (VersaSenseTemperatureSensor sensor : temperatureSensors) {
            if ((sensor.getDeviceMac()+"/"+sensor.getId()).equals(uniqueID)) {
                return sensor;
            }
        }
        throw new VirtualIoTDeviceNotFoundException(this.getClass().getName(), this.getSystemID(),  uniqueID);
    }

    public PressureSensor getPressureSensorBasedOnUniqueID(String uniqueID) throws Exception {
        for (VersaSensePressureSensor sensor : pressureSensors) {
            if ((sensor.getDeviceMac()+"/"+sensor.getId()).equals(uniqueID)) {
                return sensor;
            }
        }
        throw new VirtualIoTDeviceNotFoundException(this.getClass().getName(), this.getSystemID(),  uniqueID);

    }

    public LightSensor getLightSensorBasedOnUniqueID(String uniqueID) throws Exception {
        for (VersaSenseLightSensor sensor : lightSensors) {
            if ((sensor.getDeviceMac()+"/"+sensor.getId()).equals(uniqueID)) {
                return sensor;
            }
        }
        throw new VirtualIoTDeviceNotFoundException(this.getClass().getName(), this.getSystemID(), uniqueID);

    }

    public HumiditySensor getHumiditySensorBasedOnUniqueID(String uniqueID) throws Exception {
        for (VersaSenseHumiditySensor sensor : humiditySensors) {
            if ((sensor.getDeviceMac()+"/"+sensor.getId()).equals(uniqueID)) {
                return sensor;
            }
        }
        throw new VirtualIoTDeviceNotFoundException(this.getClass().getName(), this.getSystemID(),  uniqueID);

    }

    public PresenceSensor getPresenceSensorBasedOnUniqueID(String uniqueID) throws Exception {
        for (VersaSensePresenceSensor sensor : presenceSensors) {
            if ((sensor.getDeviceMac()+"/"+sensor.getId()).equals(uniqueID)) {
                return sensor;
            }
        }
        throw new VirtualIoTDeviceNotFoundException(this.getClass().getName(), this.getSystemID(),uniqueID);
    }
*/
    public VersaSenseMonitoringService getMonitoringService() {
        if (monitoringService == null) {
            monitoringService = new VersaSenseMonitoringService();
        }
        return monitoringService;
    }

    public VersaSenseRestService getRestService() {
        return restService;
    }


}

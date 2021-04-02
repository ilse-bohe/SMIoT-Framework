package be.kuleuven.msec.iot.iotframework.implementations.sensorkits.allthingstalk;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.VirtualIoTConnector;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.allthingstalk.allthingstalkjsonmodel.ATTAssetJSONModel;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.allthingstalk.allthingstalkjsonmodel.ATTDeviceJSONModel;
import be.kuleuven.msec.iot.iotframework.implementations.humiditysensors.ATTHumiditySensor;
import be.kuleuven.msec.iot.iotframework.implementations.pressuresensors.ATTPressureSensor;
import be.kuleuven.msec.iot.iotframework.implementations.temperaturesensors.ATTTemperatureSensor;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.ATT_constants;
import be.kuleuven.msec.iot.iotframework.systemmanagement.jsonmodel.JSMDevice;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ilsebohe on 06/10/2017.
 */

public class AllThingsTalkGateway extends VirtualIoTConnector {
/*
    ArrayList<ATTTemperatureSensor> temperatureSensors;
    ArrayList<ATTPressureSensor> pressureSensors;
    ArrayList<ATTHumiditySensor> humiditySensors;*/


    Map<String, String> settings = new HashMap<>();

String address;

    String username;
    String password;
    String client;
    String token;
    String deviceToken;

    AllThingsTalkGateway thisGateway;
    ATTRestService restService;
    AllThingsTalkMonitoringService monitoringService;

   /* public AllThingsTalkGateway(String systemID, String address, String username, String password, String client, String deviceToken) {
        super(systemID);
        this.address = address;

        this.username = username;
        this.password = password;
        this.client = client;
        this.deviceToken = deviceToken;
  *//*      temperatureSensors = new ArrayList<>();
        pressureSensors = new ArrayList<>();
        humiditySensors = new ArrayList<>();*//*
        thisGateway = this;
    }*/

    public AllThingsTalkGateway(String systemID, Map<String, String> settings) {
        super(systemID);

        this.settings=settings;

        this.address = settings.get("address");

        this.username = settings.get("username");
        this.password = settings.get("password");
        this.client = settings.get("client");
        this.deviceToken = settings.get("deviceToken");
        System.out.println("deviceToken= "+deviceToken);
  /*      temperatureSensors = new ArrayList<>();
        pressureSensors = new ArrayList<>();
        humiditySensors = new ArrayList<>();*/
        thisGateway = this;
    }

    @Override
    public void initialize(final OnRequestCompleted orc) {

        ATTRestService rest = new Retrofit.Builder()
                .baseUrl(address)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ATTRestService.class);

        rest.login("password", username, password, client)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(new Consumer<JsonObject>() {
                    @Override
                    public void accept(@NonNull JsonObject jsonObject) throws Exception {
                        token = jsonObject.get("access_token").getAsString();
                        System.out.println("token: " + token);
                    }
                })
                .doAfterSuccess(new Consumer<JsonObject>() {
                    @Override
                    public void accept(@NonNull JsonObject jsonObject) throws Exception {
                        System.out.println("test");

                        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
                            @Override
                            public okhttp3.Response intercept(Chain chain) throws IOException {
                                Request originalRequest = chain.request();

                                Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                                        "Bearer " + token);

                                Request newRequest = builder.build();
                                return chain.proceed(newRequest);
                            }
                        }).build();

                        restService = new Retrofit.Builder()
                                .baseUrl(address)
                                .addConverterFactory(GsonConverterFactory.create())
                                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                .client(okHttpClient)
                                .build()
                                .create(ATTRestService.class);

                        orc.onSuccess(true);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>() {
                    @Override
                    public void accept(@NonNull JsonObject jsonObject) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        System.out.println("error " + throwable.toString());
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


    @Override
    public void updateConnectedDeviceList(final OnRequestCompleted<Boolean> orc, ArrayList<JSMDevice> devices) {
        System.out.println("update devices ATT");
        restService.getAllDevices()
                .subscribeOn(Schedulers.io())
                .doOnSuccess(new Consumer<ArrayList<ATTDeviceJSONModel>>() {
                    @Override
                    public void accept(@NonNull ArrayList<ATTDeviceJSONModel> attDeviceJSONModels) throws Exception {

                        final CountDownLatch latch = new CountDownLatch(attDeviceJSONModels.size());
                        final Iterator it = attDeviceJSONModels.iterator();
                        while (it.hasNext()) {
                            final ATTDeviceJSONModel device = (ATTDeviceJSONModel) it.next();
                            restService.getAllAssetsFromDevice(device.getId())
                                    .subscribeOn(Schedulers.io())
                                    .doOnSuccess(new Consumer<ArrayList<ATTAssetJSONModel>>() {
                                        @Override
                                        public void accept(@NonNull ArrayList<ATTAssetJSONModel> attAssetJSONModels) throws Exception {
                                            Iterator it2 = attAssetJSONModels.iterator();
                                            while (it2.hasNext()) {
                                                ATTAssetJSONModel asset = (ATTAssetJSONModel) it2.next();
                                                String deviceSystemID = getSystemIDofUniqueID(devices, asset.getId());
                                                if(deviceSystemID!=null){
                                                    System.out.println("deviceSystemID "+deviceSystemID);
                                                    if (asset.getTitle().equals(ATT_constants.TEMPERATURE)) {
                                                        ATTTemperatureSensor temp = new ATTTemperatureSensor(asset.getId(), deviceSystemID,thisGateway);
//                                                    temperatureSensors.add(temp);
                                                        connectedDevices.add(temp);
                                                    }
                                                    if (asset.getTitle().equals(ATT_constants.PRESSURE)) {
                                                        ATTPressureSensor temp = new ATTPressureSensor(asset.getId(), deviceSystemID,thisGateway);
//                                                    pressureSensors.add(temp);
                                                        connectedDevices.add(temp);

                                                    }
                                                    if (asset.getTitle().equals(ATT_constants.HUMIDITY)) {
                                                        ATTHumiditySensor temp = new ATTHumiditySensor(asset.getId(), deviceSystemID,thisGateway);
//                                                    humiditySensors.add(temp);
                                                        connectedDevices.add(temp);
                                                    }
                                                }

                                                it2.remove();
                                            }
                                            latch.countDown();
                                            // orc.onSuccess(true);
                                        }
                                    })
                                    //.observeOn(AndroidSchedulers.mainThread()) //--> moet volgens mij niet geobserved worden op de main thread? Observe on main thread to catch errors
                                    .subscribe(new Consumer<ArrayList<ATTAssetJSONModel>>() {
                                        @Override
                                        public void accept(@NonNull ArrayList<ATTAssetJSONModel> attAssetJSONModels) throws Exception {

                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(@NonNull Throwable throwable) throws Exception {
                                            orc.onFailure(new Exception(throwable));
                                        }
                                    });

                            it.remove();
                        }
System.out.println("BEFORE");
                        latch.await();
                        System.out.println("AFTER");

                        orc.onSuccess(true);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread()) //--> moet volgens mij niet geobserved worden op de main thread? Observe on main thread to catch errors
                .subscribe(new Consumer<ArrayList<ATTDeviceJSONModel>>() {
                    @Override
                    public void accept(@NonNull ArrayList<ATTDeviceJSONModel> attDeviceJSONModels) throws Exception {
                        System.out.println("ACCEPT");

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        orc.onFailure(new Exception(throwable));
                        System.out.println("FAIL");

                    }
                });

    }

    private String getSystemIDofUniqueID(ArrayList<JSMDevice> devices, String id) {
        for (JSMDevice dev :devices) {
            if (dev.getModel().equals("ATT")){
                if(dev.getSettings().get("uniqueID").equals(id)) return dev.getSystemID();
            }


        }
        return null;
    }

 /*   public TemperatureSensor getTemperatureSensorBasedOnUniqueID(String uniqueID) throws Exception {
        for (ATTTemperatureSensor sensor : temperatureSensors) {
            if (sensor.getId().equals(uniqueID)) {
                return sensor;
            }
        }
        throw new VirtualIoTDeviceNotFoundException(this.getClass().getName(), this.getSystemID(),  uniqueID);
    }

    public PressureSensor getPressureSensorBasedOnUniqueID(String uniqueID) throws Exception {
        for (ATTPressureSensor sensor : pressureSensors) {

            if (sensor.getId().equals(uniqueID)) {
                return sensor;
            }
        }
        throw new VirtualIoTDeviceNotFoundException(this.getClass().getName(), this.getSystemID(), uniqueID);
    }

    public HumiditySensor getHumiditySensorBasedOnUniqueID(String uniqueID) throws Exception {
        for (ATTHumiditySensor sensor : humiditySensors) {

            if (sensor.getId().equals(uniqueID)) {
                return sensor;
            }
        }
        throw new VirtualIoTDeviceNotFoundException(this.getClass().getName(), this.getSystemID(), uniqueID);
    }
*/
    public ATTRestService getRestService() {
        return restService;
    }

    public AllThingsTalkMonitoringService getMonitoringService() {
        if (monitoringService == null) {
            monitoringService = new AllThingsTalkMonitoringService(deviceToken);
        }
        return monitoringService;
    }

}

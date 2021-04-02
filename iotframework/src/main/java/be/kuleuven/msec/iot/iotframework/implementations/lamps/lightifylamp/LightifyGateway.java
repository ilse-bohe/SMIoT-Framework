package be.kuleuven.msec.iot.iotframework.implementations.lamps.lightifylamp;

import java.io.IOException;
import java.util.ArrayList;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.*;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.VirtualIoTConnector;
import be.kuleuven.msec.iot.iotframework.implementations.lamps.lightifylamp.lightifyjsonmodel.LightifyJSONModel;
import be.kuleuven.msec.iot.iotframework.implementations.lamps.lightifylamp.lightifyjsonmodel.LightifyLoginJSONModel;
import be.kuleuven.msec.iot.iotframework.implementations.lamps.lightifylamp.retrofitrestbodies.LoginBody;
import be.kuleuven.msec.iot.iotframework.systemmanagement.jsonmodel.JSMDevice;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by michielwillocx on 18/09/17.
 */

public class LightifyGateway extends VirtualIoTConnector {

    ArrayList<LightifyLamp> lamps;

    String address;
    String username;
    String password;
    String gatewayId;
    String authToken;

    LightifyGateway thisGateway;
    LightifyLampRestService restService;


    public LightifyGateway(String systemID, String username, String password, String gatewayId) {
        super(systemID);

        this.username = username;
        this.password = password;
        this.gatewayId = gatewayId;

        this.address = "https://eu.lightify-api.org/lightify/services/";

        lamps = new ArrayList<>();
        this.thisGateway = this;
    }

    @Override
    public void initialize(final OnRequestCompleted orc) {
        LightifyLampRestService rest = new Retrofit.Builder()
                .baseUrl(address)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // <- add this
                .build()
                .create(LightifyLampRestService.class);

        rest.authenticate(new LoginBody(username, password, gatewayId))
                .subscribeOn(Schedulers.io())
                .doOnSuccess(new Consumer<LightifyLoginJSONModel>() {
                    @Override
                    public void accept(@NonNull LightifyLoginJSONModel loginDetails) throws Exception {
                        authToken = loginDetails.getSecurityToken();
                        //orc.onSuccess(null);
                    }
                })
                .doAfterSuccess(new Consumer<LightifyLoginJSONModel>() {
                    @Override
                    public void accept(@NonNull LightifyLoginJSONModel jsonObject) throws Exception {
                        System.out.println("test");

                        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
                            @Override
                            public okhttp3.Response intercept(Chain chain) throws IOException {
                                Request originalRequest = chain.request();

                                Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                                        authToken);

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
                                .create(LightifyLampRestService.class);

                        orc.onSuccess(true);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LightifyLoginJSONModel>() {
                    @Override
                    public void accept(@NonNull LightifyLoginJSONModel loginDetails) {
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

    @Override
    public void updateConnectedDeviceList(final OnRequestCompleted<Boolean> orc, ArrayList<JSMDevice> devices) {
        Single<ArrayList<LightifyJSONModel>> result = restService.getAllLamps();

        Disposable disposable = result.subscribeOn(Schedulers.io())
                .doOnSuccess(new Consumer<ArrayList<LightifyJSONModel>>() {
                    @Override
                    public void accept(@NonNull ArrayList<LightifyJSONModel> lightifyJSONModels) throws Exception {
                        System.out.println("here");
                        for (LightifyJSONModel lamp:lightifyJSONModels) {
                            LightifyLamp temp = new LightifyLamp(lamp.getColor(),(int)(lamp.getBrightnessLevel()*100),lamp.getOn()==1?true:false,lamp.getOnline()==1?true:false,lamp.getHue(),(int)(lamp.getSaturation()*100),lamp.getTemperature(),lamp.getDeviceId(),lamp.getName(),thisGateway);
                            lamps.add(temp);
                            connectedDevices.add(temp);
                        }

                        orc.onSuccess(true);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread()) //--> moet volgens mij niet geobserved worden op de main thread?
                .subscribe(new Consumer<ArrayList<LightifyJSONModel>>() {
                    @Override
                    public void accept(@NonNull ArrayList<LightifyJSONModel> lampList) {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        orc.onFailure(new Exception(throwable));
                    }
                });

    }

/*    public Lamp getLampBasedOnUniqueID(String uniqueID) {
        for (LightifyLamp lamp : lamps) {
            if (lamp.getUniqueID().equals(uniqueID)) {
                return lamp;
            }
        }
        throw new VirtualIoTDeviceNotFoundException(this.getClass().getName(), this.getSystemID(), Lamp.class.getName(), uniqueID);
    }*/

    public String getAuthToken() {
        return authToken;
    }

    public ArrayList<LightifyLamp> getLamps() {
        return lamps;
    }

    public LightifyLampRestService getRestService() {
        return restService;

    }
}

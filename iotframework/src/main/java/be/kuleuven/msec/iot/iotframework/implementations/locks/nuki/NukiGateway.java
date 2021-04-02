package be.kuleuven.msec.iot.iotframework.implementations.locks.nuki;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.VirtualIoTConnector;
import be.kuleuven.msec.iot.iotframework.implementations.locks.nuki.nukijsonmodel.NukiLockJsonModel;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense.VersaSenseRestService;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Model_constants;
import be.kuleuven.msec.iot.iotframework.systemmanagement.jsonmodel.JSMDevice;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NukiGateway extends VirtualIoTConnector {


    private String ip;
    private String port;
    private String bridgeId;
    private String token;

    private NukiGateway thisGateway;
    private NukiRestService restService;

    public NukiGateway(String systemID, Map<String, String> settings) {
        super(systemID);
        this.ip = settings.get("ip");
        this.port = settings.get("port");
        this.bridgeId = settings.get("bridgeId");
        this.token = settings.get("token");
        thisGateway=this;
    }

    @Override
    public void initialize(OnRequestCompleted orc) {
         restService = new Retrofit.Builder()
                .baseUrl("http://"+ip+":"+port)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                // uncommment .client(...) to log
                .client(new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)).writeTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).connectTimeout(30, TimeUnit.SECONDS).build())
                .build()
                .create(NukiRestService.class);

        orc.onSuccess(true);
    }

    @Override
    public void updateConnectedDeviceList(OnRequestCompleted<Boolean> orc, ArrayList<JSMDevice> devices) {
        restService.getAllDevices(token).subscribeOn(Schedulers.io()).subscribe(new Consumer<ArrayList<NukiLockJsonModel>>() {
            @Override
            public void accept(ArrayList<NukiLockJsonModel> nukiLockJsonModels) throws Exception {
                final Iterator it = nukiLockJsonModels.iterator();
                while(it.hasNext()){
                    final NukiLockJsonModel device = (NukiLockJsonModel) it.next();
                    System.out.println(device.getNukiId()+" "+device.getName());

                    String deviceSystemID = getSystemIDofNukiId(devices, device.getNukiId());
                    if (deviceSystemID!=null){
                        NukiLock temp = new NukiLock(deviceSystemID, Integer.toString(device.getNukiId()), thisGateway);
                        connectedDevices.add(temp);
                    }
                    it.remove();
                }
                orc.onSuccess(true);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                orc.onFailure(new Exception(throwable));
            }});
    }

    private String getSystemIDofNukiId(ArrayList<JSMDevice> devices, int nukiId) {
        for (JSMDevice dev : devices) {
            System.out.println("NUKIID "+dev.getSettings().get("nukiId"));
            System.out.println("NUKIMODEL "+dev.getModel() + " "+Model_constants.MODEL_NUKI);
            if (dev.getModel().equals(Model_constants.MODEL_NUKI)) {
                System.out.println("NUKIID COMPARE "+dev.getSettings().get("nukiId") + " "+nukiId);
                if (dev.getSettings().get("nukiId").equals(Integer.toString(nukiId))) {
                    System.out.println("lock ffound");
                    System.out.println(dev.getSystemID());
                    return dev.getSystemID();
                }
            }
        }
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

    public NukiRestService getRestService() {
        return restService;
    }

    void openLock(String nukiId, OnRequestCompleted<Boolean> orc) {
        String openLock = "1";
        restService.changeLockState(nukiId, openLock, token).subscribeOn(Schedulers.io()).subscribe(new Consumer<ArrayList<NukiLockJsonModel>>() {
            @Override
            public void accept(ArrayList<NukiLockJsonModel> nukiLockJsonModels) throws Exception {
                orc.onSuccess(true);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                orc.onFailure(new Exception(throwable));
            }});

    }

    void closeLock(String nukiId, OnRequestCompleted<Boolean> orc) {
        String closeLock = "2";
        restService.changeLockState(nukiId, closeLock, token).subscribeOn(Schedulers.io()).subscribe(new Consumer<ArrayList<NukiLockJsonModel>>() {
            @Override
            public void accept(ArrayList<NukiLockJsonModel> nukiLockJsonModels) throws Exception {
                orc.onSuccess(true);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                orc.onFailure(new Exception(throwable));
            }});

    }
}

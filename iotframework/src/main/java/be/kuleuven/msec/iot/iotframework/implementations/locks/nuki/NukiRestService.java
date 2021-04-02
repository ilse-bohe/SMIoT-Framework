package be.kuleuven.msec.iot.iotframework.implementations.locks.nuki;

import java.util.ArrayList;

import be.kuleuven.msec.iot.iotframework.implementations.locks.nuki.nukijsonmodel.NukiLockJsonModel;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense.versasensejsonmodel.VersaSenseDeviceJSONModel;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NukiRestService {
    //GATEWAY FUNCTIONALITY
    @GET("/list")
    Single<ArrayList<NukiLockJsonModel>> getAllDevices(@Query("token") String token);

    //LOK FUNCTIONALITY
    @GET("/lockAction")
    Single<ArrayList<NukiLockJsonModel>> changeLockState(@Query("nukiId") String nukiId,@Query("action") String action,@Query("token") String token);
}

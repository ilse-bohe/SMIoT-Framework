package be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense;

import java.util.ArrayList;

import be.kuleuven.msec.iot.iotframework.implementations.lamps.huelamp.retrofitrestbodies.HueBody;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense.retrofitrestbodies.SamplingRateBody;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense.versasensejsonmodel.VersaSenseDeviceJSONModel;

import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense.versasensejsonmodel.VersaSensePeripheralJSONModel;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense.versasensejsonmodel.VersaSensePeripheralSampleJSONModel;
import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by ilsebohe on 13/10/2017.
 */

public interface VersaSenseRestService {

    //GATEWAY FUNCTIONALITY
    @GET("api/v1/devices")
    Single<ArrayList<VersaSenseDeviceJSONModel>> getAllDevices();
    @GET("api/v1/devices/{mac}")
    Single<VersaSenseDeviceJSONModel> getDevice(@Path("mac") String mac);
    @GET("api/v1/devices/{id}/peripherals")
    Single<ArrayList<VersaSensePeripheralJSONModel>> getAllPeripheralsFromDevice(@Path("id") String id);

    //PERIPHERAL FUNCTIONALITY
    @GET("api/v1/devices/{mac}/peripherals/{id}")
    Single<VersaSensePeripheralJSONModel> getPeripheral(@Path("mac") String deviceMac, @Path(value = "id", encoded = true) String peripheralId);
    @GET("api/v1/devices/{mac}/peripherals/{id}/sample")
    Single<VersaSensePeripheralSampleJSONModel> getPeripheralSample(@Path("mac") String deviceMac, @Path(value = "id", encoded = true) String peripheralId);
    @PUT("api/v1/devices/{mac}/peripherals/{id}/rate")
    Completable changeSamplingRate(@Path("mac") String deviceMac, @Path(value = "id", encoded = true) String peripheralId, @Body SamplingRateBody body);
    @PUT("api/v1/devices/00-17-0D-00-00-30-E9-A7/peripherals/3303/5702/rate")
    Completable changeRate(@Body SamplingRateBody body);
}

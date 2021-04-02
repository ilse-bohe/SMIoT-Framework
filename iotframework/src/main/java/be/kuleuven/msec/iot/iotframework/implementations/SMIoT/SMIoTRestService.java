package be.kuleuven.msec.iot.iotframework.implementations.SMIoT;

import java.util.ArrayList;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.VirtualIoTDevice;
import be.kuleuven.msec.iot.iotframework.implementations.SMIoT.smiotjsonmodel.SMIoTDeviceJSONModel;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by ilsebohe on 10/01/2018.
 */

public interface SMIoTRestService {
    @GET("/devices")
    Single<ArrayList<SMIoTDeviceJSONModel>> getAllDevices();

    @POST("/lamp/{id}/on")
    Completable turnLampOn(@Path("id") String uniqueID);

    @POST("/lamp/{id}/off")
    Completable turnLampOff(@Path("id") String systemID);

    @POST("/lamp/{id}/color/{color}")
    Completable changeLampColor(@Path("id") String systemID, @Path("color") String rgBcolor);

    @POST("/lamp/{id}/brightness/{brightness}")
    Completable changeLampBrightness(@Path("id") String systemID, @Path("brightness") int brightness);

    @POST("/lamp/{id}/hue/{hue}")
    Completable changeLampHue(@Path("id") String systemID, @Path("hue") double hue);

    @POST("/lamp/{id}/saturation/{saturation}")
    Completable changeLampSaturation(@Path("id")String systemID,@Path("saturation") int saturation);

    @POST("/lamp/{id}/temperature/{temperature}")
    Completable changeLampTemperature(@Path("id")String systemID,@Path("temperature") int temperature);

    @GET("/{type}/{id}/value")
    Single<String> getValue(@Path("type") String type, @Path("id") String systemID);

    @GET("/{type}/{id}/monitor")
    Observable<String> monitorValue(@Path("type") String type, @Path("id") String systemID);

}

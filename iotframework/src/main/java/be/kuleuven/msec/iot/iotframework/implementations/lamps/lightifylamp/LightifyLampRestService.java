package be.kuleuven.msec.iot.iotframework.implementations.lamps.lightifylamp;

import be.kuleuven.msec.iot.iotframework.implementations.lamps.lightifylamp.lightifyjsonmodel.LightifyJSONModel;
import be.kuleuven.msec.iot.iotframework.implementations.lamps.lightifylamp.lightifyjsonmodel.LightifyLoginJSONModel;
import be.kuleuven.msec.iot.iotframework.implementations.lamps.lightifylamp.retrofitrestbodies.LoginBody;
import retrofit2.http.POST;




import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static android.R.attr.delay;


/**
 * Created by michielwillocx on 27/09/17.
 */

public interface LightifyLampRestService {


    //BASEURL: https://eu.lightify-api.org/lightify/services

    //GATEWAY FUNCTIONALITY
    @POST("session")
    Single<LightifyLoginJSONModel>authenticate(@Body LoginBody login);

    @GET("devices")
    Single<ArrayList<LightifyJSONModel>> getAllLamps();



    //LAMP FUNCTIONALITY
    @GET("devices/{id}")
    Single<LightifyJSONModel> getLamp ( @Path("id") int id);

    @GET("device/set")
    Completable changeOnOffState ( @Query("idx")int idx, @Query("time") int delay, @Query("onoff") int onoff);

    @GET("device/set")
    Completable changeBrightness ( @Query("idx")int idx, @Query("time") int delay, @Query("level") double brightness);

    @GET("device/set")
    Completable changeSaturation ( @Query("idx")int idx, @Query("time") int delay, @Query("saturation") double saturation);

    @GET("device/set")
    Completable changeHue (@Query("idx")int idx, @Query("time") int delay, @Query("hue") double hue);

    @GET("device/set")
    Completable changeColor (@Query("idx")int idx, @Query("time") int delay, @Query("color") String color);

    @GET("device/set")
    Completable changeTemperature (@Query("idx")int idx, @Query("time") int delay, @Query("ctemp") int temperature);







}

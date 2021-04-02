package be.kuleuven.msec.iot.iotframework.implementations.lamps.huelamp;



import java.util.Map;

import be.kuleuven.msec.iot.iotframework.implementations.lamps.huelamp.huejsonmodel.HueLampJSONModel;
import be.kuleuven.msec.iot.iotframework.implementations.lamps.huelamp.retrofitrestbodies.BrightnessBody;
import be.kuleuven.msec.iot.iotframework.implementations.lamps.huelamp.retrofitrestbodies.ColorBody;
import be.kuleuven.msec.iot.iotframework.implementations.lamps.huelamp.retrofitrestbodies.HueBody;
import be.kuleuven.msec.iot.iotframework.implementations.lamps.huelamp.retrofitrestbodies.OnBody;
import be.kuleuven.msec.iot.iotframework.implementations.lamps.huelamp.retrofitrestbodies.SaturationBody;
import be.kuleuven.msec.iot.iotframework.implementations.lamps.huelamp.retrofitrestbodies.TemperatureBody;
import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import static android.R.attr.id;

/**
 * Created by michielwillocx on 18/09/17.
 */

public interface HueLampRestService {



    //LAMP FUNCTIONALITY
    @GET("api/{username}/lights/{id}")
    Single<HueLampJSONModel> getState(@Path("username") String username, @Path("id")int id);

    @PUT("api/{username}/lights/{id}/state")
    Completable changeOnOffState(@Path("username") String username, @Path("id")int id, @Body OnBody body);

    @PUT("api/{username}/lights/{id}/state")
    Completable changeBrightness(@Path("username") String username, @Path("id")int id, @Body BrightnessBody body);

    @PUT("api/{username}/lights/{id}/state")
    Completable changeHue(@Path("username") String username, @Path("id")int id, @Body HueBody body);

    @PUT("api/{username}/lights/{id}/state")
    Completable changeSaturation(@Path("username") String username, @Path("id")int id, @Body SaturationBody saturation);

    @PUT("api/{username}/lights/{id}/state")
    Completable changeTemperature(@Path("username") String username, @Path("id")int id, @Body TemperatureBody onbody);

    @PUT("api/{username}/lights/{id}/state")
    Completable changeColor(@Path("username") String username, @Path("id")int id, @Body ColorBody onbody);


    //GATEWAY FUNCTIONALITY
    @GET("api/{username}/lights")
    Single<Map<String,HueLampJSONModel>> getAllLamps(@Path("username") String username);

}

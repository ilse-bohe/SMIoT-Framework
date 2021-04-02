package be.kuleuven.msec.iot.iotframework.implementations.sensorkits.allthingstalk;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.allthingstalk.allthingstalkjsonmodel.ATTAssetJSONModel;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.allthingstalk.allthingstalkjsonmodel.ATTDeviceJSONModel;
import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by ilsebohe on 04/10/2017.
 */

public interface ATTRestService {

    //BASEURL: https://api.allthingstalk.io

    //GATEWAY FUNCTIONALITY
    @FormUrlEncoded // --> header: 'Content-Type: application/x-www-form-urlencoded'
    @POST("login")
    Single<JsonObject> login(@Field("grant_type") String type, @Field("username")String username, @Field("password") String password, @Field("client_id") String client);
    @GET("devices")
    Single<ArrayList<ATTDeviceJSONModel>> getAllDevices();
    @GET("device/{id}/assets")
    Single<ArrayList<ATTAssetJSONModel>> getAllAssetsFromDevice(@Path("id") String id);


    //ASSET FUNCTIONALITY
    @GET("asset/{id}")
    Single<ATTAssetJSONModel> getAsset(@Path("id") String id);
}

package be.kuleuven.msec.iot.iotframework.implementations.lamps.huelamp;


import android.util.Log;

import java.io.Console;

import androidx.core.graphics.ColorUtils;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Lamp;
import be.kuleuven.msec.iot.iotframework.implementations.lamps.huelamp.huejsonmodel.HueLampJSONModel;
import be.kuleuven.msec.iot.iotframework.implementations.lamps.huelamp.huejsonmodel.HueState;
import be.kuleuven.msec.iot.iotframework.implementations.lamps.huelamp.retrofitrestbodies.BrightnessBody;
import be.kuleuven.msec.iot.iotframework.implementations.lamps.huelamp.retrofitrestbodies.ColorBody;
import be.kuleuven.msec.iot.iotframework.implementations.lamps.huelamp.retrofitrestbodies.HueBody;
import be.kuleuven.msec.iot.iotframework.implementations.lamps.huelamp.retrofitrestbodies.OnBody;
import be.kuleuven.msec.iot.iotframework.implementations.lamps.huelamp.retrofitrestbodies.SaturationBody;
import be.kuleuven.msec.iot.iotframework.implementations.lamps.huelamp.retrofitrestbodies.TemperatureBody;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by michielwillocx on 11/09/17.
 */

public class HueLamp extends Lamp {
    //TODO, After testing, this should probably be put in a seperate gateway object

    int gatewayId; //id op de gateway (0,1,2,...)
    String uniqueID; //unieke string die uniek is voor elke lamp...

    HueGateway gateway;

    public HueLamp(String color, int brightness, boolean on, boolean online, double hue, int saturation, int temperature, int gatewayId, String uniqueID, String systemID,  HueGateway gateway) {
        super(systemID, color, brightness, on, online, hue, saturation, temperature);
        this.gatewayId = gatewayId;
        this.uniqueID = uniqueID;
        this.gateway = gateway;
        //super.setSystemID(systemID);

    }
/*
    public HueLamp(int id, String uniqueID, HueGateway gateway){
        this.id=id;
        this.uniqueID=uniqueID;
        this.gateway=gateway;
        restService=createRetrofit();
    }*/

    public String getUniqueID() {
        return uniqueID;
    }


    /*private HueLampRestService createRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(gateway.getAddress())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // <- add this
                //OKHTTPCLIENT?
                .build()
                .create(HueLampRestService.class);
    }*/

    private void updateState(HueState state) {
        setOn(state.isOn());
        setBrightness(calculateBrSatToPercent(state.getBri()));
        float[] f = new float[]{(float) state.getXy()[0], (float) state.getXy()[1]};
        setColor(convertXYtoRGB(f));
        setHue(state.getHue());
        setOnline(state.isReachable());
        setTemperature(state.getCt());
        setSaturation(calculateBrSatToPercent(state.getSat()));

    }

    @Override
    public void turnOn(final OnRequestCompleted<Boolean> orc) {
        Completable result = gateway.getRestService().changeOnOffState(gateway.getAuthID(), gatewayId, new OnBody(true));
        Disposable disposable = result.subscribeOn(Schedulers.io())
                .observeOn(observeScheduler)
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        setOn(true);
                        orc.onSuccess(true);
                    }
                });

    }

    @Override
    public void turnOff(final OnRequestCompleted<Boolean> orc) {
        Completable result = gateway.getRestService().changeOnOffState(gateway.getAuthID(), gatewayId, new OnBody(false));
        Disposable disposable = result.subscribeOn(Schedulers.io())
                .observeOn(observeScheduler)
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        setOn(false);
                        orc.onSuccess(true);
                    }
                });
    }

    @Override
    public void requestStatus(OnRequestCompleted<Boolean> orc) {
        updateLampInfo(new OnRequestCompleted<Boolean>() {
            @Override
            public void onSuccess(Boolean response) {
                orc.onSuccess(isOn());
            }
        });
    }

    @Override
    public void changeColor(final String RGBcolor, final OnRequestCompleted<Boolean> orc) {
        Completable result = gateway.getRestService().changeColor(gateway.getAuthID(), gatewayId, new ColorBody(convertRGBtoXY(RGBcolor)));
        Disposable disposable = result.subscribeOn(Schedulers.io())
                .observeOn(observeScheduler)
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        setColor(RGBcolor);
                        orc.onSuccess(true);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        orc.onFailure(new Exception(throwable));
                    }
                });

    }

    @Override
    public void requestColor(OnRequestCompleted<String> orc) {
        updateLampInfo(new OnRequestCompleted<Boolean>() {
            @Override
            public void onSuccess(Boolean response) {
                orc.onSuccess(getColor());
            }
        });
    }

    @Override
    public void changeBrightness(final int brightness, final OnRequestCompleted<Boolean> orc) {

        Completable result = gateway.getRestService().changeBrightness(gateway.getAuthID(), gatewayId, new BrightnessBody((int)(((double) brightness/100)*254)));
        Disposable disposable = result.subscribeOn(Schedulers.io())
                .observeOn(observeScheduler)
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        setBrightness(brightness);
                        orc.onSuccess(true);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        orc.onFailure(new Exception(throwable));
                    }
                });
    }

    @Override
    public void requestBrightness(OnRequestCompleted<Integer> orc) {
        updateLampInfo(new OnRequestCompleted<Boolean>() {
            @Override
            public void onSuccess(Boolean response) {
                orc.onSuccess(getBrightness());
            }
        });
    }

    @Override
    public void changeHue(final double hue, final OnRequestCompleted<Boolean> orc) {
        Completable result = gateway.getRestService().changeHue(gateway.getAuthID(), gatewayId, new HueBody(hue*655.35));
        Disposable disposable = result.subscribeOn(Schedulers.io())
                .observeOn(observeScheduler)
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        setHue(hue);
                        orc.onSuccess(true);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        orc.onFailure(new Exception(throwable));
                    }
                });
    }

    @Override
    public void requestHue(OnRequestCompleted<Double> orc) {
        updateLampInfo(new OnRequestCompleted<Boolean>() {
            @Override
            public void onSuccess(Boolean response) {
                orc.onSuccess(getHue());
            }
        });
    }

    @Override
    public void changeSaturation(final int saturation, final OnRequestCompleted<Boolean> orc) {
        Completable result = gateway.getRestService().changeSaturation(gateway.getAuthID(), gatewayId, new SaturationBody((int)(saturation*2.54)));
        Disposable disposable = result.subscribeOn(Schedulers.io())
                .observeOn(observeScheduler)
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        setSaturation(saturation);
                        orc.onSuccess(true);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        orc.onFailure(new Exception(throwable));
                    }
                });

    }

    @Override
    public void requestSaturation(OnRequestCompleted<Integer> orc) {
        updateLampInfo(new OnRequestCompleted<Boolean>() {
            @Override
            public void onSuccess(Boolean response) {
                orc.onSuccess(getSaturation());
            }
        });
    }

    @Override
    public void changeTemperature(final int temperature, final OnRequestCompleted<Boolean> orc) {
        Completable result = gateway.getRestService().changeTemperature(gateway.getAuthID(), gatewayId, new TemperatureBody((int) (temperature*4.47+153)));
        Disposable disposable = result.subscribeOn(Schedulers.io())
                .observeOn(observeScheduler)
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        setTemperature(temperature);
                        orc.onSuccess(true);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        orc.onFailure(new Exception(throwable));
                    }
                });
    }

    @Override
    public void requestTemperature(OnRequestCompleted<Integer> orc) {
        updateLampInfo(new OnRequestCompleted<Boolean>() {
            @Override
            public void onSuccess(Boolean response) {
                orc.onSuccess(getTemperature());
            }
        });
    }

    @Override
    public void updateLampInfo(final OnRequestCompleted<Boolean> orc) {
        Single<HueLampJSONModel> result = gateway.getRestService().getState(gateway.getAuthID(), gatewayId);
        Disposable disposable = result.subscribeOn(Schedulers.io())
                .observeOn(observeScheduler)
                .subscribe(new Consumer<HueLampJSONModel>() {
                    @Override
                    public void accept(@NonNull HueLampJSONModel lampInfo) {
                        updateState(lampInfo.getState());
                        orc.onSuccess(true);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        orc.onFailure(new Exception(throwable));
                    }
                });

    }

    @Override
    public void monitorLampInfo(OnEventOccurred<Lamp> oeo) {
        //TODO
    }

    @Override
    public String toString() {
        return "Lamp OVERALL{" +
                "color='" + super.getColor() + '\'' +
                ", brightness=" + super.getBrightness() +
                ", on=" + super.isOn() +
                ", online=" + super.isOnline() +
                ", hue=" + super.getHue() +
                ", saturation=" + super.getSaturation() +
                ", temperature=" + super.getTemperature() +
                '}';
    }

    @Override
    public void isReachable(OnRequestCompleted<Boolean> orc) {
        gateway.getRestService().getState(gateway.getAuthID(), gatewayId).subscribeOn(Schedulers.io()).observeOn(observeScheduler).subscribe(new Consumer<HueLampJSONModel>() {
            @Override
            public void accept(HueLampJSONModel hueLampJSONModel) throws Exception {
                if(hueLampJSONModel.getState().isReachable()) orc.onSuccess(true);
                else orc.onFailure(new Exception("Lamp is unreachable"));
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                orc.onFailure(new Exception(throwable));
            }
        });
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
    /////////
    //Conversion Stuff
    /////////

    private int calculateBrSatToPercent(int inp) {
        return (int) (((double) inp / 254) * 100);
    }

    private int calculateBrSatToHueValue(int inp) {
        return (int) (((double) inp / 100) * 254);
    }

    public static String convertXYtoRGB(float[] xy) {
        Log.e("convertXYtoRGB", "in: " + xy);
        //moet deze /100 wel?
        float x = xy[0];///100;
        float y = xy[1];///100;
        float z = 1.0f - x - y;
        float Y = 100; // TODO: The given brightness value
        float X = (Y / y) * x;
        float Z = (Y / y) * z;
        int res = ColorUtils.XYZToColor(X, Y, Z);
        String strColor = String.format("#%06X", 0xFFFFFF & res).substring(1);
        Log.e("convertXYtoRGB", "out: " + strColor);
        return strColor;

    }

    public static float[] convertRGBtoXY(String rgb) {
        Log.e("convertRGBtoXYZ", "in: " + rgb);
        int r = Integer.parseInt(rgb.substring(0, 2), 16);
        int g = Integer.parseInt(rgb.substring(2, 4), 16);
        int b = Integer.parseInt(rgb.substring(4, 6), 16);
        Log.e("convertRGBtoXYZ", "rgb value check (remove this later): " + r + " " + g + " " + b);
        double[] result = new double[3];
        ColorUtils.RGBToXYZ(r, g, b, result);
        double X = result[0];
        double Y = result[1];
        double Z = result[2];
        float x = (float) (X / (X + Y + Z));
        float y = (float) (Y / (X + Y + Z));
        float[] ret = new float[]{x, y};
        Log.e("convertRGBtoXYZ", "out: " + ret);
        return ret;

    }

}

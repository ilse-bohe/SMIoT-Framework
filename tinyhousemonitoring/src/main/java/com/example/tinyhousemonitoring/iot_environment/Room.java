package com.example.tinyhousemonitoring.iot_environment;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.exceptions.VirtualIoTDeviceNotFoundException;
import be.kuleuven.msec.iot.iotframework.generic.componentlayer.Component;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Co2Sensor;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.ColorSensor;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Curtains;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.HumiditySensor;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Lamp;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.LightSensor;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Lock;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.TemperatureSensor;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Fireplace;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ilsebohe on 16/01/2018.
 */

public class Room extends Component {


    Lock lock;
    ArrayList<Lamp> lamps;
    Fireplace fireplace;
    Curtains curtains;

    TemperatureSensor temperatureSensor;
    HumiditySensor humiditySensor;
    LightSensor lightsensor;
    Co2Sensor co2Sensor;
    ColorSensor colorSensor;

    Boolean minLampOn = false;

    public Room(String componentname) {
        super(componentname);
        lamps = new ArrayList<>();
    }

    // LOCK
    public void setLock(Lock lock) {
        this.lock = lock;
    }

    public void openLock(OnRequestCompleted orc) {
        lock.open(orc);
    }

    public void closeLock(OnRequestCompleted orc) {
        lock.close(orc);
    }

    public void getLockStatus(OnRequestCompleted orc) {lock.requestState(orc);}

    public boolean hasLock(){
        if(lock!= null) return true;
        return false;
    }

    //LIGHTNING
    public void addLamp(Lamp lamp) {
        this.lamps.add(lamp);
    }

    public void lightsOn(OnRequestCompleted<Boolean> orc) {
        for (Lamp l : lamps) {
            l.turnOn(orc);
        }
    }

    public void lightsOff(OnRequestCompleted<Boolean> orc) {

        for (Lamp l : lamps) {
            l.turnOff(orc);
        }
    }

    public void changeBrightness(int brightness, OnRequestCompleted orc) {
        for (Lamp l : lamps) {
            l.changeBrightness(brightness, orc);
        }
    }

    public void changeColor(String color, OnRequestCompleted orc) {
        for (Lamp l : lamps) {
            l.changeColor(color, orc);
        }
    }

    public void getLightningStatus(OnRequestCompleted<Boolean> orc) {
        final CountDownLatch l = new CountDownLatch(lamps.size());
        minLampOn = false;
        for (final Lamp lamp : lamps) {
            lamp.observeOn(Schedulers.io());
            lamp.requestStatus(new OnRequestCompleted<Boolean>() {
                @Override
                public void onSuccess(Boolean isOn) {
                    if (isOn) minLampOn = true;
                    l.countDown();
                    lamp.observeOn(AndroidSchedulers.mainThread());
                }
            });
        }
        try {
           l.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("LightStatus minlampOn: "+minLampOn);
        orc.onSuccess(minLampOn);

    }

    String color;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean hasLamps(){
        if(lamps.size()>0) return true;
        return false;
    }

    public Lamp getLamp(String systemID) {
        for (Lamp lamp :
                lamps) {

            if(lamp.getSystemID().equals(systemID)) return lamp;
        }
        throw new VirtualIoTDeviceNotFoundException(this.getClass().getName(), "", systemID);
    }

    public ArrayList<Lamp> getLamps() {
        return lamps;
    }


    //FIREPLACE
    public void setFireplace(Fireplace fireplace) {
        this.fireplace = fireplace;
    }

    public void ligthenFireplace(OnRequestCompleted orc){
        fireplace.light(orc);
    }

    public void extinguishFireplace(OnRequestCompleted orc){
        fireplace.extinguish(orc);
    }

    public void getFireplaceStatus(OnRequestCompleted orc) {fireplace.requestState(orc);}

    public boolean hasFireplace(){
        if(fireplace!= null) return true;
        return false;
    }

    //CURTAINS
    public void setCurtains(Curtains curtains) {
        this.curtains = curtains;
    }

    public void openCurtains(OnRequestCompleted orc) {curtains.open(orc);}

    public void closeCurtains(OnRequestCompleted orc) {curtains.close(orc);}

    public void getCurtainStatus(OnRequestCompleted orc) {curtains.requestState(orc);}

    public boolean hasCurtains(){
        if(curtains!= null) return true;
        return false;
    }

    //TEMPERATURE
    public void setTemperatureSensor(TemperatureSensor temperatureSensor) {
        this.temperatureSensor = temperatureSensor;
    }

    public void getTemperature(OnRequestCompleted<Double> orc) {
        temperatureSensor.requestTemperature(orc);
    }

    public void monitorTemperature(OnEventOccurred<Double> oeo) {
        temperatureSensor.monitorTemperature(oeo);
    }

    public void unmonitorTemperature() {
        temperatureSensor.unmonitorTemperature();
    }

    public boolean hasTemperatureSensor(){
        if(temperatureSensor!= null) return true;
        return false;
    }

    //HUMIDITY
    public void setHumiditySensor(HumiditySensor humiditySensor) {
        this.humiditySensor = humiditySensor;
    }

    public void getHumidity(OnRequestCompleted<Double> orc) {
        humiditySensor.requestHumidity(orc);
    }

    public void monitorHumidity(OnEventOccurred<Double> oeo) {
        humiditySensor.monitorHumidity(oeo);
    }

    public void unmonitorHumidity() {
        humiditySensor.unmonitorHumidity();
    }

    public boolean hasHumiditySensor(){
        if(humiditySensor!= null) return true;
        return false;
    }

    //LIGHT_INTENSITY
    public void setLightSensor(LightSensor lightsensor) {
        this.lightsensor = lightsensor;
    }

    public void getLightIntensity(OnRequestCompleted<Double> orc) {
        lightsensor.requestLightIntensity(orc);
    }

    public void monitorLightIntensity(OnEventOccurred<Double> oeo) {
        lightsensor.monitorLightIntensity(oeo);
    }

    public void unmonitorLightIntensity() {
        lightsensor.unmonitorLightIntensity();
    }

    public boolean hasLightSensor(){
        if(lightsensor!= null) return true;
        return false;
    }

    //CO2 VALUES
    public void setCo2Sensor(Co2Sensor co2Sensor) {
        this.co2Sensor = co2Sensor;
    }

    public void getCo2(OnRequestCompleted<Double> orc) {
        co2Sensor.requestCo2Value(orc);
    }

    public void monitorCo2(OnEventOccurred<Double> oeo) {
        co2Sensor.monitorCo2Value(oeo);
    }

    public void unmonitorCo2() {
        co2Sensor.unmonitorCo2Value();
    }

    public boolean hasCo2Sensor(){
        if(co2Sensor!= null) return true;
        return false;
    }

    //COLOR VALUES
    public void setColorSensor(ColorSensor colorSensor) {
        this.colorSensor = colorSensor;
    }

    public void getColorValue(OnRequestCompleted<String> orc) {
        colorSensor.requestColor(orc);
    }

    public void monitorColorValue(OnEventOccurred<String> oeo) {
        colorSensor.monitorColor(oeo);
    }

    public void unmonitorColorValue() {
        colorSensor.unmonitorColor();
    }

    public boolean hasColorSensor(){
        if(colorSensor!= null) return true;
        return false;
    }

























}
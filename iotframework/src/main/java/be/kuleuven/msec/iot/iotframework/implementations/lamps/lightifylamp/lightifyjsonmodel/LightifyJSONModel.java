package be.kuleuven.msec.iot.iotframework.implementations.lamps.lightifylamp.lightifyjsonmodel;

import java.util.List;

/**
 * Created by michielwillocx on 27/09/17.
 */

public class LightifyJSONModel {

    private int deviceId;
    private String deviceType;
    private String manufacturer;
    private String modelName;
    private String name;
    private List groupList;
    private List bmpClusters;
    private int online;
    private int on;
    private double brightnessLevel;
    private double hue;
    private double saturation;
    private int temperature;
    private String firmwareVersion;
    private String color;

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List getGroupList() {
        return groupList;
    }

    public void setGroupList(List groupList) {
        this.groupList = groupList;
    }

    public List getBmpClusters() {
        return bmpClusters;
    }

    public void setBmpClusters(List bmpClusters) {
        this.bmpClusters = bmpClusters;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public int getOn() {
        return on;
    }

    public void setOn(int on) {
        this.on = on;
    }

    public double getBrightnessLevel() {
        return brightnessLevel;
    }

    public void setBrightnessLevel(double brightnessLevel) {
        this.brightnessLevel = brightnessLevel;
    }

    public double getHue() {
        return hue;
    }

    public void setHue(double hue) {
        this.hue = hue;
    }

    public double getSaturation() {
        return saturation;
    }

    public void setSaturation(double saturation) {
        this.saturation = saturation;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

}

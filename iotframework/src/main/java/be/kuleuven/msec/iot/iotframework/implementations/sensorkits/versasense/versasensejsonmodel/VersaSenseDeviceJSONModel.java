package be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense.versasensejsonmodel;

import java.util.ArrayList;

/**
 * Created by ilsebohe on 13/10/2017.
 */

public class VersaSenseDeviceJSONModel {
    String uid;
    String last_updated;
    ArrayList<VersaSensePeripheralJSONModel> peripherals;
    String name;
    String description;
    String location;
    String address;
    String type;
    double battery;
    String version;
    String mac;
    String Status;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }

    public ArrayList<VersaSensePeripheralJSONModel> getPeripherals() {
        return peripherals;
    }

    public void setPeripherals(ArrayList<VersaSensePeripheralJSONModel> peripherals) {
        this.peripherals = peripherals;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIp_address() {
        return address;
    }

    public void setIp_address(String ip_address) {
        this.address = ip_address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getBattery() {
        return battery;
    }

    public void setBattery(double battery) {
        this.battery = battery;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    @Override
    public String toString() {
        return "VersaSenseDeviceJSONModel{" +
                "uid='" + uid + '\'' +
                ", peripherals=" + peripherals +
                ", name='" + name + '\'' +
                '}';
    }
}

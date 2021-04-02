package be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense.versasensejsonmodel;

import java.util.ArrayList;

/**
 * Created by ilsebohe on 27/10/2017.
 */

public class VersaSensePeripheralSampleJSONModel {
    String identifier;
    String address;
    ArrayList<VersaSenseDataJSONModel> data;
    String name;
    String location;
    String mac;
    String timestamp;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<VersaSenseDataJSONModel> getData() {
        return data;
    }

    public void setData(ArrayList<VersaSenseDataJSONModel> data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

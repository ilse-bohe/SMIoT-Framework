package be.kuleuven.msec.iot.iotframework.systemmanagement.jsonmodel;

import java.util.Map;

/**
 * Created by michielwillocx on 25/09/17.
 */

public class JSMDevice {
    String type;
    String model;
    String systemID;
    Map<String, String> settings;
    String connector;




    public Map<String, String> getSettings() {
        return settings;
    }

    public void setSettings(Map<String, String> settings) {
        this.settings = settings;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSystemID() {
        return systemID;
    }

    public void setSystemID(String uniqueID) {
        this.systemID = uniqueID;
    }

    public String getConnector() {
        return connector;
    }

    public void setConnector(String connector) {
        this.connector = connector;
    }
}

package be.kuleuven.msec.iot.iotframework.systemmanagement.jsonmodel;

import java.util.Map;

/**
 * Created by michielwillocx on 25/09/17.
 */

public class JSMConnector {
    String systemID;
    String type;
    Map<String, String> settings;

    public String getSystemID() {
        return systemID;
    }

    public void setSystemID(String systemID) {
        this.systemID = systemID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public   Map<String, String> getSettings() {
        return settings;
    }

    public void setSettings(  Map<String, String> settings) {
        this.settings = settings;
    }
}

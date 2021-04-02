package be.kuleuven.msec.iot.iotframework.systemmanagement.jsonmodel;


import java.util.ArrayList;

/**
 * Created by michielwillocx on 25/09/17.
 */

public class JSMConfiguration {
    ArrayList<JSMConnector> connectors;
    ArrayList<JSMComponent> components;

    public ArrayList<JSMConnector> getConnectors() {
        return connectors;
    }

    public void setConnectors(ArrayList<JSMConnector> connectors) {
        this.connectors = connectors;
    }

    public ArrayList<JSMComponent> getComponents() {
        return components;
    }

    public void setComponents(ArrayList<JSMComponent> components) {
        this.components = components;
    }




}

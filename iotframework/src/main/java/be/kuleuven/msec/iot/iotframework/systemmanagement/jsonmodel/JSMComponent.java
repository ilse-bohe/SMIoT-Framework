package be.kuleuven.msec.iot.iotframework.systemmanagement.jsonmodel;

import java.util.ArrayList;

/**
 * Created by michielwillocx on 25/09/17.
 */

public class JSMComponent {

    String componentName;
    ArrayList<JSMComponent> childComponents;
    ArrayList<JSMDevice> devices;

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public ArrayList<JSMComponent> getChildComponents() {
        return childComponents;
    }

    public void setChildComponents(ArrayList<JSMComponent> childComponents) {
        this.childComponents = childComponents;
    }

    public ArrayList<JSMDevice> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<JSMDevice> devices) {
        this.devices = devices;
    }
}

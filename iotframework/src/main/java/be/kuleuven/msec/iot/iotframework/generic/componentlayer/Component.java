package be.kuleuven.msec.iot.iotframework.generic.componentlayer;



import java.util.ArrayList;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.VirtualIoTDevice;

/**
 * Created by michielwillocx on 25/09/17.
 */

public abstract class Component {

    String componentName;
    ArrayList<VirtualIoTDevice> devices;
    ArrayList<Component> childComponents;

    public Component(String componentName, ArrayList<VirtualIoTDevice> devices, ArrayList<Component> childComponents) {
        this.componentName = componentName;
        this.devices = devices;
        this.childComponents = childComponents;
    }
        public Component(String componentName, ArrayList<VirtualIoTDevice> devices) {
        this.componentName = componentName;
        this.devices = devices;
        this.childComponents = new ArrayList<Component>();
    }

    public Component(String componentName) {
        this.componentName = componentName;
        devices= new ArrayList<VirtualIoTDevice>();
        childComponents= new ArrayList<Component>();
    }


    public Component getChildComponentByName(String componentName){
        for (Component component:childComponents) {
            if (componentName.equals(component.getComponentName())){
                return component;
            }
        }
        return null;
    }

    public void addChildComponent(Component component){
        childComponents.add(component);
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public ArrayList<VirtualIoTDevice> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<VirtualIoTDevice> devices) {
        this.devices = devices;
    }

    public ArrayList<Component> getChildComponents() {
        return childComponents;
    }

    public void addDevice(VirtualIoTDevice virtualIoTDevice) {
        devices.add(virtualIoTDevice);
    }
}

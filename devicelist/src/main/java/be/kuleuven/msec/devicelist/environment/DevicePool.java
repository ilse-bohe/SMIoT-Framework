package be.kuleuven.msec.devicelist.environment;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.componentlayer.Component;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.HeartrateSensor;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.VirtualIoTDevice;

public class DevicePool extends Component {

    private String TAG= "DevicePool";

    private List<VirtualIoTDevice> devices = new ArrayList<>();

    DevicePool(String componentName) {
        super(componentName);
    }


}

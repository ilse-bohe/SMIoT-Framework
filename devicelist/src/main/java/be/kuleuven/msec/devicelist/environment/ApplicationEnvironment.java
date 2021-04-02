package be.kuleuven.msec.devicelist.environment;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.componentlayer.Environment;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Co2Sensor;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Curtains;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.HeartrateSensor;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.HumiditySensor;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Lamp;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Lock;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.LoudnessSensor;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Plug;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.PresenceSensor;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.PressureSensor;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.TemperatureSensor;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.VirtualIoTConnector;
import be.kuleuven.msec.iot.iotframework.implementations.heartratesensors.polarH7.PolarH7Device;
import be.kuleuven.msec.iot.iotframework.implementations.lamps.huelamp.HueGateway;
import be.kuleuven.msec.iot.iotframework.implementations.locks.nuki.NukiGateway;
import be.kuleuven.msec.iot.iotframework.implementations.loudnesssensors.androiddevice.AndroidLoudnessConnector;
import be.kuleuven.msec.iot.iotframework.implementations.plugs.tplinkhs110.HS110Connector;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.allthingstalk.AllThingsTalkGateway;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.dramco.DramcoGateway;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense.VersaSenseGateway;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Connector_constants;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Device_constants;
import be.kuleuven.msec.iot.iotframework.systemmanagement.jsonmodel.JSMConnector;
import be.kuleuven.msec.iot.iotframework.systemmanagement.jsonmodel.JSMDevice;

/**
 * Created by ilsebohe on 16/01/2018.
 */

public class ApplicationEnvironment extends Environment{

    final private String TAG = "ApplicationEnvironment";
    private static DevicePool devicePool;

    private static List<JSMDevice> devices;

    private Context context;

    public ApplicationEnvironment(Context context) {
        super();
        this.context = context;
        devicePool = new DevicePool("DevicePool");
    }

    public static DevicePool getDevicePool() {
        return devicePool;
    }

    public ArrayList<JSMDevice> getJSMDevices(){
        return configuration.getComponents().get(0).getDevices();
    }


    @Override
    public void loadEnvironment(final OnRequestCompleted<Boolean> orc) {
        final CountDownLatch latch = new CountDownLatch(configuration.getConnectors().size());
        Log.i(TAG, latch.getCount() + "connectors to load.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                //initializeConnectors
                for (JSMConnector connector : configuration.getConnectors()) {
                    switch (connector.getType()) {
                        case Connector_constants.CONNECTORTYPE_HUE:
                            virtualIoTConnectors.add(new HueGateway(connector.getSystemID(), connector.getSettings()));
                            break;
                        case Connector_constants.CONNECTORTYPE_ALL_THINGS_TALK:
                            virtualIoTConnectors.add(new AllThingsTalkGateway(connector.getSystemID(), connector.getSettings()));
                            break;
                        case Connector_constants.CONNECTORTYPE_VERSASENSE:
                            virtualIoTConnectors.add(new VersaSenseGateway(connector.getSystemID(), connector.getSettings()));
                            break;
                        case Connector_constants.CONNECTORTYPE_TPLINKPLUG:
                            virtualIoTConnectors.add(new HS110Connector(connector.getSystemID(), connector.getSettings()));
                            break;
                        case Connector_constants.CONNECTORTYPE_ANDROID_LOUDNESS:
                            virtualIoTConnectors.add(new AndroidLoudnessConnector(connector.getSystemID(), connector.getSettings()));
                            break;
                        case Connector_constants.CONNECTORTYPE_POLARH7:
                            virtualIoTConnectors.add(new PolarH7Device(connector.getSystemID(), connector.getSettings(), context));
                            break;
                        case Connector_constants.CONNECTORTYPE_NUKI:
                            virtualIoTConnectors.add(new NukiGateway(connector.getSystemID(), connector.getSettings()));
                            break;
                        case Connector_constants.CONNECTORTYPE_DRAMCO:
                            virtualIoTConnectors.add(new DramcoGateway(connector.getSystemID(), connector.getSettings()));
                            break;
                    }
                }
                for (final VirtualIoTConnector c : virtualIoTConnectors) {
                    c.initialize(new OnRequestCompleted() {
                        @Override
                        public void onSuccess(Object response) {
                            c.updateConnectedDeviceList(new OnRequestCompleted<Boolean>() {
                                @Override
                                public void onSuccess(Boolean response) {
                                    latch.countDown();
                                   Log.i(TAG, "Connector "+c.getSystemID()  + " succesfully loaded and updated.");
                                }
                            }, configuration.getComponents().get(0).getDevices());
                        }
                    });
                }
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (JSMDevice d : configuration.getComponents().get(0).getDevices()) {
                    Log.i(TAG, "New " + d.getType() + " added.");
                    devicePool.addDevice(getConnectorBySystemID(d.getConnector()).getConnectedDeviceBasedOnSystemID(d.getSystemID()));
                }
                orc.onSuccess(true);
            }
        }).start();
    }
}

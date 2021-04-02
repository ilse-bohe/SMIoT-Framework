package com.example.tinyhousemonitoring.iot_environment;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.componentlayer.Environment;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Button;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Co2Sensor;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.ColorSensor;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Curtains;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Fireplace;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.HeartrateSensor;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.HumiditySensor;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Lamp;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.LightSensor;
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
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.androidwear.AndroidWearDevice;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.dramco.DramcoGateway;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense.VersaSenseGateway;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.xiaomi.XiaomiGateway;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Connector_constants;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Device_constants;
import be.kuleuven.msec.iot.iotframework.systemmanagement.jsonmodel.JSMConnector;
import be.kuleuven.msec.iot.iotframework.systemmanagement.jsonmodel.JSMDevice;

/**
 * Created by ilsebohe on 16/01/2018.
 */

public class ApplicationEnvironment extends Environment{

    final private String TAG = "ApplicationEnvironment";
    private static Room room;
    private static Patient patient;

    private static List<JSMDevice> devices;

    private Context context;

    public ApplicationEnvironment(Context context) {
        super();
        this.context = context;
        room = new Room("Room");
        patient = new Patient("Patient");
    }

    public static Room getRoom() {
        //if (room!=null)
        return room;
    }

    public static Patient getPatient(){
        return patient;
    }

    public ArrayList<JSMDevice> getJSMDevices(){
        return configuration.getComponents().get(0).getDevices();
    }


    @Override
    public void loadEnvironment(final OnRequestCompleted<Boolean> orc) {
        final CountDownLatch latch = new CountDownLatch(configuration.getConnectors().size());
        System.out.println("number of connectors "+latch.getCount());
        new Thread(new Runnable() {
            @Override
            public void run() {
                //initializeConnectors
                for (JSMConnector connector : configuration.getConnectors()) {
                    switch (connector.getType()) {
                        case Connector_constants.CONNECTORTYPE_HUE:
                            //virtualIoTConnectors.add(new HueGateway(connector.getSystemID(), "http://" + connector.getSettings()[0], connector.getSettings()[1]));
                            virtualIoTConnectors.add(new HueGateway(connector.getSystemID(), connector.getSettings()));

                            break;
                     /*   case Connector_constants.CONNECTORTYPE_OSRAM:
                            virtualIoTConnectors.add(new LightifyGateway(connector.getSystemID(), connector.getSettings()[0], connector.getSettings()[1], connector.getSettings()[2]));
                            break;*/
                        case Connector_constants.CONNECTORTYPE_ALL_THINGS_TALK:
                            //virtualIoTConnectors.add(new AllThingsTalkGateway(connector.getSystemID(), connector.getSettings()[0], connector.getSettings()[1], connector.getSettings()[2], connector.getSettings()[3], connector.getSettings()[4]));
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

                        case Connector_constants.CONNECTORTYPE_ANDROID_WEAR:
                            virtualIoTConnectors.add(new AndroidWearDevice(connector.getSystemID(), connector.getSettings(), context));
                            break;

                        case Connector_constants.CONNECTORTYPE_NUKI:
                            virtualIoTConnectors.add(new NukiGateway(connector.getSystemID(), connector.getSettings()));
                            break;

                        case Connector_constants.CONNECTORTYPE_DRAMCO:
                            virtualIoTConnectors.add(new DramcoGateway(connector.getSystemID(), connector.getSettings()));
                            break;

                        case Connector_constants.CONNECTORTYPE_XIAOMI:
                            virtualIoTConnectors.add(new XiaomiGateway(connector.getSystemID(), connector.getSettings()));
                            break;


                        /*case Connector_constants.CONNECTORTYPE_ARDUINO:
                            virtualIoTConnectors.add(new Arduino101Gateway(connector.getSystemID(), connector.getSettings()[0], context));
                            break;

                        case Connector_constants.CONNECTORTYPE_SMIOT:
                            virtualIoTConnectors.add(new SMIoTGateway(connector.getSystemID(), connector.getSettings()[0], connector.getSettings()[1], connector.getSettings()[2]));*/
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
                                    System.out.println("SUCCES UPDATE "+c.getSystemID());
                                }
                            }, configuration.getComponents().get(0).getDevices());
                        }
                    });
                }
                try {
                    latch.await();
                    System.out.println("HERE");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (JSMDevice d : configuration.getComponents().get(0).getDevices()) {
                    System.out.println("type: " + d.getType() + " id: " +d.getSystemID());
                    switch (d.getType()) {
                        case Device_constants.TYPE_LAMP:
                            room.addLamp((Lamp) getConnectorBySystemID(d.getConnector()).getConnectedDeviceBasedOnSystemID(d.getSystemID()));
                       break;
                        case Device_constants.TYPE_TEMPERATURE_SENSOR:
                            room.setTemperatureSensor((TemperatureSensor) getConnectorBySystemID(d.getConnector()).getConnectedDeviceBasedOnSystemID(d.getSystemID()));
                            break;
                        case Device_constants.TYPE_HUMIDITY_SENSOR:
                            room.setHumiditySensor((HumiditySensor) getConnectorBySystemID(d.getConnector()).getConnectedDeviceBasedOnSystemID( d.getSystemID()));
                            break;
                        case Device_constants.TYPE_HEARTRATE_SENSOR:
                            patient.setHeartrateSensor((HeartrateSensor) getConnectorBySystemID(d.getConnector()).getConnectedDeviceBasedOnSystemID(d.getSystemID()));
                            break;
                        case Device_constants.TYPE_LOCK:
                            room.setLock((Lock) getConnectorBySystemID(d.getConnector()).getConnectedDeviceBasedOnSystemID(d.getSystemID()));
                            break;
                        case Device_constants.TYPE_CO2_SENSOR:
                            room.setCo2Sensor((Co2Sensor) getConnectorBySystemID(d.getConnector()).getConnectedDeviceBasedOnSystemID(d.getSystemID()));
                            break;
                        case Device_constants.TYPE_CURTAINS:
                            room.setCurtains((Curtains) getConnectorBySystemID(d.getConnector()).getConnectedDeviceBasedOnSystemID(d.getSystemID()));
                            break;
                        case Device_constants.TYPE_FIREPLACE:
                            room.setFireplace((Fireplace) getConnectorBySystemID(d.getConnector()).getConnectedDeviceBasedOnSystemID(d.getSystemID()));
                            break;
                        case Device_constants.TYPE_COLOR_SENSOR:
                            room.setColorSensor((ColorSensor) getConnectorBySystemID(d.getConnector()).getConnectedDeviceBasedOnSystemID(d.getSystemID()));
                            break;
                        case Device_constants.TYPE_LIGHT_SENSOR:
                            room.setLightSensor((LightSensor) getConnectorBySystemID(d.getConnector()).getConnectedDeviceBasedOnSystemID(d.getSystemID()));
                            break;
                        case Device_constants.TYPE_BUTTON:
                            patient.setEmergencyButon((Button) getConnectorBySystemID(d.getConnector()).getConnectedDeviceBasedOnSystemID(d.getSystemID()));
                            break;
                        /*case Device_constants.TYPE_LOCK:
                            room.setLock((Lock) getConnectorBySystemID(d.getConnector()).getConnectedDeviceBasedOnSystemID(Device_constants.TYPE_LOCK, d.getUniqueID()));
                        break;*/
                            //TODO AirQuility
                                /*case Device_constants.TYPE_AIR_QUALITY_SENSOR:
                                room.setAirQualitySensor((AirQualitySensor) getConnectorBySystemID(d.getConnector()).getConnectedDeviceBasedOnSystemID(d.getUniqueID()));
                                break;*/
                        default:
                            Log.w(TAG, "Unknown device type: " + d.getType());
                    }
                }
                orc.onSuccess(true);
            }
        }).start();
    }
}

package be.kuleuven.msec.iot.iotframework.implementations.sensorkits.arduino101;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.VirtualIoTConnector;
import be.kuleuven.msec.iot.iotframework.implementations.locks.Arduino101Lock;
import be.kuleuven.msec.iot.iotframework.implementations.potentiometers.Arduino101Potentiometer;
import be.kuleuven.msec.iot.iotframework.implementations.temperaturesensors.Arduino101TemperatureSensor;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Arduino101_constants;
import be.kuleuven.msec.iot.iotframework.systemmanagement.jsonmodel.JSMDevice;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ilsebohe on 25/10/2017.
 */

public class Arduino101Gateway extends VirtualIoTConnector {
    final private String TAG = "ArduinoGateway";


/*

    ArrayList<Arduino101TemperatureSensor> temperatureSensors;
    ArrayList<Arduino101Potentiometer> potentiometers;

*/

    String macAddress;
    int value=0;


    private Arduino101Gateway thisGateway;
    private Arduino101BluetoothLeService bluetoothLeService;

    public Arduino101BluetoothLeService getBluetoothLeService() {
        return bluetoothLeService;
    }

    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private CountDownLatch latch = new CountDownLatch(1);


    Context context;

    public Arduino101Gateway(String systemID, String macAddress, Context context) {
        super(systemID);
 /*       temperatureSensors = new ArrayList<>();
        potentiometers = new ArrayList<>();*/

        this.macAddress = macAddress;
        this.context = context;
        this.thisGateway = this;
    }

    public int getValue() {
        return value;
    }

    @Override
    public void initialize(final OnRequestCompleted orc) {
        bluetoothLeService= new Arduino101BluetoothLeService(context, macAddress);
        orc.onSuccess(true);
    }

    @Override
    public void isReachable(OnRequestCompleted<Boolean> orc) {

    }

    @Override
    public void monitorReachability(OnEventOccurred<Boolean> oeo) {

    }

    @Override
    public void connect(OnRequestCompleted<Boolean> orc) {

    }

    @Override
    public void disconnect(OnRequestCompleted<Boolean> orc) {

    }


    @Override
    public void updateConnectedDeviceList(final OnRequestCompleted<Boolean> orc, ArrayList<JSMDevice> devices) {
        bluetoothLeService.getCharacteristics().observeOn(Schedulers.io()).doOnSuccess(new Consumer<ArrayList<BluetoothGattCharacteristic>>() {
            @Override
            public void accept(ArrayList<BluetoothGattCharacteristic> bluetoothGattCharacteristics) throws Exception {
                for (BluetoothGattCharacteristic characteristic:bluetoothGattCharacteristics) {
                    switch (characteristic.getUuid().toString().toLowerCase()) {
                        case Arduino101_constants.TEMPERATURE:
                            Arduino101TemperatureSensor temp = new Arduino101TemperatureSensor(thisGateway, characteristic.getUuid());
//                            temperatureSensors.add(temp);
                            connectedDevices.add(temp);
                            break;
                        case Arduino101_constants.POTENTIOMETER:
                            Arduino101Potentiometer pot = new Arduino101Potentiometer(thisGateway, characteristic.getUuid());
//                            potentiometers.add(pot);
                            connectedDevices.add(pot);
                            break;
                        case Arduino101_constants.HUMIDITY:
                            //TODO humiditySensors
                            break;
                        case Arduino101_constants.LOCK:
                            Arduino101Lock lock = new Arduino101Lock(thisGateway, characteristic.getUuid());
                            connectedDevices.add(lock);
                            break;
                        default:
                            Log.w(TAG, "Unknown BluetoothGattCharacteristic");


                    }
                }
                orc.onSuccess(true);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<BluetoothGattCharacteristic>>() {
            @Override
            public void accept(@NonNull ArrayList<BluetoothGattCharacteristic> bluetoothGattCharacteristics) throws Exception {

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                orc.onFailure(new Exception(throwable));
            }
        });

    }
 /*   public TemperatureSensor getTemperatureSensorBasedOnUniqueID(String uniqueID) throws Exception {
        for (Arduino101TemperatureSensor sensor : temperatureSensors) {
            if (sensor.getCharacteristicUUID().toString().equals(uniqueID)) {
                return sensor;
            }
        }
        throw new VirtualIoTDeviceNotFoundException(this.getClass().getName(), this.getSystemID(), uniqueID);
    }

    public Potentiometer getPotentiometerBasedOnUniqueID(String uniqueID) throws Exception {
        for (Arduino101Potentiometer sensor : potentiometers) {
            if (sensor.getCharacteristicUUID().toString().equals(uniqueID)) {
                return sensor;
            }
        }
        throw new VirtualIoTDeviceNotFoundException(this.getClass().getName(), this.getSystemID(), uniqueID);
    }*/
}

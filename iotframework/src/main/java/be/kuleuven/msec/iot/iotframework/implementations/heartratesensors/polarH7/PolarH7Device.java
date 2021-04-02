package be.kuleuven.msec.iot.iotframework.implementations.heartratesensors.polarH7;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.HeartrateSensor;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.VirtualIoTConnector;
import be.kuleuven.msec.iot.iotframework.implementations.heartratesensors.polarH7.communication.PolarH7Client;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Device_constants;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Model_constants;
import be.kuleuven.msec.iot.iotframework.systemmanagement.jsonmodel.JSMDevice;
import io.reactivex.ObservableEmitter;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by Thomas on 6/03/2018.
 */

public class PolarH7Device extends VirtualIoTConnector {
    private PolarH7Client client;
    private PolarH7HeartrateSensor heartrateSensor;
    private PolarH7Device thisConnector;


    private LinkedList<OnRequestCompleted<Boolean>> reachabilityRequests = new LinkedList<>();

    public PolarH7Device(String systemID, Map<String, String> settings, Context context) {
        super(systemID);
        client = new PolarH7Client(context, settings.get("mac"));
        thisConnector = this;
       // heartRateSensor = new PolarH7HeartRateSensor(client);
    }


    public void register() {

    }

    public void unregister() {
        //bluetoothAdapter.;
    }

    @Override
    public void updateConnectedDeviceList(OnRequestCompleted<Boolean> orc, ArrayList<JSMDevice> devices) {
        String TAG = "PolarH7Device";
        Log.i(TAG, "updateConnectedDeviceList");
        String systemID= null;

        for (JSMDevice dev :devices) {
            if (dev.getModel().equals(Model_constants.MODEL_POLARH7) && dev.getType().equals(Device_constants.TYPE_HEARTRATE_SENSOR) ){
                systemID=dev.getSystemID();
            }
        }
        Log.i(TAG, "systemID "+systemID +" connector "+thisConnector);
        PolarH7HeartrateSensor temp = new PolarH7HeartrateSensor(systemID, thisConnector);
        connectedDevices.add(temp);

        orc.onSuccess(true);
    }

    @Override
    public void initialize(OnRequestCompleted orc) {
        orc.onSuccess(true);

    }

    @Override
    public void isReachable(OnRequestCompleted<Boolean> orc) {
        client.isReachable(orc);
    }

    @Override
    public void monitorReachability(OnEventOccurred<Boolean> oeo) {

    }

    @Override
    public void connect(OnRequestCompleted<Boolean> orc) {
        client.connect(orc);
    }

    @Override
    public void disconnect(OnRequestCompleted<Boolean> orc) {
        client.disconnect();
    }

    private HeartrateSensor getHeartRateSensor() {
        return this.heartrateSensor;
    }

    private void monitorSensor(OnEventOccurred<Integer> oeo) {
        client.monitorSensor(oeo);





    }

    void unmonitor() {
        client.unmonitor();
    }

    /*public void requestHeartRate(OnRequestCompleted orc){
        monitorSensor(new OnEventOccurred<Integer>() {
            @Override
            public void onUpdate(Integer response) {
                if (response!=0){
                    orc.onSuccess(response);
                    unmonitor();
                }
            }
        });
    }*/

    Observable<Integer> requestHeartRate(){

        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter emitter) {

                try {

                    /*
                     * The emitter can be used to emit each list item
                     * to the subscriber.
                     *
                     * */
                    monitorSensor(new OnEventOccurred<Integer>() {
                        @Override
                        public void onUpdate(Integer response) {
                            if (response!=0){
                                emitter.onNext(response);
                                unmonitor();
                            }
                        }
                    });

                    /*
                     * Once all the items in the list are emitted,
                     * we can call complete stating that no more items
                     * are to be emitted.
                     *
                     * */
                    emitter.onComplete();

                } catch (Exception e) {

                    /*
                     * If an error occurs in the process,
                     * we can call error.
                     *
                     * */
                    emitter.onError(e);
                }
            }
        });

    }

    Observable<Integer> monitorHeartRate(){

        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter emitter) {

                try {

                    /*
                     * The emitter can be used to emit each list item
                     * to the subscriber.
                     *
                     * */
                    monitorSensor(new OnEventOccurred<Integer>() {
                        @Override
                        public void onUpdate(Integer response) {
                            if (response!=0){
                                emitter.onNext(response);
                            }
                        }
                    });

                    /*
                     * Once all the items in the list are emitted,
                     * we can call complete stating that no more items
                     * are to be emitted.
                     *
                     * */
                   // emitter.onComplete();

                } catch (Exception e) {

                    /*
                     * If an error occurs in the process,
                     * we can call error.
                     *
                     * */
                    emitter.onError(e);
                }
            }
        });




    }



    void exceeds(int value, OnEventOccurred<Integer> oeo) {
        client.exceeds(value, oeo);
    }
}

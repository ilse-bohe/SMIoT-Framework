package be.kuleuven.msec.iot.iotframework.implementations.sensorkits.dramco;

import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.HashMap;
import java.util.Map;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense.versasensejsonmodel.VersaSenseDataJSONModel;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense.versasensejsonmodel.VersaSensePeripheralSampleJSONModel;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DramcoMonitoringService {

    private MqttClient client;
    private Observable<Pair<String, String>> monitor;
    Map<String, OnEventOccurred<String>> subscribers = new HashMap<String, OnEventOccurred<String>>();
    //CountDownLatch connectLatch;
    boolean connecting = false;

    public Observable<Pair<String, String>> getMonitor() {
        return monitor;
    }

    public MqttClient getClient() {
        return client;
    }

    public void setClient(MqttClient client) {
        this.client = client;
    }

    public DramcoMonitoringService(String address) {
        //connectLatch = new CountDownLatch(1);
        try {
            client = new MqttClient("tcp://"+address+":1883",
                    MqttClient.generateClientId(),
                    new MemoryPersistence()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        monitor = Observable.create(new ObservableOnSubscribe<Pair<String, String>>() {
            @Override
            public void subscribe(ObservableEmitter<Pair<String, String>> emitter) throws Exception {
                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        String str = new String(message.getPayload(), "UTF-8");
                        Log.e(topic, str);
                        emitter.onNext(new Pair<>(topic, str));
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                    }
                });
                //Log.e("Subscribing client", "waiting....");
                //client.subscribe("asset/"+assetId+"/feed", 1); --> executed in subscribe(id) method
            }
        });



        try {
            connectClient();
        } catch (MqttException e) {
            e.printStackTrace();
        }



    }

    private void connectClient() throws MqttException {
        connecting = true;
        Log.e("Connecting client", "waiting....");
//        MqttConnectOptions options = new MqttConnectOptions();
//        options.setUserName("user");
//        String password = "password";
//        options.setPassword(password.toCharArray());
        client.connect(/*options*/);
        while (!client.isConnected()) {
        }
        connecting = false;
        Log.e("Client connected", "client should be connected");

    }

    public void subscribe(final String type, final String id, final OnEventOccurred<String> oeo) {
        try {
            while (connecting) {
            }
            if (!client.isConnected())
                connectClient();
            System.out.println("subscribing");

            Disposable disposable = monitor
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Pair<String, String>>() {
                        @Override
                        public void accept(@NonNull Pair<String, String> pair) throws Exception {
                            //Gson gson = new Gson();
                            //VersaSensePeripheralSampleJSONModel sample = gson.fromJson(pair.second, VersaSensePeripheralSampleJSONModel.class);
                            //for (VersaSenseDataJSONModel data : sample.getData()) {
                                OnEventOccurred oeo = subscribers.get(pair.first);
                                if (oeo != null) {
                                    oeo.onUpdate(pair.second);
                                }
                            //}

                        }
                    });


            subscribers.put(type + "/" + id + "/status", oeo);
            client.subscribe(type + "/" + id + "/status", 1);
            Log.i("Subscribed to device", id);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void unsubscribe(String type, String id) {
        try {
            client.unsubscribe(type + "/" + id + "/status");
            subscribers.remove(type + "/" + id + "/status");
            if (subscribers.isEmpty())
                client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public void publish(String type, String id, String msg) {
        try {
            client.publish(type + "/" + id + "/set", new MqttMessage(msg.getBytes()));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}

package be.kuleuven.msec.iot.iotframework.implementations.sensorkits.allthingstalk;

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
import java.util.concurrent.CountDownLatch;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.implementations.sensorkits.allthingstalk.allthingstalkjsonmodel.AssetState;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ilsebohe on 10/10/2017.
 */

public class AllThingsTalkMonitoringService {

    MqttClient client;
    private Observable<Pair<String, String>> monitor;
    Map<String, OnEventOccurred<String>> subscribers = new HashMap<String, OnEventOccurred<String>>();
    String deviceToken;
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

    public void setMonitor(Observable<Pair<String, String>> monitor) {
        this.monitor = monitor;
    }

    public AllThingsTalkMonitoringService(final String deviceToken) {
        //connectLatch = new CountDownLatch(1);
        System.out.println("deviceToken: " + deviceToken);
        this.deviceToken = deviceToken;
        try {
            client = new MqttClient("tcp://api.allthingstalk.io:1883",
                    MqttClient.generateClientId(),
                    new MemoryPersistence()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        monitor = Observable.create(new ObservableOnSubscribe<Pair<String, String>>() {
                                        @Override
                                        public void subscribe(@NonNull final ObservableEmitter<Pair<String, String>> emitter) throws Exception {
                                            client.setCallback(new MqttCallback() {
                                                @Override
                                                public void connectionLost(Throwable cause) {
                                                    emitter.onError(cause);
                                                }

                                                @Override
                                                public void messageArrived(String topic, MqttMessage message) throws Exception {
                                                    String str = new String(message.getPayload(), "UTF-8");
                                                    Log.e(topic, str);
                                                    emitter.onNext(new Pair<String, String>(topic, str));
                                                }

                                                @Override
                                                public void deliveryComplete(IMqttDeliveryToken token) {
                                                }
                                            });
                                        }
                                    }
        );


        try {
            connectClient();
        } catch (MqttException e) {
            e.printStackTrace();
        }


    }

    private void connectClient() throws MqttException {
        connecting = true;
        Log.e("Connecting client", "waiting....");
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(deviceToken);
        String password = "password";
        options.setPassword(password.toCharArray());
        options.setCleanSession(true);
        client.connect(options);

        while (!client.isConnected()) {
        }
        connecting = false;
        Log.e("Client connected", "client should be connected");
    }

    public void subscribe(final String assetId, final OnEventOccurred<String> oeo) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (connecting) {
                    }
                    if (!client.isConnected())
                        connectClient();
                    System.out.println("subscribing");


                    monitor.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Pair<String, String>>() {
                                @Override
                                public void accept(@NonNull Pair<String, String> pair) throws Exception {
                                    Gson gson = new Gson();
                                    AssetState state = gson.fromJson(pair.second, AssetState.class);
                                    subscribers.get(pair.first).onUpdate(state.getValue().toString());
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(@NonNull Throwable throwable) throws Exception {
                                }
                            });

                    subscribers.put("asset/" + assetId + "/feed", oeo);
                    client.subscribe("asset/" + assetId + "/feed", 0);
                    Log.i("Subscribed to asset", assetId);
                } catch (MqttException e) {
                    oeo.onErrorOccurred(e);
                }
            }
        });
        thread.start();
    }

    public void unsubscribe(String assetId) {
        OnEventOccurred oeo = subscribers.get("asset/" + assetId + "/feed");
        //TODO fix error (komt voort van connectie die niet wordt opgezet
        if (oeo!= null){
            try {
                client.unsubscribe("asset/" + assetId + "/feed");
                subscribers.remove("asset/" + assetId + "/feed");
                if (subscribers.isEmpty())
                    client.disconnect();
            } catch (MqttException e) {
                oeo.onErrorOccurred(e);
            }
        }


    }
}

package be.kuleuven.msec.iot.iotframework.implementations.SMIoT;

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

import static java.lang.Thread.sleep;

/**
 * Created by ilsebohe on 12/01/2018.
 */

public class SMIoTMonitoringService {
    MqttClient client;
    private Observable<Pair<String, String>> monitor;    Map<String, OnEventOccurred<String>> subscribers = new HashMap<String, OnEventOccurred<String>>();
    CountDownLatch createLatch;
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

    public SMIoTMonitoringService(String ip_address, String mqttPort) {
        createLatch = new CountDownLatch(1);

        monitor = Observable.create(new ObservableOnSubscribe<Pair<String, String>>() {
            @Override
            public void subscribe(ObservableEmitter<Pair<String, String>> emitter) throws Exception {
                try {
                    client = new MqttClient("tcp://"+ip_address+":"+mqttPort,
                            MqttClient.generateClientId(),
                            new MemoryPersistence()
                    );

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

                    //connectClient();
                    createLatch.countDown();
                    //Log.e("Subscribing client", "waiting....");
                    //client.subscribe("asset/"+assetId+"/feed", 1); --> executed in subscribe(id) method

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        monitor.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Pair<String, String>>() {
                    @Override
                    public void accept(@NonNull Pair<String, String> pair) throws Exception {
                        OnEventOccurred oeo = subscribers.get(pair.first);
                        if (oeo != null) {
                            oeo.onUpdate(pair.second);
                        }

                    }
                });


    }

    private void connectClient() throws MqttException {
        if (!connecting) {
            connecting=true;
            Log.e("Connecting client", "waiting....");
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName("user");
            String password = "password";
            options.setPassword(password.toCharArray());
            client.connect(options);
        }
        while (!client.isConnected()) {
            //do nothing
        }
        if (client.isConnected()) {
            connecting=false;
            Log.e("Client connected", "client should be connected");
        }
    }

    public void subscribe(final String type, final String systemID, final OnEventOccurred<String> oeo) {
        try {
            createLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        System.out.println("subscribing");

        /*Disposable disposable = monitor
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Pair<String, String>>() {
                    @Override
                    public void accept(Pair<String, String> pair) throws Exception {
                        OnEventOccurred oeo = subscribers.get(pair.first);
                        if (oeo != null) {
                            oeo.onUpdate(pair.second);
                        }
                    }
                });*/

        System.out.println("subscribing");
        try {
            //connectLatch.await();
            //while(!connectionInitialized){};
            if (!client.isConnected()) {
                if (!connecting)
                    connectClient();
                while (!client.isConnected()) {
                }
            }
            //while(!client.isConnected()){};
            subscribers.put(type+"/"+systemID, oeo);
            client.subscribe(type+"/"+systemID, 1);
            Log.i("Subscribed for", type+"/"+systemID);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        /*Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connectLatch.await();

                    //while(!connectionInitialized){};
                    if (!client.isConnected()) connectClient();
                    //while(!client.isConnected()){};
                    subscribers.put(type+"/"+systemID, oeo);
                    client.subscribe(type+"/"+systemID, 1);
                    Log.i("Subscribed for", type+"/"+systemID);
                } catch (MqttException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();*/
    }

    public void unsubscribe(String type, String systemID) {
        try {
            subscribers.remove(type+"/"+systemID);
            if (subscribers.isEmpty())
                client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }



}

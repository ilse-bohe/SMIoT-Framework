package be.kuleuven.msec.iot.iotframework.implementations.fireplaces;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Fireplace;
import be.kuleuven.msec.iot.iotframework.implementations.lamps.huelamp.HueGateway;
import be.kuleuven.msec.iot.iotframework.implementations.lamps.huelamp.HueLamp;

public class HueFireplaceSimulation extends Fireplace {

    HueLamp lamp;
    Boolean lampOn;
    ArrayList<String> colors = new ArrayList<String>();

    ScheduledExecutorService executor;
    Runnable colorChange = new Runnable() {
        public void run() {
            lamp.changeColor(colors.get(new Random().nextInt(colors.size())), new OnRequestCompleted<Boolean>() {
                @Override
                public void onSuccess(Boolean response) {

                }
            });
        }
    };

    public HueFireplaceSimulation(HueLamp lamp){
        super(lamp.getSystemID());
        this.lamp=lamp;
        colors.add("FF1100");
        colors.add("FF2200");
        colors.add("ff8700");
        colors.add("ff8700");
        colors.add("ef5803");
        colors.add("ee5500");


    }



    @Override
    public void light(OnRequestCompleted<Boolean> orc) {


        lamp.turnOn(new OnRequestCompleted<Boolean>() {
            @Override
            public void onSuccess(Boolean response) {

                orc.onSuccess(response);
                executor= Executors.newScheduledThreadPool(1);
                executor.scheduleAtFixedRate(colorChange, 0, 1, TimeUnit.SECONDS);
            }
        });








        /*while (lampOn) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    lamp.changeColor(colors.get(new Random().nextInt(colors.size())), new OnRequestCompleted<Boolean>() {
                        @Override
                        public void onSuccess(Boolean response) {

                        }
                    });
                }
            }).start();
        }
*/



    }

    @Override
    public void extinguish(OnRequestCompleted<Boolean> orc) {
        lamp.turnOff(new OnRequestCompleted<Boolean>() {
            @Override
            public void onSuccess(Boolean response) {
                orc.onSuccess(response);
                if (executor != null) {
                    executor.shutdown();

                }
            }
        });

    }

    @Override
    public void requestState(OnRequestCompleted<Boolean> orc) {
        lamp.requestStatus(new OnRequestCompleted<Boolean>() {
            @Override
            public void onSuccess(Boolean response) {
                lampOn= response;
                orc.onSuccess(response);
            }
        });
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
}

package be.kuleuven.msec.iot.iotframework.implementations.plugs.tplinkhs110;

import com.polidea.rxandroidble.internal.connection.Connector;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Plug;
import be.kuleuven.msec.iot.iotframework.implementations.lamps.huelamp.huejsonmodel.HueLampJSONModel;
import be.kuleuven.msec.iot.iotframework.implementations.plugs.tplinkhs110.model.HS110Response;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HS110Plug extends Plug{
    HS110Connector connector;
    private static final byte[] ON = HS110Connector.encrypt("{\"system\":{\"set_relay_state\":{\"state\":1}}}");

    private static final byte[] OFF = HS110Connector.encrypt("{\"system\":{\"set_relay_state\":{\"state\":0}}}");

    private static final byte[] EMETER = HS110Connector.encrypt("{ \"emeter\":{ \"get_realtime\":null } }");



    public HS110Plug(String systemID, HS110Connector connector) {
        super(systemID);
        this.connector=connector;
    }

    @Override
    public void turnOn(final OnRequestCompleted<Boolean> orc) {
        try {
            Observable.fromCallable(new Callable<byte[]>() {

                @Override
                public byte[] call() throws Exception {
                    return connector.send(ON);
                }
            })

            .subscribeOn(Schedulers.io())
                    .observeOn(observeScheduler)
                    .subscribe(new io.reactivex.functions.Consumer<byte[]>() {
                        @Override
                        public void accept(byte[] bytes) {
                            try {
                                connector.parse(connector.decrypt(bytes), HS110Response.class);
                                orc.onSuccess(true);
                            } catch (IOException e) {
                                e.printStackTrace();
                                orc.onFailure(e);
                            }
                        }
                    });


           // connector.parse(connector.decrypt(connector.send(ON)), HS110Response.class);
        } catch (Exception e) {
            e.printStackTrace();
            orc.onFailure(e);
        }

    }

    @Override
    public void turnOff(final OnRequestCompleted<Boolean> orc) {
        try {
            Observable.fromCallable(new Callable<byte[]>() {

                @Override
                public byte[] call() throws Exception {
                    return connector.send(OFF);
                }
            })

                    .subscribeOn(Schedulers.io())
                    .observeOn(observeScheduler)
                    .subscribe(new io.reactivex.functions.Consumer<byte[]>() {
                        @Override
                        public void accept(byte[] bytes) {
                            try {
                                connector.parse(connector.decrypt(bytes), HS110Response.class);
                                orc.onSuccess(true);
                            } catch (IOException e) {
                                e.printStackTrace();
                                orc.onFailure(e);
                            }
                        }
                    });


            // connector.parse(connector.decrypt(connector.send(ON)), HS110Response.class);
        } catch (Exception e) {
            e.printStackTrace();
            orc.onFailure(e);
        }

    }

    @Override
    public void getCurrent(OnRequestCompleted<Double> orc) {

        try {
            Observable.fromCallable(new Callable<byte[]>() {

                @Override
                public byte[] call() throws Exception {
                    return connector.send(EMETER);
                }
            })

                    .subscribeOn(Schedulers.io())
                    .observeOn(observeScheduler)
                    .subscribe(new io.reactivex.functions.Consumer<byte[]>() {
                        @Override
                        public void accept(byte[] bytes) {
                            try {
                                HS110Response response = connector.parse(connector.decrypt(bytes), HS110Response.class);
                                orc.onSuccess(response.getEmeter().getRealtime().getCurrent());
                            } catch (IOException e) {
                                e.printStackTrace();
                                orc.onFailure(e);
                            }
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            orc.onFailure(e);
        }


    }

    @Override
    public void getVoltage(OnRequestCompleted<Double> orc) {

        try {
            Observable.fromCallable(new Callable<byte[]>() {

                @Override
                public byte[] call() throws Exception {
                    return connector.send(EMETER);
                }
            })

                    .subscribeOn(Schedulers.io())
                    .observeOn(observeScheduler)
                    .subscribe(new io.reactivex.functions.Consumer<byte[]>() {
                        @Override
                        public void accept(byte[] bytes) {
                            try {
                                HS110Response response = connector.parse(connector.decrypt(bytes), HS110Response.class);
                                orc.onSuccess(response.getEmeter().getRealtime().getVoltage());
                            } catch (IOException e) {
                                e.printStackTrace();
                                orc.onFailure(e);
                            }
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            orc.onFailure(e);
        }


    }

    @Override
    public void getCurrentPowerConsumption(OnRequestCompleted<Double> orc) {

        try {
            Observable.fromCallable(new Callable<byte[]>() {

                @Override
                public byte[] call() throws Exception {
                    return connector.send(EMETER);
                }
            })

                    .subscribeOn(Schedulers.io())
                    .observeOn(observeScheduler)
                    .subscribe(new io.reactivex.functions.Consumer<byte[]>() {
                        @Override
                        public void accept(byte[] bytes) {
                            try {
                                HS110Response response = connector.parse(connector.decrypt(bytes), HS110Response.class);
                                orc.onSuccess(response.getEmeter().getRealtime().getPower());
                            } catch (IOException e) {
                                e.printStackTrace();
                                orc.onFailure(e);
                            }
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            orc.onFailure(e);
        }


    }

    @Override
    public void getTotalPowerConsumption(OnRequestCompleted<Double> orc) {

        try {
            Observable.fromCallable(new Callable<byte[]>() {

                @Override
                public byte[] call() throws Exception {
                    return connector.send(EMETER);
                }
            })

                    .subscribeOn(Schedulers.io())
                    .observeOn(observeScheduler)
                    .subscribe(new io.reactivex.functions.Consumer<byte[]>() {
                        @Override
                        public void accept(byte[] bytes) {
                            try {
                                HS110Response response = connector.parse(connector.decrypt(bytes), HS110Response.class);
                                orc.onSuccess(response.getEmeter().getRealtime().getConsumption());
                            } catch (IOException e) {
                                e.printStackTrace();
                                orc.onFailure(e);
                            }
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            orc.onFailure(e);
        }


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
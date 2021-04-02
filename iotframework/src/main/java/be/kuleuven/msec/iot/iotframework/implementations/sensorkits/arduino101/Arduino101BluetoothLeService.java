package be.kuleuven.msec.iot.iotframework.implementations.sensorkits.arduino101;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.RxBleConnection;
import com.polidea.rxandroidble.RxBleDevice;
import com.polidea.rxandroidble.RxBleDeviceServices;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class Arduino101BluetoothLeService {

    final String TAG = "Arduino101BLEService";

    private RxBleClient rxBleClient;
    private RxBleDevice rxBleDevice;
    private RxBleConnection rxBleConnection;
    private Subscription subscription;

    ArrayList<UUID> monitoringCharacteristics =  new ArrayList<>();

    public Arduino101BluetoothLeService(Context context, String macAddress) {
        rxBleClient = RxBleClient.create(context);
        //RxBleClient.setLogLevel(RxBleLog.DEBUG);
        rxBleDevice = rxBleClient.getBleDevice(macAddress);
    }

    public Single<ArrayList<BluetoothGattCharacteristic>> getCharacteristics() {
        CountDownLatch latch = new CountDownLatch(1);

        final ArrayList<BluetoothGattCharacteristic> characteristics = new ArrayList<>();
        subscription = rxBleDevice.establishConnection(false).subscribe(new Observer<RxBleConnection>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(RxBleConnection connection) {
                connection.discoverServices().subscribe(new Observer<RxBleDeviceServices>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(RxBleDeviceServices rxBleDeviceServices) {
                        System.out.println("1 " + rxBleDevice.getConnectionState().toString());
                        for (BluetoothGattService service : rxBleDeviceServices.getBluetoothGattServices()) {
                            for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                                characteristics.add(characteristic);
                            }
                        }
                        latch.countDown();
                    }
                });

            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        subscription.unsubscribe();
        System.out.println("2 " + rxBleDevice.getConnectionState().toString());
        Single<ArrayList<BluetoothGattCharacteristic>> result = Single.just(characteristics);
        return result;
    }

    public Single<String> readCharacteristic(UUID characteristicUUID) {

        CountDownLatch latch = new CountDownLatch(1);
        final ArrayList<String> value = new ArrayList<>();
        if (rxBleDevice.getConnectionState().equals(RxBleConnection.RxBleConnectionState.CONNECTED)) {
            rxBleConnection.readCharacteristic(characteristicUUID).subscribe(new Observer<byte[]>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onNext(byte[] bytes) {

                    System.out.println(Integer.valueOf(bytesToString(bytes), 16));
                    value.add(Integer.valueOf(bytesToString(bytes), 16).toString());
                    latch.countDown();
                }
            });
        } else {
           subscription = rxBleDevice.establishConnection(false).subscribe(new Observer<RxBleConnection>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(RxBleConnection connection) {
                    rxBleConnection = connection;
                    rxBleConnection.readCharacteristic(characteristicUUID).subscribe(new Observer<byte[]>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(byte[] bytes) {
                            System.out.println(Integer.valueOf(bytesToString(bytes), 16));
                            value.add(Integer.valueOf(bytesToString(bytes), 16).toString());
                            latch.countDown();
                        }
                    });

                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Single<String> result = Single.just(value.get(0));
        closeConnectionIfNeeded();
        return result;

    }

    public Completable writeCharacteristic(UUID characteristicUUID, int value) {
        Log.i(TAG, "byte-array: "+new String(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array()));

        CountDownLatch latch = new CountDownLatch(1);
        if (rxBleDevice.getConnectionState().equals(RxBleConnection.RxBleConnectionState.CONNECTED)) {
            rxBleConnection.writeCharacteristic(characteristicUUID, ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array()).subscribe(new Observer<byte[]>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(byte[] bytes) {
                    // succes
                    latch.countDown();
                }
            });
        } else {
            subscription = rxBleDevice.establishConnection(false).subscribe(new Observer<RxBleConnection>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(RxBleConnection connection) {
                    rxBleConnection = connection;

                    rxBleConnection.writeCharacteristic(characteristicUUID, ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array()).subscribe(new Observer<byte[]>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(byte[] bytes) {
                            // succes
                            latch.countDown();
                        }
                    });

                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        closeConnectionIfNeeded();
        return Completable.complete();
    }

    public PublishSubject<String> monitorCharacteristic(UUID characteristicUUID) {

        PublishSubject<String> result = PublishSubject.create();

        if(!monitoringCharacteristics.contains(characteristicUUID)){

            if (rxBleDevice.getConnectionState().equals(RxBleConnection.RxBleConnectionState.CONNECTED)) {
                rxBleConnection.setupNotification(characteristicUUID).subscribe(new Observer<Observable<byte[]>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Observable<byte[]> observable) {
                        monitoringCharacteristics.add(characteristicUUID);
                        observable.subscribeOn(Schedulers.io())
                                .subscribe(new Observer<byte[]>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onNext(byte[] bytes) {
                                        System.out.println(bytesToString(bytes));
                                        System.out.println(Integer.valueOf(bytesToString(bytes), 16));
                                        result.onNext(Integer.valueOf(bytesToString(bytes), 16).toString());

                                    }
                                });
                    }
                });
            } else {
                subscription = rxBleDevice.establishConnection(false).subscribe(new Observer<RxBleConnection>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(RxBleConnection connection) {
                        rxBleConnection = connection;
                        rxBleConnection.setupNotification(characteristicUUID).subscribe(new Observer<Observable<byte[]>>() {
                            @Override
                            public void onCompleted() {


                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Observable<byte[]> observable) {
                                monitoringCharacteristics.add(characteristicUUID);
                                observable.subscribeOn(Schedulers.io())
                                        .subscribe(new Observer<byte[]>() {
                                            @Override
                                            public void onCompleted() {

                                            }

                                            @Override
                                            public void onError(Throwable e) {

                                            }

                                            @Override
                                            public void onNext(byte[] bytes) {
                                                System.out.println(bytesToString(bytes));
                                                System.out.println(Integer.valueOf(bytesToString(bytes), 16));
                                                result.onNext(Integer.valueOf(bytesToString(bytes), 16).toString());

                                            }
                                        });
                            }
                        });

                    }
                });
            }
        }

        return result;

    }

    public void unmonitorCharacteristic(UUID characteristicUUID) {

        if(monitoringCharacteristics.contains(characteristicUUID)){
            rxBleConnection.setupNotification(characteristicUUID);
            monitoringCharacteristics.remove(characteristicUUID);
            closeConnectionIfNeeded();
        }

    }

    private void closeConnectionIfNeeded() {
        if(monitoringCharacteristics.isEmpty()) subscription.unsubscribe();
    }

    private String bytesToString(byte[] bytes) {
        if (bytes != null && bytes.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(bytes.length);
            for (byte byteChar : bytes)
                stringBuilder.append(String.format("%02X", byteChar));
            return stringBuilder.toString();
        }
        return null;
    }


}

package be.kuleuven.msec.iot.iotframework.implementations.sensorkits.xiaomi;

import android.os.ParcelUuid;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.subjects.PublishSubject;
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

public class XiaomiBluetoothLeService {


    private BluetoothLeScannerCompat mscanner;
    private ScanSettings msettings;
    private List<ScanFilter> mfilters;
    private boolean scanningTemperature = false;
    private boolean scanningHumidity = false;
    private ScanCallback scanCallback ;
    private PublishSubject<String> temperatureResult;
    private PublishSubject<String> humidityResult;




    public XiaomiBluetoothLeService(String macAddress) {
        mscanner = BluetoothLeScannerCompat.getScanner();
        msettings = new ScanSettings.Builder()
                .setLegacy(false)
                .setScanMode(ScanSettings.SCAN_MODE_BALANCED).build();
        mfilters = new ArrayList<>();
        System.out.println("XIAOMI MAC: "+macAddress);
        mfilters.add(new ScanFilter.Builder()
                .setDeviceAddress(macAddress).build());

        temperatureResult = PublishSubject.create();
        humidityResult = PublishSubject.create();

        scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, @NonNull ScanResult result) {
                super.onScanResult(callbackType, result);

                System.out.println("XIAMOI DATA RECEIVED");
                String mUuid = "0000fe95-0000-1000-8000-00805f9b34fb";
                String rawData = null;
                if (result.getScanRecord().getServiceData(ParcelUuid.fromString(mUuid)) != null) {

                    rawData = toHexString(result.getScanRecord().getServiceData(ParcelUuid.fromString(mUuid)));
                    if (rawData.length() == 36) {
                        String rawHumidity = rawData.substring(rawData.length() - 4);
                        String rawTemperature = rawData.substring(rawData.length() - 8, rawData.length() - 4);
                        double humidity = toLittleEndian(rawHumidity) / (10.0);
                        double temperature = toLittleEndian(rawTemperature) / 10.0;
                        if(scanningTemperature){
                            temperatureResult.onNext(Double.toString(temperature));
                        }
                        if(scanningHumidity){
                            humidityResult.onNext(Double.toString(humidity));
                        }



                    }
                }
            }
        };
    }

    public PublishSubject<String> monitorTemperature(){
        startScanningIfnotScanning();
        scanningTemperature=true;
        return temperatureResult;
    }

    public void unmonitorTemperature(){
        scanningTemperature=false;
        stopScanningIfNeeded();
    }


    public PublishSubject<String> monitorHumidity(){
        startScanningIfnotScanning();
        scanningHumidity=true;
        return humidityResult;
    }

    public void unmonitorHumidity(){
        scanningHumidity=false;
        stopScanningIfNeeded();
    }

    private void startScanningIfnotScanning() {
        System.out.println("TEST START XIAOMI SCAN");
        if(!scanningTemperature&&!scanningHumidity) {
            System.out.println("START XIAOMI SCAN");
            mscanner.startScan(mfilters, msettings, scanCallback);
        }
    }

    private void stopScanningIfNeeded() {
        System.out.println("TEST STOP XIAOMI SCAN");
        if(!scanningTemperature&&!scanningHumidity){
            System.out.println("STOP XIAOMI SCAN");

            mscanner.stopScan(scanCallback);
        }
    }

    private static String toHexString(final byte[] byteArray) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[byteArray.length * 2];
        for (int j = 0; j < byteArray.length; j++) {
            int v = byteArray[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static int toLittleEndian(final String hex) {
        int ret = 0;
        String hexLittleEndian = "";
        if (hex.length() % 2 != 0) return ret;
        for (int i = hex.length() - 2; i >= 0; i -= 2) {
            hexLittleEndian += hex.substring(i, i + 2);
        }
        ret = Integer.parseInt(hexLittleEndian, 16);
        return ret;
    }


}

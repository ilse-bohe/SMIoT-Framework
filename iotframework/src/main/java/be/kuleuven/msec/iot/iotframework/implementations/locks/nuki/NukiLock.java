package be.kuleuven.msec.iot.iotframework.implementations.locks.nuki;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Lock;

public class NukiLock extends Lock {

    private NukiGateway gateway;
    private String nukiId;


    NukiLock(String systemID, String nukiId, NukiGateway gateway) {
        super(systemID);
        this.nukiId=nukiId;
        this.gateway=gateway;
    }

    @Override
    public void open(OnRequestCompleted<Boolean> orc) {
        System.out.println("OPENNUKILOCK");
        gateway.openLock(nukiId, orc);


    }

    @Override
    public void close(OnRequestCompleted<Boolean> orc) {
        System.out.println("CLOSENUKILOCK");
        gateway.closeLock(nukiId, orc);

    }

    @Override
    public void requestState(OnRequestCompleted<Boolean> orc) {
        //TODO
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

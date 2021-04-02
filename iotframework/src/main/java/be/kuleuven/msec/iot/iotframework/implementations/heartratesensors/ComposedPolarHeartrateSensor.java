package be.kuleuven.msec.iot.iotframework.implementations.heartratesensors;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.HeartrateSensor;
import be.kuleuven.msec.iot.iotframework.implementations.heartratesensors.polarH7.PolarH7HeartrateSensor;

/**
 * Created by ilsebohe on 22/10/2018.
 */

public class ComposedPolarHeartrateSensor extends HeartrateSensor {

    private PolarH7HeartrateSensor h7HR;
    private AndroidWearHeartrateSensor awHR;


    public ComposedPolarHeartrateSensor(String systemID, String unit) {
        super(systemID, unit);
    }

    @Override
    public void requestHeartrate(OnRequestCompleted<Integer> orc) {
        h7HR.requestHeartrate(orc);
        awHR.requestHeartrate(orc);
    }

    @Override
    public void monitorHeartrate(OnEventOccurred<Integer> oeo) {
        h7HR.monitorHeartrate(oeo);
        awHR.monitorHeartrate(oeo);
    }

    @Override
    public void unmonitorHeartrate() {
        h7HR.unmonitorHeartrate();
        awHR.unmonitorHeartrate();
    }

    @Override
    public void exceeds(int value, OnEventOccurred<Integer> oeo) {
        h7HR.exceeds(value, oeo);
        awHR.exceeds(value, oeo);

    }

    @Override
    public void isReachable(OnRequestCompleted<Boolean> orc) {
        h7HR.isReachable(orc);
        awHR.isReachable(orc);
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

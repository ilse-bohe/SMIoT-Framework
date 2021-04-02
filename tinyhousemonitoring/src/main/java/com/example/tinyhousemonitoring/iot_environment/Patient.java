package com.example.tinyhousemonitoring.iot_environment;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.componentlayer.Component;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.Button;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.HeartrateSensor;

public class Patient extends Component {

    HeartrateSensor heartrateSensor;
    Button emergencyButton;

    Patient(String componentName) {
        super(componentName);
    }

    //HEARTBEAT
    void setHeartrateSensor(HeartrateSensor heartrateSensor) {
        this.heartrateSensor =heartrateSensor;
    }
    public void getHeartrate(OnRequestCompleted<Integer> orc){
        heartrateSensor.requestHeartrate(orc);
    }

    public void monitorHeartrate(OnEventOccurred<Integer> oeo){
        heartrateSensor.monitorHeartrate(oeo);
    }

    public void unmonitorHeartrate(){
        heartrateSensor.unmonitorHeartrate();
    }

    public boolean hasHeartrateSensor(){
        if(heartrateSensor != null) return true;
        return false;
    }

    //EMERGENCYBUTTON
    void setEmergencyButon(Button emergencyButton) {
        this.emergencyButton =emergencyButton;
    }

    public void monitorEmergency(OnEventOccurred<Boolean> oeo){
        emergencyButton.monitorButton(oeo);
    }

    public void unmonitorEmergency(){
        emergencyButton.unmonitorButton();
    }

    public boolean hasEmergencyButton(){
        if(emergencyButton != null) return true;
        return false;
    }



}

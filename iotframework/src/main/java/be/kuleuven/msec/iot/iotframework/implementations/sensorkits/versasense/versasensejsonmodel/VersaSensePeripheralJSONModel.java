package be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense.versasensejsonmodel;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by ilsebohe on 13/10/2017.
 */

public class VersaSensePeripheralJSONModel {
    String identifier;
    String last_updated;
    ArrayList<VersaSenseMeasurementJSONModel> measurements;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }

    public ArrayList<VersaSenseMeasurementJSONModel> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(ArrayList<VersaSenseMeasurementJSONModel> measurements) {
        this.measurements = measurements;
    }
}

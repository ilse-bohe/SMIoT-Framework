package be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense.versasensejsonmodel;

/**
 * Created by ilsebohe on 27/10/2017.
 */

public class VersaSenseDataJSONModel {
    String unit;
    String datatype;
    String value;
    String measurement;
    String timestamp;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


}

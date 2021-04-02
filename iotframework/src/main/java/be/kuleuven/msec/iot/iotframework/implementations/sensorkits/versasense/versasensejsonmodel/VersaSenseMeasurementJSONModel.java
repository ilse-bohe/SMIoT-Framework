package be.kuleuven.msec.iot.iotframework.implementations.sensorkits.versasense.versasensejsonmodel;

/**
 * Created by ilsebohe on 27/10/2017.
 */

public class VersaSenseMeasurementJSONModel {
    String unit;
    String datatype;
    String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

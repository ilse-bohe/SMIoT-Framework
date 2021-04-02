package be.kuleuven.msec.iot.iotframework.implementations.sensorkits.allthingstalk.allthingstalkjsonmodel;

/**
 * Created by ilsebohe on 12/10/2017.
 */

public class ATTProfile {
    String type;
    String minimum;
    String maximum;
    String unit;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMinimum() {
        return minimum;
    }

    public void setMinimum(String minimum) {
        this.minimum = minimum;
    }

    public String getMaximum() {
        return maximum;
    }

    public void setMaximum(String maximum) {
        this.maximum = maximum;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "ATTProfile{" +
                "type='" + type + '\'' +
                ", minimum='" + minimum + '\'' +
                ", maximum='" + maximum + '\'' +
                ", unit='" + unit + '\'' +
                '}';
    }
}

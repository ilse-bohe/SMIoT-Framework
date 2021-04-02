package be.kuleuven.msec.iot.iotframework.implementations.sensorkits.allthingstalk.allthingstalkjsonmodel;

import java.util.Date;

/**
 * Created by ilsebohe on 04/10/2017.
 */

public class AssetState {

    Date at;
    Object value;

    public Date getAt() {
        return at;
    }

    public void setAt(Date at) {
        this.at = at;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "AssetState{" +
                "at=" + at +
                ", value=" + value +
                '}';
    }
}

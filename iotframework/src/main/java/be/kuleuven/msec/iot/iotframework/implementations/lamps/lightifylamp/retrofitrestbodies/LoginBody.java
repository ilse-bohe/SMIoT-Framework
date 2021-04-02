package be.kuleuven.msec.iot.iotframework.implementations.lamps.lightifylamp.retrofitrestbodies;

/**
 * Created by michielwillocx on 27/09/17.
 */

public class LoginBody {
    String username;
    String password;
    String serialNumber;

    public LoginBody(String username, String password, String serialNumber) {
        this.username = username;
        this.password = password;
        this.serialNumber = serialNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}

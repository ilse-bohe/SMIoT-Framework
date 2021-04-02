package be.kuleuven.msec.iot.iotframework.implementations.lamps.lightifylamp.lightifyjsonmodel;

/**
 * Created by michielwillocx on 27/09/17.
 */

public class LightifyLoginJSONModel {
    String userId;
    String securityToken;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }
}

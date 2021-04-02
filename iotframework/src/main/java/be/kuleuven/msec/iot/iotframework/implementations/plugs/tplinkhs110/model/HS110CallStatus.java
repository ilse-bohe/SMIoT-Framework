package be.kuleuven.msec.iot.iotframework.implementations.plugs.tplinkhs110.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HS110CallStatus
{
    @JsonProperty("err_code")
    private int errorCode;
    
    public HS110CallStatus()
    {
        super();
    }
    
    public int getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(int errorCode)
    {
        this.errorCode = errorCode;
    }
}

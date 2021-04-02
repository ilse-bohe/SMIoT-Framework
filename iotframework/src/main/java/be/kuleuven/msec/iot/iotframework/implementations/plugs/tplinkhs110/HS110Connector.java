
package be.kuleuven.msec.iot.iotframework.implementations.plugs.tplinkhs110;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.VirtualIoTConnector;
import be.kuleuven.msec.iot.iotframework.implementations.plugs.tplinkhs110.model.HS110Response;
import be.kuleuven.msec.iot.iotframework.systemmanagement.jsonmodel.JSMDevice;
import io.reactivex.Single;

public class HS110Connector extends VirtualIoTConnector {
    private static final Charset ASCII = Charset.forName("ASCII");

    private static final byte KEY = (byte) 0xAB;
    HS110Plug plug;
    HS110Connector thisConnector;
    String address;
    private int port = 9999;

    ObjectMapper mapper;

    public HS110Connector(String systemID, Map<String, String> settings) {
        super(systemID);


        this.address = settings.get("ip");
       // this.address="http://192.168.42.18";
        thisConnector=this;

    }

    @Override
    public void updateConnectedDeviceList(OnRequestCompleted<Boolean> orc, ArrayList<JSMDevice> devices) {
        //TODO id vragen uit system van plug
        plug = new HS110Plug(getSystemIdOfConnectedPlug(devices), this);
        connectedDevices.add(plug);
        orc.onSuccess(true);
    }

    private String getSystemIdOfConnectedPlug(ArrayList<JSMDevice> devices){
        for (JSMDevice dev :devices
             ) {
            if(dev.getConnector().equals(getSystemID())) return dev.getSystemID();
        }
        return null;
    }



    @Override
    public void initialize(OnRequestCompleted orc) {
        mapper = new ObjectMapper();
        this.mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        this.mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        this.mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // register response types
        this.mapper.registerSubtypes(HS110Response.class);

        orc.onSuccess(true);
    }

    @Override
    public void isReachable(OnRequestCompleted<Boolean> orc) {

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

    public static byte[] encrypt(String message)
    {
        byte[] data = message.getBytes(ASCII);
        byte[] enc = new byte[data.length + 4];
        ByteBuffer.wrap(enc).putInt(data.length);
        System.arraycopy(data, 0, enc, 4, data.length);
        byte key = KEY;
        for (int i = 4; i < enc.length; i ++)
        {
            enc[i] = (byte) (enc[i] ^ key);
            key = enc[i];
        }
        return enc;
    }

    public static String decrypt(byte[] data)
    {
        if (data == null) return null;
        byte key = KEY;
        byte nextKey = 0;
        for (int i = 4; i < data.length; i++)
        {
            nextKey = data[i];
            data[i] = (byte) (data[i] ^ key);
            key = nextKey;
        }
        return new String(data, 4, data.length - 4, ASCII);
    }

    public static String toHex(byte[] b)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
        {
            if ((b[i] & 0xF0) == 0) sb.append("0");
            sb.append(Integer.toHexString(b[i] & 0xFF));
        }
        return sb.toString();
    }

    public <T> T parse(String input, Class<T> type) throws IOException
    {
        if (input == null) return null;
        //Discuss with @intrbiz to use a Logging System
        //System.out.println("Parsing: " + input);
        try (JsonParser p = this.mapper.getFactory().createParser(input))
        {
            return this.mapper.readValue(p, type);
        }
    }

    public byte[] send(byte[] message) throws Exception
    {
        try (Socket socket = new Socket(this.address, this.port))
        {
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            // write
            out.write(message);
            out.flush();
            // read
            byte[] buffer = new byte[4096];
            int r = in.read(buffer);
            if (r == -1) return null;
            byte[] ret = new byte[r];
            System.arraycopy(buffer, 0, ret, 0, r);
            return ret;
        }
    }
/*
    public Single<byte[]> send(byte[] message) throws Exception
    {
        try (Socket socket = new Socket(this.address, this.port))
        {
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            // write
            out.write(message);
            out.flush();
            // read
            byte[] buffer = new byte[4096];
            int r = in.read(buffer);
            if (r == -1) return null;
            byte[] ret = new byte[r];
            System.arraycopy(buffer, 0, ret, 0, r);
            return Single.just(ret);
        }
    }
*/
/*
    public String sendMessage(String message) throws Exception
    {
        return decrypt(send(encrypt(message)));
    }*/

}
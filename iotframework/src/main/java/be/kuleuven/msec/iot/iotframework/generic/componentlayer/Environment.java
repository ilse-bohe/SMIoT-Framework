package be.kuleuven.msec.iot.iotframework.generic.componentlayer;


import android.bluetooth.BluetoothClass;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.VirtualIoTConnector;
import be.kuleuven.msec.iot.iotframework.systemmanagement.jsonmodel.JSMConfiguration;
import be.kuleuven.msec.iot.iotframework.systemmanagement.jsonmodel.JSMDevice;

/**
 * Created by michielwillocx on 25/09/17.
 */

public abstract class Environment {

    public ArrayList<VirtualIoTConnector> virtualIoTConnectors;
    public ArrayList<Component> components;
    public ArrayList<JSMDevice> devices;

    protected JSMConfiguration configuration;

    public Environment() {
        this.virtualIoTConnectors = new ArrayList<VirtualIoTConnector>();
        this.components = new ArrayList<Component>();
    }

    public void getConfigurationFromServer(Context applicationcontext, String fileName,  OnRequestCompleted<Boolean> orc){
        //TODO... AT THE MOMENT THIS JUST USES A MOCKUP LOCAL JSON FILE FOR TESTS
        //TODO CONTEXT ARGUMENT KAN ER WSS TERUG UIT EENS WE NAAR EEN ECHTE SERVER GAAN


        Gson gson = new Gson();
        String json = null;
        try {

            InputStream is = applicationcontext.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");



            configuration = gson.fromJson(json, JSMConfiguration.class);
            orc.onSuccess(true);
        } catch (FileNotFoundException e) {
            orc.onFailure(e);
            //e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            orc.onFailure(e1);
            e1.printStackTrace();
        } catch (IOException e1) {
            orc.onFailure(e1);
            e1.printStackTrace();
        }

        //orc.onSuccess(true);


    }


    public Component getComponentByName(String name){
        for (Component component:components) {
            if(name.equals(component.getComponentName())){
                return component;
            }
        }
        return null;
    }

    public VirtualIoTConnector getConnectorBySystemID(String systemID){

        for (VirtualIoTConnector virtualIoTConnector : virtualIoTConnectors) {
            if(systemID.equals(virtualIoTConnector.getSystemID())){
                return virtualIoTConnector;
            }
        }
        return null;
    }


//abstract methods...
    public abstract void loadEnvironment(OnRequestCompleted<Boolean> orc);

}

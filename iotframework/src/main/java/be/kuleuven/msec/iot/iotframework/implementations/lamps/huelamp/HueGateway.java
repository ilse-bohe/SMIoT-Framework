package be.kuleuven.msec.iot.iotframework.implementations.lamps.huelamp;

import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.VirtualIoTConnector;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import be.kuleuven.msec.iot.iotframework.implementations.fireplaces.HueFireplaceSimulation;
import be.kuleuven.msec.iot.iotframework.implementations.lamps.huelamp.huejsonmodel.HueLampJSONModel;
import be.kuleuven.msec.iot.iotframework.systemmanagement.constants.Device_constants;
import be.kuleuven.msec.iot.iotframework.systemmanagement.jsonmodel.JSMDevice;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by michielwillocx on 18/09/17.
 */

public class HueGateway extends VirtualIoTConnector {

    ArrayList<HueLamp> lamps;
    HueGateway thisGateway;


    Map<String, String> settings = new HashMap<>();
    String address;
    String authID;

    HueLampRestService restService;


   /* public HueGateway(String systemID, String address, String authID) {

        super(systemID);

        this.address = "http://" + address;
        this.AuthID = authID;
        lamps = new ArrayList<>();
        thisGateway = this;

    }
*/
    public HueGateway(String systemID, Map<String, String> settings) {

        super(systemID);

        this.settings=settings;

        this.address = "http://" + settings.get("ip");

        this.authID = settings.get("authID");
        lamps = new ArrayList<>();
        thisGateway = this;

    }

    @Override
    public void updateConnectedDeviceList(final OnRequestCompleted<Boolean> orc, ArrayList<JSMDevice> devices) {

        Single<Map<String, HueLampJSONModel>> result = restService.getAllLamps(authID);

        //TODO error handling
        Disposable disposable = result.subscribeOn(Schedulers.io())
                // .observeOn(AndroidSchedulers.mainThread()) //--> moet volgens mij niet geobserved worden op de main thread?
                .subscribe(new Consumer<Map<String, HueLampJSONModel>>() {
                    @Override
                    public void accept(@NonNull Map<String, HueLampJSONModel> lampMap) {
                        Iterator it = lampMap.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry) it.next();
                            System.out.println(pair.getKey() + " = " + pair.getValue());

                            String entry = (String) pair.getKey();
                            HueLampJSONModel dev = (HueLampJSONModel) pair.getValue();

                            //TODO DEZE MAPPING HIER zal aangepast moeten worden nog... testing phase...
                            // lamps.add(new HueLamp(HueLamp.convertXYtoRGB(new float[]{(float) dev.getState().getXy()[0], (float) dev.getState().getXy()[1]}), (int)(((double)dev.getState().getBri() / 254) * 100), dev.getState().isOn(), dev.getState().isReachable(), dev.getState().getHue(), (int)(((double)dev.getState().getSat() / 254) * 100), dev.getState().getCt(), Integer.valueOf(entry), hg, dev.getType(), dev.getName(), dev.getModelid(), dev.getManufacturername(), dev.getUniqueid(), dev.getSwversion(), dev.getSwconfigid(), dev.getProductid()));

                           String deviceSystemID = getSystemIDofUniqueID(devices, dev.getUniqueid());
                           System.out.println("deviceSystemID "+deviceSystemID);
                           if(deviceSystemID != null){
                               String type =getTypeofSystemID(devices, deviceSystemID) ;
                               switch (type){
                                      case Device_constants.TYPE_LAMP:  HueLamp temp = new HueLamp(HueLamp.convertXYtoRGB(new float[]{(float) dev.getState().getXy()[0], (float) dev.getState().getXy()[1]}), (int) (((double) dev.getState().getBri() / 254) * 100), dev.getState().isOn(), dev.getState().isReachable(), dev.getState().getHue(), (int) (((double) dev.getState().getSat() / 254) * 100), dev.getState().getCt(), Integer.valueOf((String) pair.getKey()), dev.getUniqueid(), deviceSystemID, thisGateway);
                                          lamps.add(temp); connectedDevices.add(temp); break;
                                   case Device_constants.TYPE_FIREPLACE:
                                       HueFireplaceSimulation temp2 = new HueFireplaceSimulation(new HueLamp(HueLamp.convertXYtoRGB(new float[]{(float) dev.getState().getXy()[0], (float) dev.getState().getXy()[1]}), (int) (((double) dev.getState().getBri() / 254) * 100), dev.getState().isOn(), dev.getState().isReachable(), dev.getState().getHue(), (int) (((double) dev.getState().getSat() / 254) * 100), dev.getState().getCt(), Integer.valueOf((String) pair.getKey()), dev.getUniqueid(), deviceSystemID, thisGateway));
                                       connectedDevices.add(temp2); break;
                               }


                               it.remove();
                           }

                        }
                        orc.onSuccess(true);
                    }
                });
    }

    private String getTypeofSystemID(ArrayList<JSMDevice> devices, String deviceSystemID) {
        for (JSMDevice dev :devices) {
            if (dev.getModel().equals("HUE")){
                if(dev.getSystemID().equals(deviceSystemID)) return dev.getType();
            }
        }
        return null;
    }

    private String getSystemIDofUniqueID(ArrayList<JSMDevice> devices, String uniqueid) {
        for (JSMDevice dev :devices) {
            if (dev.getModel().equals("HUE")){
                if(dev.getSettings().get("uniqueID").equals(uniqueid)) return dev.getSystemID();
            }

            
        }
        return null;
    }

    @Override
    public void initialize(OnRequestCompleted orc) {

        restService = new Retrofit.Builder()
                .baseUrl(address)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(HueLampRestService.class);

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

    /*public Lamp getLampBasedOnUniqueID(String uniqueID) throws Exception {
        for (HueLamp lamp : lamps) {
            if (lamp.getUniqueID().equals(uniqueID)) {
                return lamp;
            }
        }
        throw new VirtualIoTDeviceNotFoundException(this.getClass().getName(), this.getSystemID(), Lamp.class.getName(), uniqueID);
    }*/

    public HueLampRestService getRestService() {
        return restService;
    }

    public ArrayList<HueLamp> getLamps() {
        return lamps;
    }

    public void setLamps(ArrayList<HueLamp> lamps) {
        this.lamps = lamps;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAuthID() {
        return authID;
    }

    public void setAuthID(String authID) {
        this.authID = authID;
    }
}

package be.kuleuven.msec.iot.iotframework.implementations.sensorkits.androidwear.communication;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.annotation.NonNull;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnEventOccurred;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;

/**
 * Created by Thomas on 12/12/2017.
 * This class is used to communicate with a certain Android Wear device
 */

public class AndroidWearClient extends WearableListenerService {

    private final static String TAG = "AndroidWearClient";

    private String nodeId;
    private Context context;

    private static HashMap<String, OnRequestCompleted> requestsMap = new HashMap<>();
    private static HashMap<String, OnEventOccurred> sensorMonitorMap = new HashMap<>();

    public AndroidWearClient() {

    }

    public AndroidWearClient(String nodeId, Context context) {
        this.nodeId = nodeId;
        this.context=context;
/*        Wearable.getMessageClient(context.getApplicationContext()).addListener(this);
        Wearable.getDataClient(context.getApplicationContext()).addListener(this);*/
    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        Log.d(TAG, "message received");

        final String message = new String(messageEvent.getData());
        final String path = messageEvent.getPath();
        Log.d(TAG, "message received "+path);

        switch (path) {
            case Constants.REACHABLE:
                OnRequestCompleted orc = requestsMap.get(Constants.REACHABLE);
                if (orc != null) {
                    //String data = new String(messageEvent.getData(), StandardCharsets.UTF_8);
                    orc.onSuccess(true);
                }
                break;
            case Constants.MONITOR_HEARTRATE:
                Log.d(TAG, path);
                OnEventOccurred oeo = sensorMonitorMap.get(Constants.HEARTRATE_ID);
                if (oeo != null) {
                    Log.d(TAG, "oeo not null" );

                    String data = new String(messageEvent.getData(), StandardCharsets.UTF_8);
                    oeo.onUpdate(data);
                }
                else{
                    Log.d(TAG, "oeo is null" );

                }
                break;
            case Constants.REQUEST_HEARTRATE:
                OnRequestCompleted orc2 = requestsMap.get(Constants.HEARTRATE_ID);
                if (orc2 != null) {
                    String data = new String(messageEvent.getData(), StandardCharsets.UTF_8);
                    orc2.onSuccess(data);
                }
                break;
        }
    }

    public static byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value};
    }

    public static int byteArrayToInt(byte[] value) {
        int result = 0;
        for (int i = 0; i < value.length; i++) {
            result += value[i];
            if (i != value.length - 1)
                result = result << 8;
        }
        return result;
    }

    public void monitorSensor(OnEventOccurred oeo, String sensorId) {
        Log.d(TAG, "monitor");
        switch (sensorId){
            case Constants.HEARTRATE_ID:
                Log.d(TAG, "monitor heartrate");

                new SendMessageThread(Constants.MONITOR_HEARTRATE, "Request Monitor HeartRate").start();
                sensorMonitorMap.put(Constants.HEARTRATE_ID, oeo);
                break;
        }


       /* Task<Integer> task = Wearable.getMessageClient(context).sendMessage(nodeId, REQUEST_SENSOR_MONITORING, sensorId.getBytes(StandardCharsets.UTF_8));
        task.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                Log.d(TAG, "Sensor monitor message delivered!");
                sensorMonitorMap.put(sensorId, oeo);
            }
        });*/
    }

    public void unmonitor(String sensorId) {
        Log.d(TAG, "unmonitor");
        switch (sensorId){
            case Constants.HEARTRATE_ID:
                Log.d(TAG, "unmonitor heartrate");

                new SendMessageThread(Constants.UNMONITOR_HEARTRATE, "Request Unmonitor HeartRate").start();
                sensorMonitorMap.remove(Constants.HEARTRATE_ID);
                break;
        }



        /*Task<Integer> task = Wearable.getMessageClient(context).sendMessage(nodeId, UNMONITOR_SENSOR, sensorId.getBytes(StandardCharsets.UTF_8));
        task.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                Log.d(TAG, "Sensor unmonitor message delivered!");
                sensorMonitorMap.remove(sensorId);
                onFallMonitors.remove(sensorId);
                exceedsMap.remove(sensorId);
            }
        });*/
    }

    public void sensorValueExceeded(float[] values, OnEventOccurred oeo, String sensorId) {
       /* exceedsMap.put(sensorId, oeo);
        Wearable.getMessageClient(context).sendMessage(nodeId, REQUEST_SENSOR_VALUE_EXCEEDED, (sensorId + "|" + Arrays.toString(values)).getBytes(StandardCharsets.UTF_8));
   */
    }

    public void monitorReachability(OnEventOccurred<Boolean> oeo) {
/*
        reachabilityMonitors.add(oeo);
*/
    }

    public void requestSensorValue(OnRequestCompleted<float[]> orc, String sensorId) {
      /*  Task<Integer> task = Wearable.getMessageClient(context).sendMessage(nodeId, REQUEST_SENSOR_VALUE, sensorId.getBytes(StandardCharsets.UTF_8));
        task.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                requestQueue.add(orc);
            }
        });*/
    }

    public void fallDetected(OnEventOccurred<float[]> orc, String sensorId) {
       //TODO

        /*// Check for fall on mobile device
        // TODO move this to gateway
        onFallMonitors.put(sensorId, orc);*/
    }

    public void requestSensors(OnRequestCompleted<String[]> request) {
       //TODO
    }

    public void disconnect(OnRequestCompleted<Boolean> orc) {
       /* // Stop monitoring of sensors
        Task<Integer> task = Wearable.getMessageClient(context).sendMessage(nodeId, DISCONNECT, null);
        task.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                Wearable.getMessageClient(context.getApplicationContext()).removeListener(AndroidWearClient.this);
                Wearable.getDataClient(context.getApplicationContext()).removeListener(AndroidWearClient.this);
                orc.onSuccess(true);
            }
        });*/
    }

    public void connect(OnRequestCompleted<Boolean> orc) {
        /*Wearable.getMessageClient(context.getApplicationContext()).addListener(this);
        Wearable.getDataClient(context.getApplicationContext()).addListener(this);
        // Resume monitoring of sensors
        for (String sensorId : sensorMonitorMap.keySet()) {
            Wearable.getMessageClient(context).sendMessage(nodeId, REQUEST_SENSOR_MONITORING, sensorId.getBytes(StandardCharsets.UTF_8));
        }
        for (String sensorId : exceedsMap.keySet()) {
            Wearable.getMessageClient(context).sendMessage(nodeId, REQUEST_SENSOR_MONITORING, sensorId.getBytes(StandardCharsets.UTF_8));
        }
        orc.onSuccess(true);*/
    }

  /*  @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {
        // Used for synchronising data using data maps
        Log.d(TAG, "Data changed " + dataEventBuffer);
        for (DataEvent dataEvent : dataEventBuffer) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                // Check if sensorMap contains URI (=sensorId)
                String[] uri = dataEvent.getDataItem().getUri().toString().split("/");
                if (uri.length > 0) {
                    String sensorId = uri[uri.length - 1];
                    // Update monitor
                    OnEventOccurred oeo = sensorMonitorMap.get(sensorId);
                    if (oeo != null) {
                        // Sensormap has event
                        DataMapItem dataMapItem = DataMapItem.fromDataItem(dataEvent.getDataItem());
                        float[] sensorValues = dataMapItem.getDataMap().getFloatArray(SENSORVALUE_PATH);
                        oeo.onUpdate(sensorValues);
                    }
                    // Check if fall detected
                    OnEventOccurred o = onFallMonitors.get(sensorId);
                    if (o != null) {
                        DataMapItem dataMapItem = DataMapItem.fromDataItem(dataEvent.getDataItem());
                        float[] sensorValues = dataMapItem.getDataMap().getFloatArray(SENSORVALUE_PATH);
                        float[] lastValues;
                        if (sensorValues.length > 2 && (lastValues = lastSensorValues.get(sensorId)) != null && lastValues.length > 2) {
                            // Calculate delta
                            double gs = Math.sqrt(Math.pow(sensorValues[0] - lastValues[0], 2) + Math.pow(sensorValues[1] - lastValues[1], 2) + Math.pow(sensorValues[2] - lastValues[2], 2));
                            Log.d(TAG, "delta: " + gs);
                            // Check if delta exceeds 3G's
                            if (gs > (3d * 9.81)) {
                                // Fall detected
                                o.onUpdate(sensorValues);
                            }
                        }
                    }
                    if (oeo != null) {
                        // Update previous sensorValues
                        DataMapItem dataMapItem = DataMapItem.fromDataItem(dataEvent.getDataItem());
                        float[] sensorValues = dataMapItem.getDataMap().getFloatArray(SENSORVALUE_PATH);
                        lastSensorValues.put(sensorId, sensorValues);
                    }
                }
            }
        }
    }*/

    private class RequestSensorsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
            //TODO
        }
       /* @Override
        protected Void doInBackground(Void... voids) {
            Task<Integer> task = Wearable.getMessageClient(context.getApplicationContext()).sendMessage(nodeId, REQUEST_SENSORS, new byte[0]);
            try {
                Tasks.await(task);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
        */
    }

    public void isReachable(OnRequestCompleted<Boolean> orc) {
        requestsMap.put(Constants.REACHABLE, orc);
        new SendMessageThread(Constants.REACHABLE, null).start();
    }

    public static float[] stringToArray(String array) {
        // array looks like [1.02,12.54,538]
        if (array.length() > 0) {
            String values = array.substring(1, array.length() - 1);
            String[] stringArray = values.split(",");
            float[] floatValues = new float[stringArray.length];
            for (int i = 0; i < stringArray.length; i++) {
                floatValues[i] = Float.parseFloat(stringArray[i]);
            }
            return floatValues;
        }
        return new float[0];
    }

    class SendMessageThread extends Thread {
        String path;
        String message;

        //Constructor for sending information to the Data Layer//
        SendMessageThread(String p, String m) {
            path = p;
            message = m;
        }

        public void run() {
            Log.d(TAG,"run SendMessageThread");
            //Retrieve the connected devices, known as nodes//
            Task<List<Node>> wearableList =
                    Wearable.getNodeClient(context).getConnectedNodes();
            try {
                List<Node> nodes = Tasks.await(wearableList);
                for (Node node : nodes) {
                    Task<Integer> sendMessageTask =
                            //Send the message//
                            Wearable.getMessageClient(context).sendMessage(node.getId(), path, message.getBytes());
                    try {
                        //Block on a task and get the result synchronously//
                        Integer result = Tasks.await(sendMessageTask);
                        //sendmessage("I just sent the wearable a message " + sentMessageNumber++);
                        //if the Task fails, then…..//
                    } catch (ExecutionException exception) {
                        //TO DO: Handle the exception//
                    } catch (InterruptedException exception) {
                        //TO DO: Handle the exception//
                    }

                }

            } catch (ExecutionException exception) {
                //TO DO: Handle the exception//
            } catch (InterruptedException exception) {
                //TO DO: Handle the exception//
            }

        }
    }

}
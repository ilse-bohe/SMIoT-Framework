package be.kuleuven.msec.iot.heartrate;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MainWearActivity extends WearableActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor heartrateSensor;

    private SensorEventListener monitorSensorEventListener;

    private int samplingPeriod = 2;

    private TextView heartRateTextView;
    Button monitorHeartRateButton;
    Button unmonitorHeartRateButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wear);


        // Enables Always-on
        setAmbientEnabled();

        // Check if app has permission to access body sensors
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            // Request permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BODY_SENSORS}, 0);
        }
        sensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));
        heartrateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);


        heartRateTextView = (TextView) findViewById(R.id.heart_rate);
        monitorHeartRateButton = findViewById(R.id.monitor_heart_rate);
        //Create an OnClickListener//
        monitorHeartRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.registerListener(MainWearActivity.this, heartrateSensor, samplingPeriod);
            }
        });

        unmonitorHeartRateButton = findViewById(R.id.unmonitor_heart_rate);
        //Create an OnClickListener//
        unmonitorHeartRateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sensorManager.unregisterListener(MainWearActivity.this);
                heartRateTextView.setText("HR");
            }
        });

        //Register to receive local broadcasts, which we'll be creating in the next step//
        IntentFilter newFilter = new IntentFilter(Intent.ACTION_SEND);
        Receiver messageReceiver = new Receiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, newFilter);

    }

    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getStringExtra("path")) {
                case Constants.REACHABLE:
                    new SendMessage(Constants.REACHABLE, "I am reachable!").start();
                    break;
                case Constants.MONITOR_HEARTRATE:
                    monitorSensorEventListener = new SensorEventListener() {
                        @Override
                        public void onSensorChanged(SensorEvent sensorEvent) {
                            String msg = Float.toString(sensorEvent.values[0]);
                            new SendMessage(Constants.MONITOR_HEARTRATE, msg).start();
                        }

                        @Override
                        public void onAccuracyChanged(Sensor sensor, int i) {
                        }
                    };
                    sensorManager.registerListener(monitorSensorEventListener, heartrateSensor, samplingPeriod);
                    break;
                case Constants.UNMONITOR_HEARTRATE:
                    sensorManager.unregisterListener(monitorSensorEventListener);
                    break;
                case Constants.REQUEST_HEARTRATE:
                    sensorManager.registerListener(new SensorEventListener() {
                        @Override
                        public void onSensorChanged(SensorEvent sensorEvent) {
                            String msg = Float.toString(sensorEvent.values[0]);
                            new SendMessage(Constants.REQUEST_HEARTRATE, msg).start();
                            sensorManager.unregisterListener(this);
                        }

                        @Override
                        public void onAccuracyChanged(Sensor sensor, int i) {
                        }
                    }, heartrateSensor, samplingPeriod);
                    break;

            }

        }
    }

    class SendMessage extends Thread {
        String path;
        String message;
        //Constructor for sending information to the Data Layer//

        SendMessage(String p, String m) {
            path = p;
            message = m;
        }

        public void run() {
            //Retrieve the connected devices//
            Task<List<Node>> nodeListTask =
                    Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            try {
                //Block on a task and get the result synchronously//
                List<Node> nodes = Tasks.await(nodeListTask);
                for (Node node : nodes) {
                    //Send the message///
                    Task<Integer> sendMessageTask =
                            Wearable.getMessageClient(MainWearActivity.this).sendMessage(node.getId(), path, message.getBytes());
                    try {
                        Integer result = Tasks.await(sendMessageTask);
                        //Handle the errors//
                    } catch (ExecutionException exception) {
                        //TO DO//
                    } catch (InterruptedException exception) {
                        //TO DO//
                    }

                }

            } catch (ExecutionException exception) {
                //TO DO//
            } catch (InterruptedException exception) {
                //TO DO//
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        heartRateTextView.setText("" + (int) sensorEvent.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}

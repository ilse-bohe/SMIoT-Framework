package be.kuleuven.msec.devicelist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import be.kuleuven.msec.devicelist.environment.ApplicationEnvironment;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;
import be.kuleuven.msec.iot.iotframework.generic.devicelayer.VirtualIoTDevice;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    ApplicationEnvironment e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final CountDownLatch latch = new CountDownLatch(1);
        e = new ApplicationEnvironment(this);
        e.getConfigurationFromServer(this, "configurations.json", new OnRequestCompleted<Boolean>() {
            @Override
            public void onSuccess(Boolean response) {
                e.loadEnvironment(new OnRequestCompleted<Boolean>() {
                    @Override
                    public void onSuccess(Boolean response) {
                        latch.countDown();
                    }
                });
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        setContentView(R.layout.activity_main);
        CustomListAdapter ca = new CustomListAdapter(this, e.getDevicePool().getDevices());
        ListView lv = (ListView) findViewById(R.id.list);
        lv.setAdapter(ca);



    }
}

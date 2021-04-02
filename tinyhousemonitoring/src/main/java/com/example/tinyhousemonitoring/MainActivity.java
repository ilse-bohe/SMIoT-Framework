package com.example.tinyhousemonitoring;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.tinyhousemonitoring.fragments.HealthFragment;
import com.example.tinyhousemonitoring.fragments.HomeFragment;
import com.example.tinyhousemonitoring.iot_environment.ApplicationEnvironment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import be.kuleuven.msec.iot.iotframework.*;
import java.util.concurrent.CountDownLatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;

public class MainActivity extends AppCompatActivity {

    ApplicationEnvironment environment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
                    return true;

                case R.id.navigation_health:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HealthFragment()).commit();
                    return true;

                case R.id.navigation_switch_room:
                    //selectedFragment = SwitchFragment.newInstance();

                    SharedPreferences values = getSharedPreferences(getString(R.string.app_values), MODE_PRIVATE);
                    SharedPreferences.Editor editor = values.edit();
                    editor.clear();
                    editor.commit();

                    Intent intent = new Intent(getApplicationContext(), SwitchRoomActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finishAffinity();

                    return true;

            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadView();
    }

    private void loadView() {
        final String roomID = getSharedPreferences(getString(R.string.app_values), MODE_PRIVATE).getString(getString(R.string.room_id), null);
        System.out.println(roomID);
        if(roomID!=null){
            final CountDownLatch latch = new CountDownLatch(1);

            environment = new ApplicationEnvironment(this);
            environment.getConfigurationFromServer(this, roomID+".json" , new OnRequestCompleted<Boolean>() {
                @Override
                public void onSuccess(Boolean response) {
                    environment.loadEnvironment(new OnRequestCompleted<Boolean>() {
                        @Override
                        public void onSuccess(Boolean response) {
                            latch.countDown();
                        }
                    });
                }

//

            });
            try {
                latch.await();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        else {
            Intent intent = new Intent(this, SwitchRoomActivity.class);
            this.startActivity(intent);
        }

        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportActionBar().setTitle("Tiny House Monitoring - " +roomID);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.distrinet);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, new HomeFragment());
        transaction.commitAllowingStateLoss();


    }

}

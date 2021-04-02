package com.example.tinyhousemonitoring;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tinyhousemonitoring.iot_environment.ApplicationEnvironment;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.appcompat.app.AppCompatActivity;
import be.kuleuven.msec.iot.iotframework.callbackinterfaces.OnRequestCompleted;

import static android.content.Context.MODE_PRIVATE;

public class SwitchRoomActivity extends AppCompatActivity {

    Button startScanButton;
    Button submitButton;

    EditText barcodeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);

        if (!hasRoomID()) {
            loadView();
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
        }
    }

    private boolean hasRoomID() {
        SharedPreferences values = getSharedPreferences(getString(R.string.app_values), MODE_PRIVATE);
        if (!values.contains(getString(R.string.room_id))){
            return false;
        }
        if (values.getString(getString(R.string.room_id), null)==null){
            return false;

        }
        return true;
    }

    private void loadView() {

        setContentView(R.layout.activity_switch_room);

        final ImageView vRoom1 = (ImageView) findViewById(R.id.imageViewRoom1);
        final ImageView vRoom2 = (ImageView) findViewById(R.id.imageViewRoom2);
        final ImageView vOutdoor = (ImageView) findViewById(R.id.imageViewOutdoor);

        /*barcodeInfo = (EditText) findViewById(R.id.editText);
        startScanButton = (Button) findViewById(R.id.btn_scan);
        submitButton = (Button) findViewById(R.id.btn_submit);*/
       /* startScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQRScanner();
            }
        });*/
     /*   submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToSharedPreferences();
                ApplicationEnvironment e = new ApplicationEnvironment(getApplicationContext());
                e.getConfigurationFromServer(getApplicationContext(), barcodeInfo.getText().toString()+".json", new OnRequestCompleted<Boolean>() {
                    @Override
                    public void onSuccess(Boolean response) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        getApplicationContext().startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SwitchRoomActivity.this);
                        dialogBuilder.setTitle("Error");
                        dialogBuilder.setMessage("Could not find file " + barcodeInfo.getText().toString() + "\nPlease retry.");
                        dialogBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                        dialogBuilder.show();
                    }
                });

            }
        });*/



        vRoom1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToSharedPreferences("room1");
                ApplicationEnvironment e = new ApplicationEnvironment(getApplicationContext());
                e.getConfigurationFromServer(getApplicationContext(), "room1.json", new OnRequestCompleted<Boolean>() {
                    @Override
                    public void onSuccess(Boolean response) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        getApplicationContext().startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SwitchRoomActivity.this);
                        dialogBuilder.setTitle("Error");
                        dialogBuilder.setMessage("Could not find file " + barcodeInfo.getText().toString() + "\nPlease retry.");
                        dialogBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                        dialogBuilder.show();
                    }
                });
            }
        });

        vRoom2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToSharedPreferences("room2");
                ApplicationEnvironment e = new ApplicationEnvironment(getApplicationContext());
                e.getConfigurationFromServer(getApplicationContext(), "room2.json", new OnRequestCompleted<Boolean>() {
                    @Override
                    public void onSuccess(Boolean response) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        getApplicationContext().startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SwitchRoomActivity.this);
                        dialogBuilder.setTitle("Error");
                        dialogBuilder.setMessage("Could not find file " + barcodeInfo.getText().toString() + "\nPlease retry.");
                        dialogBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                        dialogBuilder.show();
                    }
                });
            }
        });

        vOutdoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToSharedPreferences("outdoor");
                ApplicationEnvironment e = new ApplicationEnvironment(getApplicationContext());
                e.getConfigurationFromServer(getApplicationContext(), "outdoor.json", new OnRequestCompleted<Boolean>() {
                    @Override
                    public void onSuccess(Boolean response) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        getApplicationContext().startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SwitchRoomActivity.this);
                        dialogBuilder.setTitle("Error");
                        dialogBuilder.setMessage("Could not find file " + barcodeInfo.getText().toString() + "\nPlease retry.");
                        dialogBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                        dialogBuilder.show();
                    }
                });

            }
        });

       
    }


    private void startQRScanner() {
        new IntentIntegrator(this).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                updateText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateText(String scanCode) {
        barcodeInfo.setText(scanCode);
    }
    private void saveToSharedPreferences() {
        SharedPreferences values = getSharedPreferences(getString(R.string.app_values), MODE_PRIVATE);
        SharedPreferences.Editor editor = values.edit();
        editor.putString(getString(R.string.room_id), barcodeInfo.getText().toString());
        editor.commit();
    }


    private void saveToSharedPreferences(String s) {
        SharedPreferences values = getSharedPreferences(getString(R.string.app_values), MODE_PRIVATE);
        SharedPreferences.Editor editor = values.edit();
        editor.putString(getString(R.string.room_id),s);
        editor.commit();
    }
}

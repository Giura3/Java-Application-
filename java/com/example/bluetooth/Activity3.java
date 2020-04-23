package com.example.bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

public class Activity3 extends Activity2 {

    Button statusbtn, btnDis;
    BluetoothDevice selectedDevice = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    MyApplication myapp;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothGatt mGatt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myapp = (MyApplication) getApplication();
        Intent newint = getIntent();
        myapp = (MyApplication) getApplication();
        selectedDevice = myapp.selectedDevice;
        connectToDevice(selectedDevice);
        setContentView(R.layout.activity_3);

        btnDis = (Button) findViewById(R.id.button4);
        statusbtn = (Button) findViewById(R.id.status);
        Button back= (Button)findViewById(R.id.back);



        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Disconnect();
                msg("Disconected");
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                openActivity1();
            }
        });
    }


    private void Disconnect () {
        if ( myapp.mGatt!=null ) {
            myapp.mGatt.disconnect();
        }
        finish();
    }

    private void msg (String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    public void connectToDevice(BluetoothDevice device) {
        if (mGatt == null) {
            mGatt = device.connectGatt(this, false, gattCallback);
            myapp.mGatt = mGatt;
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    statusbtn.setText("Connected");
                    Log.i("gattCallback", "STATE_CONNECTED");
                   // Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                    gatt.discoverServices();

                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            Log.i("onServicesDiscovered", services.toString());
            List<BluetoothGattCharacteristic> characteristics = services.get(1).getCharacteristics();
//            gatt.readCharacteristic(services.get(1).getCharacteristics().get
//                    (0));
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {
//            Log.i("onCharacteristicRead", characteristic.toString());
//            gatt.disconnect();
        }
    };

    public void openActivity1(){

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
    }
}
package com.example.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;


public class MainActivity extends AppCompatActivity{


    private static final String TAG = "MainActivity";
    TextView mPairedTv;
    BluetoothAdapter mBluetoothAdapter;
    private OutputStream outputStream;
    MyApplication myapp;

    //Create a Broadcast receiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //when discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "onReceive: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "onReceive: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "onReceive: STATE TURNING ON");
                        break;
                }
            }
        }
    };


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myapp = (MyApplication) getApplication();
        //turn on button
        Button turnon = (Button) findViewById(R.id.turnon);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        turnon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "OnClick: enabling bluetooth.");
                enableBT();
                if(mBluetoothAdapter.isEnabled()){
                    showToast("Is already enabled!");
                }
            }
        });


        //turn off button
        Button turnoff = (Button) findViewById(R.id.turnoff);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        turnoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "OnClick: disabling bluetooth.");
                disableBT();
                showToast("Bluetooth is turned off!");

            }
        });

        // close app button
        Button closeapp = (Button) findViewById(R.id.closeapp);
        closeapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        mPairedTv = findViewById(R.id.deviceslist);


        // show paired devices
        Button getpaired = (Button) findViewById(R.id.getpaired);
        getpaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBluetoothAdapter.isEnabled()) {
                    openActivity2();
                } else {
                    showToast("Turn on the Bluetooth");
                }
            }
        });


        // the forward command
        Button forward = (Button) findViewById(R.id.forward);
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    writeCharacteristic("f");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // the backward command
        Button backward = (Button) findViewById(R.id.backward);
        backward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    writeCharacteristic("b");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // the left command
        Button left = (Button) findViewById(R.id.left);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    writeCharacteristic("l");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // the right command
        Button right = (Button) findViewById(R.id.right);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Log.d("","right");
                    writeCharacteristic("r");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        // the stop command
        Button stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    writeCharacteristic("s");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }


    //toast message function
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void enableBT() {
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "enableBT: Does not have BT capabilities.");
        }
        if (!mBluetoothAdapter.isEnabled()) {


            Log.d(TAG, "enableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);

        }

    }

    public void disableBT() {
        if (mBluetoothAdapter.isEnabled()) {

            Log.d(TAG, "disableBT: disabling BT.");
            mBluetoothAdapter.disable();
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
    }

//    public boolean send(byte[] data) {
//        if (myapp.mGatt == null || mBluetoothGattService == null) {
//            Log.w(TAG, "BluetoothGatt not initialized");
//            return false;
//        }
//
//        BluetoothGattCharacteristic characteristic =
//                mBluetoothGattService.getCharacteristic(UUID_SEND);
//
//        if (characteristic == null) {
//            Log.w(TAG, "Send characteristic not found");
//            return false;
//        }
//
//        characteristic.setValue(data);
//        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
//        return mBluetoothGatt.writeCharacteristic(characteristic);
//    }

    public void openActivity2(){
        Intent intent = new Intent(this, Activity2.class);
        startActivity(intent);
    }

    public void write(String s) throws IOException{
        outputStream = myapp.btSocket.getOutputStream();
        outputStream.write(s.getBytes());
    }

//    public boolean writeCharacteristic(){
//
//        //check mBluetoothGatt is available
//        if (mBluetoothGatt == null) {
//            Log.e(TAG, "lost connection");
//            return false;
//        }
//        BluetoothGattService Service = mBluetoothGatt.getService(your Services);
//        if (Service == null) {
//            Log.e(TAG, "service not found!");
//            return false;
//        }
//        BluetoothGattCharacteristic charac = Service
//                .getCharacteristic(your characteristic);
//        if (charac == null) {
//            Log.e(TAG, "char not found!");
//            return false;
//        }
//
//        byte[] value = new byte[1];
//        value[0] = (byte) (21 & 0xFF);
//        charac.setValue(value);
//        boolean status = mBluetoothGatt.writeCharacteristic(charac);
//        return status;
//    }

    public void writeCharacteristic(final String data) throws IOException {
        Log.d("","Inainte" );
        if (mBluetoothAdapter == null || myapp.mGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        Log.d("", "Dupa");
        final String HM_CONF = "0000ffe0-0000-1000-8000-00805f9b34fb";//predefind identifici dispozitivul
        final String HM_RX_TX ="0000ffe1-0000-1000-8000-00805f9b34fb";//canalul prin care comunica dispozitivul cu bluetooth
        final UUID UUID_HM_RX_TX =
                UUID.fromString(HM_RX_TX);
        final UUID UUID_HM_CONF =
                UUID.fromString(HM_CONF);
        List<BluetoothGattService> services = myapp.mGatt.getServices();
        services.forEach(new Consumer<BluetoothGattService>() {
            @Override
            public void accept(BluetoothGattService service) {
                if (service.getUuid().equals(UUID_HM_CONF)) {
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID_HM_RX_TX);
                    characteristic.setValue(data.getBytes());
                    myapp.mGatt.writeCharacteristic(characteristic);
                }
            }
        });

    }

}


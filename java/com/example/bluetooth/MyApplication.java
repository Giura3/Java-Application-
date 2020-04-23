package com.example.bluetooth;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothSocket;

public class MyApplication extends Application {
    BluetoothSocket btSocket;
    BluetoothDevice selectedDevice;
    BluetoothGatt mGatt;

}

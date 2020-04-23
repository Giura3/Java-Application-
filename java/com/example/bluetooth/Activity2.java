package com.example.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Activity2 extends MainActivity {

    Button show;
    ListView devicelist;


    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    public static String EXTRA_ADDRESS = "device_address";
    BluetoothSocket mBluetoothSocket = null;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothDevice device;

    private int REQUEST_ENABLE_BT = 1;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private BluetoothGatt mGatt;
    ArrayList list = new ArrayList();
    ArrayList listOfDevice = new ArrayList<BluetoothDevice>();


    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        mHandler = new Handler();
        devicelist = (ListView) findViewById(R.id.deviceslist);
        devicelist.setOnItemClickListener(myListClickListener);
        show = (Button) findViewById(R.id.showthem);



        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
                mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                        .setReportDelay(100)
                        .build();
                filters = new ArrayList<ScanFilter>();
        }



        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("For a connection choose a device.");

                if (isBluetoothAdminPermissionGranted() && isBluetoothPermissionGranted() && isLocationPermissionGranted()) {
                    scanLeDevice(true);
                }
            }
        });

    }

//        public void pairedDevicesList () {
//            pairedDevices = myBluetooth.getBondedDevices();
//            ArrayList list = new ArrayList();
//
//
//            if (pairedDevices.size() > 0) {
//                for (BluetoothDevice bt : pairedDevices) {
//                    list.add(bt.getName().toString() + "\n" + bt.getAddress().toString());
//
//                }
//            } else {
//                Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
//            }
//
//            final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
//
//            devicelist.setAdapter(adapter);
//            devicelist.setOnItemClickListener(myListClickListener);
//        }


    AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);
            BluetoothDevice selectedDevice;
            for (int i = 0; i < listOfDevice.size(); i++) {
                BluetoothDevice bd = (BluetoothDevice)listOfDevice.get(i);
                if (bd.getAddress().equals(address)) {
                    selectedDevice = bd;
                    Intent intent = new Intent(Activity2.this, Activity3.class);
                    myapp.selectedDevice = selectedDevice;
                    startActivity(intent);
                    break;
                }
            }

        }


    };




    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLEScanner.stopScan(mScanCallback);
                }
            }, SCAN_PERIOD);
            mLEScanner.startScan(filters, settings, mScanCallback);
        } else {
            mLEScanner.stopScan(mScanCallback);
        }
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
//            Log.i("callbackType", String.valueOf(callbackType));
//            Log.i("result", result.toString());
//            TextView address = findViewById(R.id.address);
//            address.setText(result.getDevice().toString());
//            BluetoothDevice btDevice = result.getDevice();
//            connectToDevice(btDevice);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            list.clear();
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
                list.add(sr.getDevice().getName() + "\n" + sr.getDevice().getAddress());
                listOfDevice.add(sr.getDevice());
            }
            setupAdapter();

        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };

    private void setupAdapter() {
        adapter = new ArrayAdapter(this, R.layout.mylist, list);
        scanLeDevice(false);
        devicelist.setAdapter(adapter);

    }


    // GRANT PERMISSIONS
    public boolean isLocationPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted");
                return true;
            } else {
                Log.v("TAG", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                return false;
            }
        } else {
            Log.v("TAG", "Permission is granted");
            return true;
        }
    }

    public boolean isBluetoothPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted");
                return true;
            } else {
                Log.v("TAG", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, 0);
                return false;
            }
        } else {
            Log.v("TAG", "Permission is granted");
            return true;
        }
    }

    public boolean isBluetoothAdminPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted");
                return true;
            } else {
                Log.v("TAG", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 0);
                return false;
            }
        } else {
            Log.v("TAG", "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                    //send sms here call your method
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}



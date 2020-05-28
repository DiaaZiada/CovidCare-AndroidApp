package com.example.covidcare;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class MyService extends Service {

    private static final String TAG = "MyService";

    private BluetoothAdapter mBluetoothAdapter;
    private final IBinder mBinder = new MyBinder();
//    private Handler mHandler;
//    private ArrayAdapter<String> listAdapter;
    String status, bluetoothAdapterStatus;
    private static DeviceRepository deviceRepository;
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private LocalDateTime now = LocalDateTime.now();
//   System.out.println(dtf.format(now));
    public void setDeviceRepository(DeviceRepository deviceRepo) {
        if (deviceRepository == null)
            deviceRepository = deviceRepo;
    }




    public class MyBinder extends Binder {
        MyService getService(){
            return MyService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        mHandler = new Handler();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }



    public void checkBluetoothState() {
        if (mBluetoothAdapter == null) {
            bluetoothAdapterStatus = "not supported";
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                if (mBluetoothAdapter.isDiscovering()) {
                    bluetoothAdapterStatus = "discovering process ...";
                } else {
                    bluetoothAdapterStatus = "enabled";
                }
            } else {
                    bluetoothAdapterStatus = "need enable";
            }
        }
//        return bluetoothAdapterStatus;
    }


    private final BroadcastReceiver devicesFoundReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals( action )){
                // device : represent the nearest device ...
                BluetoothDevice device = intent.getParcelableExtra( BluetoothDevice.EXTRA_DEVICE );
                // adding the devices into array list of strings
//                listAdapter.add(device.getName()  +"\n" + device.getAddress());
                mBluetoothAdapter.getAddress();
                Device dev = new Device(device.getName(), device.getAddress(), dtf.format(now).toString());
                deviceRepository.insert(dev);

                Log.d(TAG, mBluetoothAdapter.getAddress()+"\t"+mBluetoothAdapter.getName()+"\t"+device.getName()  +"+" + dev.getMacAddress() + dev.getTime()+dev.getTime()+"zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzinsersion");

                status = "found a device";
            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals( action )) {
                status = "scanning devices ...";
            }else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals( action )){
                status = "scanning..";
            }
        }
    };



    public boolean getIsBluetoothDiscovering(){
        return mBluetoothAdapter.isDiscovering();
    }

    public boolean getIsBluetoothEnabled(){
        return mBluetoothAdapter.isEnabled();
    }

    public String getStatus(){
        return status;
    }

//    public ArrayAdapter<String> getListAdapter(){
//        return listAdapter;
//    }

    public String getBluetoothAdapterStatus(){
        return bluetoothAdapterStatus;
    }



    public void startDiscovering(){
//        listAdapter.clear();
        mBluetoothAdapter.startDiscovery();
    }

    public void onPause() {
        unregisterReceiver( devicesFoundReceiver );
    }


    public void onResume() {
        registerReceiver( devicesFoundReceiver, new IntentFilter( BluetoothDevice.ACTION_FOUND ) );
        registerReceiver( devicesFoundReceiver, new IntentFilter( BluetoothAdapter.ACTION_DISCOVERY_STARTED ) );
        registerReceiver( devicesFoundReceiver, new IntentFilter( BluetoothAdapter.ACTION_DISCOVERY_FINISHED ) );

    }






    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d(TAG, "onTaskRemoved: called.");
        onPause();
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called.");
        onPause();
    }

}

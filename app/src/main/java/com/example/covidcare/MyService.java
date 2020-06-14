package com.example.covidcare;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.net.NetworkInterface;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import requests.RequestsModel;
import table.Device;


public class MyService extends LifecycleService {

    private static final String TAG = "MyService";

    private BluetoothAdapter mBluetoothAdapter;
    private final IBinder mBinder = new MyBinder();
    //    private Handler mHandler;
//    private ArrayAdapter<String> listAdapter;
    String status, bluetoothAdapterStatus;
    private static Repository deviceRepository;
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    //   System.out.println(dtf.format(now));
    private boolean startService = true;
    private boolean addLocation = false;
//    private String location = "";
    private RequestsModel requestsModel;
    private String macAddress;
//    MediaPlayer player;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    public void setAddLocation(boolean bool) {
        addLocation = bool;
    }

    private void saveDeviceMeetingInfo(final String name, final String macAddress, final String time) {
//        Log.e(TAG, "found ad DV VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV");


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (addLocation) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Device dev = new Device(name, macAddress, time, 9999, 9999);
                    deviceRepository.deviceInsert(dev);
                    return;
                }

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            try {
                                Location currentLocation = (Location) task.getResult();
                                Log.e(TAG, currentLocation.getLatitude() + "\t" + currentLocation.getLongitude());
                                Device dev = new Device(name, macAddress, time, currentLocation.getLatitude(), currentLocation.getLongitude());
                                deviceRepository.deviceInsert(dev);
                            } catch (Exception e) {
                                Device dev = new Device(name, macAddress, time, 9999, 9999);
                                deviceRepository.deviceInsert(dev);
                            }
                        } else {
                            Device dev = new Device(name, macAddress, time, 9999, 9999);
                            deviceRepository.deviceInsert(dev);
                        }
                    }
                });
            } else {
                Device dev = new Device(name, macAddress, time, 9999, 9999);
                deviceRepository.deviceInsert(dev);
            }
        } catch (SecurityException e) {
            Device dev = new Device(name, macAddress, time, 9999, 9999);
            deviceRepository.deviceInsert(dev);
        }

    }

    public class MyBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        requestsModel = RequestsModel.getInstance();
        macAddress = getMacAddr();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
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
    }


    private final BroadcastReceiver devicesFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                LocalDateTime now = LocalDateTime.now();
                saveDeviceMeetingInfo(device.getName(), device.getAddress(), dtf.format(now).toString());
                status = "found a device";
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                status = "scanning devices ...";
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                status = "scanning..";
            }
        }
    };

    public boolean getIsBluetoothDiscovering() {
        return mBluetoothAdapter.isDiscovering();
    }

    public boolean getIsBluetoothEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    public String getStatus() {
        return status;
    }

    public String getBluetoothAdapterStatus() {
        return bluetoothAdapterStatus;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        deviceRepository = Repository.getInstance();
        if (startService) {
//            player = MediaPlayer.create(this,
//                    Settings.System.DEFAULT_ALARM_ALERT_URI);
//            player.setLooping(true);
//            player.start();
            onResume();
            startDiscovering();
            setObserver();

            startService = false;
        }
        return START_STICKY;
    }

    public void startDiscovering() {
        mBluetoothAdapter.startDiscovery();
    }

    public void onPause() {
        unregisterReceiver(devicesFoundReceiver);
//        player.stop();
    }


    public void onResume() {
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
//        Log.d(TAG, "onTaskRemoved: called.");
        onPause();
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.d(TAG, "onDestroy: called.");
        onPause();
    }

    private void setObserver() {
        deviceRepository.getAllDevices().observe(this, new Observer<List<Device>>() {
            @Override
            public void onChanged(@Nullable List<Device> devices) {
                if (devices.size() > 0)
                    requestsModel.addMeeting(devices, macAddress);
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

}

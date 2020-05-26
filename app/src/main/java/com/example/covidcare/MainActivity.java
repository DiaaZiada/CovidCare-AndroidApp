package com.example.covidcare;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.security.KeyStore;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final int REQUEST_ACCESS_COARSE_LOCATION = 1;
    public static final int REQUEST_ENABLE_BLUETOOTH = 11;

    private ListView deviceList;
    private Button ScanButton;
//    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> listAdapter;

    // Vars
    private MyService mService;
    private MainActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        startService(new Intent(this, DeviceDiscoveryService.class));

//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        setObservers();

        deviceList = findViewById( R.id.devicesList );
        ScanButton = findViewById( R.id.button );

        listAdapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1 );
        deviceList.setAdapter( listAdapter );

        checkBluetoothState();
        ScanButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBluetoothState();
                mService.onResume();
            }

        } );
        checkCoarseLocationPermission();
    }

    private void setObservers() {

        mViewModel.getBinder().observe(this, new Observer<MyService.MyBinder>() {

            @Override
            public void onChanged(@Nullable MyService.MyBinder myBinder) {

                if (myBinder == null) {
                    Log.d(TAG, "onChanged: unbound from service");

                } else {
                    Log.d(TAG, "onChanged: bound to service.");
                    mService = myBinder.getService();
                }
            }
        });
    }



    private boolean checkCoarseLocationPermission() {
        if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION )
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION );
            return false;
        } else {
            return true;
        }
    }

    private void checkBluetoothState() {
        if (mService != null) {
            mService.checkBluetoothState();
            String state = mService.getBluetoothAdapterStatus();
            mService.startDiscovering();
            Toast.makeText(this, state, Toast.LENGTH_SHORT).show();
            if (state.equals("need enable")){
                Intent enableIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
                startActivityForResult( enableIntent, REQUEST_ENABLE_BLUETOOTH );
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if(requestCode == REQUEST_ENABLE_BLUETOOTH){
            checkBluetoothState();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        switch (requestCode){
            case REQUEST_ACCESS_COARSE_LOCATION :
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText( this, "allowed", Toast.LENGTH_SHORT ).show();
                }else{
                    Toast.makeText( this, "forbidden", Toast.LENGTH_SHORT ).show();
                }
                break;
        }
    }
    // Adding devices into list ..
//    private final BroadcastReceiver devicesFoundReceiver  = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if(BluetoothDevice.ACTION_FOUND.equals( action )){
//                // device : represent the nearest device ...
//                BluetoothDevice device = intent.getParcelableExtra( BluetoothDevice.EXTRA_DEVICE );
//                // adding the devices into array list of strings
//                listAdapter.add( device.getName()  +"\n" + device.getAddress());
//
//            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals( action )) {
//                ScanButton.setText( "scanning devices ..." );
//            }else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals( action )){
//                ScanButton.setText("scanning..");
//            }
//        }
//    };


    private void toggleUpdates(){
        if(mService != null){
//            if(mService.getProgress() == mService.getMaxValue()){
//                mService.resetTask();
//                mButton.setText("Start");
//            }
//            else{
//                if(mService.getIsPaused()){
//                    mService.unPausePretendLongRunningTask();
//                    mViewModel.setIsProgressBarUpdating(true);
//                }
//                else{
//                    mService.pausePretendLongRunningTask();
//                    mViewModel.setIsProgressBarUpdating(false);
//                }
//            }

        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        startService();

    }


    @Override
    protected void onStop() {
        super.onStop();
        if(mViewModel.getBinder() != null){
            unbindService(mViewModel.getServiceConnection());
        }
    }

    private void startService(){
        Intent serviceIntent = new Intent(this, MyService.class);
        startService(serviceIntent);
        bindService();

    }

    private void bindService(){
        Intent serviceBindIntent =  new Intent(this, MyService.class);
        bindService(serviceBindIntent, mViewModel.getServiceConnection(), Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // stop receiving devices
        mService.onPause();
    }



}

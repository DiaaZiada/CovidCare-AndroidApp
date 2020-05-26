package com.example.covidcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.security.KeyStore;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_ACCESS_COARSE_LOCATION = 1;
    public static final int REQUEST_ENABLE_BLUETOOTH = 11;
    private ListView deviceList;
    private Button ScanButton;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> listAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        startService(new Intent(this, DeviceDiscoveryService.class));

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceList = findViewById( R.id.devicesList );
        ScanButton = findViewById( R.id.button );

        listAdapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1 );
        deviceList.setAdapter( listAdapter );

        checkBluetoothState();
// start scanning ...
        ScanButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                    if (checkCoarseLocationPermission()) {
                        listAdapter.clear();
                        bluetoothAdapter.startDiscovery();
                    }
                } else {
                    checkBluetoothState();
                }

            }

        } );
        checkCoarseLocationPermission();
    }

    // when app is paused  ...
    @Override
    protected void onPause() {
        super.onPause();
        // stop receiving devices
        unregisterReceiver( devicesFoundReceiver );
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver( devicesFoundReceiver, new IntentFilter( BluetoothDevice.ACTION_FOUND ) );
        registerReceiver( devicesFoundReceiver, new IntentFilter( BluetoothAdapter.ACTION_DISCOVERY_STARTED ) );
        registerReceiver( devicesFoundReceiver, new IntentFilter( BluetoothAdapter.ACTION_DISCOVERY_FINISHED ) );

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
        if (bluetoothAdapter == null) {
            Toast.makeText( this, "not supported", Toast.LENGTH_SHORT ).show();
        } else {
            if (bluetoothAdapter.isEnabled()) {
                if (bluetoothAdapter.isDiscovering()) {
                    Toast.makeText( this, "discovering process ...", Toast.LENGTH_SHORT ).show();
                } else {
                    Toast.makeText( this, "enabled", Toast.LENGTH_SHORT ).show();
                }
            } else {
                Toast.makeText( this, "need enable", Toast.LENGTH_SHORT ).show();
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
    private final BroadcastReceiver devicesFoundReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals( action )){
                // device : represent the nearest device ...
                BluetoothDevice device = intent.getParcelableExtra( BluetoothDevice.EXTRA_DEVICE );
                // adding the devices into array list of strings
                listAdapter.add( device.getName()  +"\n" + device.getAddress());

            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals( action )) {
                ScanButton.setText( "scanning devices ..." );
            }else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals( action )){
                ScanButton.setText("scanning..");
            }
        }
    };
}

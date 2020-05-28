package com.example.covidcare;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import table.Device;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final int REQUEST_ACCESS_COARSE_LOCATION = 1;
    public static final int REQUEST_ENABLE_BLUETOOTH = 11;



    private Button ScanButton;

    private ArrayAdapter<String> listAdapter;

    // Vars
    private MyService mService;




    private ModelView mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.d(TAG,mBluetoothAdapter.getAddress() +mBluetoothAdapter.getName()+"\t"+getMacAddr()+"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\n\n\n\n");
        Toast.makeText(MainActivity.this, mBluetoothAdapter.getAddress(), Toast.LENGTH_SHORT).show();

        mViewModel = ViewModelProviders.of(this).get(ModelView.class);
        mViewModel.getAllDevices().observe(this, new Observer<List<Device>>() {
            @Override
            public void onChanged(@Nullable List<Device> devices) {
                for (int i=0; i<devices.size(); i++) {
                    Device dev = devices.get(i);
                    Log.d(TAG, i+"\t"+dev.getName() +"\t" + dev.getMacAddress() + "\t" + dev.getTime() + "\taaaaaaaaaa");
                }
//                Toast.makeText(MainActivity.this,  devices.get(devices.size()-1).getTime()+ devices.get(devices.size()-1).getMacAddress(), Toast.LENGTH_SHORT).show();
            }
        });


        setObservers();

        ScanButton = findViewById(R.id.button);
        ScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBluetoothState();
                mService.onResume();

            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.meetingInfo:
                Toast.makeText(this, "meeting Info selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.updataStatus:
                Toast.makeText(this, "updata Status selected", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    private void setObservers() {

        mViewModel.getBinder().observe(this, new Observer<MyService.MyBinder>() {

            @Override
            public void onChanged(@Nullable MyService.MyBinder myBinder) {

                if (myBinder == null) {
                    Log.d(TAG, "onChanged: unbound from service");

                } else {
                    Log.d(TAG, "onChanged: bound to service.");
                    mService = myBinder.getService();
                    mService.setDeviceRepository(mViewModel.getRepository());
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
//
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if(requestCode == REQUEST_ENABLE_BLUETOOTH){
            checkBluetoothState();
        }
    }
//
//
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
        mService.onPause();
    }



}


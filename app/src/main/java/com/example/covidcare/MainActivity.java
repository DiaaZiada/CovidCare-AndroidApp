package com.example.covidcare;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import table.Device;
import table.Summary;
import table.User;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final int UPDATE_STATUS_REQUEST = 1;
    public static final String EXTRA_INDEX = "com.example.covidcare.MainActivity.EXTRA_INDEX";


    public static final int REQUEST_ACCESS_COARSE_LOCATION = 1;
    public static final int REQUEST_ENABLE_BLUETOOTH = 11;


    private Button ScanButton;

    private ArrayAdapter<String> listAdapter;

    // Vars
    private MyService mService;
    private ModelView modelView;

    private Map<String, Integer> status2Index;
    private Map<Integer, String> index2Status;

    private User user;
    private Summary summary;
    private boolean startService = true;

    private int isAddLocation = -1;
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    private SwitchCompat btnLocationSwitch;
    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Toast.makeText(MainActivity.this, mBluetoothAdapter.getAddress(), Toast.LENGTH_SHORT).show();

        modelView = ViewModelProviders.of(this).get(ModelView.class);

        status2Index = new HashMap<String, Integer>();
        index2Status = new HashMap<Integer, String>();

        status2Index.put("unknown", 0);
        status2Index.put("Health", 1);
        status2Index.put("Infected", 2);
        status2Index.put("treated", 3);

        index2Status.put(0, "unknown");
        index2Status.put(1, "Health");
        index2Status.put(2, "Infected");
        index2Status.put(3, "treated");


        setObservers();


        ScanButton = findViewById(R.id.button);
        ScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }

        });

        btnLocationSwitch = (SwitchCompat) findViewById(R.id.btnLocationSwitch);
//        btnLocationSwitch.setChecked(checkPermissions());
        btnLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    int id = user.getId();
                    requestPermissions();

                    User updatedUser = new User(user.getName(), user.getStatus(), user.getMacAddress(), true);
                    updatedUser.setId(user.getId());
                    modelView.userUpdate(updatedUser);

                    Toast.makeText(getBaseContext(), "True", Toast.LENGTH_SHORT).show();
//                    mService.setAddLocation(true);
                    isAddLocation = 1;
                } else {
                    Toast.makeText(getBaseContext(), "False", Toast.LENGTH_SHORT).show();
//                    mService.setAddLocation(false);
                    User updatedUser = new User(user.getName(), user.getStatus(), user.getMacAddress(), false);
                    updatedUser.setId(user.getId());
                    modelView.userUpdate(updatedUser);
                    isAddLocation = 0;
                }
            }
        });
    }

    private boolean checkPermissions(){
        return !(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED);
    }
    protected void requestPermissions() {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
            }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + " not granted, exiting", Toast.LENGTH_LONG).show();

//                        finish();

                        mService.setAddLocation(false);
                        User updatedUser = new User(user.getName(), user.getStatus(), user.getMacAddress(), false);
                        updatedUser.setId(user.getId());
                        modelView.userUpdate(updatedUser);
                        isAddLocation = 0;
                        return;
                    }
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_STATUS_REQUEST && resultCode == RESULT_OK) {
            int index = data.getIntExtra(StatusActivity.EXTRA_NEW_INDEX, 0);
            String status = index2Status.get(index);
            int id = user.getId();
            User updatedUser = new User(user.getName(), status, user.getMacAddress(), user.getAddLocation());
            updatedUser.setId(user.getId());
            modelView.userUpdate(updatedUser);
            user = updatedUser;
            Toast.makeText(this, "Note saved" + index, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Note not saved", Toast.LENGTH_SHORT).show();
        }
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
                Intent meetingIntent = new Intent(MainActivity.this, MeetingInfoActivity.class);
                startActivity(meetingIntent);
                return true;
            case R.id.updataStatus:
                Intent statusIntent = new Intent(MainActivity.this, StatusActivity.class);
                int indx = status2Index.get(user.getStatus());
                statusIntent.putExtra(EXTRA_INDEX, indx);
                startActivityForResult(statusIntent, UPDATE_STATUS_REQUEST);
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

        modelView.getBinder().observe(this, new Observer<MyService.MyBinder>() {

            @Override
            public void onChanged(@Nullable MyService.MyBinder myBinder) {

                if (myBinder == null) {
                    Log.d(TAG, "onChanged: unbound from service");

                } else {
                    Log.d(TAG, "onChanged: bound to service.");
                    mService = myBinder.getService();
                    if(isAddLocation != -1){
                        mService.setAddLocation(isAddLocation==1);

                    }
                    checkBluetoothState();
//
                }
            }
        });

        modelView.getAllUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                if (users.size() == 0) {
                    user = new User("unknown", "unknown", getMacAddr(), false);
                    modelView.userInsert(user);
                } else {
                    user = users.get(0);
                    if (user.getAddLocation()){
                        isAddLocation = 1;
                    }else
                        isAddLocation = 0;

                    btnLocationSwitch.setChecked(isAddLocation==1);
                    if (mService != null)
                        mService.setAddLocation(isAddLocation==1);

                }
            }
        });


        modelView.getAllSummaries().observe(this, new Observer<List<Summary>>() {
            @Override
            public void onChanged(@Nullable List<Summary> summaries) {
                if (summaries.size() == 0) {
                    summary = new Summary(0, 0, 0, 0);
                    modelView.summaryInsert(summary);
                } else {
                    summary = summaries.get(0);
                }
            }
        });
    }


    private void checkBluetoothState() {
        if (mService != null) {
            mService.checkBluetoothState();
            String state = mService.getBluetoothAdapterStatus();
            mService.startDiscovering();
            Toast.makeText(this, state + "aaaaaaaaaaaa", Toast.LENGTH_SHORT).show();
            if (state.equals("need enable")) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);

//                ActivityCompat.requestPermissions(MainActivity.this, new
//                        String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 555);


            }
        }
    }
//

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                           int[] grantResults) {
//        switch (requestCode) {
//            case 1: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (ContextCompat.checkSelfPermission(MainActivity.this,
//                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
//                }
//                return;
//            }
//        }
//    }


    @Override
    protected void onResume() {
        super.onResume();
        startService();

    }


    @Override
    protected void onStop() {
        super.onStop();
        if (modelView.getBinder() != null) {
            unbindService(modelView.getServiceConnection());
        }
    }

    private void startService() {
        Intent serviceIntent = new Intent(this, MyService.class);
        startService(serviceIntent);
        bindService();

    }

    private void bindService() {
        Intent serviceBindIntent = new Intent(this, MyService.class);
        bindService(serviceBindIntent, modelView.getServiceConnection(), Context.BIND_AUTO_CREATE);
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        mService.onPause();
//    }
//


}


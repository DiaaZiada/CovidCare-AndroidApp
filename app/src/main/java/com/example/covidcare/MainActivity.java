package com.example.covidcare;

import android.Manifest;
import android.app.Dialog;
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
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import adapter.MeetingInfoListAdapter;
import requests.RequestsModel;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import table.Device;
import table.Meeting;
import table.Summary;
import table.User;
import utils.MeetingInfo;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    public static final String EXTRA_INDEX = "com.example.covidcare.MainActivity.EXTRA_INDEX";
    public static final String EXTRA_LATITUDE = "com.example.covidcare.MainActivity.EXTRA_LATITUDE";
    public static final String EXTRA_LONGITUDE = "com.example.covidcare.MainActivity.EXTRA_LONGITUDE";

    public static final int UPDATE_STATUS_REQUEST = 1;
    public static final int REQUEST_ENABLE_BLUETOOTH = 11;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private int isAddLocation = -1;

    private ModelView modelView;
    private RequestsModel requestsModel;
    private Summary summary;
    private MyService mService;
    private User user;

    private ListView mListView;
    private TextView nHealth, nInfected, nTreated, nUnknown;
    private SwitchCompat btnLocationSwitch;

    private Map<String, Integer> status2Index;
    private Map<Integer, String> index2Status;
    private ArrayList<MeetingInfo> meetingsInfo;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        modelView = ViewModelProviders.of(this).get(ModelView.class);
        requestsModel = RequestsModel.getInstance();

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

        requestsModel.getMeetings(getMacAddr());

        btnLocationSwitch = (SwitchCompat) findViewById(R.id.btnLocationSwitch);

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
                    isAddLocation = 1;
                } else {
                    Toast.makeText(getBaseContext(), "False", Toast.LENGTH_SHORT).show();
                    User updatedUser = new User(user.getName(), user.getStatus(), user.getMacAddress(), false);
                    updatedUser.setId(user.getId());
                    modelView.userUpdate(updatedUser);
                    isAddLocation = 0;
                }
            }
        });

        mListView = (ListView) findViewById(R.id.listView);
        meetingsInfo = new ArrayList<>();
        nHealth = findViewById(R.id.no_health);
        nInfected = findViewById(R.id.no_infected);
        nTreated = findViewById(R.id.no_treated);
        nUnknown = findViewById(R.id.no_un);

    }

    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        intent.putExtra(EXTRA_LATITUDE, (int) (meetingsInfo.get(v.getId()).getLatitude() * 10000000));
        intent.putExtra(EXTRA_LONGITUDE, (int) (meetingsInfo.get(v.getId()).getLogitude() * 10000000));
        startActivityForResult(intent, 1);
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean checkPermissions() {
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
                    if (isAddLocation != -1) {
                        mService.setAddLocation(isAddLocation == 1);

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
                    if (user.getAddLocation()) {
                        isAddLocation = 1;
                    } else
                        isAddLocation = 0;

                    btnLocationSwitch.setChecked(isAddLocation == 1);
                    if (mService != null)
                        mService.setAddLocation(isAddLocation == 1);

                    requestsModel.updateStatus(user, getMacAddr());

                }
            }
        });


        modelView.getAllMeetings().observe(this, new Observer<List<Meeting>>() {
            @Override
            public void onChanged(@Nullable List<Meeting> meetings) {
                meetingsInfo.clear();
                Map<String, Integer> map = new HashMap<>();

                for (int i = 0; i < meetings.size(); i++) {
                    meetingsInfo.add(new MeetingInfo(meetings.get(i).getTime(), meetings.get(i).getStatus(), meetings.get(i).getLatitude(), meetings.get(i).getLongitude()));
                    map.put(meetings.get(i).getStatus(), map.getOrDefault(meetings.get(i).getStatus(), 0) + 1);
                    Log.e(TAG, meetings.get(i).getStatus());
                }
                Log.i(TAG,"Size is "+String.valueOf(meetingsInfo.size()));
                MeetingInfoListAdapter adapter = new MeetingInfoListAdapter(MainActivity.this, R.layout.adabter_view_list, meetingsInfo);
                mListView.setAdapter(adapter);

                nHealth.setText(String.valueOf(map.getOrDefault("health", 0)));
                nInfected.setText(String.valueOf(map.getOrDefault("infected", 0)));
                nTreated.setText(String.valueOf(map.getOrDefault("treated", 0)));
                nUnknown.setText(String.valueOf(map.getOrDefault("unknown", 0)));

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
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        requestsModel.getMeetings(getMacAddr());

    }

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


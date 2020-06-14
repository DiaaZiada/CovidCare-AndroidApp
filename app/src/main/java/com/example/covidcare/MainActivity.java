package com.example.covidcare;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
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

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.MeetingInfoListAdapter;
import requests.RequestsModel;
import table.Meeting;
import table.User;
import utils.MeetingInfo;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "MainActivity";
    public static final String EXTRA_LATITUDE = "com.example.covidcare.MainActivity.EXTRA_LATITUDE";
    public static final String EXTRA_LONGITUDE = "com.example.covidcare.MainActivity.EXTRA_LONGITUDE";

    public static final int REQUEST_ENABLE_BLUETOOTH = 11;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private int isAddLocation = -1;

    private ModelView modelView;
    private RequestsModel requestsModel;
    private MyService mService;
    private User user;

    private ListView mListView;
    private TextView nHealth, nInfected, nRecovered, nUnknown;
    private SwitchCompat btnLocationSwitch;
    private Spinner dl_status;

    private Map<String, Integer> status2Index;
    private Map<Integer, String> index2Status;
    private ArrayList<MeetingInfo> meetingsInfo;

    private String macAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        macAddress = getMacAddr();
        modelView = ViewModelProviders.of(this).get(ModelView.class);
        requestsModel = RequestsModel.getInstance();

        status2Index = new HashMap<String, Integer>();
        index2Status = new HashMap<Integer, String>();

        status2Index.put("Unknown", 0);
        status2Index.put("Healthy", 1);
        status2Index.put("Infected", 2);
        status2Index.put("Recovered", 3);

        index2Status.put(0, "Unknown");
        index2Status.put(1, "Healthy");
        index2Status.put(2, "Infected");
        index2Status.put(3, "Recovered");


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
                    isAddLocation = 1;
                } else {
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
        nRecovered = findViewById(R.id.no_recovered);
        nUnknown = findViewById(R.id.no_un);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dl_status = findViewById(R.id.dl_status);
        dl_status.setAdapter(adapter);
        dl_status.setOnItemSelectedListener(this);

//        requestsModel.getMeetings(macAddress);


        setObservers();

        requestsModel.getMeetings(getMacAddr());


    }

    public void onClick(View v) {
        Toast.makeText(this, "Loading map", Toast.LENGTH_LONG).show();
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
//            // Toast.makeText(this, "You can't make map requests", // Toast.LENGTH_SHORT).show();
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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == UPDATE_STATUS_REQUEST && resultCode == RESULT_OK) {
//            int index = data.getIntExtra(StatusActivity.EXTRA_NEW_INDEX, 0);
//            String status = index2Status.get(index);
//            int id = user.getId();
//            User updatedUser = new User(user.getName(), status, user.getMacAddress(), user.getAddLocation());
//            updatedUser.setId(user.getId());
//            modelView.userUpdate(updatedUser);
//            user = updatedUser;
//        } else {
//        }
//    }

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
                    Log.i(TAG, String.valueOf(status2Index.getOrDefault(user.getStatus(), 0)) + "aaaaaaaaaaaaaa");
                    dl_status.setSelection(status2Index.getOrDefault(user.getStatus(), 0));
                    requestsModel.updateStatus(user, macAddress);
                }
            }
        });


        modelView.getAllMeetings().observe(this, new Observer<List<Meeting>>() {
            @Override
            public void onChanged(@Nullable List<Meeting> meetings) {
                Log.e(TAG, "ALLLLLLLLLLLLMEEEEEEEEEETINGSSSSSSSSSSSSSSSSs "+ requestsModel.requestFinished);

                if (requestsModel.requestFinished) {

                    Log.e(TAG, "ALLLLLLLLLLLLMEEEEEEEEEETINGSSSSSSSSSSSSSSSSs56666666666666666");

                    meetingsInfo.clear();
                    Map<String, Integer> map = new HashMap<>();
                    for (int i = 0; i < meetings.size(); i++) {
                        meetingsInfo.add(new MeetingInfo(meetings.get(i).getTime(), meetings.get(i).getStatus(), meetings.get(i).getLatitude(), meetings.get(i).getLongitude()));
                        map.put(meetings.get(i).getStatus(), map.getOrDefault(meetings.get(i).getStatus(), 0) + 1);
                    }
                    MeetingInfoListAdapter adapter = new MeetingInfoListAdapter(MainActivity.this, R.layout.adabter_view_list, meetingsInfo);
                    mListView.setAdapter(adapter);
                    nHealth.setText(String.valueOf(map.getOrDefault("Healthy", 0)));
                    nInfected.setText(String.valueOf(map.getOrDefault("Infected", 0)));
                    nRecovered.setText(String.valueOf(map.getOrDefault("Recovered", 0)));
                    nUnknown.setText(String.valueOf(map.getOrDefault("Unknown", 0)));
                }
            }
        });
    }


    private void checkBluetoothState() {
        if (mService != null) {
            mService.checkBluetoothState();
            String state = mService.getBluetoothAdapterStatus();
            mService.startDiscovering();
            if (state.equals("need enable")) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        requestsModel.getMeetings(macAddress);

    }

    @Override
    protected void onResume() {
        super.onResume();
        startService();
//        requestsModel.getMeetings(macAddress);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (user == null)
            return;
        String text = parent.getItemAtPosition(position).toString();
        User updatedUser = new User(user.getName(), text, user.getMacAddress(), user.getAddLocation());
        updatedUser.setId(user.getId());
        modelView.userUpdate(updatedUser);
        user = updatedUser;
        dl_status.setScrollBarDefaultDelayBeforeFade(1);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}


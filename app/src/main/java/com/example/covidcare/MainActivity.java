package com.example.covidcare;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;

import java.lang.reflect.Field;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener,SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "MainActivity";
    public static final String EXTRA_LATITUDE = "com.example.covidcare.MainActivity.EXTRA_LATITUDE";
    public static final String EXTRA_LONGITUDE = "com.example.covidcare.MainActivity.EXTRA_LONGITUDE";

    public static final int REQUEST_ENABLE_BLUETOOTH = 11;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private int isAddLocation = -1;

    private ModelView modelView;
    private RequestsModel requestsModel;
//    private MyService mService;
    private User user;

    private ListView mListView;
    private TextView nHealth, nInfected, nRecovered, nUnknown;
    private SwitchCompat btnLocationSwitch;
    private Spinner dl_status;

    private Map<String, Integer> status2Index;
    private Map<Integer, String> index2Status;
    private ArrayList<MeetingInfo> meetingsInfo;

    private String macAddress;










    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private MyReceiver myReceiver;

    // A reference to the service used to get location updates.
    private LocationUpdatesService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;

    // UI elements.
    private Button mRequestLocationUpdatesButton;
    private Button mRemoveLocationUpdatesButton;




    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };





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

        Toast.makeText(this, "Loading map", Toast.LENGTH_LONG).show();
//        Intent intent = new Intent(MainActivity.this, MapActivity.class);
//        intent.putExtra(EXTRA_LATITUDE, (int) (meetingsInfo.get(v.getId()).getLatitude() * 10000000));
//        intent.putExtra(EXTRA_LONGITUDE, (int) (meetingsInfo.get(v.getId()).getLogitude() * 10000000));
//        startActivityForResult(intent, 1);

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
        Log.e(TAG,BluetoothAdapter.getDefaultAdapter().getAddress()+"WWWWWWWWWWWWWWWWWWWWWWWWWW");



        myReceiver = new MyReceiver();
        if (Utils.requestingLocationUpdates(this)) {
            if (!checkPermissions()) {
                requestPermissions();
            }
        }



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

//    private boolean checkPermissions() {
//        return !(ContextCompat.checkSelfPermission(MainActivity.this,
//                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED);
//    }

//    protected void requestPermissions() {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
//                Manifest.permission.ACCESS_FINE_LOCATION)) {
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
//        } else {
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
//        }
//
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
//                                           @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_CODE_ASK_PERMISSIONS:
//                for (int index = permissions.length - 1; index >= 0; --index) {
//                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
////                        mService.setAddLocation(false);
//                        User updatedUser = new User(user.getName(), user.getStatus(), user.getMacAddress(), false);
//                        updatedUser.setId(user.getId());
//                        modelView.userUpdate(updatedUser);
//                        isAddLocation = 0;
//                        return;
//                    }
//                }
//
//                break;
//        }
//    }

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

    public static @Nullable String getAddress2(final BluetoothAdapter adapter) {
        if (adapter == null)
            return null;

        final String address = adapter.getAddress();
        // Horrible reflection hack needed to get the Bluetooth MAC for Marshmellow and above.
        try {
            final Field mServiceField = BluetoothAdapter.class.getDeclaredField("mService");
            mServiceField.setAccessible(true);
            final Object mService = mServiceField.get(adapter);
            if (mService == null)
                return null;
            return (String) mService.getClass().getMethod("getAddress").invoke(mService);
        } catch (final Exception x) {
            throw new RuntimeException(x);
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

//        modelView.getBinder().observe(this, new Observer<MyService.MyBinder>() {
//
//            @Override
//            public void onChanged(@Nullable MyService.MyBinder myBinder) {
//
//                if (myBinder == null) {
//                    Log.d(TAG, "onChanged: unbound from service");
//
//                } else {
//                    Log.d(TAG, "onChanged: bound to service.");
//                    mService = myBinder.getService();
//                    if (isAddLocation != -1) {
//                        mService.setAddLocation(isAddLocation == 1);
//
//                    }
//                    checkBluetoothState();
//                }
//            }
//        });

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
//                    if (mService != null)
//                        mService.setAddLocation(isAddLocation == 1);

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

//
//    private void checkBluetoothState() {
//        if (mService != null) {
//            mService.checkBluetoothState();
//            String state = mService.getBluetoothAdapterStatus();
//            mService.startDiscovering();
//            if (state.equals("need enable")) {
//                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
//            }
//        }
//
//    }

    @Override
    protected void onStart() {
        super.onStart();
        requestsModel.getMeetings(macAddress);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        mRequestLocationUpdatesButton = (Button) findViewById(R.id.request_location_updates_button);
        mRemoveLocationUpdatesButton = (Button) findViewById(R.id.remove_location_updates_button);

        mRequestLocationUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkPermissions()) {
                    requestPermissions();
                } else {
                    mService.requestLocationUpdates();
                }
            }
        });

        mRemoveLocationUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mService.removeLocationUpdates();
            }
        });

        // Restore the state of the buttons when the activity (re)launches.
        setButtonsState(Utils.requestingLocationUpdates(this));

        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        startService();
//        requestsModel.getMeetings(macAddress);
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
    }


    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {

        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection);
            mBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);

        super.onStop();
//        if (modelView.getBinder() != null) {
//            unbindService(modelView.getServiceConnection());
//        }
    }

    private void startService() {
//        Intent serviceIntent = new Intent(this, MyService.class);
//        startService(serviceIntent);
        bindService();

    }

    private void bindService() {
//        Intent serviceBindIntent = new Intent(this, MyService.class);
//        bindService(serviceBindIntent, modelView.getServiceConnection(), Context.BIND_AUTO_CREATE);
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

    private boolean checkPermissions() {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.activity_main),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                mService.requestLocationUpdates();
            } else {
                // Permission denied.
                setButtonsState(false);
                Snackbar.make(
                        findViewById(R.id.activity_main),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesService}.
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (location != null) {
                Toast.makeText(MainActivity.this, Utils.getLocationText(location),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        // Update the buttons state depending on whether location updates are being requested.
        if (s.equals(Utils.KEY_REQUESTING_LOCATION_UPDATES)) {
            setButtonsState(sharedPreferences.getBoolean(Utils.KEY_REQUESTING_LOCATION_UPDATES,
                    false));
        }
    }

    private void setButtonsState(boolean requestingLocationUpdates) {
        if (requestingLocationUpdates) {
            mRequestLocationUpdatesButton.setEnabled(false);
            mRemoveLocationUpdatesButton.setEnabled(true);
        } else {
            mRequestLocationUpdatesButton.setEnabled(true);
            mRemoveLocationUpdatesButton.setEnabled(false);
        }
    }
}


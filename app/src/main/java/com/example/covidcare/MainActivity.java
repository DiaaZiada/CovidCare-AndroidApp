package com.example.covidcare;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.MeetingInfoListAdapter;
import requests.RequestsModel;
import table.AppInfo;
import table.Meeting;
import utils.MeetingInfo;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "MainActivity";
    public static final String EXTRA_LATITUDE = "com.example.covidcare.MainActivity.EXTRA_LATITUDE";
    public static final String EXTRA_LONGITUDE = "com.example.covidcare.MainActivity.EXTRA_LONGITUDE";

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    public static AppInfo appInfo;

    private ModelView modelView;
    private RequestsModel requestsModel;

    private ListView mListView;
    private TextView nHealth, nInfected, nRecovered;
    private SwitchCompat btnLocationSwitch;
    private Spinner dl_status;

    private Map<String, Integer> status2Index;
    private Map<Integer, String> index2Status;
    private ArrayList<MeetingInfo> meetingsInfo;


    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private LocationUpdatesService mService = null;


    // UI elements.
    private Button mRequestLocationUpdatesButton;
    private Button mRemoveLocationUpdatesButton;


    private ServiceConnection mServiceConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Log.i(TAG,"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

        if (Utils.requestingLocationUpdates(this)) {
            Log.i(TAG,"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

            if (!checkPermissions()) {
                Log.i(TAG,"AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                requestPermissions();
            }
        }
        Log.i(TAG,"EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");

        modelView = ViewModelProviders.of(this).get(ModelView.class);

        requestsModel = RequestsModel.getInstance();

        status2Index = new HashMap<String, Integer>();
        index2Status = new HashMap<Integer, String>();

        status2Index.put("Healthy", 0);
        status2Index.put("Infected", 1);
        status2Index.put("Recovered", 2);

        index2Status.put(0, "Healthy");
        index2Status.put(1, "Infected");
        index2Status.put(2, "Recovered");


        mServiceConnection = modelView.getServiceConnection();

//        btnLocationSwitch = (SwitchCompat) findViewById(R.id.btnLocationSwitch);
//
//        btnLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//            }
//        });

        mListView = (ListView) findViewById(R.id.listView);
        meetingsInfo = new ArrayList<>();
        nHealth = findViewById(R.id.no_health);
        nInfected = findViewById(R.id.no_infected);
        nRecovered = findViewById(R.id.no_recovered);
//        nUnknown = findViewById(R.id.no_un);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dl_status = findViewById(R.id.dl_status);
        dl_status.setAdapter(adapter);
        dl_status.setOnItemSelectedListener(this);




        setObservers();




    }
    @Override
    protected void onStart() {
        super.onStart();

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

        setButtonsState(Utils.requestingLocationUpdates(this));

        bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);

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
        }
        return false;
    }


    private void setObservers() {
        modelView.getBinder().observe(this, new Observer<LocationUpdatesService.LocalBinder>() {
            @Override
            public void onChanged(LocationUpdatesService.LocalBinder localBinder) {
                if (localBinder == null)
                    Log.d(TAG, "onChanged: unbound from service");
                else
                    mService = localBinder.getService();
            }
        });

        modelView.getAllappInfos().observe(this, new Observer<List<AppInfo>>() {
            @Override
            public void onChanged(List<AppInfo> appInfos) {
                if (appInfos.size() == 0) {
                    appInfo = new AppInfo(-1, "Healthy");
                    modelView.appInfoInsert(appInfo);
                } else
                    appInfo = appInfos.get(0);
                Log.e(TAG,String.valueOf(status2Index.get(appInfo.getStatus()))+"\t"+appInfo.getStatus());
                dl_status.setSelection(status2Index.getOrDefault(appInfo.getStatus(),0));

                if (appInfo.getAppId() == -1) {
                    requestsModel.requestId();
                    return;
                }
                requestsModel.updateStatus();
                requestsModel.getMeetings();

            }
        });

        modelView.getAllMeetings().

                observe(this, new Observer<List<Meeting>>() {
                    @Override
                    public void onChanged(@Nullable List<Meeting> meetings) {
                        if (appInfo == null)
                            return;
//                        requestsModel.getMeetings();
                        if (requestsModel.getMeetingsFinished) {
                            meetingsInfo.clear();
                            Map<String, Integer> map = new HashMap<>();
                            for (int i = 0; i < meetings.size(); i++) {
                                Log.i(TAG, "Aaaaaaaaaaaaa");
                                meetingsInfo.add(new MeetingInfo(meetings.get(i).getTime(), meetings.get(i).getStatus(), meetings.get(i).getLatitude(), meetings.get(i).getLongitude()));
                                map.put(meetings.get(i).getStatus(), map.getOrDefault(meetings.get(i).getStatus(), 0) + 1);
                            }
                            MeetingInfoListAdapter adapter = new MeetingInfoListAdapter(MainActivity.this, R.layout.adabter_view_list, meetingsInfo);
                            mListView.setAdapter(adapter);
                            nHealth.setText(String.valueOf(map.getOrDefault("Healthy", 0)));
                            nInfected.setText(String.valueOf(map.getOrDefault("Infected", 0)));
                            nRecovered.setText(String.valueOf(map.getOrDefault("Recovered", 0)));
//                            nUnknown.setText(String.valueOf(map.getOrDefault("Unknown", 0)));
                        }
                    }
                });
    }




    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {

//        if (mBound) {
        // Unbind from the service. This signals to the service that this activity is no longer
        // in the foreground, and the service can respond by promoting itself to a foreground
        // service.
        unbindService(mServiceConnection);
//            mBound = false;
//        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);

        super.onStop();

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (appInfo == null)
            return;
        String text = parent.getItemAtPosition(position).toString();
        appInfo.setStatus(text);
        modelView.appInfoUpdate(appInfo);
        Log.e(TAG, appInfo.getStatus()+"\t"+text);
        if (appInfo.getAppId() == -1) {
            requestsModel.requestId();
            return;
        }
        requestsModel.updateStatus();
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
        Log.i(TAG,"RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR");

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
//                mService.requestLocationUpdates();
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


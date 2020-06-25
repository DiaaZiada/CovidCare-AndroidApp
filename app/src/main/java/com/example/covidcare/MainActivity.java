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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import adapter.MeetingInfoListAdapter;
import requests.RequestsModel;
import table.AppInfo;
import table.Meeting;
import utils.Codes;
import utils.MeetingInfo;
import utils.SharedVars;
import utils.UtilsMethods;

import static utils.UtilsMethods.getNumberOfDays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "MainActivity";

    private ModelView modelView;
    private RequestsModel requestsModel;
    private ListView mListView;
    private TextView nHealth, nInfected, nRecovered;
    private Spinner dl_status;
    private Map<String, Integer> status2Index;
    private Map<Integer, String> index2Status;
    private ArrayList<MeetingInfo> meetingsInfo;


    private LocationUpdatesService mService = null;
    private ServiceConnection mServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Utils.requestingLocationUpdates(this)) {
            if (!checkPermissions()) {
                requestPermissions();
            }
        }

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
        mListView = (ListView) findViewById(R.id.listView);
        meetingsInfo = new ArrayList<>();
        nHealth = findViewById(R.id.no_healthy);
        nInfected = findViewById(R.id.no_infected);
        nRecovered = findViewById(R.id.no_recovered);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.status, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dl_status = findViewById(R.id.dl_status);
        dl_status.setAdapter(adapter);
        dl_status.setOnItemSelectedListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        Utils.requestingLocationUpdates(this);

        bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);
//        requestsModel.counter = 0;
        SharedVars.getMeetingsFinished=false;
        SharedVars.requestedMeetingStatus = true;
        SharedVars.deletingFakeOldMeeetingFinished=true;
        setObservers();


    }

    public void onClick(View v) {
        Toast.makeText(this, "Loading map", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        intent.putExtra(SharedVars.EXTRA_LATITUDE, (int) (meetingsInfo.get(v.getId()).getLatitude() * 10000000));
        intent.putExtra(SharedVars.EXTRA_LONGITUDE, (int) (meetingsInfo.get(v.getId()).getLogitude() * 10000000));
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
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, Codes.ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        return false;
    }


    private void setObservers() {

        modelView.getAllMeetings().

                observe(this, new Observer<List<Meeting>>() {
                    @Override
                    public void onChanged(@Nullable List<Meeting> meetings) {
                        Log.i(TAG, "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"+SharedVars.getMeetingsFinished+SharedVars.deletingFakeOldMeeetingFinished+"\t");
                        if (SharedVars.getMeetingsFinished && SharedVars.deletingFakeOldMeeetingFinished) {
                            Log.i(TAG, "VVVVVVVVVVVVVVVVVVVVVVVV");

                            SharedVars.deletingFakeOldMeeetingFinished = false;
                            meetingsInfo.clear();
                            Map<String, Integer> map = new HashMap<>();
                            Set<String> ids = new HashSet<String>();
                            for (int i = 0; i < meetings.size(); i++) {

                                if (ids.contains(meetings.get(i).getApp_id()) || meetings.get(i).getApp_id().equals("-1") || UtilsMethods.getNumberOfDays(meetings.get(i).getTime()) > 14) {
                                    modelView.meetinDelete(meetings.get(i));
                                    continue;
                                }
                                ids.add(meetings.get(i).getApp_id());


                                meetingsInfo.add(new MeetingInfo(meetings.get(i).getTime(), meetings.get(i).getStatus(), meetings.get(i).getLatitude(), meetings.get(i).getLongitude()));
                                map.put(meetings.get(i).getStatus(), map.getOrDefault(meetings.get(i).getStatus(), 0) + 1);
                            }
                            MeetingInfoListAdapter adapter = new MeetingInfoListAdapter(MainActivity.this, R.layout.adabter_view_list, meetingsInfo);
                            mListView.setAdapter(adapter);
                            nHealth.setText(String.valueOf(map.getOrDefault("Healthy", 0)));
                            nInfected.setText(String.valueOf(map.getOrDefault("Infected", 0)));
                            nRecovered.setText(String.valueOf(map.getOrDefault("Recovered", 0)));
                            SharedVars.deletingFakeOldMeeetingFinished = true;
                        }
                    }
                });

        modelView.getBinder().observe(this, new Observer<LocationUpdatesService.LocalBinder>() {
            @Override
            public void onChanged(LocationUpdatesService.LocalBinder localBinder) {
                if (localBinder == null)
                    Log.d(TAG, "onChanged: unbound from service");
                else {
                    mService = localBinder.getService();
                    if (!checkPermissions()) {
                        requestPermissions();
                    } else {
                        if (!mService.isRequestingLocation()) {
                            mService.requestLocationUpdates();
                            Log.e(TAG, "Start Service");
                        }
                    }
                }
            }
        });

        modelView.getAllappInfos().observe(this, new Observer<List<AppInfo>>() {

            @Override
            public void onChanged(List<AppInfo> appInfos) {
                Log.e(TAG, "getAllappInfos getAllappInfos getAllappInfos getAllappInfos getAllappInfos getAllappInfos getAllappInfos getAllappInfos getAllappInfos getAllappInfos getAllappInfos ");
                if (appInfos.size() == 0) {
                    SharedVars.appInfo = new AppInfo("-1", "Healthy");
                    modelView.appInfoInsert(SharedVars.appInfo);
                    return;
                }
                SharedVars.appInfo = appInfos.get(0);
                dl_status.setSelection(status2Index.getOrDefault(SharedVars.appInfo.getStatus(), 0));
                if (SharedVars.appInfo.getAppId().equals("-1")) {
                    requestsModel.requestId();
                    return;
                }
//                Log.e(TAG, requestsModel.counter+"");
                if( SharedVars.requestedMeetingStatus) {
                    requestsModel.updateStatus();
                    requestsModel.getMeetings();
                    SharedVars.requestedMeetingStatus=false;
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

        if (modelView.isBound()) {
            unbindService(mServiceConnection);
            modelView.setBound(false);
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);

        super.onStop();

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (SharedVars.appInfo == null)
            return;
        String text = parent.getItemAtPosition(position).toString();
        SharedVars.appInfo.setStatus(text);
        modelView.appInfoUpdate(SharedVars.appInfo);
        Log.e(TAG, SharedVars.appInfo.getStatus() + "\t" + text);
        requestsModel.updateStatus();
        dl_status.setScrollBarDefaultDelayBeforeFade(1);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private boolean checkPermissions() {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

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
                                    Codes.REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Codes.REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == Codes.REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                mService.requestLocationUpdates();
            } else {
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
        if (s.equals(Utils.KEY_REQUESTING_LOCATION_UPDATES)) {
            sharedPreferences.getBoolean(Utils.KEY_REQUESTING_LOCATION_UPDATES, false);
        }
    }



}


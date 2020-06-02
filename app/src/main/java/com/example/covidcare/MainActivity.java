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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.ArrayList;
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
    private boolean startService=true;
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

//        setObservers();

        ScanButton = findViewById(R.id.button);
        ScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBluetoothState();
//                if (startService){
//                    mService.onResume();
//                    startService = false;
//                }
//                Log.d(TAG, "NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN\t\tNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
//
//                Log.d(TAG, String.valueOf(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) + String.valueOf(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED));
//                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    Log.e(TAG, "OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
//
//                    return;
//                }           locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, (LocationListener) MainActivity.this);
//                Criteria criteria = new Criteria();
//                String bestProvider = locationManager.getBestProvider(criteria, true);
//                Location location = locationManager.getLastKnownLocation(bestProvider);
//
//                if (location == null) {
//                    Toast.makeText(getApplicationContext(), "GPS signal not found", Toast.LENGTH_SHORT).show();
//                }
//                if (location != null) {
//                    Log.e("locatin", "location--" + location);
//
//                    Log.e("latitude at beginning",
//                            "@@@@@@@@@@@@@@@" + location.getLatitude());
//                    onLocationChanged(location);
//                }
//                Log.e(TAG, "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL\t\tLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL");
            }

        });
    }





    public void onLocationChanged(Location location) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Log.e("latitude", "latitude--" + latitude);
        try {
            Log.e("latitude", "inside latitude--" + latitude);
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();

                Log.d(TAG, state + " , " + city + " , " + country);
                Log.d(TAG, address + " , " + knownName + " , " + postalCode);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_STATUS_REQUEST && resultCode == RESULT_OK) {
            int index = data.getIntExtra(StatusActivity.EXTRA_NEW_INDEX, 0);
            String status = index2Status.get(index);
            int id = user.getId();
            User updatedUser = new User(user.getName(), status, user.getMacAddress());
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
//                    mService.setDeviceRepository(modelView.getRepository());
                }
            }
        });


//        modelView.getAllDevices().observe(this, new Observer<List<Device>>() {
//            @Override
//            public void onChanged(@Nullable List<Device> devices) {
//                for (int i = 0; i < devices.size(); i++) {
//                    Device dev = devices.get(i);
////                    Log.d(TAG, i + "\t" + dev.getName() + "\t" + dev.getMacAddress() + "\t" + dev.getTime() + "\taaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//                }
//            }
//        });


        modelView.getAllUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                if (users.size() == 0) {
                    user = new User("unknown", "unknown", getMacAddr());
                    modelView.userInsert(user);
                } else {
                    user = users.get(0);
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

    private boolean checkCoarseLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_COARSE_LOCATION);
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
            if (state.equals("need enable")) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
            }
        }
    }
//

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "allowed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "forbidden", Toast.LENGTH_SHORT).show();
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


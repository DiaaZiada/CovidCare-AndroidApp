package com.example.covidcare;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.MeetingInfoListAdapter;
import requests.RequestsModel;
import table.Device;
import table.Meeting;
import table.Summary;
import table.User;
import utils.MeetingInfo;

public class MeetingInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MeetingInfoActivity";
    public static final String EXTRA_LATITUDE = "com.example.covidcare.MeetingInfoActivity.EXTRA_LATITUDE";
    public static final String EXTRA_LONGITUDE = "com.example.covidcare.MeetingInfoActivity.EXTRA_LONGITUDE";

    private ListView mListView;

    private ModelView modelView;
    private Summary summary;
    private ArrayList<MeetingInfo> meetingsInfo;
    private RequestsModel requestsModel;


    private TextView nHealth, nInfected, nTreated, nUnknown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestsModel = RequestsModel.getInstance();

        setContentView(R.layout.meeting_info);
        Log.d(TAG, "onCreate: Started.");
        mListView = (ListView) findViewById(R.id.listView);
        modelView = ViewModelProviders.of(this).get(ModelView.class);
        meetingsInfo = new ArrayList<>();
        ArrayList<MeetingInfo> peopleList = new ArrayList<>();
        setObservers();

        nHealth = findViewById(R.id.nHealthy);
        nInfected = findViewById(R.id.nInfected);
        nTreated = findViewById(R.id.nTreated);
        nUnknown = findViewById(R.id.nUnknown);


    }


    private void setObservers() {
//        modelView.getAllSummaries().observe(this, new Observer<List<Summary>>() {
//            @Override
//            public void onChanged(@Nullable List<Summary> summaries) {
//                if (summaries.size() == 0) {
//                    summary = new Summary(0, 0, 0, 0);
//                    modelView.summaryInsert(summary);
//                } else {
//                    summary = summaries.get(0);
//                }
//
//                nHealth.setText(String.valueOf(summary.getHealthy()));
//                nInfected.setText(String.valueOf(summary.getInfected()));
//                nTreated.setText(String.valueOf(summary.getTreated()));
//                nUnknown.setText(String.valueOf(summary.getUnknown()));
//            }
//        });


        modelView.getAllMeetings().observe(this, new Observer<List<Meeting>>() {
            @Override
            public void onChanged(@Nullable List<Meeting> meetings) {
                meetingsInfo.clear();
                Map<String, Integer> map = new HashMap<>();



                for (int i = 0; i < meetings.size(); i++) {
                    meetingsInfo.add(new MeetingInfo(meetings.get(i).getTime(), meetings.get(i).getStatus(), meetings.get(i).getLatitude(), meetings.get(i).getLongitude()));
                    map.put( meetings.get(i).getStatus(), map.getOrDefault( meetings.get(i).getStatus(),0)+1);
                    Log.e(TAG, meetings.get(i).getStatus());

                }
                MeetingInfoListAdapter adapter = new MeetingInfoListAdapter(MeetingInfoActivity.this, R.layout.adabter_view_list, meetingsInfo);
                mListView.setAdapter(adapter);

                nHealth.setText(String.valueOf(map.getOrDefault("health",0)));
                nInfected.setText(String.valueOf(map.getOrDefault("infected",0)));
                nTreated.setText(String.valueOf(map.getOrDefault("treated",0)));
                nUnknown.setText(String.valueOf(map.getOrDefault("unknown",0)));

            }
        });
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


    @Override
    public void onClick(View v) {

        Intent intent = new Intent(MeetingInfoActivity.this, MapActivity.class);
        intent.putExtra(EXTRA_LATITUDE, (int) (meetingsInfo.get(v.getId()).getLatitude() * 10000000));
        intent.putExtra(EXTRA_LONGITUDE, (int) (meetingsInfo.get(v.getId()).getLogitude() * 10000000));
        startActivityForResult(intent, 1);
        Log.e(TAG, String.valueOf(meetingsInfo.get(v.getId()).getLatitude()) + " clickedclickedclickedclickedclickedclickedclickedclickedclickedclickedclickedclickedclickedclickedclicked");
    }
}

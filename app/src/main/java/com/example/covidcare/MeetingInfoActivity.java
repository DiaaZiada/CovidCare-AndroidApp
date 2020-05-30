package com.example.covidcare;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import adapter.MeetingInfoListAdapter;
import table.Device;
import table.Meeting;
import table.Summary;
import table.User;
import utils.MeetingInfo;

public class MeetingInfoActivity extends AppCompatActivity {

    private static final String TAG = "MeetingInfoActivity";

    private ListView mListView;

    private ModelView modelView;
    private Summary summary;
    private ArrayList<MeetingInfo> meetingsInfo;

    private TextView nHealth, nInfected, nTreated, nUnknown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        modelView.getAllSummaries().observe(this, new Observer<List<Summary>>() {
            @Override
            public void onChanged(@Nullable List<Summary> summaries) {
                if (summaries.size() == 0) {
                    summary = new Summary(0, 0, 0, 0);
                    modelView.summaryInsert(summary);
                } else {
                    summary = summaries.get(0);
                }

                nHealth.setText(String.valueOf(summary.getHealthy()));
                nInfected.setText(String.valueOf(summary.getInfected()));
                nTreated.setText(String.valueOf(summary.getTreated()));
                nUnknown.setText(String.valueOf(summary.getUnknown()));
            }
        });


    modelView.getAllMeetings().observe(this, new Observer<List<Meeting>>() {
        @Override
        public void onChanged(@Nullable List<Meeting> meetings) {
           meetingsInfo.clear();
           for (int i = meetings.size()-1; i>=0; i--) {
               meetingsInfo.add(new MeetingInfo(meetings.get(i).getTime(), meetings.get(i).getStatus(), meetings.get(i).getLocation()));

           }
            MeetingInfoListAdapter adapter = new MeetingInfoListAdapter(MeetingInfoActivity.this, R.layout.adabter_view_list, meetingsInfo);
            mListView.setAdapter(adapter);
        }
    });
}


}

package com.example.covidcare;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;

import adapter.MeetingInfoListAdapter;
import utils.MeetingInfo;

public class MeetingInfoActivity extends AppCompatActivity {

    private static final String TAG = "MeetingInfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meeting_info);
        Log.d(TAG, "onCreate: Started.");
        ListView mListView = (ListView) findViewById(R.id.listView);

        //Create the MeetingInfo objects
        ArrayList<MeetingInfo> peopleList = new ArrayList<>();

        for (int i=31; i>0; i--)
            peopleList.add(new MeetingInfo("2020/04/"+i+" 10:20:14","Health","36,5465654, 25,65465468"));

        MeetingInfoListAdapter adapter = new MeetingInfoListAdapter(this, R.layout.adabter_view_list, peopleList);
        mListView.setAdapter(adapter);
    }
}

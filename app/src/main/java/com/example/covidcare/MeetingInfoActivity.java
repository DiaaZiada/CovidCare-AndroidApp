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
        MeetingInfo john = new MeetingInfo("John","12-20-1998","Male");
        MeetingInfo steve = new MeetingInfo("Steve","08-03-1987","Male");
        MeetingInfo stacy = new MeetingInfo("Stacy","11-15-2000","Female");
        MeetingInfo ashley = new MeetingInfo("Ashley","07-02-1999","Female");
        MeetingInfo matt = new MeetingInfo("Matt","03-29-2001","Male");
        MeetingInfo matt2 = new MeetingInfo("Matt2","03-29-2001","Male");
        MeetingInfo matt3 = new MeetingInfo("Matt3","03-29-2001","Male");
        MeetingInfo matt4 = new MeetingInfo("Matt4","03-29-2001","Male");
        MeetingInfo matt5 = new MeetingInfo("Matt5","03-29-2001","Male");
        MeetingInfo matt6 = new MeetingInfo("Matt6","03-29-2001","Male");
        MeetingInfo matt7 = new MeetingInfo("Matt7","03-29-2001","Male");
        MeetingInfo matt8 = new MeetingInfo("Matt8","03-29-2001","Male");
        MeetingInfo matt9 = new MeetingInfo("Matt9","03-29-2001","Male");
        MeetingInfo matt10 = new MeetingInfo("Matt10","03-29-2001","Male");
        MeetingInfo matt11 = new MeetingInfo("Matt11","03-29-2001","Male");

        //Add the MeetingInfo objects to an ArrayList
        ArrayList<MeetingInfo> peopleList = new ArrayList<>();
        peopleList.add(john);
        peopleList.add(steve);
        peopleList.add(stacy);
        peopleList.add(ashley);
        peopleList.add(matt);
        peopleList.add(matt2);
        peopleList.add(matt3);
        peopleList.add(matt4);
        peopleList.add(matt5);
        peopleList.add(matt6);
        peopleList.add(matt7);
        peopleList.add(matt8);
        peopleList.add(matt9);
        peopleList.add(matt10);
        peopleList.add(matt11);

        MeetingInfoListAdapter adapter = new MeetingInfoListAdapter(this, R.layout.adabter_view_list, peopleList);
        mListView.setAdapter(adapter);
    }
}

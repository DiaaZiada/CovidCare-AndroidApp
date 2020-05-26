package com.example.covidcare;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class MainActivityViewModel extends ViewModel{
    private static final String TAG = "MainActivityViewModel";

    private MutableLiveData<ArrayAdapter<String>> mListAdapter = new MutableLiveData<>();
    private MutableLiveData<String> mStatus = new MutableLiveData<>();
    private MutableLiveData<MyService.MyBinder> mBinder = new MutableLiveData<>();


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder iBinder) {
            Log.d(TAG, "ServiceConnection: connected to service.");
            // We've bound to MyService, cast the IBinder and get MyBinder instance
            MyService.MyBinder binder = (MyService.MyBinder) iBinder;
            mBinder.postValue(binder);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "ServiceConnection: disconnected from service.");
            mBinder.postValue(null);
        }
    };

    public ServiceConnection getServiceConnection(){
        return serviceConnection;
    }

    public LiveData<MyService.MyBinder> getBinder(){
        Log.d(TAG, "qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
        return mBinder;
    }


    public LiveData<String> getmStatus(){
        return mStatus;
    }

    public LiveData<ArrayAdapter<String>> getListAdabter(){
        return mListAdapter;
    }

//    public void setIsListAdabterUpdating(boolean isUpdating){
//        mIsProgressBarUpdating.postValue(isUpdating);
//    }




}

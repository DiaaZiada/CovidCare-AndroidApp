package adapter;

import android.content.Context;
import android.icu.util.MeasureUnit;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.covidcare.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import utils.MeetingInfo;

public class MeetingInfoListAdapter extends ArrayAdapter<MeetingInfo> {

    private static final String TAG = "MeetingListAdabter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;
    private DateTimeFormatter dtf= DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
    private LocalDateTime now;

    private static class ViewHolder {
        TextView time;
        TextView status;
        Button locaton;
    }

    public MeetingInfoListAdapter(Context context, int resource, ArrayList<MeetingInfo> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String time = getItem(position).getTime();
        String status = getItem(position).getStatus();
        double latitude = getItem(position).getLatitude();
        double longitude = getItem(position).getLogitude();




        time = getNumberOfDays(time);




        MeetingInfo meetingInfo = new MeetingInfo(time, status, latitude, longitude);

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        ViewHolder holder;


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.status = (TextView) convertView.findViewById(R.id.textView1);
            holder.time = (TextView) convertView.findViewById(R.id.textView2);
            holder.locaton = (Button) convertView.findViewById(R.id.textView3);

            result = convertView;

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }


        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        result.startAnimation(animation);
        lastPosition = position;




        holder.time.setText("From " + meetingInfo.getTime()+" ago");
        holder.status.setText(meetingInfo.getStatus());
        holder.locaton.setText(String.valueOf(meetingInfo.getLatitude())+" "+ String.valueOf(meetingInfo.getLogitude()));
        holder.locaton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, String.valueOf(holder.locaton.getText()));
            }
        });
        return convertView;
    }

    private String getNumberOfDays(String time){
        now = LocalDateTime.now();
        String nowString = dtf.format(now).toString();

        Date firstDate = null;
        Date secondDate = null;
        try {
            firstDate = sdf.parse(nowString);
            secondDate = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d(TAG, time+"\t"+nowString);
        long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
        long seconds = diffInMillies/1000;
        int day = (int)TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day *24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);

//        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        return String.valueOf(day)+"d:"+String.valueOf(hours)+"h:"+String.valueOf(minute)+"m";

    }

}

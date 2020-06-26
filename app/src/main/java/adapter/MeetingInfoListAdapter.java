package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);
    private LocalDateTime now;
    private int counter = 0;

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


        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.status = (TextView) convertView.findViewById(R.id.tvStatus);
            holder.time = (TextView) convertView.findViewById(R.id.tvTime);
            holder.locaton = (Button) convertView.findViewById(R.id.btnLocation);
            holder.imageView = (ImageView) convertView.findViewById(R.id.image);
            result = convertView;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }


        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        result.startAnimation(animation);
        lastPosition = position;
        switch (status) {
            case "Healthy":
                holder.imageView.setBackgroundResource(R.drawable.healthy_background);
                holder.imageView.setImageResource(R.mipmap.healthy_foreground);
                break;
            case "Recovered":
                holder.imageView.setBackgroundResource(R.drawable.treated_background);
                holder.imageView.setImageResource(R.mipmap.treated_foreground);
                break;
            case "Infected":
                holder.imageView.setBackgroundResource(R.drawable.infected_background);
                holder.imageView.setImageResource(R.mipmap.infected_foreground);
                break;
        }


        holder.time.setText(meetingInfo.getTime());
        holder.status.setText(meetingInfo.getStatus());
        if (latitude == 9999 && longitude == 9999)
            holder.locaton.setEnabled(false);
        else
            holder.locaton.setEnabled(true);


        holder.locaton.setId(position);

        return convertView;
    }

    private String getNumberOfDays(String time) {
        now = LocalDateTime.now();
        String nowString = dtf.format(now);
        Date firstDate = null;
        Date secondDate = null;
        try {
            firstDate = sdf.parse(nowString);
            secondDate = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
        long seconds = diffInMillies / 1000;
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);


        return "From " + day + " day: " + hours + " hours ago";

    }

    private static class ViewHolder {
        TextView time;
        TextView status;
        Button locaton;
        ImageView imageView;
    }

}

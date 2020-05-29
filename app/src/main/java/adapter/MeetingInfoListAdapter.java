package adapter;

import android.content.Context;
import android.icu.util.MeasureUnit;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.covidcare.R;

import java.util.ArrayList;

import utils.MeetingInfo;

public class MeetingInfoListAdapter extends ArrayAdapter<MeetingInfo> {

    private static final String TAG = "MeetingListAdabter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    private static class ViewHolder {
        TextView time;
        TextView status;
        TextView locaton;
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
        String location = getItem(position).getLocation();

        MeetingInfo meetingInfo = new MeetingInfo(time, status, location);

        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        ViewHolder holder;


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.time = (TextView) convertView.findViewById(R.id.textView1);
            holder.status = (TextView) convertView.findViewById(R.id.textView2);
            holder.locaton = (TextView) convertView.findViewById(R.id.textView3);

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

        holder.time.setText(meetingInfo.getTime());
        holder.status.setText(meetingInfo.getStatus());
        holder.locaton.setText(meetingInfo.getLocation());

        return convertView;
    }

}

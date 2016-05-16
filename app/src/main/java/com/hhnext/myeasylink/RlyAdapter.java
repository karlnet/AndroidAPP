package com.hhnext.myeasylink;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

/**
 * Created by Administrator on 2016/5/10.
 */


public class RlyAdapter extends MyBaseAdapter {

    public static CompoundButton.OnCheckedChangeListener mToggleListener;
    //    private Context context;
    private List<String> rlyArray;

    public RlyAdapter(Context ctx, GHCB theGHCB) {
        super(ctx, theGHCB);
//        this.context = ctx;
//        this.mGHCB = theGHCB;
        rlyArray = theGHCB.getPortTypeNums().get("rly");

        mToggleListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                RlyPort r = (RlyPort) buttonView.getTag();
                if (isChecked)
                    r.OFF();
                else
                    r.ON();
//                Toast.makeText(DeviceActivity.this, "xxx" + buttonView.toString(), Toast.LENGTH_LONG).show();
//                cameraButton.performClick();
            }
        };
//        mDelListener=new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RlyPort r = (RlyPort) v.getTag();
//                mGHCB.DelPort(r.getPort());
//                notifyDataSetChanged();
//            }
//        };
    }
//    private View.OnClickListener mDelListener;

    @Override
    public int getCount() {
        return rlyArray.size();
    }

    @Override
    public Object getItem(int position) {
        return mGHCB.getPort(rlyArray.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RlyViewHolder viewHolder;

        final RlyPort r = (RlyPort) mGHCB.getPort(rlyArray.get(position));

        r.setListViewIndex(position);

        if (convertView == null) {
            convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.rly, null);
            viewHolder = new RlyViewHolder();
//                viewHolder.Index = position;
//                viewHolder.RlyDescription = ((LinearLayout) convertView.findViewById(R.id.devicesLinerLayout));
            viewHolder.Name = ((TextView) convertView.findViewById(R.id.rlyName));
            viewHolder.RlyState = ((TextView) convertView.findViewById(R.id.rlyState));
            viewHolder.RlyToggleButton = ((ToggleButton) convertView.findViewById(R.id.rlyToggle));
            viewHolder.DelButton = ((ImageButton) convertView.findViewById(R.id.rlyDelete));
//                rlytoggleButton = ((ToggleButton) convertView.findViewById(R.id.rlyToggle));
//                viewHolder.RlytoggleButton=rlytoggleButton;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (RlyViewHolder) convertView.getTag();
//                rlytoggleButton = ((ToggleButton) convertView.findViewById(R.id.rlyToggle));
        }
//        viewHolder.Index = position;

        setViewHolder(viewHolder, r);
//        viewHolder.Name.setText(r.getPortName() + "：");
        viewHolder.RlyState.setText(MyUtil.togglgText(r.isRlyState()));


//        viewHolder.DelButton.setTag(r);
//        viewHolder.DelButton.setVisibility(r.isPortDelFlag() ? View.VISIBLE : View.GONE);
//        viewHolder.DelButton.setOnClickListener(mDelListener);

        viewHolder.RlyToggleButton.setTag(r);
        viewHolder.RlyToggleButton.setOnCheckedChangeListener(null);
        viewHolder.RlyToggleButton.setChecked(!r.isRlyState());
        viewHolder.RlyToggleButton.setOnCheckedChangeListener(mToggleListener);

        return convertView;
    }

    public class RlyViewHolder extends ViewHolder {
        //        public TextView RlyName;
        public TextView RlyState;
        public ToggleButton RlyToggleButton;


        public RlyViewHolder() {
        }
    }


}
package com.hhnext.myeasylink;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2016/5/10.
 */
public class TempAdapter extends MyBaseAdapter {

    private List<String> tempArray;

//    private Context context;
//    private GHCB mGHCB;

    public TempAdapter(Context ctx, GHCB theGHCB) {
        super(ctx, theGHCB);
//        this.context = ctx;
//        this.mGHCB = theGHCB;
        tempArray = theGHCB.getPortTypeNums().get("temp");


//        mDelListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                TempPort t = (TempPort) v.getTag();
//                mGHCB.DelPort(t.getPort());
//                notifyDataSetChanged();
//            }
//        };

    }

//    private View.OnClickListener mDelListener;

    @Override
    public int getCount() {
        return tempArray.size();
    }

    @Override
    public Object getItem(int position) {
        return mGHCB.getPort(tempArray.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TempViewHolder viewHolder;

        final TempPort t = (TempPort) mGHCB.getPort(tempArray.get(position));

        t.setListViewIndex(position);

        if (convertView == null) {
            convertView = ((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.temp, null);
            viewHolder = new TempViewHolder();
            viewHolder.Index = position;
//                viewHolder.RlyDescription = ((LinearLayout) convertView.findViewById(R.id.devicesLinerLayout));
            viewHolder.Name = ((TextView) convertView.findViewById(R.id.tempName));
            viewHolder.TempTemp = ((TextView) convertView.findViewById(R.id.tempTemp));
            viewHolder.TempHumi = ((TextView) convertView.findViewById(R.id.tempHumi));
            viewHolder.DelButton = ((ImageButton) convertView.findViewById(R.id.tempDelete));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (TempViewHolder) convertView.getTag();
        }

        setViewHolder(viewHolder, t);
//        viewHolder.Name.setText(t.getPortName() + "：");
        viewHolder.TempTemp.setText(t.getTemperature() + "");
        viewHolder.TempHumi.setText(t.getHumidity() + "");

//        viewHolder.DelButton.setTag(t);
//        viewHolder.DelButton.setOnClickListener(mDelListener);
//        viewHolder.DelButton.setVisibility(t.isPortDelFlag() ? View.VISIBLE : View.GONE);

        return convertView;
    }

    public class TempViewHolder extends ViewHolder {

        public TextView TempTemp;
        public TextView TempHumi;


        public TempViewHolder() {
        }
    }
}

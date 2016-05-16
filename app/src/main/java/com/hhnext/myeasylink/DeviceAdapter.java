package com.hhnext.myeasylink;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/5/10.
 */
public class DeviceAdapter extends MyBaseAdapter {

    private GHCB[] mGHCBList;

    public DeviceAdapter(Context ctx/*,GHCB[] theGHCBList*/) {
        super(ctx);

        this.mGHCBList =/*theGHCBList;// */(GHCB[]) GHCBManage.GHCBs.values().toArray(new GHCB[0]);
    }

    public int getCount() {
        return mGHCBList.length;
    }

    public Object getItem(int position) {
        return mGHCBList[position];
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        GHCB mm = mGHCBList[position];
        mm.setListViewindex(position);

        DeviceViewHolder viewHolder;
        if (convertView == null) {
            convertView = ((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.devices, null);
            viewHolder = new DeviceViewHolder();
            viewHolder.devId = mm.getDevID();
//                viewHolder.DeviceLinerLayout = ((LinearLayout) convertView.findViewById(R.id.devicesLinerLayout));
            viewHolder.Name = ((TextView) convertView.findViewById(R.id.deviceDescription));
            viewHolder.rly1Name = ((TextView) convertView.findViewById(R.id.rly1Description));
            viewHolder.rly2Name = ((TextView) convertView.findViewById(R.id.rly2Description));
            viewHolder.humidity = ((TextView) convertView.findViewById(R.id.deviceHumidity));
            viewHolder.temperature = ((TextView) convertView.findViewById(R.id.deviceTemperature));
            viewHolder.rly2 = ((TextView) convertView.findViewById(R.id.devicePump));
            viewHolder.rly1 = ((TextView) convertView.findViewById(R.id.deviceLamp));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (DeviceViewHolder) convertView.getTag();
        }

        viewHolder.Name.setText(mGHCBList[position].getDevAlias());


        int no = mm.getInBoardTempPort();
        if (no != -1) {
            TempPort p = (TempPort) mm.getPort(String.valueOf(no));
            String res = p.getUploadData();
//                String[] s = res.split(":");
//                viewHolder.DeviceTemperature.setText(s[0]);
//                viewHolder.DeviceHumidity.setText(s[1]);
            viewHolder.temperature.setText(String.valueOf(p.getTemperature()));
            viewHolder.humidity.setText(String.valueOf(p.getHumidity()));
        }

        no = mm.getRly1();
        if (no != -1) {
            RlyPort t = (RlyPort) mm.getPort(String.valueOf(no));
            viewHolder.rly1.setText(MyUtil.togglgText(t.isRlyState()));
            viewHolder.rly1Name.setText(t.getPortName() + "：");
        } else {
            viewHolder.rly1.setText("");
            viewHolder.rly1Name.setText("");

        }

        no = mm.getRly2();
        if (no != -1) {
            RlyPort t = (RlyPort) mm.getPort(String.valueOf(no));
            viewHolder.rly2.setText(MyUtil.togglgText(t.isRlyState()));
            viewHolder.rly2Name.setText(t.getPortName() + "：");
        } else {
            viewHolder.rly2.setText("");
            viewHolder.rly2Name.setText("");

        }

        return convertView;
    }

    public class DeviceViewHolder extends MyBaseAdapter.ViewHolder {

        public TextView rly1Name;
        public TextView rly2Name;
        public TextView rly1;
        public TextView rly2;
        public TextView temperature;
        public TextView humidity;
        public String devId;
//        public LinearLayout deviceLinerLayout;

        private DeviceViewHolder() {
        }
    }

}



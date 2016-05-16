package com.hhnext.myeasylink;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/5/10.
 */
public class MyBaseAdapter extends BaseAdapter {

    protected Context context;
    protected GHCB mGHCB = null;
    protected View.OnClickListener mDelListener;

    public MyBaseAdapter(Context context) {
        this.context = context;
    }

    public MyBaseAdapter(Context context, GHCB theGHCB) {
        this.context = context;
        this.mGHCB = theGHCB;

        mDelListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Port p = (Port) v.getTag();
                mGHCB.AddOrDelToCloud(p, false);
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    protected void setViewHolder(ViewHolder viewHolder, Port p) {

        viewHolder.Name.setText(p.getPortName() + "：");

        viewHolder.DelButton.setTag(p);
        viewHolder.DelButton.setVisibility(p.isPortDelFlag() ? View.VISIBLE : View.GONE);
        viewHolder.DelButton.setOnClickListener(mDelListener);

    }

    public class ViewHolder {

        public int Index;

        public TextView Name;
        public ImageButton DelButton;

        public ViewHolder() {
        }
    }
}

package com.hhnext.myeasylink;

import android.widget.ListView;

/**
 * Created by Administrator on 2016/5/10.
 */
public class Refresh {

    public static void refreshListView(ListView lv, SetViewHolderData setViewHolderData, int current) {

        int start, last;

        start = lv.getFirstVisiblePosition();
        last = lv.getLastVisiblePosition();

        if ((current <= last) && (current >= start)) {
            MyBaseAdapter.ViewHolder viewHolder = (MyBaseAdapter.ViewHolder) lv.getChildAt(current - start).getTag();
            setViewHolderData.setViewHolder(viewHolder);
        }

    }
}

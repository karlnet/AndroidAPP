package com.hhnext.myeasylink;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Administrator on 2016/5/16.
 */
public class AutoAdjustHeightListView extends ListView {

    public AutoAdjustHeightListView(Context context) {
        super(context);
    }

    public AutoAdjustHeightListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoAdjustHeightListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
package com.hhnext.myeasylink;

import com.google.gson.JsonObject;

/**
 * Created by Administrator on 2016/5/4.
 */
public class RlyPort extends Port {

//    public  List<String> rlyArray = new ArrayList<>();

//    public static ListView rlyListView;

    private boolean rlyState;

    public RlyPort(GHCB mGHCB) {
        super(mGHCB);

    }

//    @Override
//    public void setListView(ListView listView) {
//        this.rlyListView = listView;
//    }

    public RlyPort() {

    }

//    @Override
//    public ListView getListView() {
//        return rlyListView;
//    }

    @Override
    public void addPortNo(String no) {
        addToMap("rly", no);
//        if (!mGHCB.isRlyInit())
//            mGHCB.RlyInit(true);
    }

//    @Override
//    public void refreshListView() {
//
//        Refresh.refreshListView(rlyListView,this,this.listViewIndex);
//
//    }

    @Override
    public void delPortNo(String no) {
        delFromMap("rly", no);
    }

    @Override
    public void setViewHolder(MyBaseAdapter.ViewHolder ViewHolder) {

        RlyAdapter.RlyViewHolder rlyViewHolder = (RlyAdapter.RlyViewHolder) ViewHolder;
        rlyViewHolder.RlyState.setText(MyUtil.togglgText(rlyState));

        if (rlyState == rlyViewHolder.RlyToggleButton.isChecked()) {

            rlyViewHolder.RlyToggleButton.setOnCheckedChangeListener(null);
            rlyViewHolder.RlyToggleButton.setChecked(!rlyState);
            rlyViewHolder.RlyToggleButton.setOnCheckedChangeListener(RlyAdapter.mToggleListener);
        }

    }

    public boolean isRlyState() {
        return rlyState;
    }

//    public void setRlyState(boolean rlyState) {
//        this.rlyState = rlyState;
//    }

    @Override
    public String getUploadData() {
        return String.valueOf(rlyState);
    }

    @Override
    public void setUploadData(UpdateJsonData.Data uploadData) {

        Boolean b = Boolean.valueOf(uploadData.value);

        if (this.rlyState != b) {
            this.rlyState = b;
            mGHCB.sendMsgToWindows(this.port);
        }
    }

    public void OFF() {
        Switch(false);
    }

    public void ON() {
        Switch(true);
    }


    private void Switch(boolean flag) {
        JsonObject content = new JsonObject();
        content.addProperty("port", port);
        content.addProperty("value", flag);
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("rly", content);
        mGHCB.PublishCommand(jsonObject.toString());

    }


}

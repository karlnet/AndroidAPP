package com.hhnext.myeasylink;

/**
 * Created by Administrator on 2016/5/4.
 */
public class TempPort extends Port {

//    public  List<String> tempArray = new ArrayList<>();

//    public static ListView tempListView;


    private float temperature;
    private float humidity;


    public TempPort(GHCB mGHCB) {
        super(mGHCB);
    }

    public TempPort() {
    }

    public float getTemperature() {
        return temperature;
    }

//    public void setTemperature(float temperature) {
//        this.temperature = temperature;
//    }

    public float getHumidity() {
        return humidity;
    }

//    public void setHumidity(float humidity) {
//        this.humidity = humidity;
//    }

//    @Override
//    public ListView getListView() {
//        return tempListView;
//    }

//    @Override
//    public  void setListView(ListView tempListView) {
//        this.tempListView = tempListView;
//    }

    @Override
    public void addPortNo(String no) {

        addToMap("temp", no);
    }

    @Override
    public void delPortNo(String no) {

        delFromMap("temp", no);
    }

    @Override
    public String getUploadData() {
        return String.valueOf(temperature) + ":" + humidity;
    }

    @Override
    public void setUploadData(UpdateJsonData.Data uploadData) {
        String[] t = uploadData.value.split(":");

        float f1 = Float.parseFloat(t[0]);
        if (this.temperature != f1) {
            this.temperature = f1;
            mGHCB.sendMsgToWindows(this.port);
        }

        float f2 = Float.parseFloat(t[1]);
        if (this.humidity != f2) {
            this.humidity = f2;
            mGHCB.sendMsgToWindows(this.port);
        }
    }

    @Override
    public void setViewHolder(MyBaseAdapter.ViewHolder ViewHolder) {

        TempAdapter.TempViewHolder tempViewHolder = (TempAdapter.TempViewHolder) ViewHolder;

        tempViewHolder.TempHumi.setText(String.valueOf(humidity));
        tempViewHolder.TempTemp.setText(String.valueOf(temperature));

    }

}

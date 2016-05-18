package com.hhnext.myeasylink;

import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/5/4.
 */
public class Port implements RefreshData, SetViewHolderData {


//    public CountDownLatch countDownLatch;

    protected GHCB mGHCB;
    protected int BoardPortId;
    protected int portId;
    protected int port;
    protected String portName;
    protected int listViewIndex;
    protected int portTypeId;
    protected String portModel;
    protected String portType;
    protected String portDescName;
    protected boolean portDelFlag = false;

    public Port() {
    }

    public Port(GHCB mGHCB) {
        this.mGHCB = mGHCB;
    }

    public int getBoardPortId() {
        return BoardPortId;
    }

    public void setBoardPortId(int boardPortId) {
        BoardPortId = boardPortId;
    }

    public void setPortTypeId(int portTypeId) {
        this.portTypeId = portTypeId;
    }

    public int getPortId() {
        return portId;
    }

    public void setFromPortBindingModel(BoardPortsModel model) {

        this.BoardPortId = model.getBoardId();

        this.portId = model.getPortId();
        this.port = model.getPortNo();
        this.portName = model.getPortName();

        this.portTypeId = model.getPortTypeId();
        this.portModel = model.getPortTypeName();
        this.portType = model.getPortType();
        this.portDescName = model.getPortTypeDescription();

    }

    public BoardPortsModel getPortBindingModel() {
        BoardPortsModel model = new BoardPortsModel();
        model.mac = mGHCB.getMAC();
//        model.PortId = this.portId;
        model.setPortTypeId(this.portTypeId);
        model.setPortNo(this.port);
        model.setPortName(this.portName);
        return model;
    }

    public GHCB getGHCB() {
        return mGHCB;
    }

    public void setGHCB(GHCB mGHCB) {
        this.mGHCB = mGHCB;
    }

    public String getPortDescName() {
        return portDescName;
    }

    public void setPortDescName(String portDescName) {
        this.portDescName = portDescName;
    }

    public String getUploadData() {
        return null;
    }

    public void setUploadData(UpdateJsonData.Data uploadData) {
    }

    public void addPortNo(String no) {
    }

    public void delPortNo(String no) {
    }

    public ListView getListView() {
        return null;
    }

    public void setListView(ListView listView) {
    }

    public void setViewHolder(MyBaseAdapter.ViewHolder viewHolder) {

    }

    public void refresh() {

        RefreshDataBase.refreshListViewData(getListView(), this, listViewIndex);
    }


    public boolean isPortDelFlag() {
        return portDelFlag;
    }

    public void setPortDelFlag(boolean portDelFlag) {
        if (this.port != GHCB.inBoardPort)
            this.portDelFlag = portDelFlag;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPortType() {
        return portType;
    }

    public void setPortType(String portType) {
        this.portType = portType;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public String getPortModel() {
        return portModel;
    }

    public void setPortModel(String portModel) {
        this.portModel = portModel;
    }

    public int getListViewIndex() {
        return listViewIndex;
    }

    public void setListViewIndex(int listViewPosition) {
        this.listViewIndex = listViewPosition;
    }

    public void addToMap(String name, String value) {
        HashMap<String, List<String>> portTypeNum = mGHCB.getPortTypeNums();
        if (!portTypeNum.containsKey(name)) {
            List<String> r = new ArrayList<>();
            r.add(value);
            portTypeNum.put(name, r);
        } else {
            if (!portTypeNum.get(name).contains(value))
                portTypeNum.get(name).add(value);
        }
        Collections.sort(mGHCB.getPortTypeNums().get(name));
    }

    public void delFromMap(String name, String value) {
        mGHCB.getPortTypeNums().get(name).remove(value);
//        if( mGHCB.getPortTypeNums().get(name).size()==0){
//            mGHCB.getPortTypeNums().remove(name);
//        }
        Collections.sort(mGHCB.getPortTypeNums().get(name));
    }
}

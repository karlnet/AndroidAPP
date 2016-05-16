package com.hhnext.myeasylink;

/**
 * Created by Administrator on 2016/5/12.
 */
public class BoardPortsModel {
    public String mac;
    private int boardId;

    private int portId;
    private int portNo;
    private String portName;

    private int portTypeId;
    private String portType;
    private String portTypeName;
    private String portTypeDescription;
    private String classType;


    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getBoardId() {
        return boardId;
    }

    public void setBoardId(int boardId) {
        this.boardId = boardId;
    }

    public int getPortId() {
        return portId;
    }

    public void setPortId(int portId) {
        this.portId = portId;
    }

    public int getPortNo() {
        return portNo;
    }

    public void setPortNo(int portNo) {
        this.portNo = portNo;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public int getPortTypeId() {
        return portTypeId;
    }

    public void setPortTypeId(int portTypeId) {
        this.portTypeId = portTypeId;
    }

    public String getPortType() {
        return portType;
    }

    public void setPortType(String portType) {
        this.portType = portType;
    }

    public String getPortTypeName() {
        return portTypeName;
    }

    public void setPortTypeName(String portTypeName) {
        this.portTypeName = portTypeName;
    }

    public String getPortTypeDescription() {
        return portTypeDescription;
    }

    public void setPortTypeDescription(String portTypeDescription) {
        this.portTypeDescription = portTypeDescription;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }


}
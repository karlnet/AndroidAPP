package com.hhnext.myeasylink;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/5/8.
 */
public class PortDescription {

    private List<String> portType;
    private List<String> portTypeId;
    private String portDesc;
    private String portModel;
    private String portClassInfo;

    private HashMap<String, String> portDescriptionMap;

    public PortDescription(String portModel, String portDesc, String portClassInfo, List<String> portType) {
        portModel = portModel;
        portDesc = portDesc;
        portClassInfo = portClassInfo;
        portType = portType;


    }

    public void setMap() {

        portDescriptionMap = new HashMap<>();
        for (int i = 0; i < portType.size(); i++) {

            portDescriptionMap.put(portType.get(i), portTypeId.get(i));
        }

    }

    public HashMap<String, String> getMap() {

        return portDescriptionMap;

    }

    public List<String> getPortType() {
        return portType;
    }

    public void setPortType(List<String> portType) {
        this.portType = portType;
    }

    public String getPortDesc() {
        return portDesc;
    }

    public void setPortDesc(String portDesc) {
        this.portDesc = portDesc;
    }

    public String getPortModel() {
        return portModel;
    }

    public void setPortModel(String portModel) {
        this.portModel = portModel;
    }

    public String getPortClassInfo() {
        return portClassInfo;
    }

    public void setPortClassInfo(String portClassInfo) {
        this.portClassInfo = portClassInfo;
    }


}

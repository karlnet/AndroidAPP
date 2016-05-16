package com.hhnext.myeasylink;

import com.google.gson.Gson;

public class ProtocolParser {

    Gson gson = new Gson();
//    JsonObject/* jsonObject2,*/ jsonObject;

    public void Parse(GHCB theGHCB, String jsonString) {


        UpdateJsonData r = gson.fromJson(jsonString, UpdateJsonData.class);
        theGHCB.setJsonData(r);
//        jsonObject = (JsonObject) new JsonParser().parse(jsonString);
//        if (jsonObject.has("payload")) {
//
//            try {
//                jsonObject = jsonObject2.getAsJsonObject("payload");
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.i("orinoco", "get json string error :" + jsonObject2.toString());
//            }
//            Log.i("orinoco", "get json string is :" + jsonObject);

//            if (jsonObject.has("temp"))
//                theGHCB.setHumidity(jsonObject.get("dht11_humidity").getAsString());

//            if (jsonObject.has("dht11_temperature"))
//                theGHCB.setTemperature(jsonObject.get("dht11_temperature").getAsString());
//
////        if (jsonObject.has("rgbled_switch"))
////            theGHCB.setLamp(jsonObject.get("rgbled_switch").getAsBoolean());
//
//            if (jsonObject.has("hasImage"))
//                theGHCB.setHasImage(jsonObject.get("hasImage").getAsBoolean());
//
//            if (jsonObject.has("lamp_switch"))
//                theGHCB.setLamp(jsonObject.get("lamp_switch").getAsBoolean());
//
//            if (jsonObject.has("pump_switch"))
//                theGHCB.setPump(jsonObject.get("pump_switch").getAsBoolean());
//
//            if (jsonObject.has("ip_address"))
//                theGHCB.setIPAddress(jsonObject.get("ip_address").getAsString());
        }
    }



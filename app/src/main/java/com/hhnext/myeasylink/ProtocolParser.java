package com.hhnext.myeasylink;

import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ProtocolParser {
    public void Parse(GHCB theGHCB, String jsonString) {
        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonString);
        Log.i("orinoco", jsonObject.toString());
        theGHCB.setHumidity(jsonObject.get("payload").getAsJsonObject().get("dht11_humidity").getAsString());
        theGHCB.setTemperature(jsonObject.get("payload").getAsJsonObject().get("dht11_temperature").getAsString());
        theGHCB.setLamp(jsonObject.get("payload").getAsJsonObject().get("rgbled_switch").getAsBoolean());
        theGHCB.setHasImage(jsonObject.get("payload").getAsJsonObject().get("hasImage").getAsBoolean());
    }

    public class UpData {


    }
}
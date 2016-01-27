package com.hhnext.myeasylink;

import android.util.Log;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ProtocolParser
{
    public void Parse(GHCB paramGHCB, String paramString)
    {
        JsonObject localJsonObject = (JsonObject)new JsonParser().parse(paramString);
        Log.i("orinoco", localJsonObject.toString());
        paramGHCB.setHumidity(localJsonObject.get("payload").getAsJsonObject().get("dht11_humidity").getAsString());
        paramGHCB.setTemperature(localJsonObject.get("payload").getAsJsonObject().get("dht11_temperature").getAsString());
        paramGHCB.setLamp(localJsonObject.get("payload").getAsJsonObject().get("rgbled_switch").getAsBoolean());
        paramGHCB.setPump(localJsonObject.get("payload").getAsJsonObject().get("motor_switch").getAsBoolean());
    }
}
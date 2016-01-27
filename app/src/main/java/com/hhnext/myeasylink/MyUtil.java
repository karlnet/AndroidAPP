package com.hhnext.myeasylink;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Calendar;

import org.json.JSONObject;
import org.xutils.http.RequestParams;

public class MyUtil
{
    private static String appSecretKey;

    private static String HexEncode(byte[] paramArrayOfByte)
    {
        StringBuilder localStringBuilder = new StringBuilder(2 * paramArrayOfByte.length);
        int i = paramArrayOfByte.length;
        for (int j = 0; j < i; j++)
        {
            int k = paramArrayOfByte[j];
            localStringBuilder.append(Integer.toHexString((k & 0xF0) >>> 4));
            localStringBuilder.append(Integer.toHexString(k & 0xF));
        }
        return localStringBuilder.toString();
    }

    public static String MD5Encode(String paramString)
    {
        try
        {
            byte[] arrayOfByte = paramString.getBytes();
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.reset();
            localMessageDigest.update(arrayOfByte);
            String str = HexEncode(localMessageDigest.digest());
            return str;
        }
        catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
        {
            localNoSuchAlgorithmException.printStackTrace();
        }
        return "";
    }

    public static String getCurrentTimeStamp()
    {
        try
        {
            String str = String.valueOf(new Timestamp(Calendar.getInstance().getTime().getTime()).getTime() / 1000L);
            return str;
        }
        catch (Exception localException)
        {
            localException.printStackTrace();
        }
        return null;
    }

    public static void setRequestParamsHeader(RequestParams paramRequestParams)
    {
        paramRequestParams.addHeader("content-type", "application/json");
        paramRequestParams.addHeader("X-Application-Id", "6a3d6800-1b07-4fc5-86ca-12bba8f8dc67");
        paramRequestParams.addHeader("X-Request-Sign", APPUser.getRequestSign());
        paramRequestParams.addHeader("Authorization", APPUser.getRequestAuthorization());
    }

    public static String togglgText(boolean paramBoolean)
    {
        if (paramBoolean)
            return "开";
        return "关";
    }

    public String getAppSecretKey()
    {
        return appSecretKey;
    }

    protected void httpMsgToBoard(JSONObject paramJSONObject, String paramString)
    {
        try
        {
            HttpURLConnection localHttpURLConnection = (HttpURLConnection)new URL(paramString).openConnection();
            localHttpURLConnection.setDoOutput(true);
            localHttpURLConnection.setDoInput(true);
            localHttpURLConnection.setUseCaches(false);
            localHttpURLConnection.setConnectTimeout(30000);
            localHttpURLConnection.setReadTimeout(30000);
            localHttpURLConnection.setRequestProperty("Content-type", "application/json");
            localHttpURLConnection.setRequestMethod("POST");
            localHttpURLConnection.connect();
            ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(localHttpURLConnection.getOutputStream());
            localObjectOutputStream.writeObject(paramJSONObject);
            localObjectOutputStream.flush();
            localObjectOutputStream.close();
            new ObjectInputStream(localHttpURLConnection.getInputStream());
            return;
        }
        catch (Exception localException)
        {
        }
    }

    public void setAppSecretKey(String paramString)
    {
        appSecretKey = paramString;
    }
}
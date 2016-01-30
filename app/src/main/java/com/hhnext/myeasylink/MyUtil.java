package com.hhnext.myeasylink;

import org.xutils.http.RequestParams;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Calendar;



public class MyUtil {

/*    private final static char[] hexArray = "0123456789abcdef".toCharArray();

    public static String hmacSha1(String value, String key)
            throws UnsupportedEncodingException, NoSuchAlgorithmException,
            InvalidKeyException {
        String type = "HmacSHA1";
        SecretKeySpec secret = new SecretKeySpec(key.getBytes(), type);
        Mac mac = Mac.getInstance(type);
        mac.init(secret);
        byte[] bytes = mac.doFinal(value.getBytes("UTF-8"));
        return bytesToHex(bytes);
    }

    public static String sha1(String s, String keyString) throws
            UnsupportedEncodingException, NoSuchAlgorithmException,
            InvalidKeyException {

        SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(key);

        byte[] bytes = mac.doFinal(s.getBytes("UTF-8"));
        return new String(Base64.encodeToString(bytes, 0));

    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }*/

    private static String HexEncode(byte[] paramArrayOfByte) {
        StringBuilder localStringBuilder = new StringBuilder(2 * paramArrayOfByte.length);
        int i = paramArrayOfByte.length;
        for (int j = 0; j < i; j++) {
            int k = paramArrayOfByte[j];
            localStringBuilder.append(Integer.toHexString((k & 0xF0) >>> 4));
            localStringBuilder.append(Integer.toHexString(k & 0xF));
        }
        return localStringBuilder.toString();
    }

    public static String MD5Encode(String paramString) {
        try {
            byte[] arrayOfByte = paramString.getBytes();
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.reset();
            localMessageDigest.update(arrayOfByte);
            String str = HexEncode(localMessageDigest.digest());
            return str;
        } catch (NoSuchAlgorithmException localNoSuchAlgorithmException) {
            localNoSuchAlgorithmException.printStackTrace();
        }
        return "";
    }

    public static String getCurrentTimeStamp() {
        try {
            String str = String.valueOf(new Timestamp(Calendar.getInstance().getTime().getTime()).getTime() / 1000L);
            return str;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return null;
    }

    public static void setRequestParamsHeader(RequestParams requestParamsHeader) {
        requestParamsHeader.addHeader("content-type", "application/json");
        requestParamsHeader.addHeader("X-Application-Id", "6a3d6800-1b07-4fc5-86ca-12bba8f8dc67");
        requestParamsHeader.addHeader("X-Request-Sign", APPUser.getRequestSign());
        requestParamsHeader.addHeader("Authorization", APPUser.getRequestAuthorization());
    }

    public static String togglgText(boolean toggle) {
        return toggle ? "开" : "关";
    }


}
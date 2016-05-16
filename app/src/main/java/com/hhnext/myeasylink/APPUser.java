package com.hhnext.myeasylink;

import com.qiniu.util.Auth;

import org.xutils.http.RequestParams;

import java.io.Serializable;

public class APPUser
        implements Serializable {

    public static final String QiniuAccesskey = "CPrn8bw4AyovxztgH88EZc7Q7Nf6Zfb0140gzgEh";
    public static final String QiniuSecretkey = "rxJYJMNUPyx1CnWs87Yl2o26VPceJEi4ot-9Bq06";
    public static final Auth QiniuAuth = Auth.create(QiniuAccesskey, QiniuSecretkey);
    public static final String Bucket = "hhnext";

    public static final String APPID = "6a3d6800-1b07-4fc5-86ca-12bba8f8dc67";
    public static final String APPSecretKey = "7c734a44ed8450aff7f8fb7f958e7d90";

    public static final String ProductID = "2f10b621";
    public static final String ProductSecertKey = "9a740523-d5f3-40f3-afb4-29b6d7c2726a";

    public static final String WifiTagHead = "_easylink._tcp.local.";

    public final static String BaseURL = "http://easylink.io/";
    public final static String LoginURL = BaseURL + "v2/users/login";
    public final static String SMSVerificationURL = BaseURL + "v2/users/sms_verification_code";
    public final static String RegisterURL = BaseURL + "v2/users";
    public final static String ResetPasswordURL = BaseURL + "v2/users/password/reset";


    public static final String activeURL = ":8000/dev-activate";
    public static final String devicesURL = BaseURL + "v1/device/devices";
    public static final String modifyURL = BaseURL + "v1/device/modify";
    public static final String authorizeURL = BaseURL + "v1/key/user/authorize";
    public static final String userQueryURL = BaseURL + "v1/device/user/query";


    public final static String MyBaseURL = "http://192.168.1.119:57793/";
    public final static String MyAddUserURL = MyBaseURL + "api/accounts/create";
    public final static String MyResetPasswordURL = MyBaseURL + "api/accounts/reset";
    public final static String MyLoginURL = MyBaseURL + "api/accounts/login";
    public final static String MyAuthorizeURL = MyBaseURL + "api/boards/authorize";
    public final static String MyBoardPortsURL = MyBaseURL + "api/ports";
    public final static String MyBoardPortCreateURL = MyBaseURL + "api/ports/create";
    public final static String MyBoardPortRemoveURL = MyBaseURL + "api/ports/remove";
    public final static String MyBoardPortDescriptionURL = MyBaseURL + "api/portdescriptions";
    //    public static String userEmail;
    public static String UserID = "";
    //    public static String userMobile;
    public static String UserName = "";
    public static String UserPassword = "";
    //    public static String userSMSToken;
    public static String UserToken = "";
    public static String MyUserToken = "";


    public static String getRequestAuthorization() {
        return "token " + UserToken;
    }

    public static String getMyRequestAuthorization() {
        return "Basic " + MyUserToken;
    }

    public static String getRequestSign() {
        String str = MyUtil.getCurrentTimeStamp();
        return MyUtil.MD5Encode(new StringBuilder().append(APPSecretKey).append(str).toString()) + ", " + str;
    }

    public static String getActiveToken(String mac) {
        return MyUtil.MD5Encode(mac + ProductSecertKey);
    }

    public static String getActiveURL(String ip) {
        return "http://" + ip + activeURL;
    }

    public static void setRequestParamsHeader(RequestParams requestParamsHeader) {
        requestParamsHeader.addHeader("content-type", "application/json");
        requestParamsHeader.addHeader("X-Application-Id", APPUser.APPID);
        requestParamsHeader.addHeader("X-Request-Sign", APPUser.getRequestSign());
        requestParamsHeader.addHeader("Authorization", APPUser.getRequestAuthorization());
    }

    public static void setMyRequestParamsHeader(RequestParams requestParamsHeader) {
        requestParamsHeader.addHeader("content-type", "application/json");
        String auth=APPUser.getMyRequestAuthorization();
        requestParamsHeader.addHeader("Authorization",auth );
    }

}
package com.hhnext.myeasylink;

import com.qiniu.util.Auth;

import java.io.Serializable;

public class APPUser
        implements Serializable {
    public static final String QiniuAccesskey = "CPrn8bw4AyovxztgH88EZc7Q7Nf6Zfb0140gzgEh";
    public static final String QiniuSecretkey = "rxJYJMNUPyx1CnWs87Yl2o26VPceJEi4ot-9Bq06";
    public static final Auth QiniuAuth = Auth.create(QiniuAccesskey, QiniuSecretkey);
    public static final String bucket = "hhnext";

    public static final String APPID = "6a3d6800-1b07-4fc5-86ca-12bba8f8dc67";
    public static final String APPSecretKey = "7c734a44ed8450aff7f8fb7f958e7d90";
    public static final String loginURL = "http://easylink.io/v2/users/login";
    public static final String registerURL = "http://easylink.io/v2/users";
    public static String userEmail;
    public static String userID = "a48ee14e-96a8-4f1f-b40e-6c27af902e51";
    public static String userMobile;
    public static String userName = "13701308059@vip.163.com";
    public static String userPassword = "123456";
    public static String userSMSToken;
    public static String userToken = "d250cabf-9339-4abe-9fc5-c53932734edf";


    public static String getRequestAuthorization() {
        return "token " + userToken;
    }

    public static String getRequestSign() {
        String str = MyUtil.getCurrentTimeStamp();
        return MyUtil.MD5Encode(new StringBuilder().append("7c734a44ed8450aff7f8fb7f958e7d90").append(str).toString()) + ", " + str;
    }
}
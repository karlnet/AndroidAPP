package com.hhnext.myeasylink;

import java.util.List;

/**
 * Created by Administrator on 2016/5/4.
 */
public class UpdateJsonData {


    public List<Data> payload;

    public static class Data {
        public int port;
        public String type;
        public String value;
    }

}

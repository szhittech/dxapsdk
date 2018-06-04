package com.ap.moni.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static String getCurrentTime() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        System.out.println(sf.format(new Date()));
        return sf.format(new Date());
    }

    public static String getCurTime() {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");
        System.out.println(sf.format(new Date()));
        return sf.format(new Date());
    }
}

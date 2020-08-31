package com.cpit.cpmt.biz.utils;

public class ShareInfo {
    static final ThreadLocal<String> infos = new ThreadLocal<String>();

    public static String get(){
        return infos.get();
    }

    public static void put(String version){
        infos.set(version);
    }


    public static void close() {
        infos.remove();
    }
}

package com.jm.online_store.model;


public class CurrentUrl {

    private static String Url;

    public static void setUrl(String url) {
        Url = url;
    }

    public static String getUrl() {
        return Url;
    }
}

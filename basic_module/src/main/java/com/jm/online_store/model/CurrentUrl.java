package com.jm.online_store.model;

public class CurrentUrl {

    private static String url;

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        CurrentUrl.url = url;
    }
}

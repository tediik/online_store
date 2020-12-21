package com.jm.online_store.model;

import io.swagger.annotations.ApiModel;

@ApiModel(description =  "CurrentUrl, описывающий текущий url")
public class CurrentUrl {

    private static String url;

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        CurrentUrl.url = url;
    }
}

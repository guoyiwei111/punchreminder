package com.example.guoyiwei.dk.model;

import android.graphics.drawable.Drawable;

/**
 * Created by guoyiwei on 2017/6/13.
 */
public class MyAppInfo {
    private Drawable image;
    private String appName;
    public String packName;


    public MyAppInfo(Drawable image, String appName) {
        this.image = image;
        this.appName = appName;
    }
    public MyAppInfo() {

    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
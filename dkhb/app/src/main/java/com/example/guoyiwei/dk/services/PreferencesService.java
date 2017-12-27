package com.example.guoyiwei.dk.services;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.format.Time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreferencesService {
    private Context context;

    public PreferencesService(Context context) {
        this.context = context;
    }


    public void save(List<Date> time) {
        //获得SharedPreferences对象
        SharedPreferences preferences = context.getSharedPreferences("dkgyw", Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        String res = "";
        if(time.size()>=1 && time.get(time.size()-1).getMonth() != time.get(0).getMonth() ){
            res =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time.get(time.size()-1));
            //res = time.get(time.size()-1).to.format2445();
        }else{
            for(int i=0;i<time.size();i++) {


                if (res.length() != 0) {
                    res += ",";
                }
                res += new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time.get(i));
            }
        }
        editor.putString("time", res);
        //editor.putString("returndefault", returndefault);
        //editor.putString("show", show);
        editor.commit();
    }
    public String getTimeString(){
        SharedPreferences preferences = context.getSharedPreferences("dkgyw", Context.MODE_PRIVATE);
        String timeStr = preferences.getString("time", "");
        return timeStr;
    }
    public void saveTimeString(String res){
        SharedPreferences preferences = context.getSharedPreferences("dkgyw", Context.MODE_PRIVATE);
        Editor editor = preferences.edit();

        editor.putString("time", res);
        //editor.putString("returndefault", returndefault);
        //editor.putString("show", show);
        editor.commit();
    }

    public String getAppName(){
        SharedPreferences preferences = context.getSharedPreferences("dkgyw", Context.MODE_PRIVATE);
        String timeStr = preferences.getString("app", "");
        return timeStr;
    }
    public String getAppPackName(){
        SharedPreferences preferences = context.getSharedPreferences("dkgyw", Context.MODE_PRIVATE);
        String timeStr = preferences.getString("pack", "");
        return timeStr;
    }

    public String getValue(String value){
        SharedPreferences preferences = context.getSharedPreferences("dkgyw", Context.MODE_PRIVATE);
        String def="";
        switch (value){
            case "endtype": def="0";break;
            case "endtime": def="17:40";break;
            case "starttime": def="08:00";break;
            case "delaytime": def="0";break;
            case "alarmswitch": def="1";break;
            case "wdlist": def="[]";break;
            case "mhtype": def="0";break;
            case "workdayversion":def="0.0";break;
            case "fence":def="[]";break;
            case "fenceswitch":def="0";break;
            case "change":def="0";break;
            case "lastpush":def="";break;
        }

        String timeStr = preferences.getString(value, def);
        return timeStr;
    }
    public void setValue(String value, String res){
        SharedPreferences preferences = context.getSharedPreferences("dkgyw", Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(value, res);
        //editor.putString("show", show);
        editor.commit();
    }

    public void setAppName(String res,String pack){
        SharedPreferences preferences = context.getSharedPreferences("dkgyw", Context.MODE_PRIVATE);
        Editor editor = preferences.edit();

        editor.putString("app", res);
        editor.putString("pack", pack);
        editor.putString("component", "");
        //editor.putString("show", show);
        editor.commit();


    }

    public void setCompName(String res,String pack,String component){
        SharedPreferences preferences = context.getSharedPreferences("dkgyw", Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString("app", res);
        editor.putString("pack", pack);
        editor.putString("component", component);
        //editor.putString("show", show);
        editor.commit();


    }





}
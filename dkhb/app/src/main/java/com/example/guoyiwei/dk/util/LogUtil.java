package com.example.guoyiwei.dk.util;

import android.content.Context;

import com.example.guoyiwei.dk.services.PreferencesService;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by guoyi on 2017/9/17.
 */

public class LogUtil {
    public static Object lock = new Object();
    public static void addLog(Context context, String cls , String content){
        synchronized (lock){
            PreferencesService service =new PreferencesService(context);
            String e =service.getValue("log");
            if(e==null) e="";

            SimpleDateFormat sdf = new SimpleDateFormat("[MM-dd HH:mm:ss] ");
            Calendar now = Calendar.getInstance();
            String timeStr = sdf.format(now.getTime());

            e= timeStr+ "" +cls+" :"+content+"\r\n"+e;
            //e =  "已重新拉起服务！"+ Calendar.getInstance().getTime()+"\r\n"+e;
            if(e.length()>500000){
                e=e.substring(0,100000);
            }
            service.setValue("log",e);
        }

    }
}

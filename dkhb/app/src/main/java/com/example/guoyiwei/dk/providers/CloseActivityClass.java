package com.example.guoyiwei.dk.providers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guoyiwei on 2017/6/16.
 */
public class CloseActivityClass{

    public static List<Activity> activityList = new ArrayList<Activity>();

    public static void exitClient(Context ctx)
    {
        int lastyuan = -1;
        int lastyijian = -1;
        // 关闭所有Activity
        for (int i = activityList.size()-1; i >=0 ; i--)
        {
            if (null != activityList.get(i))
            {
                Intent intent = activityList.get(i).getIntent();
                if (intent != null) {
                    String tName = intent.getStringExtra("from");
                    if (tName != null && tName.equals("yijian")) {
                        if(lastyijian==-1){
                            lastyijian=i;
                        }
                    }else{
                        lastyuan = i;
                        break;
                    }
                }
            }
        }
        if(lastyijian<lastyuan){
            lastyijian= lastyuan;
        }

        for (int i = 0; i < activityList.size(); i++)
        {
            if (null != activityList.get(i) && lastyijian != i)
            {
                activityList.get(i).finish();

            }
        }

        List<Activity> tmpList = new ArrayList<Activity>();
        for (int i = 0; i < activityList.size(); i++)
        {
            if (null != activityList.get(i) )
            {
                tmpList.add(activityList.get(i));

            }
        }
        activityList = tmpList;

    }
}

package com.example.guoyiwei.dk.util;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.guoyiwei.dk.R;
import com.example.guoyiwei.dk.services.PreferencesService;
import com.example.guoyiwei.dk.model.MyTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by guoyiwei on 2017/8/20.
 */
public class AlarmUtil {


    public static void setAlarm(Activity activity,Context context,Calendar time) {
        isIgnoreBatteryOption(activity);
        SimpleDateFormat sdf = new SimpleDateFormat("[MM-dd HH:mm:ss] ");
        LogUtil.addLog(context,"AlarmUtil","设置闹钟："+sdf.format(time.getTime()));

        PreferencesService service =new PreferencesService(context);

        service.setValue("alarmtime",String.valueOf(time.getTimeInMillis()));

        AlarmManagerUtil.setAlarm(context, 0, time, 0, 0, "闹钟响了", 2);

/*
        PreferencesService service =new PreferencesService(context);
        String e =service.getValue("log");
        if(e==null) e="";
        e=  "闹钟设置为： "+time.getTime()+"\r\n"+e;
        service.setValue("log",e);*/

        //Toast.makeText(context, "闹钟设置成功" + time.getTime(), Toast.LENGTH_LONG).show();
    }
    public static void setAlarm(Context context,Calendar time) {
        SimpleDateFormat sdf = new SimpleDateFormat("[MM-dd HH:mm:ss] ");
        LogUtil.addLog(context,"AlarmUtil","设置闹钟："+sdf.format(time.getTime()));

        PreferencesService service =new PreferencesService(context);

        service.setValue("alarmtime",String.valueOf(time.getTimeInMillis()));

        AlarmManagerUtil.setAlarm(context, 0, time, 0, 0, "闹钟响了", 2);

        /*PreferencesService service =new PreferencesService(context);
        String e =service.getValue("log");
        if(e==null) e="";
        e = "闹钟设置为： "+time.getTime()+"(Back)\r\n"+e;
        service.setValue("log",e);*/


        // Toast.makeText(context, "闹钟设置成功" + time.getTime(), Toast.LENGTH_LONG).show();
    }
    public static  void removeClock(Activity activity) {
        LogUtil.addLog(activity,"AlarmUtil","取消闹钟");
        AlarmManagerUtil.cancelAlarm(activity,0);
        //Toast.makeText(this, "闹钟设置成功", Toast.LENGTH_LONG).show();
    }
    /**
     * 针对N以上的Doze模式
     *
     * @param activity
     */
    public static void isIgnoreBatteryOption(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Intent intent = new Intent();
                String packageName = activity.getPackageName();
                PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    //               intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                    int REQUEST_IGNORE_BATTERY_CODE = 1001;  ;
                    activity.startActivityForResult(intent, REQUEST_IGNORE_BATTERY_CODE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Calendar getLastCalendar(PreferencesService service, List<Map<String, Object>> list,Map<String,Integer> workdayList,Context context){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String end =service.getValue("endtime");

        String start = service.getValue("starttime");

        service.getValue("delaytime");
        //end type
        String endType = service.getValue("endtype");

        String mhType = service.getValue("mhtype");
        //检查是否需要提示最后一天和月末周六
        {


            Calendar tmp = Calendar.getInstance();
            boolean debug= false;
            if(tmp.get(Calendar.HOUR_OF_DAY)> 16 || debug){
                tmp.add(Calendar.DAY_OF_MONTH,1);
                Integer daytype = workdayList.get(sdf.format(tmp.getTime()));
                String pushtime = service.getValue("lastpush");


                if((daytype!=null && daytype == 1 )|| debug){
                    //yuemo
                    String nowpush = sdf.format(Calendar.getInstance().getTime());
                    if(!nowpush.equals(pushtime) || debug){
                        service.setValue("lastpush",nowpush);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.timg)
                                .setContentTitle("友情提醒")
                                .setContentText("明天月末周六，记得上班！");

                        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        manager.notify(1, builder.build());
                        Toast.makeText(context, "yuemo zhouliu", Toast.LENGTH_LONG).show();

                    }

                    //manager.cancel(notifyId);
                }else if(daytype!=null && daytype == 2){
                    //zuihou
                    String nowpush = sdf.format(Calendar.getInstance().getTime());
                    if(!nowpush.equals(pushtime)){
                        service.setValue("lastpush",nowpush);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.timg)
                                .setContentTitle("友情提醒")
                                .setContentText("明天本月最后一天，请算好时间！");

                        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        manager.notify(1, builder.build());
                        Toast.makeText(context, "zuihou yitian", Toast.LENGTH_LONG).show();


                    }
                }
            }


        }

        String alrmswitch = service.getValue("alarmswitch");
        if(alrmswitch.equals("1")){
            Calendar need = Calendar.getInstance();
            Integer daytype = workdayList.get(sdf.format(need.getTime()));
            if(daytype==null || daytype==3){//如果当前天是周末。则找到第一个非周末并为其设置start闹钟。
                while(daytype==null || daytype==3){
                    need.add(Calendar.DAY_OF_MONTH,1);
                    daytype = workdayList.get(sdf.format(need.getTime()));
                }
                String tmp[] = start.split(":");
                Calendar startTime = Calendar.getInstance();
                startTime.set(Calendar.YEAR,need.get(Calendar.YEAR));
                startTime.set(Calendar.MONTH,need.get(Calendar.MONTH));
                startTime.set(Calendar.DAY_OF_MONTH,need.get(Calendar.DAY_OF_MONTH));
                startTime.set(Calendar.HOUR_OF_DAY,Integer.valueOf(tmp[0]));
                startTime.set(Calendar.MINUTE,Integer.valueOf(tmp[1]));
                startTime.set(Calendar.SECOND,0);
                startTime.set(Calendar.MILLISECOND, 0);//毫秒清零

                return startTime;
            }
            //剩下的就是当天的了。
            String tmp[] = start.split(":");
            Calendar startTime = Calendar.getInstance();
            startTime.set(Calendar.YEAR,need.get(Calendar.YEAR));
            startTime.set(Calendar.MONTH,need.get(Calendar.MONTH));
            startTime.set(Calendar.DAY_OF_MONTH,need.get(Calendar.DAY_OF_MONTH));
            startTime.set(Calendar.HOUR_OF_DAY,Integer.valueOf(tmp[0]));
            startTime.set(Calendar.MINUTE,Integer.valueOf(tmp[1]));
            startTime.set(Calendar.SECOND,0);
            startTime.set(Calendar.MILLISECOND, 0);//毫秒清零




            if(list.size()==0){
                return startTime;
            }


            List<MyTime> times = ( List<MyTime> )list.get(0).get("timecomb");
            Calendar first=null,last=null;
            if(times==null){
                return startTime;
            }
            for(int i=0;i<times.size();i++){
                if(first==null || first.after(times.get(i).getStart())){
                    first = times.get(i).getStart();
                }
                if(last==null || last.before(times.get(i).getEnd())){
                    last = times.get(i).getEnd();
                }
            }




            if(first==null || last==null || !sdf.format(need.getTime()).equals(sdf.format(first.getTime()))){//如果今天已经打过一次卡。说明肯定不用提醒第一次了
                return startTime;
            }

            Calendar e2 = Calendar.getInstance();
            e2.set(need.get(Calendar.YEAR), need.get(Calendar.MONTH), need.get(Calendar.DAY_OF_MONTH), 17, 30, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
            e2.set(Calendar.MILLISECOND, 0);//毫秒清零

            if(last.before(e2)){
                switch (endType){
                    case "0":{ //到点提醒：
                        tmp = end.split(":");
                        Calendar endTime = Calendar.getInstance();
                        endTime.set(Calendar.YEAR,need.get(Calendar.YEAR));
                        endTime.set(Calendar.MONTH,need.get(Calendar.MONTH));
                        endTime.set(Calendar.DAY_OF_MONTH,need.get(Calendar.DAY_OF_MONTH));
                        endTime.set(Calendar.HOUR_OF_DAY,Integer.valueOf(tmp[0]));
                        endTime.set(Calendar.MINUTE,Integer.valueOf(tmp[1]));
                        endTime.set(Calendar.SECOND,0);
                        endTime.set(Calendar.MILLISECOND, 0);//毫秒清零

                        if(mhType.equals("0") || mhType.equals("1")){//正常为 17：30打卡

                                return endTime;

                        }else if(mhType.equals("2")){//18：00打卡
                            e2.add(Calendar.MINUTE,30);

                                return endTime;

                        }

                    }break;
                    case "1":{

                    }break;
                    case "2":{

                    }break;
                }

            }
            need.add(Calendar.DAY_OF_MONTH,1);
            daytype = workdayList.get(sdf.format(need.getTime()));
            while(daytype==null || daytype==3){
                need.add(Calendar.DAY_OF_MONTH,1);
                daytype = workdayList.get(sdf.format(need.getTime()));
            }
            startTime.set(Calendar.YEAR,need.get(Calendar.YEAR));
            startTime.set(Calendar.MONTH,need.get(Calendar.MONTH));
            startTime.set(Calendar.DAY_OF_MONTH,need.get(Calendar.DAY_OF_MONTH));
            startTime.set(Calendar.HOUR_OF_DAY,Integer.valueOf(tmp[0]));
            startTime.set(Calendar.MINUTE,Integer.valueOf(tmp[1]));
            startTime.set(Calendar.SECOND,0);
            startTime.set(Calendar.MILLISECOND, 0);//毫秒清零

            return startTime;



        }else{
            return null;
        }

    }
}

package com.example.guoyiwei.dk.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.amap.api.location.DPoint;
import com.example.guoyiwei.dk.ClockAlarmActivity;
import com.example.guoyiwei.dk.providers.LocationProvider;
import com.example.guoyiwei.dk.MainActivity;
import com.example.guoyiwei.dk.R;
import com.example.guoyiwei.dk.util.JSONUtil;
import com.example.guoyiwei.dk.util.LogUtil;

import org.codehaus.jackson.type.TypeReference;

import java.util.Calendar;
import java.util.List;

/**
 * Created by guoyi on 2017/9/9.
 */

public class ForeService extends Service {

    private final int FORESERVICE_PID = android.os.Process.myPid();
    private AssistServiceConnection mConnection;

    public static  Calendar lastTime = Calendar.getInstance();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public static Thread thread=null;
    private static ScreenBroadcastReceiver mScreenReceiver=null;

    public static LocationProvider locationProvider=null;
    @Override
    public void onCreate() {
        super.onCreate();

        try{
            PreferencesService service =new PreferencesService(getApplicationContext());
            /**
             * 之前的额前台service（会显示通知栏）
             */
/*        //定义一个notification
        Notification.Builder builder1 = new Notification.Builder(this);
        builder1.setSmallIcon(R.mipmap.ic_launcher); //设置图标
//        builder1.setTicker("新消息");
        builder1.setContentTitle("My title"); //设置标题
        builder1.setContentText("My content"); //消息内容
//        builder1.setContentInfo("");//补充内容
//        builder1.setWhen(System.currentTimeMillis()); //发送时间
//        builder1.setDefaults(Notification.DEFAULT_ALL); //设置默认的提示音，振动方式，灯光
//        builder1.setAutoCancel(true);//打开程序后图标消失
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder1.setContentIntent(pendingIntent);
        Notification notification1 = builder1.build();
        //把该service创建为前台service
        startForeground(1, notification1);*/

            setForeground();
            if(thread==null){
                LogUtil.addLog(this,"ForeService","已重启服务");
                ClockAlarmActivity.updateAlarm(getApplicationContext());
                synchronized (ForeService.class){
                    thread = new Thread(networkTask);
                    thread.start();
                }

            }else{

            }
            if(mScreenReceiver==null){
                synchronized (ForeService.class){
                    mScreenReceiver = new ScreenBroadcastReceiver();
                    startScreenBroadcastReceiver(getApplicationContext());
                }
            }

            String fenceswitch =service.getValue("fenceswitch");
            if(locationProvider==null){
                locationProvider = LocationProvider.getDefaultProvider(getApplicationContext());


                String res =service.getValue("fence");
                try{
                    List<List<DPoint>> allFence = (List<List<DPoint>>) JSONUtil.toObject(res,new   TypeReference<List<List<DPoint>>>() {});
                    locationProvider.allFence = allFence;

                }catch(Exception es){

                }
                if(fenceswitch.equals("1")){
                    locationProvider.startLocation();
                }

            }
//        else{
//            if(fenceswitch.equals("0")){
//                if(locationProvider!=null){
//                    locationProvider.stopLocation();
//                    locationProvider.destroyLocation();
//                }
//            }
//
//        }
        }catch (Exception es){
            LogUtil.addLog(getApplicationContext(),"ForeService",es.getMessage());
        }





    }


    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            // TODO
            try {
                while (true) {
                    try {
                        if (judge(getApplicationContext())) {
                            LogUtil.addLog(getApplicationContext(), "ForeService", "执行打卡提醒");

                        } else {
                            LogUtil.addLog(getApplicationContext(), "ForeService", "轮寻中");
                        }

                        lastTime = Calendar.getInstance();


                        //String listURL = "https://raw.githubusercontent.com/guoyiwei111/punchreminder/master/version.txt";
                        //String res = HttpsProvider.httpsGet(listURL);


                        runJobscheduler(getApplicationContext());


                        Thread.sleep(2 * 60 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            } catch (Exception es){
            LogUtil.addLog(getApplicationContext(),"ForeService",es.getMessage());
        }




            //Message msg = new Message();
            //Bundle data = new Bundle();
            //data.putString("value", "请求结果");
            //msg.setData(data);
            //handler.sendMessage(msg);
        }
    };

    private void startScreenBroadcastReceiver(Context mContext) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        mContext.registerReceiver(mScreenReceiver, filter);
    }

    private class ScreenBroadcastReceiver  extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.addLog(getApplicationContext(),"ForeService","通过屏幕唤醒判断");
            judge(getApplicationContext());
        }
    }

    public static void runJobscheduler(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler mJobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            JobInfo.Builder builder = new JobInfo.Builder(1,
                    new ComponentName(context.getPackageName(), JobSchedulerService.class.getName()));

            builder.setPeriodic(2*60 * 1000); //每隔60秒运行一次

            builder.setRequiresCharging(true);
            builder.setPersisted(true);  //设置设备重启后，是否重新执行任务
            builder.setRequiresDeviceIdle(true);

            if (mJobScheduler.schedule(builder.build()) <= 0) {
                //If something goes wrong
                Toast.makeText( context,
                        "启动Job Schedule 失败...", Toast.LENGTH_SHORT )
                        .show();
            }
        }
    }
    private static Calendar lastJudgeTime = null;
    public static boolean judge(Context context){
        PreferencesService service =new PreferencesService(context);
        synchronized (ForeService.class){
            String change = service.getValue("change");
            if(change.length()>3){
               // LogUtil.addLog(context,"Judge","TestTime Enter:"+);
                Calendar calendar=Calendar.getInstance();
                calendar.setTimeInMillis(Long.valueOf(change) );
                LogUtil.addLog(context,"Judge","TestTime Need:"+ calendar.getTime());
                Calendar now = Calendar.getInstance();

                if(now.after(calendar) ){

                    if((now.get(Calendar.HOUR_OF_DAY)<22)){
                        service.setValue("change","0");
                        String timeStr = service.getValue("pack");
                        String comp = service.getValue("component");
                        if(!timeStr.equals("")){
                            if(!comp.equals("")){
                                Intent intent = new Intent(Intent. ACTION_MAIN) ;
                                intent.setFlags(Intent. FLAG_ACTIVITY_NEW_TASK ) ;
                                intent.addCategory(Intent. CATEGORY_LAUNCHER );


                                // 设置 ComponentName参数 1:packagename 参数2:MainActivity 路径
                                intent.setClassName(timeStr, comp);
                                // 设置 ComponentName参数 1:packagename 参数2:MainActivity 路径
                                //ComponentName cn = new ComponentName(timeStr , comp) ;
                                //intent.setComponent(cn) ;
                                context.startActivity(intent) ;
                            }else{
                                Intent targetapp = context.getPackageManager().getLaunchIntentForPackage(timeStr);
                                context.startActivity(targetapp);
                            }
                        }

                    }else{
                        service.setValue("change","0");
                    }


                    LogUtil.addLog(context,"Judge","TestTime");
                }


            }
        }

        if(lastJudgeTime==null){
            lastJudgeTime=Calendar.getInstance();
        }else{
            if(Calendar.getInstance().getTimeInMillis() - lastJudgeTime.getTimeInMillis() < 1*1000){
                lastJudgeTime = Calendar.getInstance();
                return false;
            }else{
                lastJudgeTime = Calendar.getInstance();
            }
        }

        synchronized (ForeService.class){
            //LogUtil.addLog(context,"FenceProvider oreService","judge");

            String as =service.getValue("alarmswitch");
            if(!as.equals("1")){
                return false;
            }



            boolean debug = false;
            String need =service.getValue("alarmtime");
            String fs =service.getValue("fenceswitch");
            if(need!=null && !need.equals("") && !need.equals("0")){
                if((Calendar.getInstance().getTimeInMillis() - Long.valueOf(need) > 2*60*1000) || debug ){
                    LogUtil.addLog(context,"ForeService","Alarm 故障，Other拉起！");
                    sendAlarmBroadcast(context);

                    return true;
                }else if(fs.equals("1")){
                    Calendar calendar=Calendar.getInstance();
                    calendar.setTimeInMillis(Long.valueOf(need) );
                    Calendar now = Calendar.getInstance();
                    //boolean sss = locationProvider.isInFence();
                    if(calendar.get(Calendar.HOUR_OF_DAY)<12){//早晨的提醒。晚上不知道咋写。。。
                        if( now.before(calendar) ){
                            calendar.add(Calendar.HOUR_OF_DAY,-1);
                            if(now.after(calendar)){
                                if(locationProvider!=null){
                                    if(locationProvider.isInFence()){
                                        LogUtil.addLog(context,"ForeService","地理围栏拉起闹钟！");
                                        sendAlarmBroadcast(context);
                                        return  true;
                                    }else{
                                        LogUtil.addLog(context,"ForeService","地理围栏：不在围栏内");
                                    }
                                }else{

                                }


                            }

                        }
                    }


                }

            }

            return false;
        }

    }
    public static void sendAlarmBroadcast(Context context){

//        Intent intent = new Intent(ALARM_ACTION);
//        intent.putExtra("intervalMillis", 0);
//        intent.putExtra("msg", "该打卡了！");
//        intent.putExtra("id", 0);
//        intent.putExtra("soundOrVibrator", 2);

        Intent clockIntent = new Intent(context, ClockAlarmActivity.class);
        clockIntent.putExtra("msg","该打卡了！");
        clockIntent.putExtra("flag", 0);

        clockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(clockIntent);

        //test
        //context.sendBroadcast(intent);
    }

    public static final String ALARM_ACTION = "com.loonggg.alarm.clock";
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void setForeground() {
        //如果sdk<18 , 直接调用startForeground即可,不会在通知栏创建通知
        if (Build.VERSION.SDK_INT < 18) {
            this.startForeground(FORESERVICE_PID, getNotification());
            return;
        }

        if (null == mConnection) {
            mConnection = new AssistServiceConnection();
        }

        this.bindService(new Intent(this, AssistService.class), mConnection,
                Service.BIND_AUTO_CREATE);
    }

    public Notification getNotification() {
        //定义一个notification
        Notification.Builder builder1 = new Notification.Builder(this);
        builder1.setSmallIcon(R.mipmap.ic_launcher); //设置图标
//        builder1.setTicker("新消息");
        builder1.setContentTitle("打卡住手保活"); //设置标题
        builder1.setContentText("关闭后可能就木有办法正常提醒了"); //消息内容
//        builder1.setContentInfo("");//补充内容
//        builder1.setWhen(System.currentTimeMillis()); //发送时间
//        builder1.setDefaults(Notification.DEFAULT_ALL); //设置默认的提示音，振动方式，灯光
//        builder1.setAutoCancel(true);//打开程序后图标消失
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder1.setContentIntent(pendingIntent);
        Notification notification1 = builder1.build();
        return notification1;
    }


    private class AssistServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            // sdk >=18 的，会在通知栏显示service正在运行，这里不要让用户感知，所以这里的实现方式是利用2个同进程的service，利用相同的notificationID，
            // 2个service分别startForeground，然后只在1个service里stopForeground，这样即可去掉通知栏的显示
            Service assistService = ((AssistService.LocalBinder) binder).getService();
            ForeService.this.startForeground(FORESERVICE_PID, getNotification());
            assistService.startForeground(FORESERVICE_PID, getNotification());
            assistService.stopForeground(true);

            ForeService.this.unbindService(mConnection);
            mConnection = null;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationProvider!=null){
            locationProvider.stopLocation();
            locationProvider.destroyLocation();
        }
    }
}
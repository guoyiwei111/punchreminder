package com.example.guoyiwei.dk.services;

/**
 * Created by guoyiwei on 2017/6/12.
 */

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.example.guoyiwei.dk.util.LogUtil;

import java.util.Calendar;

/**
 * Created by guoyiwei on 2017/6/11.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService {
    public static final String ALARM_ACTION = "com.loonggg.alarm.clock";
    private Handler mJobHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage( Message msgs ) {
           // Toast.makeText( getApplicationContext(), "JobService task running", Toast.LENGTH_SHORT ).show();
            //TODO:
            /*Intent intent= new Intent(ALARM_ACTION);
            String msg = intent.getStringExtra("msg");
            long intervalMillis = intent.getLongExtra("intervalMillis", 0);
            if (intervalMillis != 0) {
                AlarmManagerUtil.setAlarmTime(getApplicationContext(), System.currentTimeMillis() + intervalMillis,
                        intent);
            }
            int flag = intent.getIntExtra("soundOrVibrator", 0);
            Intent clockIntent = new Intent(getApplicationContext(), ClockAlarmActivity.class);
            clockIntent.putExtra("msg", msg);
            clockIntent.putExtra("flag", flag);
            clockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(clockIntent);*/


            LogUtil.addLog(getApplicationContext(),"JobScheduler","被激活");

            ForeService.judge(getApplicationContext());

            if(ForeService.thread==null){
                getApplicationContext().startService(new Intent(getApplicationContext(), ForeService.class));
            }else if(ForeService.locationProvider!=null){
                PreferencesService service = new PreferencesService(getApplicationContext());
                String fenceswitch =service.getValue("fenceswitch");
                if(fenceswitch.equals("1")){
                    if(ForeService.locationProvider.storelocation!=null){
                        try{
                            if(Calendar.getInstance().getTimeInMillis() - ForeService.locationProvider.storelocation.getTime() > 60*60*1000){
                                ForeService.locationProvider.startLocation();
                            }
                        }catch (Exception es){

                        }

                    }

                }

            }


            /*
            try {
                getApplicationContext().startService(new Intent(getApplicationContext(), ForeService.class));
            }catch (Exception e2){
                e2.printStackTrace();
            }*/

            jobFinished( (JobParameters) msgs.obj, false );
            return true;
        }

    } );


    @Override
    public boolean onStartJob(JobParameters params) {
        mJobHandler.sendMessage( Message.obtain( mJobHandler, 1, params ) );
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        return false;
    }
}
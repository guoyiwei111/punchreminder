package com.example.guoyiwei.dk.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;

import com.example.guoyiwei.dk.ClockAlarmActivity;
import com.example.guoyiwei.dk.util.AlarmManagerUtil;

import java.util.Calendar;


/**
 * Created by loongggdroid on 2016/3/21.
 */
public class LoongggAlarmReceiver extends BroadcastReceiver {
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    public static Calendar lastStart = null;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        if(lastStart==null){
            lastStart = Calendar.getInstance();
        }else{
            Calendar now = Calendar.getInstance();
            if(now.getTimeInMillis() - lastStart.getTimeInMillis() < 5*60*1000){
                return;
            }else{
                synchronized (lastStart){
                    lastStart = now;
                }
            }
        }
        String msg = intent.getStringExtra("msg");
//        long intervalMillis = intent.getLongExtra("intervalMillis", 0L);
//        if (intervalMillis != 0) {
//            AlarmManagerUtil.setAlarmTime(context, System.currentTimeMillis() + intervalMillis,
//                    intent);
//        }
        int flag = intent.getIntExtra("soundOrVibrator", 0);
        Intent clockIntent = new Intent(context, ClockAlarmActivity.class);
        clockIntent.putExtra("msg", msg);
        clockIntent.putExtra("flag", flag);
        clockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(clockIntent);
    }


}

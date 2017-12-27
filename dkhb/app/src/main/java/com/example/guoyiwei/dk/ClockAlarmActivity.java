package com.example.guoyiwei.dk;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;


import com.example.guoyiwei.dk.model.SimpleDialog;
import com.example.guoyiwei.dk.providers.IOStoreProvider;
import com.example.guoyiwei.dk.services.ForeService;
import com.example.guoyiwei.dk.services.PreferencesService;
import com.example.guoyiwei.dk.util.AlarmUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static android.media.AudioManager.STREAM_MUSIC;


public class ClockAlarmActivity extends Activity {
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private static PreferencesService service;

    Thread aaa ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        needstop = false;
        service = new PreferencesService(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_alarm);
        String message = this.getIntent().getStringExtra("msg");
        int flag = this.getIntent().getIntExtra("flag", 0);
        showDialogInBroadcastReceiver(message, flag);
        IOStoreProvider.Init(getApplicationContext());
        try{
            AudioManager audioManager = (AudioManager)getApplicationContext().getSystemService(AUDIO_SERVICE);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int current = audioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
            if(current==0){
                audioManager.setStreamVolume(STREAM_MUSIC, maxVolume/3, 0);

            }
        }catch (Exception es){

        }

//        aaa = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    while(true){
//                        if(needstop){
//                            break;
//                        }
//                        Thread.sleep(1000);
//                        ForeService.sendAlarmBroadcast(getApplicationContext());
//
//
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        aaa.start();
    }
    boolean needstop = false;

    private void showDialogInBroadcastReceiver(String message, final int flag) {
        if (flag == 1 || flag == 2) {
            mediaPlayer = MediaPlayer.create(this, R.raw.in_call_alarm);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
        //数组参数意义：第一个参数为等待指定时间后开始震动，震动时间为第二个参数。后边的参数依次为等待震动和震动的时间
        //第二个参数为重复次数，-1为不重复，0为一直震动
        if (flag == 0 || flag == 2) {
            vibrator = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
            vibrator.vibrate(new long[]{100, 10, 100, 600}, 0);
        }


        PreferencesService service =new PreferencesService(getApplicationContext());
        String e =service.getValue("log");
        if(e==null) e="";
        e = "弹出打卡界面："+Calendar.getInstance().getTime()+"(Back)\r\n"+e;
        service.setValue("log",e);


        service.setValue("alarmtime","0");



        final SimpleDialog dialog = new SimpleDialog(this, R.style.Theme_dialog);
        dialog.show();
        dialog.setTitle("打卡提醒");
        dialog.setMessage(message);
        dialog.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog.bt_confirm == v ) {
                    if (flag == 1 || flag == 2) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                    if (flag == 0 || flag == 2) {
                        vibrator.cancel();
                    }

                    if(dialog.bt_confirm == v ){

                        try {
                            IOStoreProvider.getPunchList();
                            IOStoreProvider.punchCard();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        SharedPreferences preferences = getApplicationContext().getSharedPreferences("dkgyw", Context.MODE_PRIVATE);
                        String timeStr = preferences.getString("pack", "");
                        String comp = preferences.getString("component", "");
                        if(!timeStr.equals("")){
                            if(!comp.equals("")){
                                Intent intent = new Intent(Intent.ACTION_MAIN) ;
                                intent.setFlags(Intent. FLAG_ACTIVITY_NEW_TASK ) ;
                                intent.addCategory(Intent. CATEGORY_LAUNCHER );

                                intent.setClassName(timeStr, comp);
                                // 设置 ComponentName参数 1:packagename 参数2:MainActivity 路径
                                //ComponentName cn = new ComponentName(timeStr , comp) ;
                                //intent.setComponent(cn) ;
                                startActivity(intent) ;
                            }else{
                                Intent targetapp = getPackageManager().getLaunchIntentForPackage(timeStr);
                                startActivity(targetapp);
                            }
                        }

                    }

                }
                updateAlarm(getApplicationContext());
                needstop = true;
                dialog.dismiss();
                finish();
            }
        });


    }

    public static void  updateAlarm(Context context){
        List<Map<String, Object>>  listems = new ArrayList<Map<String, Object>>();
        try{
            System.out.println("____________________________________>>>");
            List<Map<String, Object>> lists = IOStoreProvider.getFinalList(listems);
            Map<String,Integer> workday = IOStoreProvider.getWorkdayList();
            Calendar need = AlarmUtil.getLastCalendar(service,lists, workday,context);
            System.out.println("____________________________________>>>"+need.getTime());
            if(need !=null){
                //need = Calendar.getInstance();
                //need.add(Calendar.MINUTE,1);
                AlarmUtil.setAlarm(context,need);
            }



        }catch (Exception es){
            System.out.println(es);

        }
    }
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_MENU ) {
//            //TODO something
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}

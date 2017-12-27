package com.example.guoyiwei.dk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.guoyiwei.dk.providers.IOStoreProvider;
import com.example.guoyiwei.dk.services.PreferencesService;
import com.example.guoyiwei.dk.util.AlarmUtil;
import com.example.guoyiwei.dk.util.LogUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by guoyiwei on 2017/3/6.
 */
public class DakaActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.daka);

        service = new PreferencesService(this);






        try {
            IOStoreProvider.Init(this);
            IOStoreProvider.getPunchList();
            IOStoreProvider.punchCard();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Toast.makeText(Daka.this, "已添加", Toast.LENGTH_SHORT).show();


        SharedPreferences preferences = getApplicationContext().getSharedPreferences("dkgyw", Context.MODE_PRIVATE);
        String timeStr = preferences.getString("pack", "");
        String comp = preferences.getString("component", "");
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
                startActivity(intent) ;
            }else{
                Intent targetapp = this.getPackageManager().getLaunchIntentForPackage(timeStr);
                startActivity(targetapp);
            }



        }

        List<Map<String, Object>>  listems = new ArrayList<Map<String, Object>>();
        try{
            Calendar need = AlarmUtil.getLastCalendar(service,IOStoreProvider.getFinalList(listems), IOStoreProvider.getWorkdayList(),this);
            if(need !=null){
                //need = Calendar.getInstance();
                //need.add(Calendar.MINUTE,1);
                AlarmUtil.setAlarm(DakaActivity.this,getApplicationContext(),need);
            }



        }catch (Exception es){


        }

        LogUtil.addLog(this,"Daka","通过快捷方式打卡");

        finish();



        //CloseActivityClass.exitClient(Daka.this);
    }
    private static PreferencesService service;
    @Override
    protected void onResume(){
        super.onResume();

        //if(MainActivity.mainactivity==null){
        //    Intent intent = new Intent("com.example.guoyiwei.dk.MainActivity");
        //    //intent.setClass(Daka.this, MainActivity.class);
        //    intent.setFlags(Intent. FLAG_ACTIVITY_NEW_TASK ) ;
        //    intent.addCategory(Intent. CATEGORY_LAUNCHER );
        //    startService(intent);
        //}else{
        //    finish();
        //    moveTaskToBack(true);
        //}
    }
}

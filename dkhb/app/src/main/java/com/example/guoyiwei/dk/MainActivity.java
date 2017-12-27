package com.example.guoyiwei.dk;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.example.guoyiwei.dk.model.LeaveDialog;
import com.example.guoyiwei.dk.providers.CloseActivityClass;
import com.example.guoyiwei.dk.providers.HttpsProvider;
import com.example.guoyiwei.dk.providers.IOStoreProvider;
import com.example.guoyiwei.dk.services.ForeService;
import com.example.guoyiwei.dk.services.PreferencesService;
import com.example.guoyiwei.dk.services.DkNotificationListenerService;
import com.example.guoyiwei.dk.model.DkInfo;
import com.example.guoyiwei.dk.model.MyTime;
import com.example.guoyiwei.dk.util.AlarmUtil;
import com.example.guoyiwei.dk.util.LogUtil;
import com.example.guoyiwei.dk.util.TimeCombineUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static PreferencesService service;
    int num = 100;

    private static SimpleAdapter listAdapter = null;
    public static List<Map<String, Object>> listems = new ArrayList<Map<String, Object>>();

    public static Activity mainactivity;

    private Handler listUpdateHandler = null;
    Runnable   runnableUi=new  Runnable(){
        @Override
        public void run() {
            //更新界面
            updateList();

        }

    };

    public static final String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        try{
            //TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
            //String id = tm.getDeviceId();
            //Toast.makeText(MainActivity.this, "IMEI:"+id, Toast.LENGTH_SHORT).show();

            //System.out.println("MMM---"+id);
            IOStoreProvider.Init(getApplicationContext());
            CloseActivityClass.activityList.add(this);
            setContentView(R.layout.activity_main);
            listUpdateHandler = new Handler();
            //addShortcut();
            service = new PreferencesService(this);
            mainactivity= this;
            /*{
                String mmk = "{\"2017-08-04\":[{\"mhType\":0,\"time\":1501807380000},{\"mhType\":0,\"time\":1501840740000}],\"2017-08-10\":[{\"mhType\":0,\"time\":1502323200000},{\"mhType\":0,\"time\":1502357400000}],\"2017-08-14\":[{\"mhType\":0,\"time\":1502671020000},{\"mhType\":0,\"time\":1502703420000}],\"2017-08-03\":[{\"mhType\":0,\"time\":1501721340000},{\"mhType\":0,\"time\":1501753320000}],\"2017-08-15\":[{\"mhType\":0,\"time\":1502757648169},{\"mhType\":0,\"time\":1502776853234},{\"mhType\":0,\"time\":1502790090326},{\"mhType\":0,\"time\":1502809865358}],\"2017-08-08\":[{\"mhType\":0,\"time\":1502153100000},{\"mhType\":0,\"time\":1502193420000}],\"2017-08-07\":[{\"mhType\":0,\"time\":1502066820000},{\"mhType\":0,\"time\":1502100240000}],\"2017-08-11\":[{\"mhType\":0,\"time\":1502412714158},{\"mhType\":0,\"time\":1502445972928}],\"2017-08-01\":[{\"mhType\":0,\"time\":1501548180000},{\"mhType\":0,\"time\":1501581240000}],\"2017-08-09\":[{\"mhType\":0,\"time\":1502239709283},{\"mhType\":0,\"time\":1502252802044},{\"mhType\":0,\"time\":1502257673025},{\"mhType\":0,\"time\":1502271359521}],\"2017-08-02\":[{\"mhType\":0,\"time\":1501634820000},{\"mhType\":0,\"time\":1501673160000}]}";
                service.setValue("dklist",mmk);

            }*/





            Button eee = (Button)findViewById(R.id.button5);
            eee.setOnClickListener(new View.OnClickListener(){//创建监听
                public void onClick(View v) {
                    //Toast.makeText(MainActivity.this, "已添加", Toast.LENGTH_SHORT).show();
                    //String strTmp = "点击Button01";
                    //Toast.makeText(MainActivity.this, "发送！", Toast.LENGTH_SHORT).show();
                    addNew();
                    updateList();

                    PreferencesService service = new PreferencesService(getApplicationContext());
                    //SharedPreferences preferences = getApplicationContext().getSharedPreferences("dkgyw", Context.MODE_PRIVATE);
                    String timeStr = service.getValue("pack");
                    String comp = service.getValue("component");
                    if(!timeStr.equals("")){
                        if(!comp.equals("")){
                            Intent intent = new Intent(Intent. ACTION_MAIN) ;
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

                    updateAlarm();
                    /*
                    List<Time> test = new ArrayList<Time>();

                    Time a = new Time(); a.parse("20101010T080033");test.add(a);
                    a = new Time(); a.parse("20101010T182033");test.add(a);
                    a = new Time(); a.parse("20101011T090033");test.add(a);
                    a = new Time(); a.parse("20101011T175533");test.add(a);


                    int rest = service.getTotalTime(test);

                    */



                    //Toast.makeText(MainActivity.this, all.get(all.size()-1).format2445(), Toast.LENGTH_SHORT).show();

                }

            });




            listAdapter = new SimpleAdapter(this, listems, R.layout.item,
                            new String[]{"day", "week","first","end","time","punchstatus","daytype"}, new
                            int[]{R.id.day, R.id.week,R.id.first,R.id.end,R.id.time,R.id.punchstatus,R.id.daytype});
                    ListView mfilelist = (ListView) findViewById(R.id.filelist);

                    mfilelist.setAdapter(listAdapter);
                    mfilelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            System.out.println("id--------------" + (i + 1));

                            Intent intent = new Intent();
                            intent.putExtra("mmm", i);
                    intent.setClass(MainActivity.this, MediaPlayerActivity.class);
                    MainActivity.this.startActivity(intent);

                }
            });






            /*
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.timg)
                    .setContentTitle("My notification")
                    .setContentText("Hello World!");

            NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(1, builder.build());
            //manager.cancel(notifyId);
            */
            //获取最新的workday；
            new Thread(networkTask).start();


            ListView dkList = (ListView)findViewById(R.id.filelist);
            registerForContextMenu(dkList);//为ListView添加上下文菜单

            try {
                getApplicationContext().startService(new Intent(getApplicationContext(), ForeService.class));
            }catch (Exception e2){
                e2.printStackTrace();
            }
            //isNotificationListenerServiceEnabled(this);
            if(isNotificationListenerEnabled(this)){
                ensureCollectorRunning();
            }else{
                openNotificationListenSettings();
            }


            Intent intent = getIntent();
            if (intent != null) {
                String tName = intent.getStringExtra("from");
                if (tName != null && tName.equals("yijian")) {
                    //Toast.makeText(this, "快捷键", Toast.LENGTH_LONG).show();
                    Intent intent2 = new Intent();
                    intent2.setClass(MainActivity.this, DakaActivity.class);
                    MainActivity.this.startActivity(intent2);
                    //updateList();
                    //updateAlarm();
                    //moveTaskToBack(true);

                }
            }
        } catch (Exception es) {
            LogUtil.addLog(getApplicationContext(), "MainActivity", es.getMessage());
        }

    }
    public boolean isNotificationListenerEnabled(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(this);
        String name = context.getPackageName();
        if (packageNames.contains(context.getPackageName())) {
            return true;
        }
        return false;
    }

    public void openNotificationListenSettings() {
        try {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {

                new AlertDialog.Builder(MainActivity.this).setTitle("提示")//设置对话框标题
                        .setMessage("为了使应用保活，请为本程序添加监听权限！")//设置显示的内容
                        .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮
                            @Override
                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                Intent intent;
                                intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                                startActivity(intent);

                            }
                        }).show();//在按键响应事件中显示此对话框

            } else {
                Intent intent;
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void ensureCollectorRunning() {
        ComponentName collectorComponent = new ComponentName(this, /*NotificationListenerService Inheritance*/ DkNotificationListenerService.class);
        //Log.v(TAG, "ensureCollectorRunning collectorComponent: " + collectorComponent);
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        boolean collectorRunning = false;
        List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);
        if (runningServices == null ) {
            //Log.w(TAG, "ensureCollectorRunning() runningServices is NULL");
            return;
        }
        for (ActivityManager.RunningServiceInfo service : runningServices) {
            if (service.service.equals(collectorComponent)) {
                //Log.w(TAG, "ensureCollectorRunning service - pid: " + service.pid + ", currentPID: " + Process.myPid() + ", clientPackage: " + service.clientPackage + ", clientCount: " + service.clientCount
                //        + ", clientLabel: " + ((service.clientLabel == 0) ? "0" : "(" + getResources().getString(service.clientLabel) + ")"));
                if (service.pid == android.os. Process.myPid() ) {
                    collectorRunning = true;
                }
            }
        }
        if (collectorRunning) {
            //Log.d(TAG, "ensureCollectorRunning: collector is running");
            return;
        }
        //Log.d(TAG, "ensureCollectorRunning: collector not running, reviving...");
        toggleNotificationListenerService();
    }
    private void toggleNotificationListenerService() {
        PackageManager pm = getPackageManager();
        ComponentName component = new ComponentName(this, DkNotificationListenerService.class);
        pm.setComponentEnabledSetting(component,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(component,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        // TODO Auto-generated method stub
        try {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            //JSONObject object = mData.getJSONObject(info.position);
            //menu.setHeaderTitle(object.getString("title"));
        } catch (Exception e) {
            return;
        }
        menu.add(0, 1, 0, "编辑");
        //menu.add(0, 2, 0, "删除");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        AdapterView.AdapterContextMenuInfo itemInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int index=adapterContextMenuInfo.position;
        //itemInfo.position;
        //JSONObject object = mData.getJSONObject(itemInfo.position);
        switch (item.getItemId()) {
            case 1:
                //Toast.makeText(this, object.getString("title"), Toast.LENGTH_LONG).show();



                Intent intent = new Intent();
                intent.putExtra("mmm", index);
                intent.setClass(MainActivity.this, MediaPlayerActivity.class);
                MainActivity.this.startActivity(intent);
                break;
            case 2:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setMessage("确认删除？");

                builder.setTitle("提示");

                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

                    @Override

                    public void onClick(DialogInterface dialog, int which) {

                        String need = (String) MainActivity.listems.get(Integer.valueOf(index)).get("date");
                        try {
                            IOStoreProvider.deleteDkBase(need);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        updateList();
                        dialog.dismiss();



                    }

                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override

                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }

                });

                builder.create().show();
                break;
            default:
                break;
        }

        return super.onContextItemSelected(item);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            //Log.i("mylog", "请求结果为-->" + val);
            // TODO
            // UI界面的更新等相关操作
            updateList();
        }
    };
    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            // TODO
            boolean debug = false;
            String listURL = "https://raw.githubusercontent.com/guoyiwei111/punchreminder/master/newcheck.txt";
            String res = HttpsProvider.httpsGet(listURL);
            String yuanver = service.getValue("workdayversion");
            if(res==null || res.length()<7){
                return ;
            }
            if(!res.substring(0,7).equals("<check>")){
                return ;
            }
            final String[] arry = res.substring(7).split("\\|");
            if(!yuanver.equals(arry[0])){
                String ver =  arry[0];
                service.setValue("workdayversion",ver);
                listURL = "https://raw.githubusercontent.com/guoyiwei111/punchreminder/master/worklist.txt";
                res = HttpsProvider.httpsGet(listURL);
                String yuan = "";
                if(res!=null && !res .isEmpty()){
                    try {
                        yuan = service.getValue("wdlist");
                        service.setValue("wdlist",res);
                        IOStoreProvider.workdayList=null;
                        listUpdateHandler.post(runnableUi);
                        //updateList();

                    } catch (Exception e) {
                        e.printStackTrace();
                        service.setValue("wdlist",yuan);
                    }
                }
            }else{
                if(Double.valueOf(arry[1])>4.0+0.001){
                    Looper.prepare();
                    AlertDialog.Builder normalDialog =
                            new AlertDialog.Builder(MainActivity.this);
                    //normalDialog.setIcon(R.drawable.icon_dialog);
                    normalDialog.setTitle("有新版本").setMessage("更新日志：\r\n"+ arry[2]);
                    normalDialog.setPositiveButton("升级",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Uri uri = Uri.parse("https://github.com/guoyiwei111/punchreminder/raw/master/"+arry[1]+".apk");
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                }
                            });
                    normalDialog.setNeutralButton("下次",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // ...To-do
                                    dialog.dismiss();
                                }
                            });
                    normalDialog.show();
                    Looper.loop();
                }
            }



            //Message msg = new Message();
            //Bundle data = new Bundle();
            //data.putString("value", "请求结果");
            //msg.setData(data);
            //handler.sendMessage(msg);
        }
    };


    private static final int SETTING_ITEM = Menu.FIRST+2;  //Menu.FIRST的值就是1
    private static final int SHORT_ITEM = Menu.FIRST;
    private static final int LEAVE_ITEM = Menu.FIRST+1;
    private static final int ABOUT_ITEM = Menu.FIRST+3;
    private static final int TEST_ITEM = Menu.FIRST+4;

    //重写onCreateOptionMenu(Menu menu)方法，当菜单第一次被加载时调用
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //填充选项菜单（读取XML文件、解析、加载到Menu组件上）
        // getMenuInflater().inflate(R.menu.main, menu);

        //通过代码的方式来添加Menu
        //添加菜单项（组ID，菜单项ID，排序，标题）

        menu.add(0, SHORT_ITEM, 100, "创建快捷方式");
        menu.add(0, LEAVE_ITEM, 100, "添加例外");
        menu.add(0, SETTING_ITEM, 100, "设置");
        menu.add(0, ABOUT_ITEM, 200, "关于");
        menu.add(0, TEST_ITEM, 200, "Test Alarm");


        return true;
    }

    //重写OptionsItemSelected(MenuItem item)来响应菜单项(MenuItem)的点击事件（根据id来区分是哪个item）
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case SHORT_ITEM:
                addShortcut();
                break;
            case SETTING_ITEM:{
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SettingActivity.class);
                MainActivity.this.startActivity(intent);
            }

            break;

            case ABOUT_ITEM:{
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, AboutActivity.class);
                MainActivity.this.startActivity(intent);
            }

            break;

            case LEAVE_ITEM:{
                int type = IOStoreProvider.getMhType();
                List<Calendar> res = getDefaultTime(type);
                new LeaveDialog(this).setTime(res.get(0),res.get(1),0).setCancelable(true).show();

            }

            break;

            case TEST_ITEM:{
                Calendar nn = Calendar.getInstance();
                new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute) {
                                // TODO Auto-generated method stub

                                try {
                                    Calendar nn = Calendar.getInstance();
                                    nn.set(Calendar.HOUR_OF_DAY,hour);
                                    nn.set(Calendar.MINUTE,minute);


                                    AlarmUtil.setAlarm(MainActivity.this,getApplicationContext(),nn);


                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        nn.get(Calendar.HOUR_OF_DAY),
                        nn.get(Calendar.MINUTE), true).show();

            }break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public static List<Calendar> getDefaultTime(int mhType){
        List res = new ArrayList<Calendar>();;
        switch(IOStoreProvider.getMhType()){
            case 0:{
                Calendar from = Calendar.getInstance();
                from.set(from.get(Calendar.YEAR), from.get(Calendar.MONTH), from.get(Calendar.DAY_OF_MONTH), 8, 0, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
                from.set(Calendar.MILLISECOND, 0);//毫秒清零

                Calendar to = Calendar.getInstance();
                to.set(to.get(Calendar.YEAR), to.get(Calendar.MONTH), to.get(Calendar.DAY_OF_MONTH), 17, 30, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
                to.set(Calendar.MILLISECOND, 0);//毫秒清零
                res.add(from);
                res.add(to);

            }break;
            case 1:{
                Calendar from = Calendar.getInstance();
                from.set(from.get(Calendar.YEAR), from.get(Calendar.MONTH), from.get(Calendar.DAY_OF_MONTH), 8, 0, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
                from.set(Calendar.MILLISECOND, 0);//毫秒清零

                Calendar to = Calendar.getInstance();
                to.set(to.get(Calendar.YEAR), to.get(Calendar.MONTH), to.get(Calendar.DAY_OF_MONTH), 17, 30, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
                to.set(Calendar.MILLISECOND, 0);//毫秒清零
                res.add(from);
                res.add(to);


            };break;
            case 2:{
                Calendar from = Calendar.getInstance();
                from.set(from.get(Calendar.YEAR), from.get(Calendar.MONTH), from.get(Calendar.DAY_OF_MONTH), 8, 30, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
                from.set(Calendar.MILLISECOND, 0);//毫秒清零

                Calendar to = Calendar.getInstance();
                to.set(to.get(Calendar.YEAR), to.get(Calendar.MONTH), to.get(Calendar.DAY_OF_MONTH), 18, 00, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
                to.set(Calendar.MILLISECOND, 0);//毫秒清零
                res.add(from);
                res.add(to);

            };break;
        }
        return res;
    }

    public void addNew() {

        try {
            IOStoreProvider.punchCard();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void updateList(){
        try{
            IOStoreProvider.getFinalList(listems);


        }catch (Exception es){
            System.out.println(es);
        }
        if(listAdapter!=null){
            listAdapter.notifyDataSetChanged();
        }




        TextView mm = (TextView)mainactivity.findViewById(R.id.tongji);




        String res = genText(listems);

        mm.setText(res);


    }
    public static Calendar lastAlarmTime;
    public static Calendar firstPunchTime;
    public static String genText(List<Map<String, Object>> listems){
        String res = "";
        lastAlarmTime =null;
        boolean tagfortoday = true;
        boolean tagforok= true;
        boolean tagfortodayok = true;
        int timesum = 0;
        int timenum =0;
        Calendar todayFirst = null;
        Calendar todayRec = null;
        for(int i=listems.size()-1;i>=0;i--){
            int time = (int)listems.get(i).get("timenum");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            todayFirst = (Calendar)listems.get(i).get("start");
            Integer dt=0;
            if(todayFirst!=null){
                String str = sdf.format(todayFirst.getTime());

                try{
                    dt = IOStoreProvider.getWorkdayList().get(str);
                    if(dt==null) dt = 3;
                    //todayRec = TimeCombineUtil.getEndTime(times,mhType,dt,timesum);
                }catch(Exception es){

                }
            }
            Calendar todaynow = Calendar.getInstance();
            String nowString = sdf.format(todaynow.getTime());

            if(i==0 ){


                if(todayFirst==null ){
                    tagfortoday = false;
                    tagfortodayok = false;
                }else{
                    firstPunchTime=todayFirst;
                    if(tagforok){

                        List<MyTime> times = ( List<MyTime> )listems.get(i).get("timecomb");
                        Integer mhType = (Integer)listems.get(i).get("mhtype");

                        todayRec = TimeCombineUtil.getEndTime(times,mhType,dt,timesum - timenum*8*60);
                        lastAlarmTime = todayRec;
                    }

                    if(time<=0){
                        tagfortodayok=false;
                    }else{
                        tagfortodayok = true;
                        if(dt==0 || dt==2){
                            timenum++;
                            timesum+=time;
                        }

                    }
                }

                String real = (String)listems.get(i).get("date");
                if(!nowString.equals(real)){
                    tagfortoday = false;
                    tagfortodayok = false;
                }
            }else{
                if(time<=0){
                    tagforok=false;
                }else{
                    if(dt==0 || dt==2){
                        timenum++;
                        timesum+=time;
                    }
                }
            }


        }

        SimpleDateFormat sdf2=new SimpleDateFormat("HH:mm");
        if(!tagforok){
            res+="打卡记录异常，请确认！";
        }else{
            if(tagfortoday && tagfortodayok==false){
                //res+="今日打卡异常\n历史打卡天数："+timenum+"\n历史打卡总时间："+ timesum/60 +"时"+timesum%60+"分\n";
                //res+="今日打卡异常\n";
            }else if(tagfortoday && tagfortodayok){
                // res+="今日打卡正常\n打卡天数："+timenum+"\n打卡总时间："+ timesum/60 +"时"+timesum%60+"分\n";
                // res+="今日打卡正常\n";
            }else{
                //res+="打卡天数："+timenum+"\n总时间："+ timesum/60 +"时"+timesum%60+"分\n";

            }
            if(timenum*8*60 >= timesum){
                res+="欠工时："+  (timenum*8*60 - timesum)/60 +"时"+(timenum*8*60 - timesum)%60+"分\n";
                if(tagfortoday && todayRec!=null){

                    if(tagfortodayok){

                        res+="建议打卡时间："+sdf2.format(todayRec.getTime());
                    }else{

                        res+="建议打卡时间："+sdf2.format(todayRec.getTime());
                    }


                }
            }else{
                res+="工时多出："+  (timesum-timenum*8*60  )/60 +"时"+(timesum-timenum*8*60 )%60+"分\n";
                if(tagfortoday && todayRec!=null){
                    if(!tagfortodayok){
                        res+="建议打卡时间："+sdf2.format(todayRec.getTime());
                    }
                }
            }

        }
        return res;
    }
    public static boolean yijian = false;
    @Override
    protected void onResume(){
        super.onResume();
        CloseActivityClass.exitClient(this);

        updateList();
        updateAlarm();
    }
    public void updateAlarm(){
        try{
            Calendar need = AlarmUtil.getLastCalendar(service,IOStoreProvider.getFinalList(listems), IOStoreProvider.getWorkdayList(),this);
            TextView naozhong = (TextView)findViewById(R.id.naozhong);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

            if(need !=null){
                naozhong.setText("闹钟："+ DkInfo.getWeekOfDate(need)+" " +sdf.format(need.getTime()));

                //need = Calendar.getInstance();
                //need.add(Calendar.MINUTE,1);
                AlarmUtil.setAlarm(MainActivity.this,getApplicationContext(),need);
            }else{
                naozhong.setText("闹钟："+  "未开启");
            }



        } catch (Exception es) {
            LogUtil.addLog(getApplicationContext(), "MainActivity", es.getMessage());
        }

    }

    private void addShortcut() {
        Intent addShortcutIntent = new Intent(ACTION_ADD_SHORTCUT);

        // 不允许重复创建
        addShortcutIntent.putExtra("duplicate", false);// 经测试不是根据快捷方式的名字判断重复的
        // 应该是根据快链的Intent来判断是否重复的,即Intent.EXTRA_SHORTCUT_INTENT字段的value
        // 但是名称不同时，虽然有的手机系统会显示Toast提示重复，仍然会建立快链
        // 屏幕上没有空间时会提示
        // 注意：重复创建的行为MIUI和三星手机上不太一样，小米上似乎不能重复创建快捷方式

        // 名字
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "一键打卡");

        // 图标
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(MainActivity.this,
                        R.drawable.timg));

        // 设置关联程序
        Intent launcherIntent = new Intent(Intent.ACTION_MAIN);
        launcherIntent.setClassName("com.example.guoyiwei.dk", "com.example.guoyiwei.dk.MainActivity");
        //launcherIntent.setClass(MainActivity.this, MainActivity.class);
        launcherIntent.putExtra("from", "yijian");
        //launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        launcherIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        addShortcutIntent
                .putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);

        // 发送广播
        sendBroadcast(addShortcutIntent);

        Toast.makeText(this, "添加成功", Toast.LENGTH_LONG).show();

    }





    static int REQUEST_IGNORE_BATTERY_CODE = 1001;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if (requestCode == REQUEST_IGNORE_BATTERY_CODE) {
                //Log.d("Hello World!","开启省电模式成功");
            }
        }else if (resultCode == RESULT_CANCELED) {
            if (requestCode == REQUEST_IGNORE_BATTERY_CODE) {
                Toast.makeText(this, "请用户开启忽略电池优化~", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //TODO something
        moveTaskToBack(true);

        //super.onBackPressed();
    }

}

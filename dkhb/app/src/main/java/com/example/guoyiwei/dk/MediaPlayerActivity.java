package com.example.guoyiwei.dk;


import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.guoyiwei.dk.model.DkBase;
import com.example.guoyiwei.dk.model.LeaveDialog;
import com.example.guoyiwei.dk.model.LeaveInfo;
import com.example.guoyiwei.dk.providers.IOStoreProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaPlayerActivity extends AppCompatActivity implements View.OnClickListener{

    private ViewPager viewPager;
    private ArrayList<View> pageview;
    private TextView videoLayout;
    private TextView musicLayout;
    // 滚动条图片
    private ImageView scrollbar;
    // 滚动条初始偏移量
    private int offset = 0;
    // 当前页编号
    private int currIndex = 0;
    // 滚动条宽度
    private int bmpW;
    //一倍滚动量
    private int one;


    private static SimpleAdapter punchAdapter = null;
    public static List<Map<String, Object>> punchitems = new ArrayList<Map<String, Object>>();
    private static SimpleAdapter leaveAdapter = null;
    public static List<Map<String, Object>> leaveitems = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        //查找布局文件用LayoutInflater.inflate
        LayoutInflater inflater =getLayoutInflater();
        View view1 = inflater.inflate(R.layout.punch_view, null);
        View view2 = inflater.inflate(R.layout.leave_view, null);
        videoLayout = (TextView)findViewById(R.id.videoLayout);
        musicLayout = (TextView)findViewById(R.id.musicLayout);
        scrollbar = (ImageView)findViewById(R.id.scrollbar);
        videoLayout.setOnClickListener(this);
        musicLayout.setOnClickListener(this);
        pageview =new ArrayList<View>();
        //添加想要切换的界面
        pageview.add(view1);
        pageview.add(view2);
        //数据适配器
        PagerAdapter mPagerAdapter = new PagerAdapter(){

            @Override
            //获取当前窗体界面数
            public int getCount() {
                // TODO Auto-generated method stub
                return pageview.size();
            }

            @Override
            //判断是否由对象生成界面
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0==arg1;
            }
            //使从ViewGroup中移出当前View
            public void destroyItem(View arg0, int arg1, Object arg2) {
                ((ViewPager) arg0).removeView(pageview.get(arg1));
            }

            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
            public Object instantiateItem(View arg0, int arg1){
                ((ViewPager)arg0).addView(pageview.get(arg1));
                return pageview.get(arg1);
            }
        };
        //绑定适配器
        viewPager.setAdapter(mPagerAdapter);
        //设置viewPager的初始界面为第一个界面
        viewPager.setCurrentItem(0);
        //添加切换界面的监听器
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
        // 获取滚动条的宽度
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.scrollbar).getWidth();
        //为了获取屏幕宽度，新建一个DisplayMetrics对象
        DisplayMetrics displayMetrics = new DisplayMetrics();
        //将当前窗口的一些信息放在DisplayMetrics类中
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //得到屏幕的宽度
        int screenW = displayMetrics.widthPixels;
        //计算出滚动条初始的偏移量
        offset = (screenW / 2 - bmpW) / 2;
        //计算出切换一个界面时，滚动条的位移量
        one = offset * 2 + bmpW;
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        //将滚动条的初始位置设置成与左边界间隔一个offset
        scrollbar.setImageMatrix(matrix);







        punchAdapter = new SimpleAdapter(this, punchitems, R.layout.punchitem,
                new String[]{"punchtime"}, new
                int[]{R.id.punchtime});

        ListView mpunchlist = (ListView) view1.findViewById(R.id.punchlist);

        mpunchlist.setAdapter(punchAdapter);
        mpunchlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("id--------------" + (i + 1));
                //mpunchlist.get(i).get("name");
            }
        });




        leaveAdapter = new SimpleAdapter(this, leaveitems, R.layout.leaveitem,
                new String[]{"leaveinfo","leavetypeinfo"}, new
                int[]{R.id.leaveinfo,R.id.leavetypeinfo});

        ListView mleavelist = (ListView) view2.findViewById(R.id.leavelist);

        mleavelist.setAdapter(leaveAdapter);
        mleavelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("id--------------" + (i + 1));
                //mpunchlist.get(i).get("name");
            }
        });




        Button addbt=(Button)findViewById(R.id.addpunch);
        addbt.setOnClickListener(new View.OnClickListener(){//创建监听
            public void onClick(View v) {
                int no = viewPager.getCurrentItem();
                if(no==0){
                    Calendar now = Calendar.getInstance();
                    new TimePickerDialog(MediaPlayerActivity.this,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hour, int minute) {
                                    // TODO Auto-generated method stub

                                    try {
                                        String[] ttt = dateStore.split("-");
                                        Calendar tmp = Calendar.getInstance();
                                        tmp.set(Integer.valueOf(ttt[0]),Integer.valueOf(ttt[1])-1,Integer.valueOf(ttt[2]),hour,minute,0);
                                        tmp.set(Calendar.MILLISECOND, 0);//毫秒清零

                                        IOStoreProvider.punchCard(tmp);
                                        IOStoreProvider.getFinalList(MainActivity.listems);

                                        updatePunchaLeave();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    //mHour = hour;
                                    //mMinute = minute;
                                    //更新EditText控件时间 小于10加0
                                    //timeEdit.setText(new StringBuilder()
                                    //       .append(mHour < 10 ? 0 + mHour : mHour).append(:)
                                    //.append(mMinute < 10 ? 0 + mMinute : mMinute).append(:00) );
                                }
                            },
                            now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE), true).show();
                }else{
                    String[] ttt = dateStore.split("-");

                    switch(IOStoreProvider.getMhType()){
                        case 0:{
                            Calendar from = Calendar.getInstance();
                            from.set(Integer.valueOf(ttt[0]),Integer.valueOf(ttt[1])-1,Integer.valueOf(ttt[2]), 8, 0, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
                            from.set(Calendar.MILLISECOND, 0);//毫秒清零

                            Calendar to = Calendar.getInstance();
                            to.set(Integer.valueOf(ttt[0]),Integer.valueOf(ttt[1])-1,Integer.valueOf(ttt[2]), 17, 30, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
                            to.set(Calendar.MILLISECOND, 0);//毫秒清零

                            LeaveDialog dia = new LeaveDialog(MediaPlayerActivity.this);
                            dia.setDataEditable(false);
                            dia.setTime(from,to,0).setCancelable(true).show();
                        }break;
                        case 1:{
                            Calendar from = Calendar.getInstance();
                            from.set(Integer.valueOf(ttt[0]),Integer.valueOf(ttt[1])-1,Integer.valueOf(ttt[2]), 8, 0, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
                            from.set(Calendar.MILLISECOND, 0);//毫秒清零

                            Calendar to = Calendar.getInstance();
                            to.set(Integer.valueOf(ttt[0]),Integer.valueOf(ttt[1])-1,Integer.valueOf(ttt[2]), 17, 30, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
                            to.set(Calendar.MILLISECOND, 0);//毫秒清零

                            LeaveDialog dia = new LeaveDialog(MediaPlayerActivity.this);
                            dia.setDataEditable(false);
                            dia.setTime(from,to,0).setCancelable(true).show();

                        };break;
                        case 2:{
                            Calendar from = Calendar.getInstance();
                            from.set(Integer.valueOf(ttt[0]),Integer.valueOf(ttt[1])-1,Integer.valueOf(ttt[2]), 8, 30, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
                            from.set(Calendar.MILLISECOND, 0);//毫秒清零

                            Calendar to = Calendar.getInstance();
                            to.set(Integer.valueOf(ttt[0]),Integer.valueOf(ttt[1])-1,Integer.valueOf(ttt[2]), 18, 00, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
                            to.set(Calendar.MILLISECOND, 0);//毫秒清零

                            LeaveDialog dia = new LeaveDialog(MediaPlayerActivity.this);
                            dia.setDataEditable(false);
                            dia.setTime(from,to,0).setCancelable(true).show();
                        };break;
                    }
                }

            }

        });


        registerForContextMenu(mpunchlist);//为ListView添加上下文菜单
        registerForContextMenu(mleavelist);//为ListView添加上下文菜单
    }
    public static void updatePunchaLeave(){
        if(index!=-1){
            dk = (List<DkBase>) MainActivity.listems.get(Integer.valueOf(index)).get("punch");
            lv = (List<LeaveInfo>) MainActivity.listems.get(Integer.valueOf(index)).get("leave");
        }
        punchitems.clear();
        if(dk!=null){
            for(int i=0;i<dk.size();i++){
                Map<String, Object> listem = new HashMap<String, Object>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                listem.put("punchtime",sdf.format(dk.get(i).getTime().getTime()) );
                punchitems.add(listem);
            }
        }

        leaveitems.clear();
        if(lv!=null){
            for(int i=0;i<lv.size();i++){
                Map<String, Object> listem = new HashMap<String, Object>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
                listem.put("leaveinfo",sdf.format(lv.get(i).getStartTime().getTime())+"-"+sdf2.format(lv.get(i).getEndTime().getTime()) );
                listem.put("leavetypeinfo",lv.get(i).getLeaveType()==0?"请假":"公干" );

                leaveitems.add(listem);
            }
        }




        punchAdapter.notifyDataSetChanged();
        leaveAdapter.notifyDataSetChanged();
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
        menu.add(0, 2, 0, "删除");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    public static Integer selectedItem = -1;
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        //AdapterView.AdapterContextMenuInfo itemInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        selectedItem =adapterContextMenuInfo.position;
        //itemInfo.position;
        //JSONObject object = mData.getJSONObject(itemInfo.position);
        switch (item.getItemId()) {
            case 1:
                //Toast.makeText(this, object.getString("title"), Toast.LENGTH_LONG).show();
                int in = viewPager.getCurrentItem();
                if(in==0){
                    Calendar calendar = dk.get(selectedItem).getTime();

                    new TimePickerDialog(MediaPlayerActivity.this,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hour, int minute) {
                                    // TODO Auto-generated method stub
                                    Calendar calendar = dk.get(selectedItem).getTime();
                                    calendar.set(Calendar.HOUR_OF_DAY,hour);
                                    calendar.set(Calendar.MINUTE,minute);
                                    try {
                                        List<DkBase> ne = IOStoreProvider.dkBaseList.get(dateStore);
                                        Collections.sort(ne);

                                        IOStoreProvider.savePunchList();
                                        updatePunchaLeave();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    //mHour = hour;
                                    //mMinute = minute;
                                    //更新EditText控件时间 小于10加0
                                    //timeEdit.setText(new StringBuilder()
                                    //       .append(mHour < 10 ? 0 + mHour : mHour).append(:)
                                    //.append(mMinute < 10 ? 0 + mMinute : mMinute).append(:00) );
                                }
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE), true).show();
                }else{
                    //TODO 写编辑请假的罗技
                    Calendar from = lv.get(selectedItem).getStartTime();
                    Calendar to = lv.get(selectedItem).getEndTime();
                    LeaveDialog dialog = new LeaveDialog(this);
                    dialog.setDataEditable(false);
                    dialog.setTime(from,to,lv.get(selectedItem).getLeaveType());
                    dialog.setEditObj(lv.get(selectedItem));
                    dialog.isAddTitle(false);
                    dialog.setCancelable(true);
                    dialog.show();

                }


                break;
            case 2:

                AlertDialog.Builder builder = new AlertDialog.Builder(MediaPlayerActivity.this);

                builder.setMessage("确认删除？");

                builder.setTitle("提示");

                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        int in = viewPager.getCurrentItem();

                        try {
                            if(in == 0){
                                IOStoreProvider.deleteDkBase(dateStore, selectedItem);
                            }else{
                                IOStoreProvider.deleteLvBase(dateStore,selectedItem);
                            }



                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        updatePunchaLeave();
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



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
                case 0:
                    /**
                     * TranslateAnimation的四个属性分别为
                     * float fromXDelta 动画开始的点离当前View X坐标上的差值
                     * float toXDelta 动画结束的点离当前View X坐标上的差值
                     * float fromYDelta 动画开始的点离当前View Y坐标上的差值
                     * float toYDelta 动画开始的点离当前View Y坐标上的差值
                     **/
                    animation = new TranslateAnimation(one, 0, 0, 0);
                    break;
                case 1:
                    animation = new TranslateAnimation(offset, one, 0, 0);
                    break;
            }
            //arg0为切换到的页的编码
            currIndex = arg0;
            // 将此属性设置为true可以使得图片停在动画结束时的位置
            animation.setFillAfter(true);
            //动画持续时间，单位为毫秒
            animation.setDuration(200);
            //滚动条开始动画
            scrollbar.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.videoLayout:
                //点击"视频“时切换到第一页
                viewPager.setCurrentItem(0);
                break;
            case R.id.musicLayout:
                //点击“音乐”时切换的第二页
                viewPager.setCurrentItem(1);
                break;
        }
    }
    public static List<DkBase> dk = null;
    public static List<LeaveInfo> lv = null;
    public static String dateStore = null;
    public static Integer  index =-1;
    protected void onResume(){
        super.onResume();

        index = getIntent().getIntExtra("mmm",-1);

        if(index!=-1){
            String date = (String) MainActivity.listems.get(Integer.valueOf(index)).get("date");
            dateStore = date;
            this.setTitle(date);
            updatePunchaLeave();
        }



    }

}
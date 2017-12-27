package com.example.guoyiwei.dk;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.amap.api.location.DPoint;
import com.example.guoyiwei.dk.services.ForeService;
import com.example.guoyiwei.dk.services.PreferencesService;
import com.example.guoyiwei.dk.util.AlarmUtil;
import com.example.guoyiwei.dk.util.JSONUtil;

import org.codehaus.jackson.type.TypeReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by guoyiwei on 2017/3/6.
 */
public class SettingActivity extends AppCompatActivity {

    private static PreferencesService service;
    private ArrayAdapter arr_adapter;

    //end time
    TextView et ;


    //start time
    TextView st ;
    EditText selectapp ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = new PreferencesService(this);
        setContentView(R.layout.setting);
        selectapp = (EditText)findViewById(R.id.selectAppText);
        selectapp.setOnClickListener(new View.OnClickListener(){//创建监听
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SettingActivity.this, AppSelectActivity.class);
                SettingActivity.this.startActivity(intent);
            }

        });

        Spinner sp = (Spinner) findViewById(R.id.spinner2);
        //数据
        List data_list = new ArrayList<String>();
        data_list.add("固定时间");
        //data_list.add("满8小时后");
        //data_list.add("不欠工时后");

        //适配器
        arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //加载适配器
        sp.setAdapter(arr_adapter);



        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if(position!=0){
                    LinearLayout ll = (LinearLayout)findViewById(R.id.endLayout);
                    ll.setVisibility(View.GONE);
                    LinearLayout l2 = (LinearLayout)findViewById(R.id.delaylayout);
                    l2.setVisibility(View.VISIBLE);
                }else{
                    LinearLayout ll = (LinearLayout)findViewById(R.id.endLayout);
                    ll.setVisibility(View.VISIBLE);
                    LinearLayout l2 = (LinearLayout)findViewById(R.id.delaylayout);
                    l2.setVisibility(View.GONE);
                }

                //Toast.makeText(Setting.this, "你已选择： "+ (position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        Switch as = (Switch) findViewById(R.id.alarmswitch);
        as.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                /*LinearLayout ll = (LinearLayout)findViewById(R.id.alarmLayout);
                if(isChecked){

                    ll.setVisibility(View.VISIBLE);
                }else{
                    ll.setVisibility(View.GONE);
                }*/
            }
        });




        Spinner bc = (Spinner) findViewById(R.id.bcspinner);
        //数据
        List data_list2 = new ArrayList<String>();
        data_list2.add("浮动班次 9:00-17:30");
        data_list2.add("固定班次 8:00-17:30");
        data_list2.add("固定班次 8:30-18:00");
        //适配器
        ArrayAdapter arr_adapter2= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list2);
        //设置样式
        arr_adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        bc.setAdapter(arr_adapter2);


        //end time
        et = (TextView) findViewById(R.id.endalarmtime);


        //start time
        st = (TextView) findViewById(R.id.firstalrmtime);


        st.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String res[] = st.getText().toString().split(":");
                new TimePickerDialog(SettingActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute) {
                                // TODO Auto-generated method stub

                                try {
                                    SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
                                    Calendar tmp = Calendar.getInstance();
                                    tmp.set(Calendar.HOUR_OF_DAY,hour);
                                    tmp.set(Calendar.MINUTE,minute);
                                    st.setText(sdf2.format(tmp.getTime()));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        Integer.valueOf(res[0]),
                        Integer.valueOf(res[1]), true).show();
            }
        });

        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String res[] = et.getText().toString().split(":");
                new TimePickerDialog(SettingActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute) {
                                // TODO Auto-generated method stub

                                try {
                                    SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
                                    Calendar tmp = Calendar.getInstance();
                                    tmp.set(Calendar.HOUR_OF_DAY,hour);
                                    tmp.set(Calendar.MINUTE,minute);
                                    et.setText(sdf2.format(tmp.getTime()));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        Integer.valueOf(res[0]),
                        Integer.valueOf(res[1]), true).show();
            }
        });

        Button del = (Button)findViewById(R.id.deletegl);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                service.setValue("app","");
                service.setValue("pack","");
                service.setValue("component","");
                selectapp.setText("");
            }
        });

        Button geofenceButton = (Button)findViewById(R.id.geofenceButton);
        geofenceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(SettingActivity.this, PolygonSelectActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected  void onPause(){


        //addShortcut();

        //end type
        Spinner sp = (Spinner) findViewById(R.id.spinner2);
        service.setValue("endtype",String.valueOf(sp.getSelectedItemId()));


        service.setValue("endtime",et.getText().toString());



        service.setValue("starttime",st.getText().toString());


        //alarm switch
        Switch as = (Switch) findViewById(R.id.alarmswitch);
        //as.isChecked();
        service.setValue("alarmswitch",as.isChecked()?"1":"0");
        //as.setChecked(service.getValue("alarmswitch").equals("1")?true:false);
        if(!as.isChecked()){
            AlarmUtil.removeClock(this);
        }


        Switch gs = (Switch)findViewById(R.id.geofencewitch);
        service.setValue("fenceswitch",gs.isChecked()?"1":"0");
        if(!gs.isChecked()){
            if(ForeService.locationProvider!=null){
                ForeService.locationProvider.stopLocation();
            }

        }else{
            if(ForeService.locationProvider!=null){
                try{
                    String res =service.getValue("fence");
                    List<List<DPoint>> allFence = (List<List<DPoint>>) JSONUtil.toObject(res,new   TypeReference<List<List<DPoint>>>() {});
                    ForeService.locationProvider.allFence = allFence;
                    ForeService.locationProvider.startLocation();
                }catch(Exception es){

                }

            }

        }

        //late time
        EditText dt = (EditText) findViewById(R.id.delayminite);
        service.setValue("delaytime",dt.getText().toString());

        Spinner bc = (Spinner) findViewById(R.id.bcspinner);
        service.setValue("mhtype",String.valueOf(bc.getSelectedItemId()));
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        //初始化关联应用
        EditText selectapp = (EditText)findViewById(R.id.selectAppText);
        selectapp.setText(service.getAppName());


        et.setText(service.getValue("endtime"));


        st.setText(service.getValue("starttime"));


        //alarm switch
        Switch as = (Switch) findViewById(R.id.alarmswitch);


        LinearLayout ll = (LinearLayout)findViewById(R.id.alarmLayout);

        if(service.getValue("alarmswitch").equals("1")){
            //ll.setVisibility(View.VISIBLE);
            as.setChecked(true);
        }else{
            //ll.setVisibility(View.GONE);
            as.setChecked(false);
        }

        Switch gs = (Switch)findViewById(R.id.geofencewitch);
        if(service.getValue("fenceswitch").equals("1")){
            gs.setChecked(true);
        }else{
            gs.setChecked(false);
        }



        //late time
        EditText dt = (EditText) findViewById(R.id.delayminite);
        dt.setText(service.getValue("delaytime"));
        //end type
        Spinner sp = (Spinner) findViewById(R.id.spinner2);
        sp.setSelection(Integer.valueOf(service.getValue("endtype")),true);

        Spinner bc = (Spinner) findViewById(R.id.bcspinner);
        bc.setSelection(Integer.valueOf(service.getValue("mhtype")));

    }
}

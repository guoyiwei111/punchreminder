package com.example.guoyiwei.dk.model;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.guoyiwei.dk.MainActivity;
import com.example.guoyiwei.dk.MediaPlayerActivity;
import com.example.guoyiwei.dk.R;
import com.example.guoyiwei.dk.providers.IOStoreProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by guoyiwei on 2017/8/19.
 */
public class LeaveDialog extends BaseDialog {

    //这些属性无视就好了，就是布局的控件
    private static TextView fromdate;
    private static  TextView fromtime;
    private static TextView todate;
    private static TextView totime;
    private RadioButton leave;
    private RadioButton gonggan;
    private Button okButton;
    private Button cancelButton;
    private TextView titile;
    private Calendar from;
    private Calendar to;
    public Integer type;
    //构造方法还是要的哈
    public LeaveDialog(Context context) {    super(context);

    }

    //设置对话框的样式
    @Override
    protected int getDialogStyleId() {
        return R.style.Theme_dialog;
    }

    //继承于BaseDialog的方法，设置布局用的，这样对话框张啥样久随心所欲啦
    @Override
    protected View getView() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(R.layout.leave_dialog, null);

        //得到各种view
        fromdate= (TextView) view.findViewById(R.id.fromdatetext);
        fromtime = (TextView)view.findViewById(R.id.fromtimetext);
        todate = (TextView)view.findViewById(R.id.todatetext);
        totime = (TextView)view.findViewById(R.id.totimetext);
        leave = (RadioButton)view.findViewById(R.id.leaveradiobutton);
        gonggan = (RadioButton)view.findViewById(R.id.ggradiobutton);
        okButton = (Button)view.findViewById(R.id.leaveokbutton);
        cancelButton = (Button)view.findViewById(R.id.leavecancelbutton);
        titile = (TextView)view.findViewById(R.id.leavetitle);
        //初始化一些控件的方法（放下面写啦~）
        initViewEvent();
        return view;
    }
    private void initViewEvent() {
        //设置对话框那个叉叉的方法，点击关闭对话框
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(isEdit){
                        if(leave.isChecked()){
                            info.setLeaveType(0);
                        }else{
                            info.setLeaveType(1);
                        }

                        IOStoreProvider.saveLeaveList();
                        try{

                            MediaPlayerActivity.updatePunchaLeave();}catch (Exception es){}

                    }else{

                        IOStoreProvider.addLeaveItem(from,to,leave.isChecked()?0:1);
                        MainActivity. updateList();
                        try{MediaPlayerActivity.updatePunchaLeave();}catch (Exception es){}
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                dismiss();
            }
        });
        fromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                from.set(year,month,day,from.get(Calendar.HOUR_OF_DAY),from.get(Calendar.MINUTE),0);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
                                fromdate.setText(sdf.format(from.getTime()));

                            }

                        },
                        from.get(Calendar.YEAR),
                        from.get(Calendar.MONTH),from.get(Calendar.DAY_OF_MONTH)).show();
            }



        });
        fromtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(context,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute) {
                                // TODO Auto-generated method stub

                                try {

                                    from.set(from.get(Calendar.YEAR),from.get(Calendar.MONTH),from.get(Calendar.DAY_OF_MONTH),hour,minute,0);
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                    SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
                                    fromtime.setText(sdf2.format(from.getTime()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        from.get(Calendar.HOUR_OF_DAY),
                        from.get(Calendar.MINUTE), true).show();
            }
        });


        todate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                                Calendar tar = Calendar.getInstance();
                                tar.set(year,month,day,to.get(Calendar.HOUR_OF_DAY),to.get(Calendar.MINUTE),0);
                                tar.set(Calendar.MILLISECOND, 0);//毫秒清零
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
                                if(tar.after(from)){

                                    to.set(year,month,day,to.get(Calendar.HOUR_OF_DAY),to.get(Calendar.MINUTE),0);

                                    todate.setText(sdf.format(to.getTime()));
                                }else{

                                }

                                if(!sdf.format(to.getTime()).equals(sdf.format(from.getTime()))){
                                    fromtime.setEnabled(false);
                                    totime.setEnabled(false);

                                    fromtime.setText("全天");
                                    totime.setText("全天");
                                }else{
                                    fromtime.setEnabled(true);
                                    totime.setEnabled(true);

                                    fromtime.setText(sdf2.format(from.getTime()));
                                    totime.setText(sdf2.format(to.getTime()));
                                }


                            }

                        },
                        to.get(Calendar.YEAR),
                        to.get(Calendar.MONTH),to.get(Calendar.DAY_OF_MONTH)).show();
            }



        });
        totime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(context,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int minute) {
                                // TODO Auto-generated method stub

                                try {
                                    Calendar tar = Calendar.getInstance();
                                    tar.set(to.get(Calendar.YEAR),to.get(Calendar.MONTH),to.get(Calendar.DAY_OF_MONTH),hour,minute,0);
                                    tar.set(Calendar.MILLISECOND, 0);//毫秒清零

                                    if(tar.after(from)){
                                        to.set(to.get(Calendar.YEAR),to.get(Calendar.MONTH),to.get(Calendar.DAY_OF_MONTH),hour,minute,0);
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
                                        totime.setText(sdf2.format(to.getTime()));
                                    }else{

                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        to.get(Calendar.HOUR_OF_DAY),
                        to.get(Calendar.MINUTE), true).show();
            }
        });



    }
    public BaseDialog setTime(Calendar from, Calendar to,Integer type){
        this.from=from;
        this.to=to;
        this.type= type;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
        this.fromdate.setText(sdf.format(from.getTime()));
        this.fromtime.setText(sdf2.format(from.getTime()));
        this.todate.setText(sdf.format(to.getTime()));
        this.totime.setText(sdf2.format(to.getTime()));
        if(type == 0){
            leave.setChecked(true);

        }else{
            gonggan.setChecked(true);
        }
        return this;
    }
    public BaseDialog setDataEditable(Boolean editable){
        this.fromdate.setEnabled(editable);
        this.todate.setEnabled(editable);
        return this;
    }
    public boolean isEdit = false;
    public BaseDialog isAddTitle(Boolean add){
        if(add){

        }else{
            this.titile.setText("编辑例外");
            isEdit = true;
        }

        return this;
    }
    public LeaveInfo info ;
    public void setEditObj(LeaveInfo info){
        this.info = info;
    }

}

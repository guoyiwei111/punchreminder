package com.example.guoyiwei.dk.model;

import com.example.guoyiwei.dk.providers.IOStoreProvider;
import com.example.guoyiwei.dk.util.TimeCombineUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by guoyiwei on 2017/7/24.
 */
public class DkInfo {

    private String startTime="";
    public Calendar start;
    private String endTime="";
    private Integer mhType=0; //工时类型

    private List<DkBase> dkBase;

    private List<LeaveInfo> leaveInfo;

    private String dayString;

    private String dateString;

    private String weekString;

    private String punchStatus="";//打卡是否异常

    private String length = "";// 8时30分
    private Integer IntLength = 0;//8*60+30 统计时间

    private String dayType;// 0 正常工作日 ；1 月末周六；2 最后一天；3 加班
    public DkInfo(Calendar need,Boolean isToday){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        this.setDateString(sdf.format(need.getTime()));
        this.setDayString (String.valueOf(need.get(Calendar.DAY_OF_MONTH)));
        this.setWeekString(getWeekOfDate(need));
        if(isToday){

        }else{
            punchStatus="异常";
        }

        //天类型
        try {

            String str = sdf.format(need.getTime());
            Integer dt = IOStoreProvider.getWorkdayList().get(str);
            if(dt==null){
                dt=3;
                dayType="加班";
            }else if(dt==0){
                dayType= "";
            }else if(dt==1){
                dayType="月末周六";
            }else if(dt==2){
                dayType="最后一天";
            }else{
                dayType="加班";
            }
            boolean tagforleave=false;
            boolean tagforgg = false;
            if(this.leaveInfo!=null && !this.leaveInfo.isEmpty()){
                for(int i=0;i<this.leaveInfo.size();i++){
                    if(this.leaveInfo.get(i).getLeaveType()==0){
                        tagforleave=true;
                    }else{
                        tagforgg=true;
                    }
                }
            }
            if(tagforleave){
                dayType+= " 请假";
            }
            if(tagforgg){
                dayType+= "公干";
            }

            //生成工时相关参数
            //getWorkTime(this.dkBase,this.leaveInfo,dt,isToday);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setDkBase(new ArrayList<DkBase>());
        this.setLeaveInfo(new ArrayList<LeaveInfo>());
    }

    public DkInfo(List<DkBase> dkBase,List<LeaveInfo> leaveInfo,Calendar need,Boolean isToday){
        if(dkBase==null){
            this.setDkBase(new ArrayList<DkBase>());

        }
        if(leaveInfo==null){
            this.setLeaveInfo(new ArrayList<LeaveInfo>());
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        this.setDateString(sdf.format(need.getTime()));
        this.setDayString (String.valueOf(need.get(Calendar.DAY_OF_MONTH)));
        this.setWeekString(getWeekOfDate(need));

        //打卡记录
        if(dkBase!=null){
            this.dkBase=dkBase;
        }else{
            this.dkBase= new ArrayList<>();
        }
        //请假记录
        if(leaveInfo!=null){
            this.leaveInfo=leaveInfo;
        }else{
            this.leaveInfo=new ArrayList<>();
        }

        //天类型
        try {

            String str = sdf.format(need.getTime());
            Integer dt = IOStoreProvider.getWorkdayList().get(str);
            if(dt==null){
                dt=3;
                dayType="加班";
            }else if(dt==0){
                dayType= "";
            }else if(dt==1){
                dayType="月末周六";
            }else if(dt==2){
                dayType="最后一天";
            }else{
                dayType="加班";
            }
            boolean tagforleave=false;
            boolean tagforgg = false;
            if(this.leaveInfo!=null && !this.leaveInfo.isEmpty()){
                for(int i=0;i<this.leaveInfo.size();i++){
                    if(this.leaveInfo.get(i).getLeaveType()==0){
                        tagforleave=true;
                    }else{
                        tagforgg=true;
                    }
                }
            }
            if(tagforleave){
                dayType+= " 请假";
            }
            if(tagforgg){
                dayType+= "公干";
            }

            //生成工时相关参数
            getWorkTime(this.dkBase,this.leaveInfo,dt,isToday);
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
    public static String getWeekOfDate(Calendar cal) {
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
    public void getWorkTime(List<DkBase> dkBase,List<LeaveInfo> leaveInfo,Integer dt,Boolean isToday){
        if(dkBase.size()>0){
            this.mhType= dkBase.get(dkBase.size()-1).getMhType();
        }else if(leaveInfo.size()>0){
            this.mhType= leaveInfo.get(leaveInfo.size()-1).getMhType();
        }
        List<MyTime> time = TimeCombineUtil.getCombinedTime(dkBase,leaveInfo,mhType);
        //TODO 生成 start end time
        if(time.size()==0){
            timeCombine = new ArrayList<>();
        }else {
            timeCombine = time;

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Calendar st = time.get(time.size()-1).getStart();
            Calendar en = time.get(0).getEnd();
            this.setStartTime(sdf.format(st.getTime()));
            this.start = st;
            this.setEndTime(sdf.format(en.getTime()));
        }



        Integer worktime = TimeCombineUtil.getWorkTime(time,mhType,dt);
        IntLength = worktime;
        if(worktime<0 && !isToday){
            punchStatus="异常";
        }else if(worktime>0){
            length =( (worktime/60)>0?(worktime/60)+"时":"" )+ (worktime%60)+"分";
        }else{
            if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)>15){
                punchStatus="异常";
            }

        }


    }


    public List<MyTime> timeCombine;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        if(startTime.equals(endTime)){
            return "";
        }

        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getPunchStatus() {
        return punchStatus;
    }

    public void setPunchStatus(String punchStatus) {
        this.punchStatus = punchStatus;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public Integer getIntLength() {
        return IntLength;
    }

    public void setIntLength(Integer intLength) {
        IntLength = intLength;
    }

    public String getDayType() {
        return dayType;
    }

    public void setDayType(String dayType) {
        this.dayType = dayType;
    }

    public Integer getMhType() {
        return mhType;
    }

    public void setMhType(Integer mhType) {
        this.mhType = mhType;
    }

    public List<DkBase> getDkBase() {
        return dkBase;
    }

    public void setDkBase(List<DkBase> dkBase) {
        this.dkBase = dkBase;
    }

    public List<LeaveInfo> getLeaveInfo() {
        return leaveInfo;
    }

    public void setLeaveInfo(List<LeaveInfo> leaveInfo) {
        this.leaveInfo = leaveInfo;
    }
    public String getWeekString() {
        return weekString;
    }

    public void setWeekString(String weekString) {
        this.weekString = weekString;
    }

    public String getDayString() {
        return dayString;
    }

    public void setDayString(String dayString) {
        this.dayString = dayString;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }
}


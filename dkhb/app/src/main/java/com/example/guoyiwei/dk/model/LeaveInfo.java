package com.example.guoyiwei.dk.model;

import java.util.Calendar;

/**
 * Created by guoyiwei on 2017/7/24.
 */
public class LeaveInfo {

    public LeaveInfo(){}

    public LeaveInfo(Calendar start,Calendar end, Integer leaveType , Integer mhType){
        this.startTime = start;
        this.endTime = end;
        this.leaveType = leaveType;
        this.mhType = mhType;
    }
    private Calendar startTime;
    private Calendar endTime;

    private Integer leaveType;

    public Integer getMhType() {
        return mhType;
    }

    public void setMhType(Integer mhType) {
        this.mhType = mhType;
    }

    private Integer mhType;


    public Integer getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(Integer leaveType) {
        this.leaveType = leaveType;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

}

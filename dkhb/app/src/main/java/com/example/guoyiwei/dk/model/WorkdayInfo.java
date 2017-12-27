package com.example.guoyiwei.dk.model;

import java.util.Calendar;

/**
 * Created by guoyiwei on 2017/7/24.
 */
public class WorkdayInfo {

    public WorkdayInfo(){}
    private Calendar time;
    private Integer type;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

}

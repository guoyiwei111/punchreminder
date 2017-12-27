package com.example.guoyiwei.dk.model;

import java.util.Calendar;

/**
 * Created by guoyiwei on 2017/7/24.
 */
public class DkBase implements Comparable<DkBase>{
    private Calendar time;

    private Integer mhType; //工时类型

    public DkBase(){
    }

    public DkBase(Calendar time, Integer mhType){
        this.time=time;
        this.mhType = mhType;
    }

    public Integer getMhType() {
        return mhType;
    }

    public void setMhType(Integer mhType) {
        this.mhType = mhType;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }
    public int compareTo(DkBase other)
    {

        return time.compareTo(other.time);
    }
}

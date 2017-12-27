package com.example.guoyiwei.dk.model;

import java.util.Calendar;

/**
 * Created by guoyiwei on 2017/7/25.
 */
public class MyTime implements Comparable<MyTime>
{
    private Calendar start;
    private Calendar end;
    public MyTime(){}
    public MyTime(Calendar start, Calendar end)
    {
        this.start = start;
        this.end = end;
    }
    public Calendar getStart()
    {
        return start;
    }
    public Calendar getEnd()
    {
        return end;
    }
    public int compareTo(MyTime other)
    {

        if (start.compareTo(other.start)==0)
        {

            return end.compareTo(other.end);
        }
        return start.compareTo(other.start);
    }
}

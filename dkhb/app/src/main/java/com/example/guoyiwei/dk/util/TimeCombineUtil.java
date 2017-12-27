package com.example.guoyiwei.dk.util;

/**
 * Created by guoyiwei on 2017/7/25.
 */
import com.example.guoyiwei.dk.model.DkBase;
import com.example.guoyiwei.dk.model.LeaveInfo;
import com.example.guoyiwei.dk.model.MyTime;

import java.util.*;
import java.lang.*;
import java.text.SimpleDateFormat;


public class TimeCombineUtil {


    public static Integer getWorkTime(List<MyTime> time, Integer mhType, Integer dayType){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(mhType==0){
            //flex 9-17:30
            Calendar need = time.get(time.size()-1).getStart();
            Calendar s1 = Calendar.getInstance();
            s1.set(need.get(Calendar.YEAR), need.get(Calendar.MONTH), need.get(Calendar.DAY_OF_MONTH), 9, 0, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
            s1.set(Calendar.MILLISECOND, 0);//毫秒清零

            Calendar e1 = Calendar.getInstance();
            e1.set(need.get(Calendar.YEAR), need.get(Calendar.MONTH), need.get(Calendar.DAY_OF_MONTH), 12, 0, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
            e1.set(Calendar.MILLISECOND, 0);//毫秒清零

            Calendar s2 = Calendar.getInstance();
            s2.set(need.get(Calendar.YEAR), need.get(Calendar.MONTH), need.get(Calendar.DAY_OF_MONTH), 13, 30, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
            s2.set(Calendar.MILLISECOND, 0);//毫秒清零

            Calendar e2 = Calendar.getInstance();
            e2.set(need.get(Calendar.YEAR), need.get(Calendar.MONTH), need.get(Calendar.DAY_OF_MONTH), 17, 30, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
            e2.set(Calendar.MILLISECOND, 0);//毫秒清零

            int confirm = 0;
            Integer res = 0;
            if(dayType == 0 || dayType == 1 || dayType ==2){
                for(MyTime item: time){
                    if(item.getStart().compareTo(s1)<=0 && item.getEnd().compareTo(e1)>=0){
                        confirm++;

                        res += (s1.get(Calendar.HOUR_OF_DAY) - item.getStart().get(Calendar.HOUR_OF_DAY) ) *60 + (s1.get(Calendar.MINUTE) - item.getStart().get(Calendar.MINUTE) );

                        break;
                    }
                }
                for(MyTime item: time){
                    if(item.getStart().compareTo(s2)<=0 && item.getEnd().compareTo(e2)>=0){
                        confirm++;
                        if(dayType==0 || dayType ==2) {//正常工作日，非加班
                            Calendar tar = Calendar.getInstance();
                            tar.set(need.get(Calendar.YEAR), need.get(Calendar.MONTH), need.get(Calendar.DAY_OF_MONTH), 18, 00, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
                            tar.set(Calendar.MILLISECOND, 0);//毫秒清零
                            if(item.getEnd().compareTo(tar)<=0){
                                res+=420;
                            }else{
                                res += 420 + (item.getEnd().get(Calendar.HOUR_OF_DAY) - tar.get(Calendar.HOUR_OF_DAY)) * 60 + (item.getEnd().get(Calendar.MINUTE) - tar.get(Calendar.MINUTE));
                            }
                        }else{
                            res += 420 + (item.getEnd().get(Calendar.HOUR_OF_DAY) - e2.get(Calendar.HOUR_OF_DAY)) * 60 + (item.getEnd().get(Calendar.MINUTE) - e2.get(Calendar.MINUTE));
                        }
                        break;
                    }
                }

                if(res>0 && (dayType == 0 || dayType ==2 )){
                    if(res!=8*60){
                        res--;
                    }

                }
                if(confirm<2){
                    return -1*res;
                }
                return res;
            }else {//dayType ==3 加班
                for(MyTime item: time){
                    if(item.getStart().compareTo(e1)<=0 && item.getEnd().compareTo(e1)>=0){
                        confirm++;

                        res += (e1.get(Calendar.HOUR_OF_DAY) - item.getStart().get(Calendar.HOUR_OF_DAY) ) *60 + (e1.get(Calendar.MINUTE) - item.getStart().get(Calendar.MINUTE) );

                        break;
                    }
                }
                for(MyTime item: time){
                    if(item.getStart().compareTo(s2)<=0 && item.getEnd().compareTo(s2)>=0){
                        confirm++;

                            res +=  (item.getEnd().get(Calendar.HOUR_OF_DAY) - s2.get(Calendar.HOUR_OF_DAY)) * 60 + (item.getEnd().get(Calendar.MINUTE) - s2.get(Calendar.MINUTE));

                        break;
                    }
                }
                return res;
            }


        }else if(mhType==1){
            //8:00-17:30
            //flex 9-17:30
            Calendar need = time.get(time.size()-1).getStart();
            Calendar s1 = Calendar.getInstance();
            s1.set(need.get(Calendar.YEAR), need.get(Calendar.MONTH), need.get(Calendar.DAY_OF_MONTH), 8, 0, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
            s1.set(Calendar.MILLISECOND, 0);//毫秒清零

            Calendar e1 = Calendar.getInstance();
            e1.set(need.get(Calendar.YEAR), need.get(Calendar.MONTH), need.get(Calendar.DAY_OF_MONTH), 12, 0, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
            e1.set(Calendar.MILLISECOND, 0);//毫秒清零

            Calendar s2 = Calendar.getInstance();
            s2.set(need.get(Calendar.YEAR), need.get(Calendar.MONTH), need.get(Calendar.DAY_OF_MONTH), 13, 30, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
            s2.set(Calendar.MILLISECOND, 0);//毫秒清零

            Calendar e2 = Calendar.getInstance();
            e2.set(need.get(Calendar.YEAR), need.get(Calendar.MONTH), need.get(Calendar.DAY_OF_MONTH), 17, 30, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
            e2.set(Calendar.MILLISECOND, 0);//毫秒清零

            int confirm = 0;
            Integer res = 0;
            if(dayType == 0 || dayType == 1 || dayType ==2){

                for(MyTime item: time){
                    if(item.getStart().compareTo(s1)<=0 && item.getEnd().compareTo(e1)>=0){
                        confirm++;
                        break;
                    }
                }
                for(MyTime item: time){
                    if(item.getStart().compareTo(s2)<=0 && item.getEnd().compareTo(e2)>=0){
                        confirm++;

                        break;
                    }
                }


                if(confirm<2){
                    return -1;
                }
                return 480;
            }else {//dayType ==3 加班
                for(MyTime item: time){
                    if(item.getStart().compareTo(e1)<=0 && item.getEnd().compareTo(e1)>=0){
                        confirm++;

                        res += (e1.get(Calendar.HOUR_OF_DAY) - item.getStart().get(Calendar.HOUR_OF_DAY) ) *60 + (e1.get(Calendar.MINUTE) - item.getStart().get(Calendar.MINUTE) );

                        break;
                    }
                }
                for(MyTime item: time){
                    if(item.getStart().compareTo(s2)<=0 && item.getEnd().compareTo(s2)>=0){
                        confirm++;

                        res +=  (item.getEnd().get(Calendar.HOUR_OF_DAY) - s2.get(Calendar.HOUR_OF_DAY)) * 60 + (item.getEnd().get(Calendar.MINUTE) - s2.get(Calendar.MINUTE));

                        break;
                    }
                }
                return res;
            }

        }else if(mhType==2){
            //8:30-18:00
            Calendar need = time.get(time.size()-1).getStart();
            Calendar s1 = Calendar.getInstance();
            s1.set(need.get(Calendar.YEAR), need.get(Calendar.MONTH), need.get(Calendar.DAY_OF_MONTH), 8, 30, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
            s1.set(Calendar.MILLISECOND, 0);//毫秒清零

            Calendar e1 = Calendar.getInstance();
            e1.set(need.get(Calendar.YEAR), need.get(Calendar.MONTH), need.get(Calendar.DAY_OF_MONTH), 12, 0, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
            e1.set(Calendar.MILLISECOND, 0);//毫秒清零

            Calendar s2 = Calendar.getInstance();
            s2.set(need.get(Calendar.YEAR), need.get(Calendar.MONTH), need.get(Calendar.DAY_OF_MONTH), 13, 30, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
            s2.set(Calendar.MILLISECOND, 0);//毫秒清零

            Calendar e2 = Calendar.getInstance();
            e2.set(need.get(Calendar.YEAR), need.get(Calendar.MONTH), need.get(Calendar.DAY_OF_MONTH), 18, 00, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
            e2.set(Calendar.MILLISECOND, 0);//毫秒清零

            int confirm = 0;
            Integer res = 0;
            if(dayType == 0 || dayType == 1 || dayType ==2){

                for(MyTime item: time){
                    if(item.getStart().compareTo(s1)<=0 && item.getEnd().compareTo(e1)>=0){
                        confirm++;
                        break;
                    }
                }
                for(MyTime item: time){
                    if(item.getStart().compareTo(s2)<=0 && item.getEnd().compareTo(e2)>=0){
                        confirm++;

                        break;
                    }
                }


                if(confirm<2){
                    return -1;
                }
                return 480;
            }else {//dayType ==3 加班
                for(MyTime item: time){
                    if(item.getStart().compareTo(e1)<=0 && item.getEnd().compareTo(e1)>=0){
                        confirm++;

                        res += (e1.get(Calendar.HOUR_OF_DAY) - item.getStart().get(Calendar.HOUR_OF_DAY) ) *60 + (e1.get(Calendar.MINUTE) - item.getStart().get(Calendar.MINUTE) );

                        break;
                    }
                }
                for(MyTime item: time){
                    if(item.getStart().compareTo(s2)<=0 && item.getEnd().compareTo(s2)>=0){
                        confirm++;

                        res +=  (item.getEnd().get(Calendar.HOUR_OF_DAY) - s2.get(Calendar.HOUR_OF_DAY)) * 60 + (item.getEnd().get(Calendar.MINUTE) - s2.get(Calendar.MINUTE));

                        break;
                    }
                }
                return res;
            }
        }
        return -1;

    }

    public static Calendar getEndTime(List<MyTime> time, Integer mhType, Integer dayType,Integer remain){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(dayType==3) return null;

        if(mhType==0){
            //flex 9-17:30
            Calendar need = time.get(time.size()-1).getStart();
            Calendar s1 = Calendar.getInstance();
            s1.set(need.get(Calendar.YEAR), need.get(Calendar.MONTH), need.get(Calendar.DAY_OF_MONTH), 9, 0, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
            s1.set(Calendar.MILLISECOND, 0);//毫秒清零

            Calendar e1 = Calendar.getInstance();
            e1.set(need.get(Calendar.YEAR), need.get(Calendar.MONTH), need.get(Calendar.DAY_OF_MONTH), 12, 0, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
            e1.set(Calendar.MILLISECOND, 0);//毫秒清零

            Calendar s2 = Calendar.getInstance();
            s2.set(need.get(Calendar.YEAR), need.get(Calendar.MONTH), need.get(Calendar.DAY_OF_MONTH), 13, 30, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
            s2.set(Calendar.MILLISECOND, 0);//毫秒清零

            Calendar e2 = Calendar.getInstance();
            e2.set(need.get(Calendar.YEAR), need.get(Calendar.MONTH), need.get(Calendar.DAY_OF_MONTH), 17, 30, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
            e2.set(Calendar.MILLISECOND, 0);//毫秒清零

            int confirm = 0;
            Integer res = 0;
            for(MyTime item: time){
                if(item.getStart().compareTo(s1)<=0){
                    confirm++;

                    res += (s1.get(Calendar.HOUR_OF_DAY) - item.getStart().get(Calendar.HOUR_OF_DAY) ) *60 + (s1.get(Calendar.MINUTE) - item.getStart().get(Calendar.MINUTE) );

                    break;
                }
            }

            if(dayType==0 || dayType ==2){
                res+=remain;
                if(res>60){
                    Calendar tar = Calendar.getInstance();
                    tar.set(need.get(Calendar.YEAR), need.get(Calendar.MONTH), need.get(Calendar.DAY_OF_MONTH), 17, 30, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
                    tar.set(Calendar.MILLISECOND, 0);//毫秒清零
                    return tar;
                }else{
                    Calendar tar = Calendar.getInstance();
                    tar.set(need.get(Calendar.YEAR), need.get(Calendar.MONTH), need.get(Calendar.DAY_OF_MONTH), 19, 00, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
                    tar.set(Calendar.MILLISECOND, 0);//毫秒清零
                    tar.add(Calendar.MINUTE,-res+1);
                    return tar;
                }
            }else{
                if(res>60){
                    Calendar tar = Calendar.getInstance();
                    tar.set(need.get(Calendar.YEAR), need.get(Calendar.MONTH), need.get(Calendar.DAY_OF_MONTH), 17, 30, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
                    tar.set(Calendar.MILLISECOND, 0);//毫秒清零
                    return tar;
                }else{
                    Calendar tar = Calendar.getInstance();
                    tar.set(need.get(Calendar.YEAR), need.get(Calendar.MONTH), need.get(Calendar.DAY_OF_MONTH), 18, 30, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
                    tar.set(Calendar.MILLISECOND, 0);//毫秒清零
                    tar.add(Calendar.MINUTE,-res+1);
                    return tar;
                }
            }

        }else if(mhType==1){
            //8:00-17:30
            Calendar tar = Calendar.getInstance();
            tar.set(tar.get(Calendar.YEAR), tar.get(Calendar.MONTH), tar.get(Calendar.DAY_OF_MONTH), 17, 30, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
            tar.set(Calendar.MILLISECOND, 0);//毫秒清零
            //TODO
            return tar;
        }else if(mhType==2){
            Calendar tar = Calendar.getInstance();
            tar.set(tar.get(Calendar.YEAR), tar.get(Calendar.MONTH), tar.get(Calendar.DAY_OF_MONTH), 18, 00, 0);//年月日时分秒（月份0代表1月）  ，毫秒不会自动清零
            tar.set(Calendar.MILLISECOND, 0);//毫秒清零
            return tar;
        }
        return null;

    }


    public static List<MyTime> getCombinedTime(List<DkBase> dkBases, List<LeaveInfo> leaveInfos, Integer mhType) {
        List<MyTime> myTimeList = new ArrayList<MyTime>();
        if (dkBases!=null && dkBases.size() > 0) {
            if (dkBases.size() == 1) {
                myTimeList.add(new MyTime(dkBases.get(0).getTime(), dkBases.get(0).getTime()));
            } else {
                myTimeList.add(new MyTime(dkBases.get(0).getTime(), dkBases.get(dkBases.size() - 1).getTime()));
            }
        }
        if(leaveInfos!=null){
            for (LeaveInfo item : leaveInfos) {
                myTimeList.add(new MyTime(item.getStartTime(), item.getEndTime()));
            }
        }

        Collections.sort(myTimeList);


        //System.out.println(myTimeList.size());

        Stack<Calendar> s = new Stack<>();
        Stack<Calendar> e = new Stack<>();
        //s.push(0);
        //e.push(0);
        for (MyTime time : myTimeList) {


            if (e.isEmpty()) {
                s.push(time.getStart());
                e.push(time.getEnd());
            } else if (time.getStart().compareTo(e.peek()) > 0) //没有交集
            {
                s.push(time.getStart());
                e.push(time.getEnd());
            } else if (time.getEnd().compareTo(e.peek()) > 0) //有部分交集，取并集
            {
                e.pop();
                e.push(time.getEnd());
            }

        }

        List<MyTime> res = new ArrayList<>();
        while (!s.empty()) {
            res.add(new MyTime(s.pop(), e.pop()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //System.out.println(sdf.format(s.pop().getTime()) + " ~ " + sdf.format(e.pop().getTime()));

        }
        return res;
    }
}

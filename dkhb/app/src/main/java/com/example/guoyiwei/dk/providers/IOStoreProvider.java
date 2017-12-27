package com.example.guoyiwei.dk.providers;

import android.content.Context;

import com.example.guoyiwei.dk.MainActivity;
import com.example.guoyiwei.dk.model.DkBase;
import com.example.guoyiwei.dk.model.DkInfo;
import com.example.guoyiwei.dk.model.LeaveInfo;
import com.example.guoyiwei.dk.services.PreferencesService;
import com.example.guoyiwei.dk.util.JSONUtil;

import org.codehaus.jackson.type.TypeReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guoyiwei on 2017/7/24.
 */
public class IOStoreProvider {

    private static PreferencesService service;

    public static void Init(Context context){
        service = new PreferencesService(context);
    }

    //工作日 查询方法类
    public static Map <String ,Integer > workdayList = null;
    public static Map<String,Integer> getWorkdayList() throws Exception {
        if(workdayList ==null){
            String jsonStr = service.getValue("wdlist");
            if(jsonStr.isEmpty()){
                jsonStr="{}";
            }
            workdayList =  (HashMap<String, Integer>)JSONUtil.toObject(jsonStr,new   TypeReference<HashMap<String, Integer>>() {});
        }
        return workdayList;
    }
    public static void setWorkdayList() throws Exception {
        String res = JSONUtil.getJsonString(workdayList);
        service.setValue("wdlist",res);
    }


    //打卡记录查询方法类

    public static HashMap<String, List<DkBase>> dkBaseList = null;
    public static HashMap<String, List<DkBase>> getPunchList() throws Exception{
        if(true){
            String jsonStr = service.getValue("dklist");
            if(jsonStr.isEmpty()){
                jsonStr="{}";
            }
            dkBaseList =  (HashMap<String, List<DkBase>>)JSONUtil.toObject(jsonStr, new TypeReference<HashMap<String, List<DkBase>>>() {});
            //JSONUtil.getJsonString(person);
        }
        return dkBaseList;
    };
    public static void deleteDkBase(String date , Integer index) throws Exception {
        List<DkBase> dk = dkBaseList.get(date);

        dk.remove((int)index);

        if(dk.size()==0){
            dkBaseList.remove(date);

        }
        savePunchList();

    }
    public static void deleteLvBase(String date , Integer index) throws Exception {
        List<LeaveInfo> lv = leaveList.get(date);

        lv.remove((int)index);

        if(lv.size()==0){
            leaveList.remove(date);

        }
        saveLeaveList();

    }



    public static void deleteDkBase(String date) throws Exception {
        try{
            dkBaseList.remove(date);
        }catch (Exception es){

        }

        savePunchList();

    }

    public static void savePunchList() throws Exception{
        String res = JSONUtil.getJsonString(dkBaseList);
        service.setValue("dklist",res);
    }

    public static void punchCard() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar now = Calendar.getInstance();
        String timeStr = sdf.format(now.getTime());

        List<DkBase> list =  dkBaseList.get(timeStr);
        if(list==null){
            list = new ArrayList<>();
            dkBaseList.put(timeStr,list);

        }
        String mhType = service.getValue("mhtype");
        list.add(new DkBase(Calendar.getInstance(),Integer.valueOf(mhType)));
        Collections.sort(list);
        savePunchList();
    }
    public static int getMhType(){
        return Integer.valueOf( service.getValue("mhtype")).intValue();
    }

    public static void punchCard(Calendar need) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar now = need;
        String timeStr = sdf.format(now.getTime());

        List<DkBase> list =  dkBaseList.get(timeStr);
        if(list==null){
            list = new ArrayList<>();
            dkBaseList.put(timeStr,list);

        }
        String mhType = service.getValue("mhtype");
        list.add(new DkBase(need,Integer.valueOf(mhType)));
        Collections.sort(list);
        savePunchList();
    }


    //TODO ????
    //例外查询方法类
    public static  Map<String,List<LeaveInfo>> leaveList = null;
    public static Map<String,List<LeaveInfo>> getLeaveList()  throws Exception{
        if(true){
            String jsonStr = service.getValue("leavelist");
            if(jsonStr.isEmpty()){
                jsonStr="{}";
            }
            leaveList = ( Map<String,List<LeaveInfo>>)JSONUtil.toObject(jsonStr,new TypeReference<HashMap<String, List<LeaveInfo>>>() {});
            //JSONUtil.getJsonString(person);
        }

        return leaveList;
    };
    public static  void saveLeaveList() throws  Exception{
        String res = JSONUtil.getJsonString(leaveList);
        service.setValue("leavelist",res);
    }
    public static void addLeaveItem(Calendar from,Calendar to,Integer leaveType) throws Exception {
        Calendar tmp = Calendar.getInstance();
        tmp.set(from.get(Calendar.YEAR),from.get(Calendar.MONTH),from.get(Calendar.DAY_OF_MONTH),from.get(Calendar.HOUR_OF_DAY),from.get(Calendar.MINUTE),from.get(Calendar.SECOND));
        tmp.set(Calendar.MILLISECOND, 0);//毫秒清零
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int mhType = Integer.valueOf(service.getValue("mhtype"));

        int type = (int)workdayList.get(sdf.format(tmp.getTime()));

        if(from.get(Calendar.YEAR)==to.get(Calendar.YEAR) && from.get(Calendar.MONTH)==to.get(Calendar.MONTH) && from.get(Calendar.DAY_OF_MONTH)==to.get(Calendar.DAY_OF_MONTH)){
            if(type!=3){
                String timeStr = sdf.format(from.getTime());
                List<LeaveInfo> list =  leaveList.get(timeStr);
                if(list==null){
                    list = new ArrayList<>();
                }
                list.add(new LeaveInfo(from,to,leaveType,Integer.valueOf(service.getValue("mhtype"))));
                leaveList.put(timeStr,list);
            }

        }else{
            //TODO 当跨日之后需根据mhType 切分

            for(;tmp.before(to);tmp.add(Calendar.DAY_OF_MONTH,1)){

                if(type!=3){
                    List<Calendar> defaultTime = MainActivity.getDefaultTime(mhType);

                    String timeStr = sdf.format(tmp.getTime());
                    List<LeaveInfo> list =  leaveList.get(timeStr);
                    if(list==null){
                        list = new ArrayList<>();
                    }
                    Calendar tmp2 = Calendar.getInstance();
                    tmp2.set(tmp.get(Calendar.YEAR),tmp.get(Calendar.MONTH),tmp.get(Calendar.DAY_OF_MONTH),defaultTime.get(0).get(Calendar.HOUR_OF_DAY),defaultTime.get(0).get(Calendar.MINUTE),defaultTime.get(0).get(Calendar.SECOND));
                    tmp2.set(Calendar.MILLISECOND, 0);//毫秒清零

                    Calendar tmp3 = Calendar.getInstance();
                    tmp3.set(tmp.get(Calendar.YEAR),tmp.get(Calendar.MONTH),tmp.get(Calendar.DAY_OF_MONTH),defaultTime.get(1).get(Calendar.HOUR_OF_DAY),defaultTime.get(1).get(Calendar.MINUTE),defaultTime.get(1).get(Calendar.SECOND));
                    tmp3.set(Calendar.MILLISECOND, 0);//毫秒清零


                    list.add(new LeaveInfo(tmp2,tmp3,leaveType,mhType));
                    leaveList.put(timeStr,list);


                }
            }



        }
        saveLeaveList();
    }


    public static List<Map<String, Object>> getFinalList(List<Map<String, Object>> listems) throws Exception{
        List<DkInfo> list = getMidList();

        listems.clear();
        for(int i=0;i<list.size();i++){

            Map<String, Object> listem = new HashMap<String, Object>();
            //listem.put("date",list.get(i).getDayString());
            listem.put("date",list.get(i).getDateString());
            listem.put("day",list.get(i).getDayString());
            listem.put("week",list.get(i).getWeekString());

            SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");

            listem.put("first",list.get(i).getStartTime());
            listem.put("start",list.get(i).start);
            listem.put("end",list.get(i).getEndTime());

            listem.put("punchstatus",list.get(i).getPunchStatus());
            listem.put("daytype",list.get(i).getDayType());

            //listem.put("status",false);
            listem.put("time",list.get(i).getLength());
            listem.put("timenum",list.get(i).getIntLength());

            listem.put("punch",list.get(i).getDkBase());
            listem.put("leave",list.get(i).getLeaveInfo());
            listem.put("timecomb",list.get(i).timeCombine);
            listem.put("mhtype",list.get(i).getMhType());
            listems.add(listem);
        }
        return listems;
    }

    public static List<DkInfo> getMidList () throws Exception{
        Calendar today = Calendar.getInstance();
        Calendar i = Calendar.getInstance();
        i.add(Calendar.DAY_OF_MONTH,1-today.get(Calendar.DAY_OF_MONTH));
        i.set(Calendar.MILLISECOND, 0);//毫秒清零
        Map<String,Integer> workday = getWorkdayList();
        Map<String,List<DkBase>> dk = getPunchList();
        Map<String,List<LeaveInfo>> leave = getLeaveList();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<DkInfo> res = new ArrayList<>();
        for(; i.get(Calendar.DAY_OF_MONTH) <= today.get(Calendar.DAY_OF_MONTH) &&i.get(Calendar.MONTH) == today.get(Calendar.MONTH) ;i.add(Calendar.DATE,1)){
            String timeStr = sdf.format(i.getTime());
            List<DkBase> dkbase = dk.get(timeStr);
            List<LeaveInfo> leaveinfo = leave.get(timeStr);
            Integer dayType = workday.get(timeStr);


            if(dkbase==null && leaveinfo==null && (dayType==null) ){// weekend
                continue;
            }else {

                res.add(0,initDkInfo(dkbase,leaveinfo,i,dayType,i.get(Calendar.DATE) == today.get(Calendar.DATE)));
            }

        }

        return res;
    }
    static DkInfo initDkInfo(List<DkBase> dkBases,List<LeaveInfo> leavinfo,Calendar need, Integer workdayType,Boolean isToday){
        if(dkBases==null && leavinfo==null ){
            return new DkInfo(need,isToday);
        }else{
            return new DkInfo(dkBases,leavinfo,need,isToday);
        }

    }





}

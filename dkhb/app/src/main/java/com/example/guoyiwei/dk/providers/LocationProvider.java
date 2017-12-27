package com.example.guoyiwei.dk.providers;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.DPoint;
import com.example.guoyiwei.dk.util.LogUtil;

import java.util.Calendar;
import java.util.List;

/**
 * Created by guoyi on 2017/9/16.
 */

public class LocationProvider {


    private static AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    private Context context;
    private static LocationProvider locationProvider = null;

    public static LocationProvider getDefaultProvider(Context context){
        if(locationProvider==null){
            synchronized (LocationProvider.class){
                locationProvider = new LocationProvider(context);
            }
        }
        return locationProvider;
    }

    private LocationProvider(Context context){

        this.context = context;
        initLocation();
    }

    private void initLocation(){
        //初始化client
        locationClient = new AMapLocationClient(context);
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }


    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2*60*1000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(false);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    /**
     * 定位监听
     */

    public static AMapLocation storelocation=null;
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {

                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if(location.getErrorCode() == 0){
                    LogUtil.addLog(context,"LocationProvider","定位成功"+location.getLongitude()+" "+location.getLatitude());
                    storelocation = location;
                    //ForeService.judge(context);

                } else {
                    //定位失败
                    LogUtil.addLog(context,"LocationProvider","定位失败");

                }

            } else {
                LogUtil.addLog(context,"LocationProvider","定位失败");
            }

        }
    };
    private boolean isRunning= false;
    public void startLocation(){
        //根据控件的选择，重新设置定位参数
        isRunning = true;
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
        LogUtil.addLog(context,"LocationProvider","启动定位");
    }

    /**
     * 停止定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    public void stopLocation(){
        // 停止定位
        isRunning = false;
        locationClient.stopLocation();
        LogUtil.addLog(context,"LocationProvider","停止定位");
    }

    public void destroyLocation(){
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            stopLocation();
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
            LogUtil.addLog(context,"LocationProvider","清除定位");
        }
    }
    public  static List<List<DPoint>> allFence = null;
    public boolean isInFence(){


        if(storelocation==null){
            return false;
        }
        if(Calendar.getInstance().getTimeInMillis() -  storelocation.getTime() > 2*60*60*1000){
            return false;
        }

        if(allFence==null || allFence.size()==0){
            return false;
        }
        for(int i=0;i<allFence.size();i++){
            List<DPoint> tmparray = allFence.get(i);
            if(IsPtInPoly(new DPoint(storelocation.getLatitude(),storelocation.getLongitude()),tmparray)){
                return true;
            }
        }
        return false;

    }

    /**
     * 判断点是否在多边形内
     * @param point 检测点
     * @param pts   多边形的顶点
     * @return      点在多边形内返回true,否则返回false
     */
    public boolean IsPtInPoly(DPoint point, List<DPoint> pts){

        int N = pts.size();
        boolean boundOrVertex = true; //如果点位于多边形的顶点或边上，也算做点在多边形内，直接返回true
        int intersectCount = 0;//cross points count of x
        double precision = 2e-10; //浮点类型计算时候与0比较时候的容差
        DPoint p1, p2;//neighbour bound vertices
        DPoint p = point; //当前点

        p1 = pts.get(0);//left vertex
        for(int i = 1; i <= N; ++i){//check all rays
            if(p.equals(p1)){
                return boundOrVertex;//p is an vertex
            }

            p2 = pts.get(i % N);//right vertex
            if(p.getLatitude() < Math.min(p1.getLatitude(), p2.getLatitude()) || p.getLatitude() > Math.max(p1.getLatitude(), p2.getLatitude())){//ray is outside of our interests
                p1 = p2;
                continue;//next ray left point
            }

            if(p.getLatitude() > Math.min(p1.getLatitude(), p2.getLatitude()) && p.getLatitude() < Math.max(p1.getLatitude(), p2.getLatitude())){//ray is crossing over by the algorithm (common part of)
                if(p.getLongitude() <= Math.max(p1.getLongitude(), p2.getLongitude())){//x is before of ray
                    if(p1.getLatitude() == p2.getLatitude() && p.getLongitude() >= Math.min(p1.getLongitude(), p2.getLongitude())){//overlies on a horizontal ray
                        return boundOrVertex;
                    }

                    if(p1.getLongitude() == p2.getLongitude()){//ray is vertical
                        if(p1.getLongitude() == p.getLongitude()){//overlies on a vertical ray
                            return boundOrVertex;
                        }else{//before ray
                            ++intersectCount;
                        }
                    }else{//cross point on the left side
                        double xinters = (p.getLatitude() - p1.getLatitude()) * (p2.getLongitude() - p1.getLongitude()) / (p2.getLatitude() - p1.getLatitude()) + p1.getLongitude();//cross point of y
                        if(Math.abs(p.getLongitude() - xinters) < precision){//overlies on a ray
                            return boundOrVertex;
                        }

                        if(p.getLongitude() < xinters){//before ray
                            ++intersectCount;
                        }
                    }
                }
            }else{//special case when ray is crossing through the vertex
                if(p.getLatitude() == p2.getLatitude() && p.getLongitude() <= p2.getLongitude()){//p crossing over p2
                    DPoint p3 = pts.get((i+1) % N); //next vertex
                    if(p.getLatitude() >= Math.min(p1.getLatitude(), p3.getLatitude()) && p.getLatitude() <= Math.max(p1.getLatitude(), p3.getLatitude())){//p.x lies between p1.x & p3.x
                        ++intersectCount;
                    }else{
                        intersectCount += 2;
                    }
                }
            }
            p1 = p2;//next ray left point
        }

        if(intersectCount % 2 == 0){//偶数在多边形外
            return false;
        } else { //奇数在多边形内
            return true;
        }

    }
}

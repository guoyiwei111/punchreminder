package com.example.guoyiwei.dk.util;

import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.example.guoyiwei.dk.model.MyAppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gray_dog3 on 16/3/3.
 * 扫描本地安装的应用,工具类
 */
public class ApkTool {
    static  String TAG = "ApkTool";
    public static List<MyAppInfo> mLocalInstallApps = null;

    public static List<MyAppInfo> scanLocalInstallAppList(PackageManager packageManager) {
        List<MyAppInfo> myAppInfos = new ArrayList<MyAppInfo>();
        try {
            List<ApplicationInfo> appInfos =  packageManager.getInstalledApplications(0);
            List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
            for (int i = 0; i < packageInfos.size(); i++) {
                PackageInfo packageInfo = packageInfos.get(i);
                //过滤掉系统app
            if ((ApplicationInfo.FLAG_SYSTEM & packageInfo.applicationInfo.flags) != 0) {
                continue;
            }


                ActivityInfo[] aaa = packageInfo.activities;

                MyAppInfo myAppInfo = new MyAppInfo();
                myAppInfo.packName=packageInfo.packageName;
                myAppInfo.setAppName(getProgramNameByPackageName(packageManager,packageInfo.packageName));
                if (packageInfo.applicationInfo.loadIcon(packageManager) == null) {
                    continue;
                }
                myAppInfo.setImage(packageInfo.applicationInfo.loadIcon(packageManager));
                myAppInfos.add(myAppInfo);
            }
        }catch (Exception e){
            Log.e(TAG,"===============获取应用包信息失败");
        }
        return myAppInfos;
    }


    public static String getProgramNameByPackageName(PackageManager packageManager,
                                                     String packageName) {
        PackageManager pm = packageManager;
        String name = null;
        try {
            name = pm.getApplicationLabel(
                    pm.getApplicationInfo(packageName,
                            PackageManager.GET_META_DATA)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

}

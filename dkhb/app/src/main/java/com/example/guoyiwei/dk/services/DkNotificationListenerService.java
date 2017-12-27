package com.example.guoyiwei.dk.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.example.guoyiwei.dk.util.LogUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anyway on 2017/2/10.
 */

public class DkNotificationListenerService extends android.service.notification.NotificationListenerService {


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // 如果该通知的包名不是微信，那么 pass 掉
        try {
            System.out.println("------------------------");
            LogUtil.addLog(getApplicationContext(), "NotificationListener", "被激活");

            try {
                if (ForeService.thread == null) {
                    getApplicationContext().startService(new Intent(getApplicationContext(), ForeService.class));
                    ForeService.runJobscheduler(getApplicationContext());
                }

            } catch (Exception e2) {
                e2.printStackTrace();
            }


            if (!"com.tencent.mm".equals(sbn.getPackageName())) {
                return;
            }
            Notification notification = sbn.getNotification();
            if (notification == null) {
                return;
            }
            PendingIntent pendingIntent = null;
            // 当 API > 18 时，使用 extras 获取通知的详细信息
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Bundle extras = notification.extras;
                if (extras != null) {
                    // 获取通知标题
                    String title = extras.getString(Notification.EXTRA_TITLE, "");
                    // 获取通知内容
                    String content = extras.getString(Notification.EXTRA_TEXT, "");

                    PreferencesService service = new PreferencesService(getApplicationContext());

                }
            } else {
                // 当 API = 18 时，利用反射获取内容字段
                List<String> textList = getText(notification);
                if (textList != null && textList.size() > 0) {
                    for (String text : textList) {
                        if (!TextUtils.isEmpty(text) && text.contains("[微信红包]")) {
                            pendingIntent = notification.contentIntent;
                            break;
                        }
                    }
                }
            }
            // send pendingIntent to open wechat
            try {
                if (pendingIntent != null) {
                    pendingIntent.send();
                }
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        } catch (Exception es) {
            LogUtil.addLog(getApplicationContext(), "WatchmenReceiver", es.getMessage());
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

    public List<String> getText(Notification notification) {
        if (null == notification) {
            return null;
        }
        RemoteViews views = notification.bigContentView;
        if (views == null) {
            views = notification.contentView;
        }
        if (views == null) {
            return null;
        }
        // Use reflection to examine the m_actions member of the given RemoteViews object.
        // It's not pretty, but it works.
        List<String> text = new ArrayList<>();
        try {
            Field field = views.getClass().getDeclaredField("mActions");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            ArrayList<Parcelable> actions = (ArrayList<Parcelable>) field.get(views);
            // Find the setText() and setTime() reflection actions
            for (Parcelable p : actions) {
                Parcel parcel = Parcel.obtain();
                p.writeToParcel(parcel, 0);
                parcel.setDataPosition(0);
                // The tag tells which type of action it is (2 is ReflectionAction, from the source)
                int tag = parcel.readInt();
                if (tag != 2) continue;
                // View ID
                parcel.readInt();
                String methodName = parcel.readString();
                if (null == methodName) {
                    continue;
                } else if (methodName.equals("setText")) {
                    // Parameter type (10 = Character Sequence)
                    parcel.readInt();
                    // Store the actual string
                    String t = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel).toString().trim();
                    text.add(t);
                }
                parcel.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

}

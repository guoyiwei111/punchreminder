package com.example.guoyiwei.dk.providers;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by guoyiwei on 2016/10/30.
 */
public class HttpProvider {
    public static String toper;
    public static String Get(String pth) {
        try {
            // 1.声明访问的路径， url 网络资源 http ftp rtsp
            URL url = new URL(pth);
            // 2.通过路径得到一个连接 http的连接
            HttpURLConnection conn = (HttpURLConnection) url
                    .openConnection();
            // 3.判断服务器给我们返回的状态信息。
            // 200 成功 302 从定向 404资源没找到 5xx 服务器内部错误
            int code = conn.getResponseCode();
            if (code == 200) {
                // 4.利用链接成功的 conn 得到输入流
                InputStream is = conn.getInputStream();// png的图片

                // 5. ImageView设置Bitmap,用BitMap工厂解析输入流
                // 为输出创建BufferedReader
                //得到读取的内容(流)
                String resultData = "";
                InputStreamReader in = new InputStreamReader(is);
                // 为输出创建BufferedReader
                BufferedReader buffer = new BufferedReader(in);
                String inputLine = null;
                //使用循环来读取获得的数据
                while (((inputLine = buffer.readLine()) != null))
                {
                    //我们在每一行后面加上一个"\n"来换行
                    resultData += inputLine + "\n";
                }
                //关闭InputStreamReader
                in.close();
                //关闭http连接
                conn.disconnect();
                //设置显示取得的内容
                return  resultData;
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }


    }



    public static String Post(String pth,String content) {
        try {
            System.out.println("path---------->"+pth+"\ncontent----------->"+content);
            // 1.声明访问的路径， url 网络资源 http ftp rtsp
            URL url = new URL(pth);
            // 2.通过路径得到一个连接 http的连接
            HttpURLConnection conn = (HttpURLConnection) url
                    .openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            conn.setRequestProperty("Content-Type", "text/html");// 维持长连接

            conn.setRequestProperty("Charset", "UTF-8");
            conn.connect();
            String param= URLEncoder.encode(content,"UTF-8");

            DataOutputStream dos=new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(param);
            dos.flush();
            dos.close();
            int resultCode=conn.getResponseCode();
            if(HttpURLConnection.HTTP_OK==resultCode){
                StringBuffer sb=new StringBuffer();
                String readLine=new String();
                BufferedReader responseReader=new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                while((readLine=responseReader.readLine())!=null){
                    sb.append(readLine).append("\n");
                }
                responseReader.close();
                System.out.println(sb.toString());
                return sb.toString();
            }else{
                return "REEOR";
            }


        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }


    }
}

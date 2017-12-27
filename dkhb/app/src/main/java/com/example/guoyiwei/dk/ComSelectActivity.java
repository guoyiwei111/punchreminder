package com.example.guoyiwei.dk;

/**
 * Created by guoyiwei on 2017/6/18.
 */
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Handler;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.guoyiwei.dk.services.PreferencesService;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by guoyiwei on 2017/6/18.
 */
public class ComSelectActivity extends AppCompatActivity {
    private ListView lv_com_list;
    private AppAdapter mComAdapter;
    public Handler mHandler = new Handler();
    public final static int RESULT_CODE = 1;
    private static PreferencesService service;

    static String appName;

    static String packName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.componentselect);
        service = new PreferencesService(this);
        lv_com_list = (ListView) findViewById(R.id.lv_com_list);
        mComAdapter = new AppAdapter();
        lv_com_list.setAdapter(mComAdapter);

        lv_com_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                String activity =  mComAdapter.getData().get(arg2).name;
                String packageName = mComAdapter.getData().get(arg2).packageName;

                Intent intent = new Intent();
                intent.putExtra("ok", "yes");

                setResult(RESULT_CODE, intent);


                service.setCompName(appName,packageName,activity);
                finish();
            }

        });




        Intent intent = getIntent();
        appName  = intent.getStringExtra("appname");
        packName  = intent.getStringExtra("packname");
        try {
            ActivityInfo[] actInfo = ComSelectActivity.this.getPackageManager().getPackageInfo(packName, PackageManager.GET_ACTIVITIES).activities;
            //actInfo[0].launchMode LaunchMode.

            initComList(actInfo);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }



    }

    private void initComList(ActivityInfo[] actInfo){
        mComAdapter.setData(actInfo);

    }



    class AppAdapter extends BaseAdapter {

        List<ActivityInfo> myAppInfos = new ArrayList<ActivityInfo>();

        public void setData(ActivityInfo[] myAppInfos) {
            ArrayList<ActivityInfo> list = new ArrayList();
            for(int i=0;i<myAppInfos.length;i++){
                list.add(myAppInfos[i]);
            }
            this.myAppInfos = list;
            notifyDataSetChanged();
        }

        public List<ActivityInfo> getData() {
            return myAppInfos;
        }

        @Override
        public int getCount() {
            if (myAppInfos != null && myAppInfos.size() > 0) {
                return myAppInfos.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (myAppInfos != null && myAppInfos.size() > 0) {
                return myAppInfos.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder mViewHolder;
            ActivityInfo myAppInfo = myAppInfos.get(position);
            if (convertView == null) {
                mViewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_com_info, null);
               // mViewHolder.iv_app_icon = (ImageView) convertView.findViewById(R.id.iv_com_icon);
                mViewHolder.tx_app_name = (TextView) convertView.findViewById(R.id.tv_com_name);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }

            //mViewHolder.iv_app_icon.setImageDrawable(myAppInfo.getImage());
            mViewHolder.tx_app_name.setText(myAppInfo.name);
            return convertView;
        }

        class ViewHolder {

            ImageView iv_app_icon;
            TextView tx_app_name;
        }
    }


}

package com.example.guoyiwei.dk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Handler;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guoyiwei.dk.model.MyAppInfo;
import com.example.guoyiwei.dk.services.PreferencesService;
import com.example.guoyiwei.dk.util.ApkTool;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by guoyiwei on 2017/6/18.
 */
public class AppSelectActivity extends AppCompatActivity {
    private ListView lv_app_list;
    private AppAdapter mAppAdapter;
    public Handler mHandler = new Handler();
    public final static int REQUEST_CODE = 1;

    private static PreferencesService service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = new PreferencesService(this);
        setContentView(R.layout.appselect);
        lv_app_list = (ListView) findViewById(R.id.lv_app_list);
        mAppAdapter = new AppAdapter();
        lv_app_list.setAdapter(mAppAdapter);
        lv_app_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                //Intent targetapp = AppSelectActivity.this.getPackageManager().getLaunchIntentForPackage(mAppAdapter.getData().get(arg2).packName);
                //startActivity(targetapp);
                boolean debug=false;
                if(!debug &&  AboutActivity.num<10){
                    service.setAppName(mAppAdapter.getData().get(arg2).getAppName(),mAppAdapter.getData().get(arg2).packName);
                    Toast.makeText(AppSelectActivity.this, "你已选择： "+ mAppAdapter.getData().get(arg2).getAppName(), Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Intent intent = new Intent();
                    intent.putExtra("packname",mAppAdapter.getData().get(arg2).packName);
                    intent.putExtra("appname", mAppAdapter.getData().get(arg2).getAppName());
                    intent.setClass(AppSelectActivity.this, ComSelectActivity.class);
                    AppSelectActivity.this.startActivityForResult(intent,REQUEST_CODE);
                }



            }

        });


        registerForContextMenu(lv_app_list);//为ListView添加上下文菜单

        initAppList();
    }
    List<MyAppInfo> appInfos=null;
    private void initAppList(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                //扫描得到APP列表


                 appInfos = ApkTool.scanLocalInstallAppList(AppSelectActivity.this.getPackageManager());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAppAdapter.setData(appInfos);
                    }
                });
            }
        }.start();
    }



    class AppAdapter extends BaseAdapter {

        List<MyAppInfo> myAppInfos = new ArrayList<MyAppInfo>();

        public void setData(List<MyAppInfo> myAppInfos) {
            this.myAppInfos = myAppInfos;
            notifyDataSetChanged();
        }

        public List<MyAppInfo> getData() {
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
            MyAppInfo myAppInfo = myAppInfos.get(position);
            if (convertView == null) {
                mViewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.item_app_info, null);
                mViewHolder.iv_app_icon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
                mViewHolder.tx_app_name = (TextView) convertView.findViewById(R.id.tv_app_name);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }

            mViewHolder.iv_app_icon.setImageDrawable(myAppInfo.getImage());
            mViewHolder.tx_app_name.setText(myAppInfo.getAppName());
            return convertView;
        }

        class ViewHolder {

            ImageView iv_app_icon;
            TextView tx_app_name;
        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode==REQUEST_CODE)
        {
            if(resultCode==ComSelectActivity.RESULT_CODE)
            {
                Bundle bundle = data.getExtras();
                String str = bundle.getString("ok");
                if(str!=null && str.equals("yes")){
                    finish();
                }
                //Toast.makeText(HelloWorldActivity.this, str, Toast.LENGTH_LONG).show();
            }
        }
    }






    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        // TODO Auto-generated method stub
        try {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            //JSONObject object = mData.getJSONObject(info.position);
            //menu.setHeaderTitle(object.getString("title"));
        } catch (Exception e) {
            return;
        }
        menu.add(0, 1, 0, "自选模式");
        //menu.add(0, 2, 0, "删除");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        AdapterView.AdapterContextMenuInfo itemInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int index=adapterContextMenuInfo.position;
        //itemInfo.position;
        //JSONObject object = mData.getJSONObject(itemInfo.position);
        switch (item.getItemId()) {
            case 1:
                //Toast.makeText(this, object.getString("title"), Toast.LENGTH_LONG).show();

                Intent intent = new Intent();
                intent.putExtra("packname",mAppAdapter.getData().get(index).packName);
                intent.putExtra("appname", mAppAdapter.getData().get(index).getAppName());
                intent.setClass(AppSelectActivity.this, ComSelectActivity.class);
                AppSelectActivity.this.startActivityForResult(intent,REQUEST_CODE);
                break;
            default:
                break;
        }

        return super.onContextItemSelected(item);
    }







}

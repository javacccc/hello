package com.bjw.MainFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.bjw.R;
import com.bjw.Service.UpdateVersionService;
import com.bjw.bean.AllLabRoomInfo;
import com.bjw.bean.Version;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bjw.Common.Connection.getConnection;
import static com.bjw.Common.StaticConfig.LabID;
import static com.bjw.Common.StaticConfig.remoteUrl;

/**
 * 创建人：wxdn
 * 创建时间：2017/12/9
 * 功能描述：
 */

public class ChooseLabActivity extends Activity{
        @BindView (R.id.gview) GridView gview;
        private List<Map<String, Object>> data_list;
        private SimpleAdapter sim_adapter;
        String localVersionName;
        int localVersionCode;
        List<AllLabRoomInfo>allLabRoomInfoList=new ArrayList<>();
        Intent intentForUpdateVersion;
        BroadcastReceiverToUpdate broadcastReceiverToUpdate;
    /*************************************************
     *@description： 去服务器端获取相应的版本与本地的版本作比较
     * 数据包括，版本号，版本名称，该版本所对应的apk文件的路径
    *************************************************/
        public Handler handlerForGetVersion = new Handler() {
        public void handleMessage(Message msg) {
            String response = msg.obj.toString();
            String VersionBeans = null;
            try {
                JSONObject jsonObject = new JSONObject(response.toString());
                VersionBeans = jsonObject.getString("result");
            } catch (JSONException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
            if (VersionBeans != null) {
                Gson gson = new Gson();
                JsonParser parser = new JsonParser();
                JsonArray Jarray = parser.parse(VersionBeans).getAsJsonArray();
                Version version;
                for (JsonElement obj : Jarray) {
                    version = gson.fromJson(obj, Version.class);
                    //取出版本号然后与相应的本地版本进行比较
                    int versionCode= version.getVersionCode();
                    if(versionCode==localVersionCode)
                    {
                        Toast.makeText(getBaseContext(), "当前已是最新版本", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //进行相应的更新操作
//                        开启下载的线程
                        String apkPath = version.getApkPath();
                        //开启一个下载的服务。
                    }
                }
            }
        }
    };
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_choose_labroom);
//            获取root权限
//            String apkRoot="chmod 777 "+getPackageCodePath();
//            SystemManager.RootCommand(apkRoot);
//            检验版本更新
            CheckVersionUpdate();
            ButterKnife.bind(this);
//            gview = (GridView) findViewById(R.id.gview);
            data_list = new ArrayList<Map<String, Object>>();
            initData();
            String [] from ={"image","text"};
            int [] to = {R.id.image,R.id.text};
            sim_adapter = new SimpleAdapter(this, data_list, R.layout.item_choose_labroom, from, to);
            gview.setAdapter(sim_adapter);
            registerForUpdate();
            //点击事件的处理
            gview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View arg1, int idx, long arg3) {
                    // TODO Auto-generated method stub
                    LabID=allLabRoomInfoList.get(idx).getLab_room_id();
                    //将第一次进入时的LabId进行保存
                    SharedPreferences mSharedPreferences = getSharedPreferences("YCLABID", Context.MODE_PRIVATE);
                    final SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putInt("labId",LabID);
                    editor.commit();
                    Intent intent=new Intent(getBaseContext(),LoadingActivity.class);
                    startActivity(intent);
                }
            });
        }
        /*************************************************
         *@description： 初始化实验室数据与界面
        *************************************************/
        public void initData()
        {
            AllLabRoomInfo allLabRoomInfo1=new AllLabRoomInfo(895,R.drawable.a305_1,"A305");
            AllLabRoomInfo allLabRoomInfo2=new AllLabRoomInfo(899,R.drawable.a308_1,"A308");
            AllLabRoomInfo allLabRoomInfo3=new AllLabRoomInfo(900,R.drawable.a309_1,"A309");
            AllLabRoomInfo allLabRoomInfo4=new AllLabRoomInfo(904,R.drawable.a312_1,"A312");
            AllLabRoomInfo allLabRoomInfo5=new AllLabRoomInfo(902,R.drawable.a313_1,"A311/A313");
            AllLabRoomInfo allLabRoomInfo6=new AllLabRoomInfo(905,R.drawable.a314_1,"A314");
            AllLabRoomInfo allLabRoomInfo7=new AllLabRoomInfo(906,R.drawable.a315_1,"A315");
            AllLabRoomInfo allLabRoomInfo8=new AllLabRoomInfo(918,R.drawable.a410e_1,"A410西");
            AllLabRoomInfo allLabRoomInfo9=new AllLabRoomInfo(919,R.drawable.a410w_1,"A410东");
            AllLabRoomInfo allLabRoomInfo10=new AllLabRoomInfo(921,R.drawable.a412_1,"A412");
            allLabRoomInfoList.add(allLabRoomInfo1);
            allLabRoomInfoList.add(allLabRoomInfo2);
            allLabRoomInfoList.add(allLabRoomInfo3);
            allLabRoomInfoList.add(allLabRoomInfo4);
            allLabRoomInfoList.add(allLabRoomInfo5);
            allLabRoomInfoList.add(allLabRoomInfo6);
            allLabRoomInfoList.add(allLabRoomInfo7);
            allLabRoomInfoList.add(allLabRoomInfo8);
            allLabRoomInfoList.add(allLabRoomInfo9);
            allLabRoomInfoList.add(allLabRoomInfo10);
            data_list = new ArrayList<Map<String, Object>>();
            for(int i=0;i<allLabRoomInfoList.size();i++){
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("image", allLabRoomInfoList.get(i).getLab_room_local_image());
                map.put("text", allLabRoomInfoList.get(i).getLab_room_location());
                data_list.add(map);
            }
        }
        /*************************************************
         *@description： 检验版本更新
        *************************************************/
        public void CheckVersionUpdate()
        {
            //获取本地的版本
            PackageManager pm = getBaseContext().getPackageManager();
            PackageInfo pi = null;
            try {
                pi = pm.getPackageInfo(getBaseContext().getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            localVersionName = pi.versionName;
            localVersionCode = pi.versionCode;
            ApplicationInfo appInfo = pi.applicationInfo;
            String appName = pm.getApplicationLabel(appInfo).toString();
            String packname = appInfo.packageName;
            String version = pi.versionName;
            /*************************************************
             *@description： 检测用于版本的更新
             *************************************************/
            //去服务器端获取版本号
            if(false)
            {
                URL url = null;
                try {
                    url = new URL(remoteUrl + "version/");
                    getConnection(url, handlerForGetVersion);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            if(false)
            {
                final Version versionBean=new Version(2,"新版","地址");
                int versionCode=versionBean.getVersionCode();
                if(versionCode==localVersionCode)
                {
                    Toast.makeText(getBaseContext(), "当前已是最新版本", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    AlertDialog dialog = new AlertDialog.Builder(this).setTitle("提示")
                            .setNegativeButton("取消", null).setPositiveButton("确定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //处理确认按钮的点击事件
                                            String apkPath=versionBean.getApkPath();
                                            //开启一个下载的服务。
                                            intentForUpdateVersion=new Intent(getBaseContext(),UpdateVersionService.class);
                                            intentForUpdateVersion.putExtra("url","http://www.huiqu.co/public/download/apk/huiqu.apk");
                                            getBaseContext().startService(intentForUpdateVersion);


                                        }
                                    }).setMessage("当前有新版本，是否去下载").create();
                    dialog.show();
                }
            }
        }
    /*************************************************
     *@description： 当数据更新的时候回到选择实验室的界面后
     * 查看里面是否有数据SharePreference就直接进入里面
     *************************************************/
    @Override
    protected void onResume() {
        SharedPreferences  sp = getSharedPreferences("YCLABID", Context.MODE_PRIVATE);
        LabID= sp.getInt("labId",0);
        if(LabID!=0)
        {
            Intent intent10=new Intent(getBaseContext(),LoadingActivity.class);
            startActivity(intent10);
        }
        super.onResume();
    }
    /*************************************************
     *@description： 注册接收播放本地的广播
     *************************************************/
    public void registerForUpdate()
    {
        broadcastReceiverToUpdate=new BroadcastReceiverToUpdate();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(6);
        filter.addAction("com.BrocastForUPDATE");
        registerReceiver(broadcastReceiverToUpdate, filter);
    }
    /*************************************************
     *@description： 接收安装apk文件
     *************************************************/
    public class BroadcastReceiverToUpdate extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
//            String path=intent.getStringExtra("path");
            String path="/mnt/internal_sd/更新的包/t1.apk";
            installApk(path);
        }
    }
    /*************************************************
     *@description： 将下载的apk文件进行安装
    *************************************************/
    public void installApk(String path)
    {
        Intent intent1 = new Intent(Intent.ACTION_VIEW);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.setDataAndType(Uri.parse("file://"+path),"application/vnd.android.package-archive");
        startActivity(intent1);
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}

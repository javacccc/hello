package com.bjw.MainFragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.bjw.Common.NetWorkStateReceiver;
import com.bjw.R;
import com.bjw.Service.GetCourseAndCardInfoService;

/**
 * 创建人：wxdn
 * 创建时间：2017/11/16
 * 功能描述：
 */

public class LoadingActivity extends Activity {
    static BroadcastReceiverToEnterInto broadcastReceiverToEnterInto=null;
    BroadcastReceiverToExitFromMain broadcastReceiverToExitFromMain;
    NetWorkStateReceiver netWorkStateReceiver;
    Intent intentforEnterinto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        intentforEnterinto=new Intent(getBaseContext(),GetCourseAndCardInfoService.class);
        getBaseContext().startService(intentforEnterinto);
        registerForEnterInto();
        registerForExitFromMain();
    }
    /*************************************************
     *@description： 注册的广播
     *************************************************/
    public void registerForEnterInto()
    {
        broadcastReceiverToEnterInto=new BroadcastReceiverToEnterInto();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(5);
        filter.addAction("com.EnterInto");
        registerReceiver(broadcastReceiverToEnterInto, filter);
    }
    /*************************************************
     *@description： 定时进入主界面的广播接收器
     *************************************************/
    public class BroadcastReceiverToEnterInto extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String enterintoFlag=intent.getStringExtra("enterinto");
            if(enterintoFlag.equals("进入"))
            {
                Intent intentforenter=new Intent(LoadingActivity.this,MainFragment.class);
                intentforenter.putExtra("flag","数据获取成功");
                startActivity(intentforenter);
//                销毁进入的服务与广播
                getBaseContext().stopService(intentforEnterinto);
                unregisterReceiver(broadcastReceiverToEnterInto);
            }
            else
            {
                Intent intentforenter=new Intent(LoadingActivity.this,MainFragment.class);
                intentforenter.putExtra("flag","数据获取失败，请检查相应的配置");
                startActivity(intentforenter);
                //                销毁进入的服务与广播
                getBaseContext().stopService(intentforEnterinto);
                unregisterReceiver(broadcastReceiverToEnterInto);
            }
        }
    }

    /*************************************************
     *@description： 注册由主Activity退出的广播
     *************************************************/
    public void registerForExitFromMain()
    {
        broadcastReceiverToExitFromMain=new BroadcastReceiverToExitFromMain();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(5);
        filter.addAction("com.WarnninLoad");
        registerReceiver(broadcastReceiverToExitFromMain, filter);
    }
    /*************************************************
     *@description： 定时退出的广播接收器
     *************************************************/
    public class BroadcastReceiverToExitFromMain extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            finish();
        }
    }
    /*************************************************
     *@description： 注册相应网络的监听广播
     *************************************************/
    @Override
    protected void onResume() {
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, filter);
        super.onResume();
    }
    //onPause()方法注销
    @Override
    protected void onPause() {
        getBaseContext().stopService(intentforEnterinto);
        unregisterReceiver(netWorkStateReceiver);
        super.onPause();
    }
}

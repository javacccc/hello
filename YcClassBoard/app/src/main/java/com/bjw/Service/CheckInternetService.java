package com.bjw.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.IBinder;

import com.bjw.Common.DatebaseHelper;

/*************************************************
 *@date：2017/11/17
 *@author：  zxj
 *@description： 时间变化的而服务
*************************************************/

public class CheckInternetService extends Service {
    private CheckInternetThread checkInternetThread;
    private DatebaseHelper myDBHelper;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.checkInternetThread = new CheckInternetThread();
        this.checkInternetThread.start();
        return START_STICKY;
    }
    /*************************************************
     *@description： 检测网络的线程
     *************************************************/
    private class CheckInternetThread extends Thread {
        @Override
        public void run() {
            while (true) {
                if(checkInterNet()==1) {
//                    Intent intent = new Intent();
////                intent.putExtra("currentTime", currentTime);
//                    intent.setAction("com.ChangeTime");
//                    sendBroadcast(intent);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /*************************************************
     *@description： 检测相应的网络连接的方法
    *************************************************/
    public int checkInterNet()
    {
        ConnectivityManager connMgr = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取所有网络连接的信息
        Network[] networks = connMgr.getAllNetworks();
        //用于存放网络连接信息
        StringBuilder sb = new StringBuilder();
        //通过循环将网络信息逐个取出来
        for (int i=0; i < networks.length; i++){
            //获取ConnectivityManager对象对应的NetworkInfo对象
            NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
            sb.append(networkInfo.getTypeName() + " connect is " + networkInfo.isConnected());
        }
        int flag=0;
        if(networks.length==0)//网络出错
        {
            flag=1;
        }
        else       //网络正常
        {
            flag=2;
        }
        return flag;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

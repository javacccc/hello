package com.bjw.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.bjw.bean.LabMessage;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import static com.bjw.Common.Connection.getConnection;
import static com.bjw.Common.StaticConfig.LabID;
import static com.bjw.Common.StaticConfig.remoteUrl;
import static com.bjw.DynamicInfo.MessageFragment.labMessageList;

/*************************************************
 *@date：2018/1/9
 *@author：  zxj
 *@description： 动态更新数据
*************************************************/

public class RefreshMessageService extends Service {
    boolean isDes=false;
    private TimeToUpdateLabMesThread timeToUpdateLabMesThread;
    /*************************************************
     *@description： 处理获取到的数据
    *************************************************/
    public Handler handlerForGetLabMesInfo = new Handler() {
        public void handleMessage(Message msg) {
            String response = msg.obj.toString();
            String labMessages = null;
            try {
                JSONObject jsonObject = new JSONObject(response.toString());
                labMessages = jsonObject.getString("result");
            } catch (JSONException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
            if (labMessages != null) {
                labMessageList.clear();
                Gson gson = new Gson();
                JsonParser parser = new JsonParser();
                JsonArray Jarray = parser.parse(labMessages).getAsJsonArray();
                LabMessage labMessage = null;
                for (JsonElement obj : Jarray) {
                    labMessage = gson.fromJson(obj, LabMessage.class);
                    labMessageList.add(labMessage);
                }
                Intent intent = new Intent();
                intent.putExtra("RefreshForMes", "更新界面数据变化");
                intent.setAction("com.RefreshForMes");
                sendBroadcast(intent);
            }
        }
    };
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timeToUpdateLabMesThread = new TimeToUpdateLabMesThread();
        timeToUpdateLabMesThread.start();
        return START_STICKY;
    }
    /*************************************************
     *@description： 定时去服务器上面获取数据的线程
     *************************************************/
    private class TimeToUpdateLabMesThread extends Thread {
        @Override
        public void run() {
            while (!isDes) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    labMessageList.clear();
                    URL url = new URL(remoteUrl + "labRoomInfo/listNotice/" + LabID);
                    getConnection(url, handlerForGetLabMesInfo);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public void onDestroy() {
        isDes=true;
//        Log.i("zxj", "销毁操作");
        super.onDestroy();
    }
}

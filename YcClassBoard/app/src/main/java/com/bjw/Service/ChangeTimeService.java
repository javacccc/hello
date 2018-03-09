package com.bjw.Service;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import com.bjw.Common.DatebaseHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.bjw.Common.StaticConfig.TimeToUpdate;
import static com.bjw.Common.StaticConfig.databaseVersion;
import static com.bjw.Common.StaticConfig.databasename;

/*************************************************
 *@date：2017/11/17
 *@author：  zxj
 *@description： 时间变化的而服务
*************************************************/

public class ChangeTimeService extends Service {
    private ChangeTimeThread changeTimeThread;
    private DatebaseHelper myDBHelper;
    private int flagToStopThread=0;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.changeTimeThread = new ChangeTimeThread();
        this.changeTimeThread.start();
        return START_STICKY;
    }
    /*************************************************
     *@description： 定时操作的线程
     *************************************************/
    private class ChangeTimeThread extends Thread {
        @Override
        public void run() {
            while (flagToStopThread==0) {
                Calendar c = Calendar.getInstance();
                String weekday=getWeekOfDate(c);
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                month=month+1;
                int date = c.get(Calendar.DATE);
                int hour = c.get(Calendar.HOUR_OF_DAY);
                String minute1="";
                String sencond1="";
                int minute = c.get(Calendar.MINUTE);
                if(minute<10) {
                     minute1="0"+minute;
                }
                else
                {
                    minute1=""+minute;
                }
                int second = c.get(Calendar.SECOND);
                if(second<10) {
                    sencond1="0"+second;
                }
                else
                {
                    sencond1=""+second;
                }
                String currentTime=year+"年"+month+"月"+date+"日"+" "+hour+":"+minute1+":"+sencond1+" "+weekday;
                Intent intent = new Intent();
                intent.putExtra("currentTime", currentTime);
                intent.setAction("com.ChangeTime");
                sendBroadcast(intent);
                //到达特定的时间时候清空数据库
                /*************************************************
                 *@description： 到达第二天了需要更新相应的数据，
                 * 直接发送相应的广播销毁MainFragment，然后MainFragment
                 * 发送广播给LoadingActivity,销毁LoadingActivty
                 * 跳到选择实验室的界面，然后在界面上面直接获取刚才进入时的
                 * LabID再次进入就可以获取当前的数据
                *************************************************/
                String currentTimeForClearSQlite=hour+":"+minute1+":"+sencond1;
                DateFormat df = new SimpleDateFormat("HH:mm:ss");
                try {
                    Date cT = df.parse(currentTimeForClearSQlite);
                    Date TimeToClearSQlite=df.parse(TimeToUpdate);
                    if (cT.getTime()==TimeToClearSQlite.getTime())
                    {
                        myDBHelper = new DatebaseHelper(getBaseContext(), databasename, null, databaseVersion);
                        final SQLiteDatabase db = myDBHelper.getReadableDatabase();
                        db.execSQL("DELETE FROM StudentCardTable");
                        //发送广播给MainActivity
                        Intent intent1 = new Intent();
                        intent1.putExtra("Update", "一天不关闭时更新数据");
                        intent1.setAction("com.Update");
                        sendBroadcast(intent1);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static String getWeekOfDate(Calendar calendar) {
        String[] weekOfDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0){
            w = 0;
        }
        return weekOfDays[w];
    }
    @Override
    public void onDestroy() {
        flagToStopThread=1;
        super.onDestroy();
//        Log.i("zxj", "销毁时间 ");
    }
}

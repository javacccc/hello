package com.bjw.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.bjw.Common.StaticConfig.lessonTables;
/*************************************************
 *@date：2017/11/17
 *@author：  zxj
 *@description： 定时器的服务
*************************************************/

public class ChangeCourseService extends Service {
    private TimeThread timeThread;
    List<Date> timeforbegins=new ArrayList<>();
    List<Date> timeforends=new ArrayList<>();
    private int flagToStopThread=0;
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
      //将得到的课表时间数据加入到相应的界面上便于
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        for(int i=0;i<lessonTables.size();i++)
        {
            try {
                Date dateforbegin=df.parse(lessonTables.get(i).getCourse_beginning_time());
                Date dateforend=df.parse(lessonTables.get(i).getCourse_ending_time());
                timeforbegins.add(dateforbegin);
                timeforends.add(dateforend);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        this.timeThread = new TimeThread();
        this.timeThread.start();
        return START_STICKY;
    }
    /*************************************************
     *@description： 定时属性课程界面的操作的线程
     *************************************************/
    private class TimeThread extends Thread {
        @Override
        public void run() {
            while (flagToStopThread==0) {
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                String minute1=" ";
                String second1="";
                if(minute<10) {
                    minute1="0"+minute;
                }
                else
                {
                    minute1=""+minute;
                }
                int second = c.get(Calendar.SECOND);
                if(second<10) {
                    second1="0"+second;
                }
                else
                {
                    second1=""+second;
                }
                //当前的时间与课表的时间进行比较，然后动态刷新数据
                String currentTime=hour+":"+minute1+":"+second1;
                DateFormat df = new SimpleDateFormat("HH:mm:ss");
                try {
                    Date currentTimeforDate = df.parse(currentTime);
                    int flag=0;
                    for(int i=0;i<lessonTables.size();i++)
                    {
                        if (timeforbegins.get(i).getTime()<currentTimeforDate.getTime()&&currentTimeforDate.getTime()<timeforends.get(i).getTime()) {
                            Intent intent = new Intent();
                            String j=i+"";
                            intent.putExtra("Time",j);
                            intent.setAction("com.Time");
                            sendBroadcast(intent);
                            flag=1;
                            break;
                        }
                    }
                    if(flag==0)
                    {
                        Intent intent = new Intent();
                        intent.putExtra("Time","1314520");
                        intent.setAction("com.Time");
                        sendBroadcast(intent);
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
    @Override
    public void onDestroy() {
        flagToStopThread=1;
//        Log.i("zxj", "销毁定时器操作");
        super.onDestroy();
    }
}

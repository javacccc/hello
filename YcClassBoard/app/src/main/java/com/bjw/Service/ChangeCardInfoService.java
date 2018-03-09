package com.bjw.Service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.bjw.Common.DatebaseHelper;
import com.bjw.bean.StudentCard;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.bjw.Common.StaticConfig.LabID;
import static com.bjw.Common.StaticConfig.databaseVersion;
import static com.bjw.Common.StaticConfig.databasename;
import static com.bjw.Common.StaticConfig.lessonTables;
import static com.bjw.Common.StaticConfig.studentCards;
import static com.bjw.Common.StaticConfig.studentCardsTemp;
/*************************************************
 *@date：2017/11/17
 *@author：  zxj
 *@description： 得到卡号的服务
*************************************************/

public class ChangeCardInfoService extends Service {
    private getCardInfoThread getCardInfoThread;
    List<Date> timeforbegins=new ArrayList<>();
    List<Date> timeforends=new ArrayList<>();
    int flagofIndex;//记录当前所上课程的下标
    DatebaseHelper myDBHelper;
    private int flagToStopThread=0;
    /*************************************************
     *@description： 发送广播的线程
     *************************************************/
    public Handler handlerForSendReCover = new Handler() {
        public void handleMessage(Message msg)
        {
            Intent intent = new Intent();
            intent.putExtra("cardRecoverNum",studentCardsTemp.size()+"");
            intent.setAction("com.cardRecoverNum");
            sendBroadcast(intent);
        }
    };
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
        this.getCardInfoThread = new getCardInfoThread();
        this.getCardInfoThread.start();
        return START_STICKY;
    }
    /*************************************************
     *@description： 定时获取卡号信息的操作的线程
     *************************************************/
    private class getCardInfoThread extends Thread {
        @Override
        public void run() {
            myDBHelper = new DatebaseHelper(getBaseContext(),databasename, null, databaseVersion);
            final SQLiteDatabase db = myDBHelper.getReadableDatabase();
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
                //当前的时间与课表的时间进行比较，然后动态获取相应的卡号数据
                String currentTime=hour+":"+minute1+":"+second1;
                DateFormat df = new SimpleDateFormat("HH:mm:ss");
                try {
                    Date currentTimeforDate = df.parse(currentTime);
                    for(int i=0;i<lessonTables.size();i++)
                    {
                        //判断当前课程之前存在的数据进行清空，包括之前所存在的卡号，还有刷卡的缓存数据
                        if (timeforbegins.get(i).getTime()==currentTimeforDate.getTime()) {
                            flagofIndex=i;
                            studentCards.clear();
                            studentCardsTemp.clear();
                            Cursor cursor = db.rawQuery("select * from ALLDayStudentsCardTable where course_number=? and lab_room_id=? and start_class=?",new String[]{lessonTables.get(i).getCourse_number(),LabID+"",lessonTables.get(i).getStart_class()+""});
                            while(cursor.moveToNext())
                            {
                                int start_class = cursor.getInt(cursor.getColumnIndex("start_class"));
                                int end_class = cursor.getInt(cursor.getColumnIndex("end_class"));
                                int lab_room_id = cursor.getInt(cursor.getColumnIndex("lab_room_id"));
                                String student_number = cursor.getString(cursor.getColumnIndex("student_number"));
                                String course_number = cursor.getString(cursor.getColumnIndex("course_number"));
                                String card_no = cursor.getString(cursor.getColumnIndex("card_no"));
                                String student_name = cursor.getString(cursor.getColumnIndex("student_name"));
                                studentCards.add(new StudentCard(student_name,student_number,card_no,
                                        lab_room_id,course_number,start_class,end_class));
                            }
                            Log.i("zxj","------------------------------"+studentCards.size()+"");
                            cursor.close();
                            //切换数据的时候，需要提示用户，不然当前的数据刷卡的时候无法进行相应的更新
                            Intent intent = new Intent();
                            intent.putExtra("Warnning", "上课了，可以刷卡");
                            intent.setAction("com.afterClassWarnning");
                            sendBroadcast(intent);
                            break;
                        }
                        //当第一次进入的时候，或者用户可能误操作退出程序的时候需要进行相应的数据获取与相应的二数据恢复
                        if (studentCards.size() == 0) {
                            if (timeforbegins.get(i).getTime() < currentTimeforDate.getTime() && currentTimeforDate.getTime() < timeforends.get(i).getTime()) {
                                flagofIndex = i;
                                studentCards.clear();
                                studentCardsTemp.clear();
                                //获取当前数据库里面的表里面的上该节课的学生的卡号
                                Cursor cursor = db.rawQuery("select * from ALLDayStudentsCardTable where course_number=? and lab_room_id=? and start_class=?",new String[]{lessonTables.get(i).getCourse_number(),LabID+"",lessonTables.get(i).getStart_class()+""});
                                while(cursor.moveToNext())
                                {
                                    int start_class = cursor.getInt(cursor.getColumnIndex("start_class"));
                                    int end_class = cursor.getInt(cursor.getColumnIndex("end_class"));
                                    int lab_room_id = cursor.getInt(cursor.getColumnIndex("lab_room_id"));
                                    String student_number = cursor.getString(cursor.getColumnIndex("student_number"));
                                    String course_number = cursor.getString(cursor.getColumnIndex("course_number"));
                                    String card_no = cursor.getString(cursor.getColumnIndex("card_no"));
                                    String student_name = cursor.getString(cursor.getColumnIndex("student_name"));
                                    studentCards.add(new StudentCard(student_name,student_number,card_no,
                                            lab_room_id,course_number,start_class,end_class));
                                }
                                cursor.close();
                                //获取当前该节课里面已经刷过卡的学生的卡号加入到一刷卡的队列库里
                                Cursor te = db.rawQuery("select * from StudentCardTable",null);
                                while (te.moveToNext())
                                {
                                    Log.i("zxj",te.getInt(te.getColumnIndex("start_class"))+"");
                                    Log.i("zxj",te.getInt(te.getColumnIndex("lab_room_id"))+"");
                                    Log.i("zxj",te.getString(te.getColumnIndex("course_number"))+"");
                                }
                                Cursor cursorforrecover = db.rawQuery("select * from StudentCardTable where course_number=? and lab_room_id=? and start_class=?",new String[]{lessonTables.get(i).getCourse_number(),LabID+"",lessonTables.get(i).getStart_class()+""});
                                while(cursorforrecover.moveToNext())
                                {
                                    int start_class = cursorforrecover.getInt(cursor.getColumnIndex("start_class"));
                                    int end_class = cursorforrecover.getInt(cursor.getColumnIndex("end_class"));
                                    int lab_room_id = cursorforrecover.getInt(cursor.getColumnIndex("lab_room_id"));
                                    String student_number = cursorforrecover.getString(cursor.getColumnIndex("student_number"));
                                    String course_number = cursorforrecover.getString(cursor.getColumnIndex("course_number"));
                                    String card_no = cursorforrecover.getString(cursor.getColumnIndex("card_no"));
                                    String student_name = cursorforrecover.getString(cursor.getColumnIndex("student_name"));
                                    studentCardsTemp.add(new StudentCard(student_name,student_number,card_no,
                                            lab_room_id,course_number,start_class,end_class));
                                }
                                //发送消息让去发送广播
                                handlerForSendReCover.sendEmptyMessage(1);
                                cursor.close();
                                break;
                            }
                        }
                        //在下课时间(当前的时间大于上节课结束的时间小于下节课起始的时间)的时候进行相应的操作包括清空卡号,发送广播来提示用户当前是下课时间
                        if((i+1)<=lessonTables.size()) {
                            if((currentTimeforDate.getTime()<timeforbegins.get(0).getTime())||(currentTimeforDate.getTime()>timeforends.get(lessonTables.size()-1).getTime()))
                            {
                                //下课时间就直接发送广播给界面，提示用户当前是下课时间
                                Intent intent = new Intent();
                                intent.putExtra("Warnning", "当前是下课时间，请勿刷卡");
                                intent.setAction("com.afterClassWarnning");
                                sendBroadcast(intent);
                                break;
                            }
                            if ((timeforends.get(i).getTime() < currentTimeforDate.getTime() && currentTimeforDate.getTime() < timeforbegins.get(i + 1).getTime())) {
                                //下课时间就直接发送广播给界面，提示用户当前是下课时间
                                Intent intent = new Intent();
                                intent.putExtra("Warnning", "当前是下课时间，请勿刷卡");
                                intent.setAction("com.afterClassWarnning");
                                sendBroadcast(intent);
                                break;
                            }
                        }
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
        super.onDestroy();
//        Log.i("zxj", "销毁卡号数据的服务 ");
    }
}

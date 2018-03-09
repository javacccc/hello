package com.bjw.Service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.bjw.Common.DatebaseHelper;
import com.bjw.Common.ExportToCSV;
import com.bjw.Common.GetCurrentTime;
import com.bjw.bean.LessonTable;
import com.bjw.bean.StudentCard;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.bjw.Common.Connection.getConnection;
import static com.bjw.Common.StaticConfig.FlagOfConnectTimeOut;
import static com.bjw.Common.StaticConfig.IsRealData;
import static com.bjw.Common.StaticConfig.LabID;
import static com.bjw.Common.StaticConfig.databaseVersion;
import static com.bjw.Common.StaticConfig.databasename;
import static com.bjw.Common.StaticConfig.lessonTables;
import static com.bjw.Common.StaticConfig.remoteUrl;
import static com.bjw.Common.StaticConfig.studentCards;
import static com.bjw.Common.StaticConfig.studentCardsTemp;

/*************************************************
 *@date：2017/11/17
 *@author：  zxj
 *@description： 进入的服务
*************************************************/

public class GetCourseAndCardInfoService extends Service {
    private EnterIntoThread enterIntoThread;
    private getALLCardInfoThread getALLCardInfoThread;
    private int flagForCourse=0;
    int count=1;
    DatebaseHelper myDBHelper;
    /*************************************************
     *@description： 解析获取到的课程的信息
     *************************************************/
    public Handler handlerForCourse = new Handler() {
        public void handleMessage(Message msg) {
            String response = msg.obj.toString();
            String courseDetails = null;
            try {
                JSONObject jsonObject = new JSONObject(response.toString());
                courseDetails = jsonObject.getString("result");
            } catch (JSONException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
            if (courseDetails != null) {
                Gson gson = new Gson();
                JsonParser parser = new JsonParser();
                JsonArray Jarray = parser.parse(courseDetails).getAsJsonArray();
                LessonTable lessonTable = null;
                myDBHelper = new DatebaseHelper(getBaseContext(), databasename, null,databaseVersion);
                final SQLiteDatabase db = myDBHelper.getReadableDatabase();
                int i=0;
                for (JsonElement obj : Jarray) {
                    lessonTable = gson.fromJson(obj, LessonTable.class);
                    /*************************************************
                     *@description： 将获取的课表数据数据写入到本地的数据库里面
                     *************************************************/
                    ContentValues values = new ContentValues();
                    values.put("lab_room_id", lessonTable.getLab_room_id());
                    values.put("course_present_people", lessonTable.getCourse_present_people());
                    values.put("start_class", lessonTable.getStart_class());
                    values.put("end_class", lessonTable.getEnd_class());
                    values.put("total_hour",lessonTable.getTotal_hour());
                    values.put("course_name",lessonTable.getCourse_name());
                    values.put("course_number", lessonTable.getCourse_number());
                    values.put("course_image_url", lessonTable.getCourse_image_url());
                    values.put("course_teacher_name", lessonTable.getCourse_teacher_name());
                    values.put("course_beginning_time", lessonTable.getCourse_beginning_time());
                    values.put("course_ending_time",lessonTable.getCourse_ending_time());
                    values.put("classes_number",lessonTable.getClasses_number());
                    values.put("course_program",lessonTable.getCourse_program());
                    values.put("course_date", lessonTable.getCourse_date());
                    db.insert("LessonTable", null, values);
                }
                Cursor c = db.rawQuery("select * from LessonTable order by start_class ASC", null);
                while (c.moveToNext())
                {
                    int s=c.getInt(c.getColumnIndex("start_class"));
                    Log.i("zxj", "handleMessage: "+s);
                    int lab_room_id = c.getInt(c.getColumnIndex("lab_room_id"));
                    int course_present_people = c.getInt(c.getColumnIndex("course_present_people"));
                    int start_class = c.getInt(c.getColumnIndex("start_class"));
                    int end_class = c.getInt(c.getColumnIndex("end_class"));
                    int total_hour = c.getInt(c.getColumnIndex("total_hour"));
                    String course_name = c.getString(c.getColumnIndex("course_name"));
                    String course_number = c.getString(c.getColumnIndex("course_number"));
                    String course_image_url = c.getString(c.getColumnIndex("course_image_url"));
                    String course_teacher_name = c.getString(c.getColumnIndex("course_teacher_name"));
                    String course_beginning_time = c.getString(c.getColumnIndex("course_beginning_time"));
                    String course_ending_time = c.getString(c.getColumnIndex("course_ending_time"));
                    String classes_number = c.getString(c.getColumnIndex("classes_number"));
                    String course_program = c.getString(c.getColumnIndex("course_program"));
                    String course_date = c.getString(c.getColumnIndex("course_date"));
                    lessonTables.add(new LessonTable(lab_room_id,course_present_people,start_class,end_class,total_hour,course_name,course_number,course_image_url,course_teacher_name,course_beginning_time,course_ending_time,classes_number,course_program,course_date)) ;
                }
                c.close();
                Cursor cursor =  db.query("LessonTable", null, null, null, null, null, null);
                //将数据导出
                ExportToCSV.ExportToCSV(cursor, "LessonTable.csv");
                cursor.close();
                flagForCourse=1;
//                getALLCardInfoThread = new getALLCardInfoThread();
//                getALLCardInfoThread.start();
            }
            Log.i("zxj", "课表数据获取");
            getALLCardInfoThread = new getALLCardInfoThread();
            getALLCardInfoThread.start();
//            else
//            {
//                flagForCourse=2;
//            }
//            //数据获取成功之后所做的操作
//            if(flagForCourse==1)
//            {
//                flagForCourse=0;
//                FlagOfConnectTimeOut=0;
//            }
//            else
//            {
//                Intent intent = new Intent();
//                intent.putExtra("enterinto", "进入");
//                intent.setAction("com.EnterInto");
//                sendBroadcast(intent);
//                flagForCourse=0;
//                FlagOfConnectTimeOut=0;
//            }
        }
    };
    /*************************************************
     *@description： 解析获取到的学生卡号的信息
     *************************************************/
    public Handler handlerForALLStudentCard = new Handler() {
        public void handleMessage(Message msg) {
            String response = msg.obj.toString();
            myDBHelper = new DatebaseHelper(getBaseContext(), databasename, null, databaseVersion);
            final SQLiteDatabase db = myDBHelper.getReadableDatabase();
            String studentsCards = null;
            try {
                JSONObject jsonObject = new JSONObject(response.toString());
                studentsCards = jsonObject.getString("result");
            } catch (JSONException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
            if (studentsCards != null) {
                Gson gson = new Gson();
                JsonParser parser = new JsonParser();
                JsonArray Jarray = parser.parse(studentsCards).getAsJsonArray();
                StudentCard studentCard = null;
                for (JsonElement obj : Jarray) {
                    studentCard = gson.fromJson(obj, StudentCard.class);
                    /*************************************************
                     *@description： 将获取的卡号数据数据写入到本地的数据库里面
                     *************************************************/
                    ContentValues values = new ContentValues();
                    values.put("student_name", studentCard.getStudent_name());
                    values.put("card_no", studentCard.getCard_no());
                    values.put("course_number", studentCard.getCourse_number());
                    values.put("student_number", studentCard.getStudent_number());
                    values.put("lab_room_id", studentCard.getLab_room_id());
                    values.put("start_class", studentCard.getStart_class());
                    values.put("end_class", studentCard.getEnd_class());
                    db.insert("ALLDayStudentsCardTable", null, values);
                }
//                测试卡号数据
                StudentCard studentCard1 = new StudentCard("陈爱辉", "320049", "38F0ECC4", LabID, "0036022070",5,8);
                ContentValues values = new ContentValues();
                values.put("student_name", studentCard1.getStudent_name());
                values.put("card_no", studentCard1.getCard_no());
                values.put("course_number", studentCard1.getCourse_number());
                values.put("student_number", studentCard1.getStudent_number());
                values.put("lab_room_id", studentCard1.getLab_room_id());
                values.put("start_class", studentCard1.getStart_class());
                values.put("end_class", studentCard1.getEnd_class());
                db.insert("ALLDayStudentsCardTable", null, values);
//                将卡号数据导出
                Cursor cursorforSave =  db.query("ALLDayStudentsCardTable", null, null, null, null, null, null);
                ExportToCSV.ExportToCSV(cursorforSave, "ALLDayStudentsCardTable.csv");
                cursorforSave.close();
            }
            //数据获取成功之后进入整个界面
            Intent intent = new Intent();
            intent.putExtra("enterinto", "进入");
            intent.setAction("com.EnterInto");
            sendBroadcast(intent);
        }
    };
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.enterIntoThread = new EnterIntoThread();
        this.enterIntoThread.start();
        return START_STICKY;
    }
    /*************************************************
     *@description： 进入主页面操作的线程
     * 得到课表的信息之后才可以进入
     *************************************************/
    private class EnterIntoThread extends Thread {
        @Override
        public void run() {
            //打开本地的数据库，获取可读的权限
            myDBHelper = new DatebaseHelper(getBaseContext(), databasename, null, databaseVersion);
            final SQLiteDatabase db = myDBHelper.getReadableDatabase();
            db.execSQL("DELETE FROM LessonTable");
            db.execSQL("DELETE FROM ALLDayStudentsCardTable");
            studentCards.clear();
            studentCardsTemp.clear();
            while (count==1) {
                //判断当前的数据库里面是否有数据
                int amount=0;
                String course_dateFromSQ="";
                Cursor c = db.rawQuery("select * from LessonTable", null);
                amount=c.getCount();
                //数据库不为空就直接取出相应的数据库里面的数据时间
                if(amount>0)
                {
                    while(c.moveToNext())
                    {
                        course_dateFromSQ = c.getString(c.getColumnIndex("course_date"));
                    }
                }
                String cDate= GetCurrentTime.getCurrentDate();
                //没有数据或者当前里面的数据不是当天的数据就直接去服务器端获取数据
                if((amount==0||!(cDate.equals(course_dateFromSQ)))) {
                    //清空卡号的表
                    db.execSQL("DELETE FROM LessonTable");
                    db.execSQL("DELETE FROM ALLDayStudentsCardTable");
                    //下次进入之前将课表数据清空，重先对相应的数据进行获取
                    lessonTables.clear();
                   /*************************************************
                   *@description： 测试用的数据与代码
                   * *************************************************/
                   if(!IsRealData) {
                       initLesson();
                       initCard();
                       Intent intent1 = new Intent();
                       intent1.putExtra("enterinto", "当前课表数据获取错误");
                       intent1.setAction("com.EnterInto");
                       sendBroadcast(intent1);
                   }
                    /*************************************************
                     *@description： 去服务器端获取数据
                    *************************************************/
                    if(IsRealData) {
                        try {
                            URL url = new URL(remoteUrl + "labRoomInfo/course/" + LabID);
                            getConnection(url, handlerForCourse);
                            Log.i("zxj", "获取课表数据 ");
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                    //防止多次执行相应的代码
                    count++;
//                连接超时或者没有相应的网络的的时候进入但是不传入数据
                    while (true) {
                        if (FlagOfConnectTimeOut == 1) {
                            Intent intent = new Intent();
                            intent.putExtra("enterinto", "当前课表数据获取错误");
                            intent.setAction("com.EnterInto");
                            sendBroadcast(intent);
                            FlagOfConnectTimeOut = 0;
                        }
                    }
                }
                else
                {//当数据库里面的数据库里面的数据是当前的数据就直接取出相应的数据
                    count++;
                    Cursor cursor =  db.query("LessonTable", null, null, null, null, null, null);
                    while(cursor.moveToNext())
                    {
                        int lab_room_id = cursor.getInt(cursor.getColumnIndex("lab_room_id"));
                        int course_present_people = cursor.getInt(cursor.getColumnIndex("course_present_people"));
                        int start_class = cursor.getInt(cursor.getColumnIndex("start_class"));
                        int end_class = cursor.getInt(cursor.getColumnIndex("end_class"));
                        int total_hour = cursor.getInt(cursor.getColumnIndex("total_hour"));
                        String course_name = cursor.getString(cursor.getColumnIndex("course_name"));
                        String course_number = cursor.getString(cursor.getColumnIndex("course_number"));
                        String course_image_url = cursor.getString(cursor.getColumnIndex("course_image_url"));
                        String course_teacher_name = cursor.getString(cursor.getColumnIndex("course_teacher_name"));
                        String course_beginning_time = cursor.getString(cursor.getColumnIndex("course_beginning_time"));
                        String course_ending_time = cursor.getString(cursor.getColumnIndex("course_ending_time"));
                        String classes_number = cursor.getString(cursor.getColumnIndex("classes_number"));
                        String course_program = cursor.getString(cursor.getColumnIndex("course_program"));
                        String course_date = cursor.getString(cursor.getColumnIndex("course_date"));
                        lessonTables.add(new LessonTable(lab_room_id,course_present_people,start_class,end_class,total_hour,course_name,course_number,course_image_url,course_teacher_name,course_beginning_time,course_ending_time,classes_number,course_program,course_date)) ;
                    }
                    cursor.close();
                    Intent intent = new Intent();
                    intent.putExtra("enterinto", "进入");
                    intent.setAction("com.EnterInto");
                    sendBroadcast(intent);
                    FlagOfConnectTimeOut = 0;
                }
        }
        }
    }
    /*************************************************
     *@description： 获取卡号信息的操作的线程
     *************************************************/
    private class getALLCardInfoThread extends Thread {
        @Override
        public void run() {
            int count=1;
            while (count==1) {
                URL url = null;
                count++;
                try {
                    url = new URL(remoteUrl + "labRoomInfo/card/" + LabID);
                    getConnection(url, handlerForALLStudentCard);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
    /*************************************************
     *@description： 初始化卡号的假数据
    *************************************************/
    public void initCard()
    {
        myDBHelper = new DatebaseHelper(getBaseContext(), databasename, null, databaseVersion);
        final SQLiteDatabase db = myDBHelper.getReadableDatabase();
        String lessonNumFortest1="TcN1";
        List<StudentCard> studentCardforTest = new ArrayList<>();
        StudentCard studentCard1 = new StudentCard("陈爱辉", "320049", "069D2B56", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard2 = new StudentCard("顾红", "480002", "D67A2A56", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard3 = new StudentCard("韩香云", "480006", "46232B56", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard4 = new StudentCard("陈天明", "480007", "A335881E", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard5 = new StudentCard("翟平", "480008", "56493A56", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard6 = new StudentCard("金建祥", "480009", "D384881E", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard7 = new StudentCard("杨百忍", "480010", "F6B82B56", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard8 = new StudentCard("张莹莹", "480012", "06FA3B56", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard9 = new StudentCard("全桂香", "480013", "33E1881E", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard10 = new StudentCard("潘梅", "480014", "1342891E", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard11 = new StudentCard("闵敏", "480015", "36623A56", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard12 = new StudentCard("吕慧华", "480016", "E6B52B56", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard13 = new StudentCard("曹燕", "480017", "96013B56", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard14 = new StudentCard("张红梅2", "480018", "86183C56", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard15 = new StudentCard("沈丹", "480019", "C6BF3B56", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard16 = new StudentCard("严金龙", "480020", "4322891E", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard17 = new StudentCard("刘洋", "480022", "D6012B56", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard18 = new StudentCard("付强", "480023", "66422B56", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard19 = new StudentCard("崔立强", "480024", "C6902A56", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard20 = new StudentCard("高建慧", "480025", "76A33B56", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard21 = new StudentCard("肖波", "480026", "66663A56", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard22 = new StudentCard("刘本志", "480027", "56972B56", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard23 = new StudentCard("罗婷", "480028", "00D38DD9", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard24 = new StudentCard("马玉荣", "480029", "30AF8ED9", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard25 = new StudentCard("周峰", "480030", "A57E8D96", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard26 = new StudentCard("远野", "480032", "C53C56F5", LabID,lessonNumFortest1,1,2);
        StudentCard studentCard27 = new StudentCard("柳欢欢", "480033", "E582A0EB", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard28 = new StudentCard("马卫星", "480035", "159354F5", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard29 = new StudentCard("郭庆园", "480037", "A5F45C90", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard30 = new StudentCard("方楚文", "550035", "4657AB50", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard31 = new StudentCard("陈景文", "510001", "16F16B50", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard32 = new StudentCard("王丹丹", "510002", "35DA5990", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard33 = new StudentCard("李红波", "510003", "E5EAE599", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard34 = new StudentCard("王占红", "510004", "166D3B56", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard35 = new StudentCard("骆爱兰", "510005", "762F3A56", LabID, lessonNumFortest1,1,2);
        StudentCard studentCard36 = new StudentCard("马成建", "510006", "EA129298", LabID, lessonNumFortest1,1,2);
        studentCardforTest.add(studentCard1);
        studentCardforTest.add(studentCard2);
        studentCardforTest.add(studentCard3);
        studentCardforTest.add(studentCard4);
        studentCardforTest.add(studentCard5);
        studentCardforTest.add(studentCard6);
        studentCardforTest.add(studentCard7);
        studentCardforTest.add(studentCard8);
        studentCardforTest.add(studentCard9);
        studentCardforTest.add(studentCard10);
        studentCardforTest.add(studentCard11);
        studentCardforTest.add(studentCard12);
        studentCardforTest.add(studentCard13);
        studentCardforTest.add(studentCard14);
        studentCardforTest.add(studentCard15);
        studentCardforTest.add(studentCard16);
        studentCardforTest.add(studentCard17);
        studentCardforTest.add(studentCard18);
        studentCardforTest.add(studentCard19);
        studentCardforTest.add(studentCard20);
        studentCardforTest.add(studentCard21);
        studentCardforTest.add(studentCard22);
        studentCardforTest.add(studentCard23);
        studentCardforTest.add(studentCard24);
        studentCardforTest.add(studentCard25);
        studentCardforTest.add(studentCard26);
        studentCardforTest.add(studentCard27);
        studentCardforTest.add(studentCard28);
        studentCardforTest.add(studentCard29);
        studentCardforTest.add(studentCard30);
        studentCardforTest.add(studentCard31);
        studentCardforTest.add(studentCard32);
        studentCardforTest.add(studentCard33);
        studentCardforTest.add(studentCard34);
        studentCardforTest.add(studentCard35);
        studentCardforTest.add(studentCard36);
        //课程2的学生数据
        String lessonNumFortest2="TcN2";
        List<StudentCard> studentCardforTest2 = new ArrayList<>();
        StudentCard studentCard1_2 = new StudentCard("陈爱辉", "320049", "069D2B56", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard2_2 = new StudentCard("顾红", "480002", "D67A2A56", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard3_2 = new StudentCard("韩香云", "480006", "46232B56", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard4_2= new StudentCard("陈天明", "480007", "A335881E", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard5_2 = new StudentCard("翟平", "480008", "56493A56", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard6_2 = new StudentCard("金建祥", "480009", "D384881E", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard7_2 = new StudentCard("杨百忍", "480010", "F6B82B56", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard8_2 = new StudentCard("张莹莹", "480012", "06FA3B56", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard9_2 = new StudentCard("全桂香", "480013", "33E1881E", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard10_2 = new StudentCard("潘梅", "480014", "1342891E", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard11_2 = new StudentCard("闵敏", "480015", "36623A56", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard12_2 = new StudentCard("吕慧华", "480016", "E6B52B56", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard13_2 = new StudentCard("曹燕", "480017", "96013B56", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard14_2 = new StudentCard("张红梅2", "480018","86183C56", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard15_2 = new StudentCard("沈丹", "480019", "C6BF3B56", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard16_2 = new StudentCard("严金龙", "480020", "4322891E", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard17_2 = new StudentCard("刘洋", "480022", "D6012B56", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard18_2 = new StudentCard("付强", "480023", "66422B56", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard19_2 = new StudentCard("崔立强", "480024", "C6902A56", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard20_2 = new StudentCard("高建慧", "480025", "76A33B56", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard21_2 = new StudentCard("肖波", "480026", "66663A56", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard22_2 = new StudentCard("刘本志", "480027", "56972B56", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard23_2 = new StudentCard("罗婷", "480028", "00D38DD9", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard24_2 = new StudentCard("马玉荣", "480029", "30AF8ED9", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard25_2 = new StudentCard("周峰", "480030", "A57E8D96", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard26_2 = new StudentCard("远野", "480032", "C53C56F5", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard27_2 = new StudentCard("柳欢欢", "480033", "E582A0EB", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard28_2 = new StudentCard("马卫星", "480035", "159354F5", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard29_2 = new StudentCard("郭庆园", "480037", "A5F45C90", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard30_2 = new StudentCard("方楚文", "550035", "4657AB50", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard31_2 = new StudentCard("陈景文", "510001", "16F16B50", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard32_2 = new StudentCard("王丹丹", "510002", "35DA5990", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard33_2 = new StudentCard("李红波", "510003", "E5EAE599", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard34_2 = new StudentCard("王占红", "510004", "166D3B56", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard35_2 = new StudentCard("骆爱兰", "510005", "762F3A56", LabID, lessonNumFortest2,3,4);
        StudentCard studentCard36_2 = new StudentCard("马成建", "510006", "EA129298", LabID, lessonNumFortest2,3,4);
        studentCardforTest2.add(studentCard1_2);
        studentCardforTest2.add(studentCard2_2);
        studentCardforTest2.add(studentCard3_2);
        studentCardforTest2.add(studentCard4_2);
        studentCardforTest2.add(studentCard5_2);
        studentCardforTest2.add(studentCard6_2);
        studentCardforTest2.add(studentCard7_2);
        studentCardforTest2.add(studentCard8_2);
        studentCardforTest2.add(studentCard9_2);
        studentCardforTest2.add(studentCard10_2);
        studentCardforTest2.add(studentCard11_2);
        studentCardforTest2.add(studentCard12_2);
        studentCardforTest2.add(studentCard13_2);
        studentCardforTest2.add(studentCard14_2);
        studentCardforTest2.add(studentCard15_2);
        studentCardforTest2.add(studentCard16_2);
        studentCardforTest2.add(studentCard17_2);
        studentCardforTest2.add(studentCard18_2);
        studentCardforTest2.add(studentCard19_2);
        studentCardforTest2.add(studentCard20_2);
        studentCardforTest2.add(studentCard21_2);
        studentCardforTest2.add(studentCard22_2);
        studentCardforTest2.add(studentCard23_2);
        studentCardforTest2.add(studentCard24_2);
        studentCardforTest2.add(studentCard25_2);
        studentCardforTest2.add(studentCard26_2);
        studentCardforTest2.add(studentCard27_2);
        studentCardforTest2.add(studentCard28_2);
        studentCardforTest2.add(studentCard29_2);
        studentCardforTest2.add(studentCard30_2);
        studentCardforTest2.add(studentCard31_2);
        studentCardforTest2.add(studentCard32_2);
        studentCardforTest2.add(studentCard33_2);
        studentCardforTest2.add(studentCard34_2);
        studentCardforTest2.add(studentCard35_2);
        studentCardforTest2.add(studentCard36_2);
        //课程3的学生数据
        String lessonNumFortest3="TcN3";
        List<StudentCard> studentCardforTest3 = new ArrayList<>();
        StudentCard studentCard1_3 = new StudentCard("陈爱辉", "320049", "069D2B56", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard2_3  = new StudentCard("顾红", "480002", "D67A2A56", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard3_3 = new StudentCard("韩香云", "480006", "46232B56", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard4_3  = new StudentCard("陈天明", "480007", "A335881E", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard5_3  = new StudentCard("翟平", "480008", "56493A56", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard6_3  = new StudentCard("金建祥", "480009", "D384881E", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard7_3  = new StudentCard("杨百忍", "480010", "F6B82B56", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard8_3  = new StudentCard("张莹莹", "480012", "06FA3B56", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard9_3  = new StudentCard("全桂香", "480013", "33E1881E", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard10_3  = new StudentCard("潘梅", "480014", "1342891E", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard11_3  = new StudentCard("闵敏", "480015", "36623A56", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard12_3  = new StudentCard("吕慧华", "480016", "E6B52B56", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard13_3  = new StudentCard("曹燕", "480017", "96013B56", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard14_3  = new StudentCard("张红梅2", "480018", "86183C56", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard15_3  = new StudentCard("沈丹", "480019", "C6BF3B56", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard16_3  = new StudentCard("严金龙", "480020", "4322891E", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard17_3  = new StudentCard("刘洋", "480022", "D6012B56", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard18_3  = new StudentCard("付强", "480023", "66422B56", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard19_3  = new StudentCard("崔立强", "480024", "C6902A56", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard20_3  = new StudentCard("高建慧", "480025", "76A33B56", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard21_3  = new StudentCard("肖波", "480026", "66663A56", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard22_3  = new StudentCard("刘本志", "480027", "56972B56", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard23_3  = new StudentCard("罗婷", "480028", "00D38DD9", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard24_3  = new StudentCard("马玉荣", "480029", "30AF8ED9", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard25_3  = new StudentCard("周峰", "480030", "A57E8D96", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard26_3  = new StudentCard("远野", "480032", "C53C56F5", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard27_3  = new StudentCard("柳欢欢", "480033", "E582A0EB", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard28_3  = new StudentCard("马卫星", "480035", "159354F5", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard29_3  = new StudentCard("郭庆园", "480037", "A5F45C90", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard30_3  = new StudentCard("方楚文", "550035", "4657AB50", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard31_3 = new StudentCard("陈景文", "510001", "16F16B50", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard32_3 = new StudentCard("王丹丹", "510002", "35DA5990", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard33_3 = new StudentCard("李红波", "510003", "E5EAE599", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard34_3 = new StudentCard("王占红", "510004", "166D3B56", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard35_3 = new StudentCard("骆爱兰", "510005", "762F3A56", LabID, lessonNumFortest3,5,6);
        StudentCard studentCard36_3 = new StudentCard("马成建", "510006", "EA129298", LabID, lessonNumFortest3,5,6);
        studentCardforTest3.add(studentCard1_3);
        studentCardforTest3.add(studentCard2_3);
        studentCardforTest3.add(studentCard3_3);
        studentCardforTest3.add(studentCard4_3);
        studentCardforTest3.add(studentCard5_3);
        studentCardforTest3.add(studentCard6_3);
        studentCardforTest3.add(studentCard7_3);
        studentCardforTest3.add(studentCard8_3);
        studentCardforTest3.add(studentCard9_3);
        studentCardforTest3.add(studentCard10_3);
        studentCardforTest3.add(studentCard11_3);
        studentCardforTest3.add(studentCard12_3);
        studentCardforTest3.add(studentCard13_3);
        studentCardforTest3.add(studentCard14_3);
        studentCardforTest3.add(studentCard15_3);
        studentCardforTest3.add(studentCard16_3);
        studentCardforTest3.add(studentCard17_3);
        studentCardforTest3.add(studentCard18_3);
        studentCardforTest3.add(studentCard19_3);
        studentCardforTest3.add(studentCard20_3);
        studentCardforTest3.add(studentCard21_3);
        studentCardforTest3.add(studentCard22_3);
        studentCardforTest3.add(studentCard23_3);
        studentCardforTest3.add(studentCard24_3);
        studentCardforTest3.add(studentCard25_3);
        studentCardforTest3.add(studentCard26_3);
        studentCardforTest3.add(studentCard27_3);
        studentCardforTest3.add(studentCard28_3);
        studentCardforTest3.add(studentCard29_3);
        studentCardforTest3.add(studentCard30_3);
        studentCardforTest3.add(studentCard31_3);
        studentCardforTest3.add(studentCard32_3);
        studentCardforTest3.add(studentCard33_3);
        studentCardforTest3.add(studentCard34_3);
        studentCardforTest3.add(studentCard35_3);
        studentCardforTest3.add(studentCard36_3);
        //课程4的学生数据
        String lessonNumFortest4="TcN4";
        List<StudentCard> studentCardforTest4 = new ArrayList<>();
        StudentCard studentCard1_4 = new StudentCard("陈爱辉", "320049", "069D2B56", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard2_4 = new StudentCard("顾红", "480002", "D67A2A56", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard3_4 = new StudentCard("韩香云", "480006", "46232B56", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard4_4= new StudentCard("陈天明", "480007", "A335881E", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard5_4 = new StudentCard("翟平", "480008", "56493A56", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard6_4 = new StudentCard("金建祥", "480009", "D384881E", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard7_4 = new StudentCard("杨百忍", "480010", "F6B82B56", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard8_4 = new StudentCard("张莹莹", "480012", "06FA3B56", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard9_4 = new StudentCard("全桂香", "480013", "33E1881E", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard10_4 = new StudentCard("潘梅", "480014", "1342891E", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard11_4 = new StudentCard("闵敏", "480015", "36623A56", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard12_4 = new StudentCard("吕慧华", "480016", "E6B52B56", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard13_4 = new StudentCard("曹燕", "480017", "96013B56", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard14_4 = new StudentCard("张红梅2", "480018", "86183C56", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard15_4 = new StudentCard("沈丹", "480019", "C6BF3B56", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard16_4 = new StudentCard("严金龙", "480020", "4322891E", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard17_4 = new StudentCard("刘洋", "480022", "D6012B56", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard18_4 = new StudentCard("付强", "480023", "66422B56", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard19_4 = new StudentCard("崔立强", "480024", "C6902A56", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard20_4 = new StudentCard("高建慧", "480025", "76A33B56", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard21_4 = new StudentCard("肖波", "480026", "66663A56", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard22_4 = new StudentCard("刘本志", "480027", "56972B56", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard23_4 = new StudentCard("罗婷", "480028", "00D38DD9", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard24_4 = new StudentCard("马玉荣", "480029", "30AF8ED9", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard25_4 = new StudentCard("周峰", "480030", "A57E8D96", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard26_4 = new StudentCard("远野", "480032", "C53C56F5", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard27_4 = new StudentCard("柳欢欢", "480033", "E582A0EB", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard28_4 = new StudentCard("马卫星", "480035", "159354F5", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard29_4 = new StudentCard("郭庆园", "480037", "A5F45C90", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard30_4 = new StudentCard("方楚文", "550035", "4657AB50", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard31_4 = new StudentCard("陈景文", "510001", "16F16B50", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard32_4 = new StudentCard("王丹丹", "510002", "35DA5990", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard33_4 = new StudentCard("李红波", "510003", "E5EAE599", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard34_4 = new StudentCard("王占红", "510004", "166D3B56", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard35_4 = new StudentCard("骆爱兰", "510005", "762F3A56", LabID, lessonNumFortest4,7,8);
        StudentCard studentCard36_4 = new StudentCard("马成建", "510006", "EA129298", LabID, lessonNumFortest4,7,8);
        studentCardforTest4.add(studentCard1_4);
        studentCardforTest4.add(studentCard2_4);
        studentCardforTest4.add(studentCard3_4);
        studentCardforTest4.add(studentCard4_4);
        studentCardforTest4.add(studentCard5_4);
        studentCardforTest4.add(studentCard6_4);
        studentCardforTest4.add(studentCard7_4);
        studentCardforTest4.add(studentCard8_4);
        studentCardforTest4.add(studentCard9_4);
        studentCardforTest4.add(studentCard10_4);
        studentCardforTest4.add(studentCard11_4);
        studentCardforTest4.add(studentCard12_4);
        studentCardforTest4.add(studentCard13_4);
        studentCardforTest4.add(studentCard14_4);
        studentCardforTest4.add(studentCard15_4);
        studentCardforTest4.add(studentCard16_4);
        studentCardforTest4.add(studentCard17_4);
        studentCardforTest4.add(studentCard18_4);
        studentCardforTest4.add(studentCard19_4);
        studentCardforTest4.add(studentCard20_4);
        studentCardforTest4.add(studentCard21_4);
        studentCardforTest4.add(studentCard22_4);
        studentCardforTest4.add(studentCard23_4);
        studentCardforTest4.add(studentCard24_4);
        studentCardforTest4.add(studentCard25_4);
        studentCardforTest4.add(studentCard26_4);
        studentCardforTest4.add(studentCard27_4);
        studentCardforTest4.add(studentCard28_4);
        studentCardforTest4.add(studentCard29_4);
        studentCardforTest4.add(studentCard30_4);
        studentCardforTest4.add(studentCard31_4);
        studentCardforTest4.add(studentCard32_4);
        studentCardforTest4.add(studentCard33_4);
        studentCardforTest4.add(studentCard34_4);
        studentCardforTest4.add(studentCard35_4);
        studentCardforTest4.add(studentCard36_4);
        //私人测试课程一
        StudentCard studentCardTest1_1=new StudentCard("关羽","123456","38F0ECC4",LabID,"TcN1",1,2);
        StudentCard studentCardTest1_2=new StudentCard("刘备","123456","B0C7431E",LabID,"TcN1",1,2);
        studentCardforTest.add(studentCardTest1_1);
        studentCardforTest.add(studentCardTest1_2);
        //私人测试课程二
        StudentCard studentCardTest2_1=new StudentCard("关羽","123456","38F0ECC4",LabID,"TcN2",3,4);
        StudentCard studentCardTest2_2=new StudentCard("刘备","123456","B0C7431E",LabID,"TcN2",3,4);
        studentCardforTest2.add(studentCardTest2_1);
        studentCardforTest2.add(studentCardTest2_2);
        //私人测试课程三
        StudentCard studentCardTest3_1=new StudentCard("关羽","123456","38F0ECC4",LabID,"TcN3",5,6);
        StudentCard studentCardTest3_2=new StudentCard("刘备","123456","B0C7431E",LabID,"TcN3",5,6);
        studentCardforTest3.add(studentCardTest3_1);
        studentCardforTest3.add(studentCardTest3_2);
        //私人测试课程四
        StudentCard studentCardTest4_1=new StudentCard("关羽","123456","38F0ECC4",LabID,"TcN4",7,8);
        StudentCard studentCardTest4_2=new StudentCard("刘备","123456","B0C7431E",LabID,"TcN4",7,8);
        studentCardforTest4.add(studentCardTest4_1);
        studentCardforTest4.add(studentCardTest4_2);
        //课程1的卡号数据写入到数据库里面
        for(int i=0;i<studentCardforTest.size();i++){
            ContentValues values = new ContentValues();
            values.put("student_name", studentCardforTest.get(i).getStudent_name());
            values.put("card_no", studentCardforTest.get(i).getCard_no());
            values.put("course_number", studentCardforTest.get(i).getCourse_number());
            values.put("student_number", studentCardforTest.get(i).getStudent_number());
            values.put("lab_room_id", studentCardforTest.get(i).getLab_room_id());
            values.put("start_class", studentCardforTest.get(i).getStart_class());
            values.put("end_class", studentCardforTest.get(i).getEnd_class());
            db.insert("ALLDayStudentsCardTable", null, values);
        }
        //课程2的卡号数据写入到数据库里面
        for(int i=0;i<studentCardforTest2.size();i++){
            ContentValues values = new ContentValues();
            values.put("student_name", studentCardforTest2.get(i).getStudent_name());
            values.put("card_no", studentCardforTest2.get(i).getCard_no());
            values.put("course_number", studentCardforTest2.get(i).getCourse_number());
            values.put("student_number", studentCardforTest2.get(i).getStudent_number());
            values.put("lab_room_id", studentCardforTest2.get(i).getLab_room_id());
            values.put("start_class", studentCardforTest2.get(i).getStart_class());
            values.put("end_class", studentCardforTest2.get(i).getEnd_class());
            db.insert("ALLDayStudentsCardTable", null, values);
        }
        //课程1的卡号数据写入到数据库里面
        for(int i=0;i<studentCardforTest3.size();i++){
            ContentValues values = new ContentValues();
            values.put("student_name", studentCardforTest3.get(i).getStudent_name());
            values.put("card_no", studentCardforTest3.get(i).getCard_no());
            values.put("course_number", studentCardforTest3.get(i).getCourse_number());
            values.put("student_number", studentCardforTest3.get(i).getStudent_number());
            values.put("lab_room_id", studentCardforTest3.get(i).getLab_room_id());
            values.put("start_class", studentCardforTest3.get(i).getStart_class());
            values.put("end_class", studentCardforTest3.get(i).getEnd_class());
            db.insert("ALLDayStudentsCardTable", null, values);
        }
        //课程4的卡号数据写入到数据库里面
        for(int i=0;i<studentCardforTest4.size();i++){
            ContentValues values = new ContentValues();
            values.put("student_name", studentCardforTest4.get(i).getStudent_name());
            values.put("card_no", studentCardforTest4.get(i).getCard_no());
            values.put("course_number", studentCardforTest4.get(i).getCourse_number());
            values.put("student_number", studentCardforTest4.get(i).getStudent_number());
            values.put("lab_room_id", studentCardforTest4.get(i).getLab_room_id());
            values.put("start_class", studentCardforTest4.get(i).getStart_class());
            values.put("end_class", studentCardforTest4.get(i).getEnd_class());
            db.insert("ALLDayStudentsCardTable", null, values);
        }
        Cursor cursorforSave =  db.query("ALLDayStudentsCardTable", null, null, null, null, null, null);
        ExportToCSV.ExportToCSV(cursorforSave, "从服务器端取的所有学生卡号的文件.csv");
    }
    public void initLesson()
    {
                   myDBHelper = new DatebaseHelper(getBaseContext(), databasename, null, databaseVersion);
                   final SQLiteDatabase db = myDBHelper.getReadableDatabase();
                            List<LessonTable> lessonTableforTest = new ArrayList<>();
                    LessonTable lessonTable1 = new LessonTable(LabID, 36, 1, 2, 36, "大气污染原理", "TcN1", "图片地址", "张华", "6:00:00", "10:09:00", "测试班级1", "实验一", GetCurrentTime.getCurrentDate());
                    LessonTable lessonTable2 = new LessonTable(LabID, 36, 3, 4, 46, "大气污染控制", "TcN2", "图片地址", "王明", "10:10:00", "11:40:00", "测试班级2", "实验二", GetCurrentTime.getCurrentDate());
                    LessonTable lessonTable3 = new LessonTable(LabID, 36, 5, 6, 54, "大气污染治理", "TcN3", "图片地址", "李飞", "13:30:00", "15:41:00", "测试班级3", "实验三", GetCurrentTime.getCurrentDate());
                    LessonTable lessonTable4 = new LessonTable(LabID, 36, 7, 8, 42, "节能减排", "TcN4", "图片地址", "李华", "15:43:00", "21:12:00", "测试班级4", "实验四", GetCurrentTime.getCurrentDate());
                    lessonTableforTest.add(lessonTable1);
                    lessonTableforTest.add(lessonTable2);
                    lessonTableforTest.add(lessonTable3);
                    lessonTableforTest.add(lessonTable4);
                    //将数据写入数据库里面
                    for (int i = 0; i < lessonTableforTest.size(); i++) {
                        ContentValues values = new ContentValues();
                        values.put("lab_room_id", lessonTableforTest.get(i).getLab_room_id());
                        values.put("course_present_people", lessonTableforTest.get(i).getCourse_present_people());
                        values.put("start_class", lessonTableforTest.get(i).getStart_class());
                        values.put("end_class", lessonTableforTest.get(i).getEnd_class());
                        values.put("total_hour", lessonTableforTest.get(i).getTotal_hour());
                        values.put("course_name", lessonTableforTest.get(i).getCourse_name());
                        values.put("course_number", lessonTableforTest.get(i).getCourse_number());
                        values.put("course_image_url", lessonTableforTest.get(i).getCourse_image_url());
                        values.put("course_teacher_name", lessonTableforTest.get(i).getCourse_teacher_name());
                        values.put("course_beginning_time", lessonTableforTest.get(i).getCourse_beginning_time());
                        values.put("course_ending_time", lessonTableforTest.get(i).getCourse_ending_time());
                        values.put("classes_number",lessonTableforTest.get(i).getClasses_number());
                        values.put("course_program",lessonTableforTest.get(i).getCourse_program());
                        values.put("course_date", lessonTableforTest.get(i).getCourse_date());
                        db.insert("LessonTable", null, values);
                    }
                    Cursor cursor = db.query("LessonTable", null, null, null, null, null, null);
                    ExportToCSV.ExportToCSV(cursor, "实验室课程文件.csv");
                    //将数据写入到全局变量来渗透到整个项目里面
                    lessonTables.add(lessonTable1);
                    lessonTables.add(lessonTable2);
                    lessonTables.add(lessonTable3);
                    lessonTables.add(lessonTable4);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

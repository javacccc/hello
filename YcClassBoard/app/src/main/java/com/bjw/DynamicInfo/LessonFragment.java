package com.bjw.DynamicInfo;

/*************************************************
 *@date：2017/11/17
 *@author： zxj
 *@description： 课程的碎片
*************************************************/

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bjw.Adapter.LessonBelowAdapter;
import com.bjw.Common.DatebaseHelper;
import com.bjw.Common.ExportToCSV;
import com.bjw.Common.GetCurrentTime;
import com.bjw.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bjw.Common.StaticConfig.databaseVersion;
import static com.bjw.Common.StaticConfig.databasename;
import static com.bjw.Common.StaticConfig.defaultcourse;
import static com.bjw.Common.StaticConfig.defaultcoursenumber;
import static com.bjw.Common.StaticConfig.defaultteacher;
import static com.bjw.Common.StaticConfig.lessonTables;
import static com.bjw.Common.StaticConfig.studentCards;
import static com.bjw.Common.StaticConfig.studentCardsTemp;

public class LessonFragment extends Fragment {
    public static BroadcastReceiverToGetNum broadcastReceiverToGetNum;//广播声明
    public static BroadcastReceiverToTimer broadcastReceiverToTimer;//广播声明
    public static BroadcastReceiverToRecoverCardTemp broadcastReceiverToRecoverCardTemp;//广播声明
    public static BroadcastReceiverToWarnAterClass broadcastReceiverToWarnAterClass;//广播声明
    private int present_num=0,obsent_num=0;//计数
    TextView chooseNum,presentNum,obsentNum;
    ListView listView;
    TextView coursename,coursenum,courseperiod,courseteacher;
    TextView studentNameTv,cardnumberTv,AfterClassTv;
    LinearLayout llpresent,llafterschool,llcardandname;
    LessonBelowAdapter lessonBelowAdapter;
    int flag=0;//用来判断当前的刷卡用户是否在该班里面0表示当前的不是改版机学生，1表示是该班学生且没有读过，2表示已读过
    int indexoftemp;//记录得到卡号与获取的数据相同数据的下标
    private DatebaseHelper myDBHelper;  //本地数据库的帮助者
    private int numFromSQToupdate=0;//数据恢复的时候用来实现相应的数据库里面的信息
    private boolean isAfterClassTime=false;//数据恢复的时候用来实现相应的数据库里面的信息


    /*************************************************
     *@description： 动态刷新获取到的信息
     *************************************************/
    public Handler handlerForRefresh = new Handler() {
        public void handleMessage(Message msg)
        {
            int i = Integer.parseInt(msg.obj.toString());
            if(i==1314520)
            {
                coursename.setText(defaultcourse);
                coursenum.setText(defaultcoursenumber);
                courseperiod.setText("共有"+0+"学时");
                courseteacher.setText(defaultteacher);
                chooseNum.setText(0+"");
                presentNum.setText(0+"");
                obsentNum.setText(0+"");
                resetListItemcolor();
                obsent_num=0;
                present_num=0;
            }
            else {
                coursename.setText(lessonTables.get(i).getCourse_name());
                coursenum.setText(lessonTables.get(i).getCourse_number());
                if(lessonTables.get(i).getTotal_hour()==0)
                {
                    courseperiod.setText("未知");
                }
                else {
                    courseperiod.setText("共有" + lessonTables.get(i).getTotal_hour() + "学时");
                }
                courseteacher.setText(lessonTables.get(i).getCourse_teacher_name());
                chooseNum.setText(lessonTables.get(i).getCourse_present_people() + "");
                if(obsent_num==0) {
                    //判断当数据恢复的时候将相应的数据进行更新
                    if(studentCardsTemp.size()!=0){
                        obsent_num = lessonTables.get(i).getCourse_present_people()-studentCardsTemp.size();
                        obsentNum.setText(obsent_num+ "");
                    }
                    else {
                        obsent_num = lessonTables.get(i).getCourse_present_people();
                        obsentNum.setText(lessonTables.get(i).getCourse_present_people() + "");
                    }
                }
                resetListItemcolor();
                View view=listView.getChildAt(i);
                view.setBackgroundColor(Color.RED);
            }
        }
    };
    /*************************************************
     *@description： 恢复获取到的数据的条数
     *************************************************/
    public Handler handlerForReCover = new Handler() {
        public void handleMessage(Message msg)
        {
            int i = Integer.parseInt(msg.obj.toString());
            present_num=i;
            presentNum.setText(i+"");
            Log.i("zxj", "有广播接收到的数据"+i);
            if(i!=0)
            {
                numFromSQToupdate=i;
            }
            else
            {
                numFromSQToupdate=0;
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View chatView = inflater.inflate(R.layout.fragment_dynamic_lesson, container,false);
        return chatView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }
    public void onStart() {
        super.onStart();
        findById();
        if(broadcastReceiverToGetNum==null) {
            registerForNum();
        }
        if(broadcastReceiverToTimer==null) {
            registerForTimer();
        }
        if(broadcastReceiverToRecoverCardTemp==null) {
            registerForRecover();
        }
        if(broadcastReceiverToWarnAterClass==null) {
            registerForWarnAfterClass();
        }
        //记录从其他几个页面返回时保存当前数据的状态
        presentNum.setText(present_num+"");
        obsentNum.setText(obsent_num+"");
        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>() ;
        for(int i=0;i<lessonTables.size();i++){
            Map<String,Object> map = new HashMap<String,Object>() ;
            map.put("siple_tv1", lessonTables.get(i).getStart_class()+"-"+lessonTables.get(i).getEnd_class()+"节") ;
            map.put("siple_tv2", lessonTables.get(i).getCourse_name()) ;
            map.put("siple_tv3", lessonTables.get(i).getCourse_teacher_name()) ;
            map.put("siple_tv4", lessonTables.get(i).getCourse_present_people()+"") ;
            list.add(map) ;
        }
        String[] from = {  "siple_tv1", "siple_tv2","siple_tv3","siple_tv4"};
        int[] to = {  R.id.siple_tv1,R.id.siple_tv2,R.id.siple_tv3,R.id.siple_tv4} ;
         lessonBelowAdapter = new LessonBelowAdapter(getActivity(), list,R.layout.item_right_lesson_list, from, to) ;
        listView.setAdapter(lessonBelowAdapter);
//        当整个实验室没有课程的时候
        if(lessonTables.size()==0)
        {
            llcardandname.setVisibility(View.GONE);
            llafterschool.setVisibility(View.VISIBLE);
            AfterClassTv.setText("今日无课程");
        }
//       查看已经刷卡的人
        llpresent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String test="";
                for (int i=0;i<studentCardsTemp.size();i++)
                {
                    test=test+studentCardsTemp.get(i).getStudent_name();
                }
                Toast.makeText(getContext(), test, Toast.LENGTH_SHORT).show();
            }
        });
    }
    /*************************************************
     *@description： 数据绑定
    *************************************************/
    private void findById() {
        presentNum = (TextView) getActivity().findViewById(R.id.present_num);
        obsentNum = (TextView) getActivity().findViewById(R.id.obsent_num);
        chooseNum = (TextView) getActivity().findViewById(R.id.choose_num);
        listView=(ListView)getActivity().findViewById(R.id.listviewforlesson);
        coursename=(TextView)getActivity().findViewById(R.id.course_name);
        coursenum=(TextView)getActivity().findViewById(R.id.course_num);
        courseperiod=(TextView)getActivity().findViewById(R.id.course_period);
        courseteacher=(TextView)getActivity().findViewById(R.id.course_teacher);
        studentNameTv=(TextView)getActivity().findViewById(R.id.id_student_name);
        cardnumberTv=(TextView)getActivity().findViewById(R.id.id_card_number);
        llpresent=(LinearLayout)getActivity().findViewById(R.id.id_ll_present);
        llafterschool=(LinearLayout)getActivity().findViewById(R.id.id_ll_afterschool);
        llcardandname=(LinearLayout)getActivity().findViewById(R.id.id_ll_cardandname);
        AfterClassTv=(TextView)getActivity().findViewById(R.id.id_afterClass_Tv);
        llafterschool.setVisibility(View.GONE);
    }
    /*************************************************
     *@description： 注册得到卡号的广播
     *************************************************/
    public void registerForNum()
    {
        broadcastReceiverToGetNum=new BroadcastReceiverToGetNum();
        IntentFilter filter1 = new IntentFilter();
        filter1.setPriority(2);
        filter1.addAction("com.getCardNum");
        getActivity().registerReceiver(broadcastReceiverToGetNum, filter1);
    }
    /*************************************************
     *@description： 注册得定时器的广播
     *************************************************/
    public void registerForTimer()
    {
        broadcastReceiverToTimer=new BroadcastReceiverToTimer();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(3);
        filter.addAction("com.Time");
        getActivity().registerReceiver(broadcastReceiverToTimer, filter);
    }
    /*************************************************
     *@description： 注册卡号数据恢复的广播
     *************************************************/
    public void registerForRecover()
    {
        broadcastReceiverToRecoverCardTemp=new BroadcastReceiverToRecoverCardTemp();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(4);
        filter.addAction("com.cardRecoverNum");
        getActivity().registerReceiver(broadcastReceiverToRecoverCardTemp, filter);
    }
    /*************************************************
     *@description： 注册提示下课的广播
     *************************************************/
    public void registerForWarnAfterClass()
    {
        broadcastReceiverToWarnAterClass=new BroadcastReceiverToWarnAterClass();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(5);
        filter.addAction("com.afterClassWarnning");
        getActivity().registerReceiver(broadcastReceiverToWarnAterClass, filter);
    }
    /*************************************************
     *@description： 卡号信息的广播接收器同时处理相关的
     * 读卡逻辑
     *************************************************/
    public class BroadcastReceiverToGetNum extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String cardNum = intent.getStringExtra("cardnum");

//            new BigInteger("c01a2033", 16).toString();//将16进制转换为需要的10进制数
//            cardnumberTv.setText(new BigInteger("c01a2033", 16).toString());
            if (isAfterClassTime) {
                Toast.makeText(getActivity(), "当前是下课时间，请勿刷卡", Toast.LENGTH_SHORT).show();
                isAfterClassTime=false;
            }
            else {
                cardnumberTv.setText(cardNum);
                for (int i = 0; i < studentCards.size(); i++) {
                    if (cardNum.equals(studentCards.get(i).getCard_no())) {
                        if (studentCardsTemp.size() == 0) {//第一次不需要判断是否有读过的
                            flag = 1;
                            indexoftemp = i;
                            break;
                        } else {
                            //判断之前读过卡的人里面是否有读的人
                            for (int j = 0; j < studentCardsTemp.size(); j++) {
                                if (cardNum.equals(studentCardsTemp.get(j).getCard_no())) {
                                    flag = 2;
                                    break;
                                } else {
                                    indexoftemp = i;
                                    flag = 1;
                                }
                            }
                            break;
                        }
                    } else {
                        flag = 0;
                    }
                }
                if (flag == 0) {
//                    cardnumberTv.setText("");
                    studentNameTv.setText("你不是当前班级的学生");
                } else if (flag == 2) {
//                    cardnumberTv.setText("");
                    studentNameTv.setText("已刷卡，请勿重复刷卡");
                    flag = 0;
                } else {
                    cardnumberTv.setText(cardNum);
                    studentNameTv.setText(studentCards.get(indexoftemp).getStudent_name());
                    studentCardsTemp.add(studentCards.get(indexoftemp));//将刚才加入的数据加入到目前读取道卡号的信息
                    present_num++;
                    obsent_num--;
                    presentNum.setText(present_num + "");
                    obsentNum.setText(obsent_num + "");
                    //当相应的数据验证为卡号之后，将相应的数据写入到服务器
                    myDBHelper = new DatebaseHelper(getActivity(), databasename, null, databaseVersion);
                    final SQLiteDatabase db = myDBHelper.getReadableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("card_no", cardNum);
                    values.put("student_name", studentCards.get(indexoftemp).getStudent_name());
                    values.put("lab_room_id", studentCards.get(indexoftemp).getLab_room_id());
                    values.put("student_number", studentCards.get(indexoftemp).getStudent_number());
                    values.put("course_number", studentCards.get(indexoftemp).getCourse_number());
                    values.put("current_time", GetCurrentTime.getLongStringCurrentTime());
                    values.put("start_class", studentCards.get(indexoftemp).getStart_class());
                    values.put("end_class", studentCards.get(indexoftemp).getEnd_class());
//                    for(int i=0;i<1000;i++) {
                        db.insert("StudentCardTable", null, values);
//                    }
                    Cursor cursor = db.query("StudentCardTable", null, null, null, null, null, null);
                    ExportToCSV.ExportToCSV(cursor, "保存已刷过得卡的卡号的文件.csv");
                    cursor.close();
                }
            }
        }
    }
    /*************************************************
     *@description： 定时操作的广播接收器
     *************************************************/
    public class BroadcastReceiverToTimer extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String time=intent.getStringExtra("Time");
            Message mes=new Message();
            mes.obj=time;
            handlerForRefresh.sendMessage(mes);
        }
    }
    /*************************************************
     *@description： 卡号数据恢复的广播接收器
     *************************************************/
    public class BroadcastReceiverToRecoverCardTemp extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String num=intent.getStringExtra("cardRecoverNum");
            Message mes=new Message();
            mes.obj=num;
            handlerForReCover.sendMessage(mes);
        }
    }
    /*************************************************
     *@description： 提示用户这是下课时间的广播接收器
     *************************************************/
    public class BroadcastReceiverToWarnAterClass extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String Warnning=intent.getStringExtra("Warnning");
            if(Warnning.equals("当前是下课时间，请勿刷卡")) {
                isAfterClassTime = true;
                llcardandname.setVisibility(View.GONE);
                llafterschool.setVisibility(View.VISIBLE);
            }
            else if(Warnning.equals("上课了，可以刷卡"))
            {
                isAfterClassTime = false;
                llcardandname.setVisibility(View.VISIBLE);
                llafterschool.setVisibility(View.GONE);
                cardnumberTv.setText("");
                studentNameTv.setText("");
            }
//            cardnumberTv.setText(Warnning);
        }
    }
    /*************************************************
     *@description： 重设ListView的背景
    *************************************************/
    public void resetListItemcolor()
    {
        for (int i=0;i<listView.getChildCount();i++)
        {
            View view=listView.getChildAt(i);
            view.setBackgroundColor(Color.parseColor("#17385e"));
        }
    }
    @Override
    public void onDestroy()
    {
        broadcastReceiverToGetNum=null;
        broadcastReceiverToRecoverCardTemp=null;
        broadcastReceiverToTimer=null;
        broadcastReceiverToWarnAterClass=null;
        super.onDestroy();
    }
}

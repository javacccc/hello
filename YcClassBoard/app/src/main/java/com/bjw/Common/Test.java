package com.bjw.Common;

import android.os.Handler;
import android.os.Message;

import com.bjw.bean.LabRoomInfo;
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
import static com.bjw.Common.StaticConfig.LabID;
import static com.bjw.Common.StaticConfig.remoteUrl;

/**
 * 创建人：wxdn
 * 创建时间：2017/12/21
 * 功能描述：
 */

public final class Test {
    public static List<LabRoomInfo> GetInfo() {
        final List<LabRoomInfo>labRoomInfoList=new ArrayList<>();
        /*************************************************
         *@description： 数据操作
        *************************************************/
//        DatebaseHelper myDBHelper;  //本地数据库的帮助者
//        //当相应的数据验证为卡号之后，将相应的数据写入到服务器
//        myDBHelper = new DatebaseHelper(getActivity(), databasename, null, databaseVersion);
//        final SQLiteDatabase db = myDBHelper.getReadableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("card_no", cardNum);
//        values.put("student_name", studentCards.get(indexoftemp).getStudent_name());
//        values.put("lab_room_id", studentCards.get(indexoftemp).getLab_room_id());
//        values.put("student_number", studentCards.get(indexoftemp).getStudent_number());
//        values.put("course_number", studentCards.get(indexoftemp).getCourse_number());
//        values.put("current_time", GetCurrentTime.getLongStringCurrentTime());
//        values.put("start_class", studentCards.get(indexoftemp).getStart_class());
//        values.put("end_class", studentCards.get(indexoftemp).getEnd_class());
//            db.insert("StudentCardTable", null, values);
//            Cursor cursor = db.query("StudentCardTable", null, null, null, null, null, null);
//            ExportToCSV.ExportToCSV(cursor, "保存已刷过得卡的卡号的文件.csv");
//            cursor.close();
//        int lab_room_id = cursor.getInt(cursor.getColumnIndex("lab_room_id"));
//        int course_present_people = cursor.getInt(cursor.getColumnIndex("course_present_people"));
//        int start_class = cursor.getInt(cursor.getColumnIndex("start_class"));
//        int end_class = cursor.getInt(cursor.getColumnIndex("end_class"));
//        int total_hour = cursor.getInt(cursor.getColumnIndex("total_hour"));
//        String course_name = cursor.getString(cursor.getColumnIndex("course_name"));
//        String course_number = cursor.getString(cursor.getColumnIndex("course_number"));
//        String course_image_url = cursor.getString(cursor.getColumnIndex("course_image_url"));
//        String course_teacher_name = cursor.getString(cursor.getColumnIndex("course_teacher_name"));
//        String course_beginning_time = cursor.getString(cursor.getColumnIndex("course_beginning_time"));

//        db.execSQL("DELETE FROM LessonTable");

//        Cursor cursorforrecover = db.rawQuery("select * from StudentCardTable where course_number=? and lab_room_id=? and start_class=?",new String[]{lessonTables.get(i).getCourse_number(),LabID+"",lessonTables.get(i).getStart_class()+""});

//            Cursor cursor = db.query("DeciceVideos", null, null, null, null, null, null);
//            while (cursor.moveToNext())
//            {
//                int lab_room_id = cursor.getInt(cursor.getColumnIndex("lab_room_id"));
//                String lab_device_name = cursor.getString(cursor.getColumnIndex("lab_device_name"));
//                String lab_device_video_path = cursor.getString(cursor.getColumnIndex("lab_device_video_path"));
////                Log.i("zxj", lab_room_id+"名称："+lab_device_name+"地址"+lab_device_video_path);
//            }
//            ExportToCSV.ExportToCSV(cursor, "保存已刷过得卡的卡号的文件.csv");
//            cursor.close();
        /*************************************************
         *@description： 数据处理
        *************************************************/
         Handler handlerForlabRoomInfo = new Handler() {
            public void handleMessage(Message msg) {
                String response = msg.obj.toString();
                String labroominfo1=null;
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    labroominfo1 = jsonObject.getString("result");
                } catch (JSONException e) {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                }
                if(labroominfo1!=null) {
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonArray Jarray = parser.parse(labroominfo1).getAsJsonArray();
                    LabRoomInfo labRoomInfo = null;
                    for (JsonElement obj : Jarray) {
                        labRoomInfo = gson.fromJson(obj, LabRoomInfo.class);
                        labRoomInfoList.add(labRoomInfo);
                    }
                }else{
//                    Toast.makeText(getContext(), "当前无该实验室的信息", Toast.LENGTH_SHORT).show();
                }
            }
        };
        /*************************************************
         *@description： 数据连接
        *************************************************/
        try {
            URL url = new URL(remoteUrl + "labRoomInfo/labroomDetail/" + LabID);
            getConnection(url, handlerForlabRoomInfo);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return labRoomInfoList;
    }
//        String path2="http://img.my.csdn.net/uploads/201407/26/1406383243_5120.jpg";
//        ViewGroup group = (ViewGroup) getActivity().findViewById(R.id.viewGroup);
//        final LinearLayout[] linearLayouts=new LinearLayout[3];
//        final ImageView[] imageViews = new ImageView[1];
//        final TextView[] textViews=new TextView[3];
//        for (int i = 0; i < imageViews.length; i++) {
//            //第一层布局
//            LinearLayout linearLayout=new LinearLayout(getContext());
////            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(300,300));
//            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(300, 320);
//            linearLayout.setLayoutParams(layoutParams);
//            linearLayout.setPadding(10,10,10,10);
//            linearLayout.setBackgroundColor(Color.WHITE);
//            linearLayout.setOrientation(LinearLayout.VERTICAL);
//            linearLayouts[i]=linearLayout;
//            group.addView(linearLayout);
//            //第二层布局
//            ImageView imageView = new ImageView(getContext());
//            imageView.setLayoutParams(new ViewGroup.LayoutParams(100, 100));
//            imageViews[i] = imageView;
//            imageView.setImageResource(R.drawable.a305_1);
//            TextView textView = new TextView(getContext());
//            textView.setLayoutParams(new ViewGroup.LayoutParams(100,100));
//            textView.setText("实验");
//            textViews[i]=textView;
//            linearLayout.addView(imageView);
//            linearLayout.addView(textView);
//            group.addView(imageView);
//            group.addView(textView);
//        }



    /*************************************************
     *@description： 防止点击返回键时回到那个页面
     *  造成View的重复绘制
     *************************************************/
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Intent intent = new Intent();
//            intent.putExtra("BackForNotCreateMore", "发送广播使得返回的时候创建的时候不会很多");
//            intent.setAction("com.BackForNotCreateMore");
//            sendBroadcast(intent);
//            finish();
//        }
//        return super.onKeyDown(keyCode, event);
//    }












//    /*************************************************
//     *@description： 注册接收播放本地的广播
//     *************************************************/
//    public void registerForPlayLocal()
//    {
//        broadcastReceiverToPlayLocal=new BroadcastReceiverToPlayLocal();
//        IntentFilter filter = new IntentFilter();
//        filter.setPriority(6);
//        filter.addAction("com.BrocastForPlayLocal");
//        registerReceiver(broadcastReceiverToPlayLocal, filter);
//    }
//    /*************************************************
//     *@description： 定时接收播放本地广播接收器
//     *************************************************/
//    public class BroadcastReceiverToPlayLocal extends BroadcastReceiver
//    {
//        @Override
//        public void onReceive(Context context, Intent intent)
//        {
//            String path=intent.getStringExtra("BrocastForPlayLocal");
//            Message mes=new Message();
//            mes.obj=path;
//            handlerForPlayLocal.sendMessage(mes);
//        }
//    }
}

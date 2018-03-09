//package com.bjw.DynamicInfo;
//
///**
// * 创建人：wxdn
// * 创建时间：2017/10/30
// * 功能描述：
// */
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.Color;
//import android.net.wifi.WifiInfo;
//import android.net.wifi.WifiManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.provider.Settings;
//import android.support.v4.app.Fragment;
//import android.support.v4.view.ViewPager;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.bjw.Adapter.FragmentAdapter;
//import com.bjw.R;
//import com.bjw.bean.LabRoomInfo;
//import com.bumptech.glide.Glide;
//import com.google.gson.Gson;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonParser;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import io.vov.vitamio.MediaPlayer;
//import io.vov.vitamio.widget.MediaController;
//import io.vov.vitamio.widget.VideoView;
//
//import static com.bjw.Common.Connection.getConnection;
//import static com.bjw.Common.StaticConfig.AndroidId;
//import static com.bjw.Common.StaticConfig.IP;
//import static com.bjw.Common.StaticConfig.LabID;
//import static com.bjw.Common.StaticConfig.MacAddress;
//import static com.bjw.Common.StaticConfig.remoteUrl;
//
//public class DynamicInfoFragment extends Fragment {
//    private VideoView mVideoView2;
//    static BroadcastReceiverToChangeTime broadcastReceiverToChangeTime;
//    ImageView underimg1,underimg2,underimg3,img;
//    private List<Fragment> mFragmentList1 = new ArrayList<Fragment>();
//    private ViewPager rightmPageVp;
//    private FragmentAdapter mFragmentAdapter1;
//    LessonFragment lessonFragment = new LessonFragment();
//    MessageFragment messageFragment=new MessageFragment();
//    TableFragment tableFragment=new TableFragment();
//    TextView lessonTv,messageTv,tableTv;
//    TextView datetime,weathertime;
//    TextView labname,lab_introduct;
//    int flag=0;//来标志当前是否获取到实验室信息
//    String labintroduct="";//实验室介绍的字符串
//    String stringforLabname="";//实验室名字的字符串
//    List<String>imageList=new ArrayList<>();//图片地址的集合
//    Map<String,Object> map = new HashMap<String,Object>() ;//保存实验室图片与对应的URL的hashMap，用来实现点击切换的功能
//    private MediaController mMediaController,mMediaController1;
//    String path1 = "rtmp://www.gvsun.net:19350/live/sz";
//    /*************************************************
//     *@description： 解析获取到的实验室的信息
//     *************************************************/
//    public Handler handlerForlabRoomInfo = new Handler() {
//        public void handleMessage(Message msg) {
//            String response = msg.obj.toString();
//            String labroominfo1=null;
//            try {
//                JSONObject jsonObject = new JSONObject(response.toString());
//                labroominfo1 = jsonObject.getString("result");
//            } catch (JSONException e) {
//                // TODO 自动生成的 catch 块
//                e.printStackTrace();
//            }
//            if(labroominfo1!=null){
//                Gson gson = new Gson();
//                JsonParser parser = new JsonParser();
//                JsonArray Jarray = parser.parse(labroominfo1).getAsJsonArray();
//                LabRoomInfo labRoomInfo = null;
//                String managers="";
//                for(JsonElement obj : Jarray ){
//                    labRoomInfo = gson.fromJson(obj , LabRoomInfo.class);
//                    labname.setText(labRoomInfo.getLab_name());
//                    stringforLabname=labRoomInfo.getLab_name();
////                    不为空的时候将其加入到里面
//                    if(labRoomInfo.getManager_name()!=null) {
////                        managers = labRoomInfo.getManager_name() + " " + managers;
//                          managers = labRoomInfo.getManager_name();
//                    }
//                    if(labRoomInfo.getLab_image_url()!=null) {
//                        imageList.add(labRoomInfo.getLab_image_url());
//                    }
//                }
//                if(managers.equals("")) {
//                    lab_introduct.setText("面积：" + labRoomInfo.getLab_room_area() + "平方米 容量：" + labRoomInfo.getLab_room_capacity() + "人 管理员：当前没有管理员");
//                    labintroduct="面积：" + labRoomInfo.getLab_room_area() + "平方米 容量：" + labRoomInfo.getLab_room_capacity() + "人 管理员：当前没有管理员";
//                }
//                else
//                {
//                    lab_introduct.setText("面积：" + labRoomInfo.getLab_room_area() + "平方米 容量：" + labRoomInfo.getLab_room_capacity() + "人 管理员：" + managers);
//                    labintroduct="面积：" + labRoomInfo.getLab_room_area() + "平方米 容量：" + labRoomInfo.getLab_room_capacity() + "人 管理员：当前没有管理员";
//                }
//                flag=1;
//            }else{
//                Toast.makeText(getContext(), "当前无该实验室的信息", Toast.LENGTH_SHORT).show();
//            }
//        }
//    };
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
//        View chatView = inflater.inflate(R.layout.fragment_dynamic, container, false);
//        return chatView;
//    }
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//    }
//    @Override
//    public void onStart() {
//        super.onStart();
//        findViewId();
//        int a305_2 = R.drawable.a305_2;
//        Glide.with(getActivity()).load(a305_2).into(underimg1);
//        int a305_3 = R.drawable.a305_3;
//        Glide.with(getActivity()).load(a305_3).into(underimg2);
//        int a305_4 = R.drawable.a305_4;
//        Glide.with(getActivity()).load(a305_4).into(underimg3);
////        获取一些必要的地址
//         IP=getlocalip();
//        WifiManager wifi = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        WifiInfo info = wifi.getConnectionInfo();
//        MacAddress = info.getMacAddress();
//        AndroidId = Settings.System.getString(getActivity().getContentResolver(), Settings.System.ANDROID_ID);
//        //获取实验室的信息,如果之前有数据就直接显示，没有数据就去服务器获取数据
//        if(flag==0){
//            try {
//              URL url = new URL(remoteUrl+"labRoomInfo/labroomDetail/"+LabID);
//                getConnection(url,handlerForlabRoomInfo);
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
//        }
//        else
//        {
//            lab_introduct.setText(labintroduct);
//            labname.setText(stringforLabname);
//        }
//        if(broadcastReceiverToChangeTime==null) {
//            registerForChangeTime();
//        }
//        mFragmentList1.add(lessonFragment);
//        mFragmentList1.add(messageFragment);
//        mFragmentList1.add(tableFragment);
//        mFragmentAdapter1 = new FragmentAdapter(getChildFragmentManager(), mFragmentList1);
//        rightmPageVp.setAdapter(mFragmentAdapter1);
//        rightmPageVp.setCurrentItem(0);
//
////        点击事件的切换处理
//        lessonTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                mFragmentAdapter1 = new FragmentAdapter(getChildFragmentManager(), mFragmentList1);
////                rightmPageVp.setAdapter(mFragmentAdapter1);
//                resetColor();
//                lessonTv.setBackgroundColor(Color.parseColor("#0f2740"));
//                rightmPageVp.setCurrentItem(0);
//            }
//        });
//        messageTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                mFragmentAdapter1 = new FragmentAdapter(getChildFragmentManager(), mFragmentList1);
////                rightmPageVp.setAdapter(mFragmentAdapter1);
//                resetColor();
//                messageTv.setBackgroundColor(Color.parseColor("#0f2740"));
//                rightmPageVp.setCurrentItem(1);
//            }
//        });
//        tableTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                mFragmentAdapter1 = new FragmentAdapter(getChildFragmentManager(), mFragmentList1);
////                rightmPageVp.setAdapter(mFragmentAdapter1);
//                resetColor();
//                tableTv.setBackgroundColor(Color.parseColor("#0f2740"));
//                rightmPageVp.setCurrentItem(2);
//            }
//        });
//        rightmPageVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrollStateChanged(int state) {
//            }
//            @Override
//            public void onPageScrolled(int position, float offset,
//                                       int offsetPixels) {
//            }
//            @Override
//            public void onPageSelected(int position) {
//                resetColor();
//                switch (position) {
//                    case 0:
//                        lessonTv.setBackgroundColor(Color.parseColor("#0f2740"));
//                        break;
//                    case 1:
//                        messageTv.setBackgroundColor(Color.parseColor("#0f2740"));
//                        break;
//                    case 2:
//                        tableTv.setBackgroundColor(Color.parseColor("#0f2740"));
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
//        if(false) {
////            img=(ImageView)getActivity().findViewById(R.id.mainimg);
//            int a305_1 = R.drawable.a305_1;
//            Glide.with(getActivity()).load(a305_1).into(img);
//        }
//        else {
//        mVideoView2 = (VideoView) getActivity().findViewById(R.id.surface_view2);
//        mVideoView2.setVideoPath(path1);
//        mMediaController = new MediaController(getContext());
//        mVideoView2.setMediaController(mMediaController);
//        mVideoView2.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);//高画质
//        mVideoView2.requestFocus();
////            mVideoView2.start();
//        mVideoView2.setOnInfoListener(new MediaPlayer.OnInfoListener() {
//            /** 是否需要自动恢复播放，用于自动暂停，恢复播放 */
//            @Override
//            public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
//                switch (arg1) {
//                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
//                        startPlayer();
//                        break;
//                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
//                        startPlayer();
//                        break;
//                    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
//                        //显示 下载速度
//                        break;
//                }
//                return true;
//            }
//        });
//        }
//    }
//    private void startPlayer() {
//        if (mVideoView2 != null)
//            mVideoView2.start();
//    }
//    private boolean isPlaying() {
//        return mVideoView2 != null && mVideoView2.isPlaying();
//    }
//
//    /*************************************************
//     *@description： 得到IP地址
//    *************************************************/
//    private String getlocalip(){
//        WifiManager wifiManager = (WifiManager)getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        int ipAddress = wifiInfo.getIpAddress();
//        if(ipAddress==0)
//            return "未连接wifi";
//        return ((ipAddress & 0xff)+"."+(ipAddress>>8 & 0xff)+"."
//                +(ipAddress>>16 & 0xff)+"."+(ipAddress>>24 & 0xff));
//    }
//    /*************************************************
//     *@description： 控件绑定
//    *************************************************/
//    public void findViewId()
//    {
//        rightmPageVp=(ViewPager)getActivity().findViewById(R.id.change_right_tab);
//        lessonTv=(TextView) getActivity().findViewById(R.id.lesson);
//        messageTv=(TextView) getActivity().findViewById(R.id.message);
//        tableTv=(TextView) getActivity().findViewById(R.id.table);
//        underimg1=(ImageView)getActivity().findViewById(R.id.under_img1);
//        underimg2=(ImageView)getActivity().findViewById(R.id.under_img2);
//        underimg3=(ImageView)getActivity().findViewById(R.id.under_img3);
//        datetime=(TextView)getActivity().findViewById(R.id.datetime);
//        weathertime=(TextView)getActivity().findViewById(R.id.weathertime);
//        labname=(TextView)getActivity().findViewById(R.id.labname);
//        lab_introduct=(TextView)getActivity().findViewById(R.id.lab_introduct);
//    }
//    /*************************************************
//     *@description： 图片的切换点击时间的监听
//     *************************************************/
////    public void changeImage()
////    {
//////        img.setOnClickListener(new View.OnClickListener() {
//////            @Override
//////            public void onClick(View view) {
//////            }
//////        });
////        underimg1.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                  Glide.with(getActivity()).load(Integer.parseInt(map.get("underimg1").toString())).into(img);
////                  Glide.with(getActivity()).load(Integer.parseInt(map.get("img").toString())).into(underimg1);
////                  String rpforunderimg1=map.get("img").toString();
////                  String rpforimg=map.get("underimg1").toString();
////                  map.put("img",rpforimg);
////                  map.put("underimg1",rpforunderimg1);
////            }
////        });
////        underimg2.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                Glide.with(getActivity()).load(Integer.parseInt(map.get("underimg2").toString())).into(img);
////                Glide.with(getActivity()).load(Integer.parseInt(map.get("img").toString())).into(underimg2);
////                String rpforunderimg2=map.get("img").toString();
////                String rpforimg=map.get("underimg2").toString();
////                map.put("img",rpforimg);
////                map.put("underimg2",rpforunderimg2);
////            }
////        });
////        underimg3.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                Glide.with(getActivity()).load(Integer.parseInt(map.get("underimg3").toString())).into(img);
////                Glide.with(getActivity()).load(Integer.parseInt(map.get("img").toString())).into(underimg3);
////                String rpforunderimg3=map.get("img").toString();
////                String rpforimg=map.get("underimg3").toString();
////                map.put("img",rpforimg);
////                map.put("underimg3",rpforunderimg3);
////            }
////        });
////    }
//    /*************************************************
//     *@description： 重新设计背景颜色
//    *************************************************/
//    public void resetColor()
//    {
//        messageTv.setBackgroundColor(Color.parseColor("#254161"));
//        tableTv.setBackgroundColor(Color.parseColor("#254161"));
//        lessonTv.setBackgroundColor(Color.parseColor("#254161"));
//    }
//    /*************************************************
//     *@description： 注册得定时器的广播
//     *************************************************/
//    public void registerForChangeTime()
//    {
//        broadcastReceiverToChangeTime=new BroadcastReceiverToChangeTime();
//        IntentFilter filter = new IntentFilter();
//        filter.setPriority(1);
//        filter.addAction("com.ChangeTime");
//        getActivity().registerReceiver(broadcastReceiverToChangeTime, filter);
//    }
//    /*************************************************
//     *@description： 定时操作的广播接收器
//     *************************************************/
//    public class BroadcastReceiverToChangeTime extends BroadcastReceiver
//    {
//        @Override
//        public void onReceive(Context context, Intent intent)
//        {
//            String time=intent.getStringExtra("currentTime");
//            datetime.setText(time);
//        }
//    }
//    @Override
//    public void onPause()
//    {
//        super.onPause();
//    }
//    @Override
//    public void onStop()
//    {
//        super.onStop();
//        mFragmentList1.clear();
//    }
//    @Override
//    public void onDestroy()
//    {
//        broadcastReceiverToChangeTime=null;
//        super.onDestroy();
//    }
//}

package com.bjw.DynamicInfo;

/**
 * 创建人：wxdn
 * 创建时间：2017/10/30
 * 功能描述：
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bjw.Adapter.FragmentAdapter;
import com.bjw.R;
import com.bjw.bean.LabRoomInfo;
import com.bumptech.glide.Glide;
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
import static com.bjw.Common.StaticConfig.AndroidId;
import static com.bjw.Common.StaticConfig.IP;
import static com.bjw.Common.StaticConfig.LabID;
import static com.bjw.Common.StaticConfig.MacAddress;
import static com.bjw.Common.StaticConfig.remoteUrl;

public class DynamicInfoFragment extends Fragment {
    @BindView(R.id.mainimg) ImageView img;
    @BindView(R.id.under_img1)ImageView underimg1;
    @BindView(R.id.under_img2)ImageView underimg2;
    @BindView(R.id.under_img3)ImageView underimg3;
    @BindView(R.id.lesson) TextView lessonTv;
    @BindView(R.id.table) TextView tableTv;
    @BindView(R.id.message) TextView messageTv;
    @BindView(R.id.datetime) TextView datetime;
    @BindView(R.id.weathertime) TextView weathertime;
    @BindView(R.id.labname) TextView labname;
    @BindView(R.id.lab_introduct) TextView lab_introduct;
    @BindView(R.id.change_right_tab)ViewPager rightmPageVp;
    static BroadcastReceiverToChangeTime broadcastReceiverToChangeTime;
    private List<Fragment> mFragmentList1 = new ArrayList<Fragment>();
    private FragmentAdapter mFragmentAdapter1;
    LessonFragment lessonFragment = new LessonFragment();
    MessageFragment messageFragment=new MessageFragment();
    TableFragment tableFragment=new TableFragment();
    int flag=0;//来标志当前是否获取到实验室信息
    String labintroduct="";//实验室介绍的字符串
    String stringforLabname="";//实验室名字的字符串
    List<String>imageList=new ArrayList<>();//图片地址的集合
    Map<String,Object> map = new HashMap<String,Object>() ;//保存实验室图片与对应的URL的hashMap，用来实现点击切换的功能
    /*************************************************
     *@description： 解析获取到的实验室的信息
     *************************************************/
    public Handler handlerForlabRoomInfo = new Handler() {
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
            if(labroominfo1!=null){
                Gson gson = new Gson();
                JsonParser parser = new JsonParser();
                JsonArray Jarray = parser.parse(labroominfo1).getAsJsonArray();
                LabRoomInfo labRoomInfo = null;
                String managers="";
                for(JsonElement obj : Jarray ){
                    labRoomInfo = gson.fromJson(obj , LabRoomInfo.class);
                    labname.setText(labRoomInfo.getLab_name());
                    stringforLabname=labRoomInfo.getLab_name();
//                    不为空的时候将其加入到里面
                    if(labRoomInfo.getManager_name()!=null) {
//                        managers = labRoomInfo.getManager_name() + " " + managers;
                        managers = labRoomInfo.getManager_name();
                    }
                    if(labRoomInfo.getLab_image_url()!=null) {
                        imageList.add(labRoomInfo.getLab_image_url());
                    }
                }
                if(managers.equals("")) {
                    lab_introduct.setText("面积：" + labRoomInfo.getLab_room_area() + "平方米 容量：" + labRoomInfo.getLab_room_capacity() + "人 管理员：当前没有管理员");
                    labintroduct="面积：" + labRoomInfo.getLab_room_area() + "平方米 容量：" + labRoomInfo.getLab_room_capacity() + "人 管理员：当前没有管理员";
                }
                else
                {
                    lab_introduct.setText("面积：" + labRoomInfo.getLab_room_area() + "平方米 容量：" + labRoomInfo.getLab_room_capacity() + "人 管理员：" + managers);
                    labintroduct="面积：" + labRoomInfo.getLab_room_area() + "平方米 容量：" + labRoomInfo.getLab_room_capacity() + "人 管理员：当前没有管理员";
                }
                flag=1;
            }else{
                Toast.makeText(getContext(), "当前无该实验室的信息", Toast.LENGTH_SHORT).show();
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View chatView = inflater.inflate(R.layout.fragment_dynamic, container, false);
        ButterKnife.bind(this,chatView);
        return chatView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public void onStart() {
        super.onStart();
//      根据实验室ID得到实验室的图片
        getLabRoomImage();
//        点击切换图片
        changeImage();
//        获取一些必要的地址
        IP=getlocalip();
        WifiManager wifi = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        MacAddress = info.getMacAddress();
        AndroidId = Settings.System.getString(getActivity().getContentResolver(), Settings.System.ANDROID_ID);
        //获取实验室的信息,如果之前有数据就直接显示，没有数据就去服务器获取数据
        if(flag==0){
            try {
                URL url = new URL(remoteUrl+"labRoomInfo/labroomDetail/"+LabID);
                getConnection(url,handlerForlabRoomInfo);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        else
        {
            lab_introduct.setText(labintroduct);
            labname.setText(stringforLabname);
        }
        if(broadcastReceiverToChangeTime==null) {
            registerForChangeTime();
        }
        mFragmentList1.add(lessonFragment);
        mFragmentList1.add(messageFragment);
        mFragmentList1.add(tableFragment);
        mFragmentAdapter1 = new FragmentAdapter(getChildFragmentManager(), mFragmentList1);
        rightmPageVp.setAdapter(mFragmentAdapter1);
        rightmPageVp.setCurrentItem(0);
//        点击事件的切换处理
        lessonTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetColor();
                lessonTv.setBackgroundColor(Color.parseColor("#0f2740"));
                rightmPageVp.setCurrentItem(0);
            }
        });
        messageTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetColor();
                messageTv.setBackgroundColor(Color.parseColor("#0f2740"));
                rightmPageVp.setCurrentItem(1);
            }
        });
        tableTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mFragmentAdapter1 = new FragmentAdapter(getChildFragmentManager(), mFragmentList1);
//                rightmPageVp.setAdapter(mFragmentAdapter1);
                resetColor();
                tableTv.setBackgroundColor(Color.parseColor("#0f2740"));
                rightmPageVp.setCurrentItem(2);
            }
        });
        rightmPageVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
            }
            @Override
            public void onPageScrolled(int position, float offset,
                                       int offsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                resetColor();
                switch (position) {
                    case 0:
                        lessonTv.setBackgroundColor(Color.parseColor("#0f2740"));
                        break;
                    case 1:
                        messageTv.setBackgroundColor(Color.parseColor("#0f2740"));
                        break;
                    case 2:
                        tableTv.setBackgroundColor(Color.parseColor("#0f2740"));
                        break;
                    default:
                        break;
                }
            }
        });
    }
    /*************************************************
     *@description： 得到实验室的图片
    *************************************************/
    private void getLabRoomImage()
    {
        //应急使用
        //305
        if(LabID==895) {
            int a305_1 = R.drawable.a305_1;
            Glide.with(getActivity()).load(a305_1).into(img);
            int a305_2 = R.drawable.a305_2;
            Glide.with(getActivity()).load(a305_2).into(underimg1);
            int a305_3 = R.drawable.a305_3;
            Glide.with(getActivity()).load(a305_3).into(underimg2);
            int a305_4 = R.drawable.a305_4;
            Glide.with(getActivity()).load(a305_4).into(underimg3);
            map.put("img",R.drawable.a305_1+"") ;
            map.put("underimg1",R.drawable.a305_2+"") ;
            map.put("underimg2",R.drawable.a305_3+"" ) ;
            map.put("underimg3",R.drawable.a305_4+"") ;
        }
        else if(LabID==899) {
            //308
            int a308_1 = R.drawable.a308_1;
            Glide.with(getActivity()).load(a308_1).into(img);
            int a308_2 = R.drawable.a308_2;
            Glide.with(getActivity()).load(a308_2).into(underimg1);
            int a308_3 = R.drawable.a308_3;
            Glide.with(getActivity()).load(a308_3).into(underimg2);
            int a308_4 = R.drawable.a308_4;
            Glide.with(getActivity()).load(a308_4).into(underimg3);
            map.put("img",R.drawable.a308_1+"") ;
            map.put("underimg1",R.drawable.a308_2+"") ;
            map.put("underimg2",R.drawable.a308_3+"" ) ;
            map.put("underimg3",R.drawable.a308_4+"") ;
        }
        else if(LabID==900) {
            //309
            int a309_1 = R.drawable.a309_1;
            Glide.with(getActivity()).load(a309_1).into(img);
            int a309_2 = R.drawable.a309_2;
            Glide.with(getActivity()).load(a309_2).into(underimg1);
            int a309_3 = R.drawable.a309_3;
            Glide.with(getActivity()).load(a309_3).into(underimg2);
            int a309_4 = R.drawable.a309_4;
            Glide.with(getActivity()).load(a309_4).into(underimg3);
            map.put("img",R.drawable.a309_1+"") ;
            map.put("underimg1",R.drawable.a309_2+"") ;
            map.put("underimg2",R.drawable.a309_3+"" ) ;
            map.put("underimg3",R.drawable.a309_4+"") ;
        }
        else if(LabID==904) {
            //312
            int a312_1 = R.drawable.a312_1;
            Glide.with(getActivity()).load(a312_1).into(img);
            int a312_2 = R.drawable.a312_2;
            Glide.with(getActivity()).load(a312_2).into(underimg1);
            int a312_3 = R.drawable.a312_3;
            Glide.with(getActivity()).load(a312_3).into(underimg2);
            int a312_4 = R.drawable.a312_4;
            Glide.with(getActivity()).load(a312_4).into(underimg3);
            map.put("img",R.drawable.a312_1+"") ;
            map.put("underimg1",R.drawable.a312_2+"") ;
            map.put("underimg2",R.drawable.a312_3+"" ) ;
            map.put("underimg3",R.drawable.a312_4+"") ;
        }
        else if(LabID==903||LabID==902) {
            //313  //311
            int a313_1 = R.drawable.a313_1;
            Glide.with(getActivity()).load(a313_1).into(img);
            int a313_2 = R.drawable.a313_2;
            Glide.with(getActivity()).load(a313_2).into(underimg1);
            int a313_3 = R.drawable.a313_3;
            Glide.with(getActivity()).load(a313_3).into(underimg2);
            int a313_4 = R.drawable.a313_4;
            Glide.with(getActivity()).load(a313_4).into(underimg3);
            map.put("img",R.drawable.a313_1+"") ;
            map.put("underimg1",R.drawable.a313_2+"") ;
            map.put("underimg2",R.drawable.a313_3+"" ) ;
            map.put("underimg3",R.drawable.a313_4+"") ;
        }
        else if(LabID==905) {
            //314
            int a314_1 = R.drawable.a314_1;
            Glide.with(getActivity()).load(a314_1).into(img);
            int a314_2 = R.drawable.a314_2;
            Glide.with(getActivity()).load(a314_2).into(underimg1);
            int a314_3 = R.drawable.a314_3;
            Glide.with(getActivity()).load(a314_3).into(underimg2);
            int a314_4 = R.drawable.a314_4;
            Glide.with(getActivity()).load(a314_4).into(underimg3);
            map.put("img",R.drawable.a314_1+"") ;
            map.put("underimg1",R.drawable.a314_2+"") ;
            map.put("underimg2",R.drawable.a314_3+"" ) ;
            map.put("underimg3",R.drawable.a314_4+"") ;
        }
        else if(LabID==906) {
            //315
            int a315_1 = R.drawable.a315_1;
            Glide.with(getActivity()).load(a315_1).into(img);
            int a315_2 = R.drawable.a315_2;
            Glide.with(getActivity()).load(a315_2).into(underimg1);
            int a315_3 = R.drawable.a315_3;
            Glide.with(getActivity()).load(a315_3).into(underimg2);
            int a315_4 = R.drawable.a315_4;
            Glide.with(getActivity()).load(a315_4).into(underimg3);
            map.put("img",R.drawable.a315_1+"") ;
            map.put("underimg1",R.drawable.a315_2+"") ;
            map.put("underimg2",R.drawable.a315_3+"" ) ;
            map.put("underimg3",R.drawable.a315_4+"") ;
        }
        else if(LabID== 919) {
            //410东
            int a410w_1 = R.drawable.a410w_1;
            Glide.with(getActivity()).load(a410w_1).into(img);
            int a410w_2 = R.drawable.a410w_2;
            Glide.with(getActivity()).load(a410w_2).into(underimg1);
            int a410w_3 = R.drawable.a410w_3;
            Glide.with(getActivity()).load(a410w_3).into(underimg2);
            int a410w_4 = R.drawable.a410w_4;
            Glide.with(getActivity()).load(a410w_4).into(underimg3);
            map.put("img",R.drawable.a410w_1+"") ;
            map.put("underimg1",R.drawable.a410w_2+"") ;
            map.put("underimg2",R.drawable.a410w_3+"" ) ;
            map.put("underimg3",R.drawable.a410w_4+"") ;
        }
        else if(LabID==918) {
//        //410西
            int a410e_1 = R.drawable.a410e_1;
            Glide.with(getActivity()).load(a410e_1).into(img);
            int a410e_2 = R.drawable.a410e_2;
            Glide.with(getActivity()).load(a410e_2).into(underimg1);
            int a410e_3 = R.drawable.a410e_3;
            Glide.with(getActivity()).load(a410e_3).into(underimg2);
            int a410e_4 = R.drawable.a410e_4;
            Glide.with(getActivity()).load(a410e_4).into(underimg3);
            map.put("img",R.drawable.a410e_1+"") ;
            map.put("underimg1",R.drawable.a410e_2+"") ;
            map.put("underimg2",R.drawable.a410e_3+"" ) ;
            map.put("underimg3",R.drawable.a410e_4+"") ;
        }
        else  if(LabID==921) {
            //412
            int a412_1 = R.drawable.a412_1;
            Glide.with(getActivity()).load(a412_1).into(img);
            int a412_2 = R.drawable.a412_2;
            Glide.with(getActivity()).load(a412_2).into(underimg1);
            int a412_3 = R.drawable.a412_3;
            Glide.with(getActivity()).load(a412_3).into(underimg2);
            int a412_4 = R.drawable.a412_4;
            Glide.with(getActivity()).load(a412_4).into(underimg3);
            map.put("img", a412_1 + "");
            map.put("underimg1", R.drawable.a412_2 + "");
            map.put("underimg2", R.drawable.a412_3 + "");
            map.put("underimg3", R.drawable.a412_4 + "");
        }
    }

    /*************************************************
     *@description： 得到IP地址
     *************************************************/
    private String getlocalip(){
        WifiManager wifiManager = (WifiManager)getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        if(ipAddress==0)
            return "未连接wifi";
        return ((ipAddress & 0xff)+"."+(ipAddress>>8 & 0xff)+"."
                +(ipAddress>>16 & 0xff)+"."+(ipAddress>>24 & 0xff));
    }
    /*************************************************
     *@description： 图片的切换点击时间的监听
     *************************************************/
    public void changeImage()
    {
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        underimg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Glide.with(getActivity()).load(Integer.parseInt(map.get("underimg1").toString())).into(img);
                Glide.with(getActivity()).load(Integer.parseInt(map.get("img").toString())).into(underimg1);
                String rpforunderimg1=map.get("img").toString();
                String rpforimg=map.get("underimg1").toString();
                map.put("img",rpforimg);
                map.put("underimg1",rpforunderimg1);
            }
        });
        underimg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Glide.with(getActivity()).load(Integer.parseInt(map.get("underimg2").toString())).into(img);
                Glide.with(getActivity()).load(Integer.parseInt(map.get("img").toString())).into(underimg2);
                String rpforunderimg2=map.get("img").toString();
                String rpforimg=map.get("underimg2").toString();
                map.put("img",rpforimg);
                map.put("underimg2",rpforunderimg2);
            }
        });
        underimg3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Glide.with(getActivity()).load(Integer.parseInt(map.get("underimg3").toString())).into(img);
                Glide.with(getActivity()).load(Integer.parseInt(map.get("img").toString())).into(underimg3);
                String rpforunderimg3=map.get("img").toString();
                String rpforimg=map.get("underimg3").toString();
                map.put("img",rpforimg);
                map.put("underimg3",rpforunderimg3);
            }
        });
    }
    /*************************************************
     *@description： 重新设计背景颜色
     *************************************************/
    public void resetColor()
    {
        messageTv.setBackgroundColor(Color.parseColor("#254161"));
        tableTv.setBackgroundColor(Color.parseColor("#254161"));
        lessonTv.setBackgroundColor(Color.parseColor("#254161"));
    }
    /*************************************************
     *@description： 注册得定时器的广播
     *************************************************/
    public void registerForChangeTime()
    {
        broadcastReceiverToChangeTime=new BroadcastReceiverToChangeTime();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(1);
        filter.addAction("com.ChangeTime");
        getActivity().registerReceiver(broadcastReceiverToChangeTime, filter);
    }
    /*************************************************
     *@description： 定时操作的广播接收器
     *************************************************/
    public class BroadcastReceiverToChangeTime extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String time=intent.getStringExtra("currentTime");
            datetime.setText(time);
        }
    }
    @Override
    public void onPause()
    {
        super.onPause();
    }
    @Override
    public void onStop()
    {
        super.onStop();
        mFragmentList1.clear();
    }
    @Override
    public void onDestroy()
    {
        broadcastReceiverToChangeTime=null;
        super.onDestroy();
    }
}
package com.bjw.MainFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bjw.Adapter.FragmentAdapter;
import com.bjw.Common.NetWorkStateReceiver;
import com.bjw.DynamicInfo.DynamicInfoFragment;
import com.bjw.Fourth.fourth;
import com.bjw.LabDeviceInfo.LabDeviceFragment;
import com.bjw.LabReport.LabReportFragment;
import com.bjw.R;
import com.bjw.Service.ChangeCourseService;
import com.bjw.Service.ChangeTimeService;
import com.bjw.Service.ChangeCardInfoService;
import com.bjw.Service.RefreshMessageService;
import com.bjw.Service.SerialReadService;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends FragmentActivity {
    BroadcastReceiverToExit broadcastReceiverToExit;
    NetWorkStateReceiver netWorkStateReceiver;//网络状态的监听广播
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private FragmentAdapter mFragmentAdapter;
    private ViewPager mPageVp;
    private TextView firsttv, secondtv, thirdtv,fourthtv;
    private LinearLayout firsttv1, secondtv1, thirdtv1,fourthtv1;
    private DynamicInfoFragment firstFragment;
    private LabDeviceFragment secondFragment;
    private LabReportFragment thirdFragment;
    private fourth fourthFragment;
    Intent intentForGetnum,intentForTime,intentForChangeTime,
            intentforGetcardinfo,intentforReFreshMes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        findById();
        init();
        //提示用户数据获取的状况
        Intent i=getIntent();
        String info=i.getStringExtra("flag");
        Toast.makeText(getBaseContext(), info, Toast.LENGTH_SHORT).show();
        registerForExit();
        //开启串口，卡号，定时器，改变时间的服务
//        intentForGetnum=new Intent(getBaseContext(),SerialReadService.class);
//        getBaseContext().startService(intentForGetnum);
//        intentForTime=new Intent(getBaseContext(),ChangeCourseService.class);
//        getBaseContext().startService(intentForTime);
//        intentForChangeTime=new Intent(getBaseContext(),ChangeTimeService.class);
//        getBaseContext().startService(intentForChangeTime);
//        intentforGetcardinfo=new Intent(getBaseContext(),ChangeCardInfoService.class);
//        getBaseContext().startService(intentforGetcardinfo);
//        intentforCheckInterNet=new Intent(getBaseContext(),CheckInternetService.class);
//        getBaseContext().startService(intentforCheckInterNet);
        firsttv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mFragmentAdapter = new FragmentAdapter(
//                        getSupportFragmentManager(), mFragmentList);
//                mPageVp.setAdapter(mFragmentAdapter);
                resetTextView();
                firsttv1.setBackgroundColor(Color.RED);
                mPageVp.setCurrentItem(0);
            }
        });
        secondtv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mFragmentAdapter = new FragmentAdapter(
//                        getSupportFragmentManager(), mFragmentList);
//                mPageVp.setAdapter(mFragmentAdapter);
                resetTextView();
                secondtv1.setBackgroundColor(Color.RED);
                mPageVp.setCurrentItem(1);
            }
        });
        thirdtv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mFragmentAdapter = new FragmentAdapter(
//                        getSupportFragmentManager(), mFragmentList);
//                mPageVp.setAdapter(mFragmentAdapter);
                resetTextView();
                thirdtv1.setBackgroundColor(Color.RED);
                mPageVp.setCurrentItem(2);
            }
        });
        fourthtv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mFragmentAdapter = new FragmentAdapter(
//                        getSupportFragmentManager(), mFragmentList);
//                mPageVp.setAdapter(mFragmentAdapter);
                resetTextView();
                fourthtv1.setBackgroundColor(Color.RED);
                mPageVp.setCurrentItem(3);
            }
        });
                mPageVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
            }
            /**
             * position :当前页面，及你点击滑动的页面 offset:当前页面偏移的百分比
             * offsetPixels:当前页面偏移的像素位置
             */
            @Override
            public void onPageScrolled(int position, float offset,
                                       int offsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                resetTextView();
                switch (position) {
                    case 0:
                        firsttv1.setBackgroundColor(Color.RED);
                        break;
                    case 1:
                        secondtv1.setBackgroundColor(Color.RED);
                        break;
                    case 2:
                        thirdtv1.setBackgroundColor(Color.RED);
                        break;
                    case 3:
                        fourthtv1.setBackgroundColor(Color.RED);
                        break;
                    default:
                        break;
                }
            }
        });
    }
    private void findById() {
        firsttv = (TextView) this.findViewById(R.id.first_fg);
        secondtv = (TextView) this.findViewById(R.id.second_fg);
        thirdtv = (TextView) this.findViewById(R.id.third_fg);
        fourthtv= (TextView) this.findViewById(R.id.fourth_fg);
        firsttv1 = (LinearLayout) this.findViewById(R.id.first_ll);
        secondtv1 = (LinearLayout) this.findViewById(R.id.second_ll);
        thirdtv1= (LinearLayout) this.findViewById(R.id.third_ll);
        fourthtv1= (LinearLayout) this.findViewById(R.id.fourth_ll);
        mPageVp = (ViewPager) this.findViewById(R.id.id_page_vp);
    }
    /*************************************************
     *@description： 注册退出的广播
     *************************************************/
    public void registerForExit()
    {
        broadcastReceiverToExit=new BroadcastReceiverToExit();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(5);
        filter.addAction("com.Update");
        registerReceiver(broadcastReceiverToExit, filter);
    }

    /*************************************************
     *@description： 定时退出的广播接收器
     *************************************************/
    public class BroadcastReceiverToExit extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            //销毁这个程序然后直接发送广播给Loading销毁这个程序，更新数据
            Intent intent1 = new Intent();
            intent1.putExtra("WarnninLoad", "一天不关闭时调回选择界面");
            intent1.setAction("com.WarnninLoad");
            sendBroadcast(intent1);
            finish();
        }
    }
    private void init() {
        firstFragment = new DynamicInfoFragment();
        secondFragment = new LabDeviceFragment();
        thirdFragment = new LabReportFragment();
        fourthFragment = new fourth();
        mFragmentList.add(firstFragment);
        mFragmentList.add(secondFragment);
        mFragmentList.add(thirdFragment);
        mFragmentList.add(fourthFragment);
        mFragmentAdapter = new FragmentAdapter(
                this.getSupportFragmentManager(), mFragmentList);
        mPageVp.setAdapter(mFragmentAdapter);
        firsttv.setTextColor(Color.WHITE);
        firsttv1.setBackgroundColor(Color.RED);
        mPageVp.setCurrentItem(0);
    }
/*************************************************
 *@description： 重置颜色
*************************************************/
    private void resetTextView() {
        firsttv.setTextColor(Color.WHITE);
        secondtv.setTextColor(Color.WHITE);
        thirdtv.setTextColor(Color.WHITE);
        fourthtv.setTextColor(Color.WHITE);
        firsttv1.setBackgroundColor(Color.parseColor("#1b739e"));
        secondtv1.setBackgroundColor(Color.parseColor("#1b739e"));
        thirdtv1.setBackgroundColor(Color.parseColor("#1b739e"));
        fourthtv1.setBackgroundColor(Color.parseColor("#1b739e"));
    }
    //在这里面进行相应的网络判断，然后提示用户是否有网络
    /*************************************************
     *@description： 注册相应的监听广播
     *************************************************/
    @Override
    protected void onResume() {
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, filter);
//        重先进入的方法
        intentForGetnum=new Intent(getBaseContext(),SerialReadService.class);
        getBaseContext().startService(intentForGetnum);
        intentForTime=new Intent(getBaseContext(),ChangeCourseService.class);
        getBaseContext().startService(intentForTime);
        intentForChangeTime=new Intent(getBaseContext(),ChangeTimeService.class);
        getBaseContext().startService(intentForChangeTime);
        intentforGetcardinfo=new Intent(getBaseContext(),ChangeCardInfoService.class);
        getBaseContext().startService(intentforGetcardinfo);
        intentforReFreshMes=new Intent(getBaseContext(),RefreshMessageService.class);
        getBaseContext().startService(intentforReFreshMes);
        super.onResume();
    }
    //onPause()方法注销
    @Override
    protected void onPause() {
        getBaseContext().stopService(intentForGetnum);
        getBaseContext().stopService(intentForTime);
        getBaseContext().stopService(intentForChangeTime);
        getBaseContext().stopService(intentforGetcardinfo);
        getBaseContext().stopService(intentforReFreshMes);
        unregisterReceiver(netWorkStateReceiver);
        super.onPause();
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        getBaseContext().stopService(intentForGetnum);
        getBaseContext().stopService(intentForTime);
        getBaseContext().stopService(intentForChangeTime);
        getBaseContext().stopService(intentforGetcardinfo);
        getBaseContext().stopService(intentforReFreshMes);
//        getBaseContext().stopService(intentforCheckInterNet);
    }
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Intent intent=new Intent(getBaseContext(),ChooseLabActivity.class);
//            startActivity(intent);
//            return false;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

//    private void exit() {
//        if (!isExit) {
//            isExit = true;
//            Toast.makeText(getApplicationContext(), "再按一次退出程序",
//                    Toast.LENGTH_SHORT).show();
//            // 利用handler延迟发送更改状态信息
//        } else {
//        }
//    }
}

package com.bjw.LabDeviceInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

import com.bjw.R;

/**
 * 创建人：wxdn
 * 创建时间：2017/12/19
 * 功能描述：
 */

public class LabDeviceOperateActivity extends Activity {
    ImageButton btforBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_operation);
        findView();
        //点击返回的时候销毁当前的Activity
        btforBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //发送广播给返回的那个页面
                finish();
            }
        });
    }
    public void findView()
    {
        btforBack=(ImageButton)findViewById(R.id.im_bt_back);
    }
    /*************************************************
     *@description： 防止点击返回键时回到那个页面
     *  造成View的重复绘制
     *************************************************/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.putExtra("BackForNotCreateMore", "发送广播使得返回的时候创建的时候不会很多");
            intent.setAction("com.BackForNotCreateMore");
            sendBroadcast(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}

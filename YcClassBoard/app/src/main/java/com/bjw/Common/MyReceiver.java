package com.bjw.Common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bjw.MainFragment.ChooseLabActivity;

/**
 * 创建人：wxdn
 * 创建时间：2018/2/2
 * 功能描述：
 */

public class MyReceiver extends BroadcastReceiver
{
    public MyReceiver()
    {
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            Intent i = new Intent(context, ChooseLabActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
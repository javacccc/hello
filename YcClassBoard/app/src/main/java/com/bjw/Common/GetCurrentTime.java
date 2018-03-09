package com.bjw.Common;

import java.util.Calendar;

/**
 * 创建人：wxdn
 * 创建时间：2017/12/1
 * 功能描述：
 */

public class GetCurrentTime {
    /*************************************************
     *@description： 刷卡的时候保存的时间格式
     *************************************************/
    public static String  getLongStringCurrentTime() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        month = month + 1;
        int date = c.get(Calendar.DATE);
        String month_String="";
        if(month<10)
        {
            month_String="0"+month;
        }
        else
        {
            month_String=""+month;
        }
        String date_String="";
        if(date<10)
        {
            date_String="0"+date;
        }
        else
        {
            date_String=""+date;
        }
        String currentDate=year+"-"+month_String+"-"+date_String;
        int hour = c.get(Calendar.HOUR_OF_DAY);
        String minute1 = "";
        String sencond1 = "";
        int minute = c.get(Calendar.MINUTE);
        if (minute < 10) {
            minute1 = "0" + minute;
        } else {
            minute1 = "" + minute;
        }
        int second = c.get(Calendar.SECOND);
        if (second < 10) {
            sencond1 = "0" + second;
        } else {
            sencond1 = "" + second;
        }
        String currentTime = currentDate+ " " + hour + ":" + minute1 + ":" + sencond1;
        return currentTime;
    }

    public static String  getShortStringCurrentTime() {
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
        return currentTime;
    }
/*************************************************
 *@description： 得到当前日期 格式：2017-09-01
*************************************************/
    public static String  getCurrentDate() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        month = month + 1;
        int date = c.get(Calendar.DATE);
        String month_String="";
        if(month<10)
        {
            month_String="0"+month;
        }
        else
        {
            month_String=""+month;
        }
        String date_String="";
        if(date<10)
        {
            date_String="0"+date;
        }
        else
        {
            date_String=""+date;
        }
        String currentDate=year+"-"+month_String+"-"+date_String;
        return currentDate;
    }
}

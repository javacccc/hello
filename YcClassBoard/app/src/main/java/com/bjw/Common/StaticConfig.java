package com.bjw.Common;

import com.bjw.bean.LessonTable;
import com.bjw.bean.StudentCard;

import java.util.ArrayList;
import java.util.List;
/*************************************************
 *@date：2017/12/18
 *@author：  zxj
 *@description： 配置一些全局变量
*************************************************/
public  class StaticConfig {
//    public  static String imageUrl="http://www.gvsun.net:22900/ycitlims/";
//    public  static String remoteUrl="https://172.16.1.30:899/ycitlims/";
//    public  static String remoteUrl="https://222.188.2.245:899/ycitlims/";
    public  static String remoteUrl="https://192.168.1.184:899/ycitlims/";
    public  static int FlagOfConnectTimeOut=0;//标志当前的网络读取数据是否获取错误，获取错误的时候为1
    public  static int LabID=921;//实验室的ID
    public  static String TimeToUpdate="01:01:00";//动态更新数据操作的时间
    public static List<LessonTable> lessonTables=new ArrayList<>();//当天实验室的课表
    public static List<StudentCard> studentCards=new ArrayList<>();//当前时间学生卡号信息的集合
    public static List<StudentCard> studentCardsTemp=new ArrayList<>();//当前时间已经刷卡的学生卡号信息的集合
    public static String defaultcourse="当前没有课程";
    public static String defaultcoursenumber="XXXXXXXXXX";//默认课程编号
    public static String defaultteacher="XXX";//默认教师的姓名
    public static int databaseVersion=9;//数据库更新的版本
    public static String databasename="mytest.db";//数据库名称
    public static String IP="";//连接的IP地址
    public static String MacAddress="";//设备Mac地址
    public static String AndroidId="";//设备的AndroidId
    public static String TestVideoPath="http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";//测试视频的地址
    public static String TestVideoPath1="http://221.228.226.23/11/t/j/v/b/tjvbwspwhqdmgouolposcsfafpedmb/sh.yinyuetai.com/691201536EE4912BF7E4F1E2C67B8119.mp4";//测试视频的地址
    public static String TestVideoPath2="http://221.228.226.5/14/z/w/y/y/zwyyobhyqvmwslabxyoaixvyubmekc/sh.yinyuetai.com/4599015ED06F94848EBF877EAAE13886.mp4";//测试视频的地址
    public static String TestVideoPath3="http://221.228.226.5/15/t/s/h/v/tshvhsxwkbjlipfohhamjkraxuknsc/sh.yinyuetai.com/88DC015DB03C829C2126EEBBB5A887CB.mp4";//测试视频的地址
    public static boolean IsShengzhou=false;//做测试使用，判断当前是神州视翰的还是艾博德的班牌，相应的解析不同
    public static boolean IsRealData=false;//做测试使用，判断当前是否是假数据还是真实数据,主要是测试课程这一部分
    public static boolean IsRealDataForDevice=false;//做测试使用，判断当前是否是假数据还是真实数据,主要是设备详情部分的数据
}


package com.bjw.Common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.bjw.Common.StaticConfig.databasename;

/**
 * 创建人：wxdn
 * 创建时间：2017/11/29
 * 功能描述：
 */

public class DatebaseHelper extends SQLiteOpenHelper {
    private Context mContext;

    public DatebaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, databasename, null, version);
    }
    @Override
    //数据库第一次创建时被调用
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE StudentCardTable(student_name VARCHAR(40)," +
                "card_no VARCHAR(40),course_number VARCHAR(40)," +
                "lab_room_id VARCHAR(40),student_number VARCHAR(40),current_time VARCHAR(40)," +
                "start_class int,end_class int)");
        db.execSQL("CREATE TABLE ALLDayStudentsCardTable(student_name VARCHAR(40)," +
                "card_no VARCHAR(40),course_number VARCHAR(40)," +
                "lab_room_id int,student_number VARCHAR(40)," +
                "start_class int,end_class int)");
        db.execSQL("CREATE TABLE LessonTable(lab_room_id int," +
                "course_present_people int,start_class int," +
                "end_class int,total_hour int,course_name VARCHAR(40), " +
                "course_number VARCHAR(40),course_image_url VARCHAR(40)," +
                "course_teacher_name VARCHAR(40),course_beginning_time VARCHAR(40)," +
                "course_ending_time VARCHAR(40),classes_number VARCHAR(40)" +
                ",course_program VARCHAR(40),course_date VARCHAR(40))");
        db.execSQL("CREATE TABLE DeciceVideos(lab_room_id int," +
                "lab_device_id int,lab_device_name VARCHAR(40),lab_device_video_path VARCHAR(40))");
    }
    //软件版本号发生改变时调用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists StudentCardTable");
        db.execSQL("drop table if exists ALLDayStudentsCardTable");
        db.execSQL("drop table if exists LessonTable");
        db.execSQL("drop table if exists DeciceVideos");
        //读过数据的学生卡号
        db.execSQL("CREATE TABLE StudentCardTable(student_name VARCHAR(40)," +
                "card_no VARCHAR(40),course_number VARCHAR(40)," +
                "lab_room_id VARCHAR(40),student_number VARCHAR(40),current_time VARCHAR(40)," +
                "start_class int,end_class int)");
        //学生卡号的表
        db.execSQL("CREATE TABLE ALLDayStudentsCardTable(student_name VARCHAR(40)," +
                "card_no VARCHAR(40),course_number VARCHAR(40)," +
                "lab_room_id int,student_number VARCHAR(40)," +
                "start_class int,end_class int)");
        //课程的数据表
        db.execSQL("CREATE TABLE LessonTable(lab_room_id int," +
                "course_present_people int,start_class int," +
                "end_class int,total_hour int,course_name VARCHAR(40), " +
                "course_number VARCHAR(40),course_image_url VARCHAR(40)," +
                "course_teacher_name VARCHAR(40),course_beginning_time VARCHAR(40)," +
                "course_ending_time VARCHAR(40),classes_number VARCHAR(40)" +
                ",course_program VARCHAR(40),course_date VARCHAR(40))");
        //设备视屏的数据库表
        db.execSQL("CREATE TABLE DeciceVideos(lab_room_id int," +
                "lab_device_id int,lab_device_name VARCHAR(40),lab_device_video_path VARCHAR(40))");
    }
}
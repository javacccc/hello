package com.bjw.Service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.bjw.Common.DatebaseHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import static com.bjw.Common.StaticConfig.LabID;
import static com.bjw.Common.StaticConfig.databaseVersion;
import static com.bjw.Common.StaticConfig.databasename;

/**
 * 创建人：wxdn
 * 创建时间：2017/7/25
 * 功能描述：开启下载的服务
 */

public class DownLoadService extends Service {
    int downLoadFileSize;
    int fileSize;
    List<String> urls=new ArrayList<String>();
    int flag=0;//标记当前是否有进行的下载
    String url1;
    DatebaseHelper myDBHelper;  //本地数据库的帮助者
    SQLiteDatabase db;
    String SDCard;//根目录
    String filename;//文件名称，包含文件扩展名(就是设备名加上相应的括站名)
    int LabDeviceId;//设备的Id
    String LabDeviceName;//设备名称


    Handler handlerForFinish = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {//定义一个Handler，用于处理下载线程与UI间通讯
            Toast.makeText(getBaseContext(), filename+"文件下载完成", Toast.LENGTH_SHORT).show();
            //下载完成之后将数据写入到数据库里面
            ContentValues values = new ContentValues();
            values.put("lab_room_id", LabID);
            values.put("lab_device_id", LabDeviceId);
            values.put("lab_device_name", LabDeviceName);
            values.put("lab_device_video_path",SDCard+"/"+filename);
            db.insert("DeciceVideos", null, values);
            Toast.makeText(getBaseContext(), "下载的文件数据插入成功", Toast.LENGTH_SHORT).show();
            //下载完成之后直接打开本地的文件
//            Intent intent = new Intent();
//            intent.putExtra("BrocastForPlayLocal", SDCard+"/"+filename);
//            intent.setAction("com.BrocastForPlayLocal");
//            sendBroadcast(intent);
        }
    };
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "开始下载", Toast.LENGTH_LONG).show();
        Intent intentfor = new Intent();
        intentfor.putExtra("BrocastForPlayLocal", SDCard+"/"+filename);
        intentfor.setAction("com.BrocastForPlayLocal");
        sendBroadcast(intentfor);
        url1=intent.getStringExtra("url");
        filename=intent.getStringExtra("LabDeviceName");
        LabDeviceName=intent.getStringExtra("LabDeviceName");
        LabDeviceId=intent.getIntExtra("LabDeviceId",Integer.MAX_VALUE);
        urls.add(url1);
        new Thread(runnable).start();
        myDBHelper = new DatebaseHelper(getBaseContext(), databasename, null, databaseVersion);
        db = myDBHelper.getReadableDatabase();
//        db.execSQL("DELETE FROM DeciceVideos");
		return START_STICKY;
	}
    private int DownloadFile(String url) throws IOException {
            /*************************************************
             *@description： 下载文件的执行方法
            *************************************************/
            flag=0;
//            String filename=url.substring(url.lastIndexOf("/") + 1);
            URL myURL = new URL(url);
            URLConnection conn = myURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            fileSize = conn.getContentLength();//根据响应获取文件大小
            if (fileSize <= 0) throw new RuntimeException("无法获知文件大小 ");
            if (is == null) throw new RuntimeException("stream is null");
            SDCard= Environment.getExternalStorageDirectory()+"/班牌视频文件";
            filename=filename+".mp4";
            String pathName=SDCard+"/"+filename;
            File file1 = new File(SDCard);
            File file2 = new File(pathName);
            if(!file1.exists()){
                file1.mkdirs();
            }
            if(!file2.exists()){
                file2.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(pathName);
            //把数据存入路径+文件名
            byte buf[] = new byte[1024];
            downLoadFileSize = 0;
            do{
                //循环读取
                int numread = is.read(buf);
                if (numread == -1)
                {
                    break;
                }
                fos.write(buf, 0, numread);
                downLoadFileSize += numread;
            } while (true);
//          下载完成之后将信息发送给handler让其执行下一个操作
        handlerForFinish.sendEmptyMessage(1);
        try{
                is.close();
            } catch (Exception ex) {
                Log.e("tag", "error: " + ex.getMessage(), ex);
            }
        return downLoadFileSize;
    }
    /*************************************************
     *@description： 下载的线程
    *************************************************/
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                DownloadFile(urls.get(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    @Override
    public void onDestroy() {
        super.onDestroy();
//        Toast.makeText(this, "下载服务的销毁", Toast.LENGTH_LONG).show();
    }
}

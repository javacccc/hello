package com.bjw.Service;

import android.app.Service;
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

/**
 * 创建人：wxdn
 * 创建时间：2017/7/25
 * 功能描述：开启下载的服务
 */

public class UpdateVersionService extends Service {
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
            Toast.makeText(getBaseContext(), "文件下载完成", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.putExtra("path", SDCard+"/"+filename);
            intent.setAction("com.BrocastForUPDATE");
            sendBroadcast(intent);
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setDataAndType(Uri.parse(SDCard+"/"+filename),"application/vnd.android.package-archive");
//            getApplicationContext().startActivity(intent);

        }
    };
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "开始进行版本更新", Toast.LENGTH_LONG).show();
        url1=intent.getStringExtra("url");
        urls.add(url1);
        new Thread(runnable).start();
		return START_STICKY;
	}
    private int DownloadFile(String url) throws IOException {
            /*************************************************
             *@description： 下载文件的执行方法
            *************************************************/
            flag=0;
            URL myURL = new URL(url);
            URLConnection conn = myURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            fileSize = conn.getContentLength();//根据响应获取文件大小
            if (is == null) throw new RuntimeException("stream is null");
            SDCard= Environment.getExternalStorageDirectory()+"/更新的包";
            filename="new.apk";
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

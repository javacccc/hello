package com.bjw.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.bjw.ComAssistant.SerialHelper;
import com.bjw.bean.Serial;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.bjw.Common.StaticConfig.lessonTables;

/*************************************************
 *@date：2017/11/17
 *@author： zxj
 *@description： 串口读写的服务
*************************************************/
public class SerialReadService extends Service {
    SerialControl ComB;
    List<Date> timeforbegins=new ArrayList<>();
    List<Date> timeforends=new ArrayList<>();
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        for(int i=0;i<lessonTables.size();i++)
        {
            try {
                Date dateforbegin=df.parse(lessonTables.get(i).getCourse_beginning_time());
                Date dateforend=df.parse(lessonTables.get(i).getCourse_ending_time());
                timeforbegins.add(dateforbegin);
                timeforends.add(dateforend);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        ComB = new SerialControl();
        OpenComPort(ComB);
		return START_STICKY;
	}
    /*************************************************
     *@description： 串口控制类
     *************************************************/
    private class SerialControl extends SerialHelper {
        public SerialControl() {}
        protected void onDataReceived(final Serial ComRecData) {
            DispRecData(ComRecData);
        }
    }
    /*************************************************
     *@description：打开串口
     *************************************************/
    public void OpenComPort(SerialHelper ComPort) {
        try {
            ComPort.open();
        } catch (SecurityException e) {
            ShowMessage("打开串口失败:没有串口读/写权限!");
        } catch (IOException e) {
            ShowMessage("打开串口失败:未知错误!");
        } catch (InvalidParameterException e) {
            ShowMessage("打开串口失败:参数错误!");
        }
    }
    private void DispRecData(Serial ComRecData) {
/*************************************************
 *@description： 艾博德读卡
*************************************************/
        if (ComRecData.flag == 8) {
                String cardnum = ComRecData.totalnum.substring(4, 12);
                Intent intent = new Intent();
                intent.putExtra("cardnum", cardnum);
                intent.setAction("com.getCardNum");
                sendBroadcast(intent);
        }
        /*************************************************
         *@description： 神州视翰的读卡
        *************************************************/
        if (ComRecData.flag == 520) {
            String cardnum = ComRecData.totalnum.substring(10, 18);
            Intent intent = new Intent();
            intent.putExtra("cardnum", cardnum);
            intent.setAction("com.getCardNum");
            sendBroadcast(intent);
        }
    }
    /*************************************************
     *@description： 显示消息
     *************************************************/
    private void ShowMessage(String sMsg) {
        Toast.makeText(this, sMsg, Toast.LENGTH_SHORT).show();
    }
    /*************************************************
     *@description： 关闭串口
     *************************************************/
    private void CloseComPort(SerialHelper ComPort) {
        ComPort.close();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        CloseComPort(ComB);
    }
}

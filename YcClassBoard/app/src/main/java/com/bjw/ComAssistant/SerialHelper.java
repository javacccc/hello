package com.bjw.ComAssistant;

import com.bjw.bean.Serial;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android_serialport_api.SerialPort;

import static com.bjw.Common.StaticConfig.IsShengzhou;

/*************************************************
 *@date：2017/10/31
 *@author：  zxj
 *@description： 串口辅助类
*************************************************/
public abstract class SerialHelper{
	private SerialPort mSerialPort;
	private OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;
	private String sPort="/dev/ttyS4";
	private int iBaudRate=9600;
	private  int FLAG1=0;
	private List<String> listforcard=new ArrayList<>();
	private String list="";
	List<Byte> bufferfortest=new ArrayList<>();
	public SerialHelper(String sPort,int iBaudRate){
		this.sPort = sPort;
		this.iBaudRate=iBaudRate;
	}
	public SerialHelper(){
		this("/dev/ttyS4",9600);
	}
	/*************************************************
	 *@description： 打开串口
	*************************************************/
	public void open() throws IOException {
		mSerialPort =  new SerialPort(new File(sPort), iBaudRate, 0);
		mOutputStream = mSerialPort.getOutputStream();
		mInputStream = mSerialPort.getInputStream();
		mReadThread = new ReadThread();
		mReadThread.start();
	}
    /*************************************************
     *@description： 关闭串口
    *************************************************/
	public void close() {
		if (mReadThread != null){
		mReadThread.interrupt();
		}
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
		listforcard.clear();
	}
    /*************************************************
     *@description： 串口读取线程
    *************************************************/
	private class ReadThread extends Thread {
		@Override
		public void run() {
			super.run();
			while(!isInterrupted()) {
				try
				{
					if (mInputStream == null)
						{
					return;
				}
				    byte[] buffer=new byte[64];
					int size = mInputStream.read(buffer);
					/*************************************************
					 *@description： 神州视翰的数据处理
					 *************************************************/
					if(IsShengzhou) {
						for (int j = 0; j < size; j++) {
							bufferfortest.add(buffer[j]);
						}
						if (bufferfortest.size() == 13) {
							byte[] b = new byte[1024];
							for (int i = 0; i < bufferfortest.size(); i++) {
								b[i] = bufferfortest.get(i);
							}
							String cardNum = MyFunc.ByteArrToHex(b);
							Serial ComRecData = new Serial(cardNum, 520);
							onDataReceived(ComRecData);
							bufferfortest.clear();
						}
					}
					/*************************************************
					 *@description： 艾博德的读卡协议版本
					*************************************************/
					else {
						for (int i = 0; i < size; i++) {
							int length = listforcard.size();
							if (length <= 0) {
								String dataLength = MyFunc.Byte2Hex(buffer[i]);
								if (dataLength.equals("09")) {
									listforcard.add(dataLength);
								} else {
									listforcard.clear();
								}
							} else if (length < 6 && length > 0) {
								String dataandtype = MyFunc.Byte2Hex(buffer[i]);
								listforcard.add(dataandtype);
							} else if (length == 6) {
								String jiaoyanwei = MyFunc.Byte2Hex(buffer[i]);
								listforcard.add(jiaoyanwei);
							} else if (length == 7) {
								String END = MyFunc.Byte2Hex(buffer[i]);
								if (END.equals("03")) {
									listforcard.add(END);
								} else {
									listforcard.clear();
								}
							} else {
								listforcard.clear();
							}
							if (listforcard.size() == 8 && MyFunc.Byte2Hex(buffer[size - 1]).equals("03")) {
								FLAG1 = 8;
								list = "";
								for (int j = 0; j < listforcard.size(); j++) {
									list = list + listforcard.get(j);
								}
								listforcard.clear();
								Serial ComRecData = new Serial(list, FLAG1);
								onDataReceived(ComRecData);
							}
						}
					}
				} catch (Throwable e){
					e.printStackTrace();
					return;
				}
			}
		}
	}
	protected abstract void onDataReceived(Serial ComRecData);
}
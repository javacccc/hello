/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package android_serialport_api;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

public class SerialPort {
	private static final String TAG = "SerialPort";
	private FileDescriptor mFd; //文件描述，
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;

	/**
	 * Android串口通信工具源码，对字节数据传输有封装，提供动态配置串口地址，波特率，定时发送数据，文本转16进制，16进制转文本等功能
     *获得一个窗口 
     * @param device 设备
     * @param baudrate 波特率
     * @param flags 标志
     * @throws SecurityException
     * @throws IOException
     */
	public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {
		Log.i(TAG, "----SerialPort--file="+device.getName()+"--baudrate="+baudrate+"--flags="+flags);
		/* 获取权限*/
		if (!device.canRead() || !device.canWrite()) {
			try {
				/* Missing read/write permission, trying to chmod the file */
				Process su = Runtime.getRuntime().exec("/system/xbin/su");
				String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"+ "exit\n";
				su.getOutputStream().write(cmd.getBytes());
				if ((su.waitFor() != 0) || !device.canRead() || !device.canWrite()) {
					throw new SecurityException();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new SecurityException();
			}
		}else{
			Log.i(TAG, "---!device.canRead() || !device.canWrite()");
		}
		mFd = open(device.getAbsolutePath(), baudrate, flags);
		if (mFd == null) {
			Log.e(TAG, "----返回null---native static FileDescriptor open(String path, int baudrate, int flags)");
			throw new IOException();
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
	}

	// Getters and setters
	public InputStream getInputStream() {
		return mFileInputStream;
	}

	public OutputStream getOutputStream() {
		return mFileOutputStream;
	}

	// JNI
    static {
        System.loadLibrary("serial_port");
    }
	private native static FileDescriptor open(String path, int baudrate, int flags);
	public native void close();

}

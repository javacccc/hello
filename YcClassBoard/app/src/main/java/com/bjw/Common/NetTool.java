package com.bjw.Common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class NetTool {
	private static final int TIMEOUT = 10000;
/*************************************************
 *@description：文件上传
*************************************************/
	public static String sendFile(String urlPath, String filePath,
			String newName) throws Exception {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		URL url = new URL(urlPath);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false);
		con.setRequestMethod("POST");
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Charset", "UTF-8");
		con.setRequestProperty("Content-Type", "multipart/form-data;boundary="
				+ boundary);
		DataOutputStream ds = new DataOutputStream(con.getOutputStream());
		ds.writeBytes(twoHyphens + boundary + end);
		ds.writeBytes("Content-Disposition: form-data; "
				+ "name=\"file1\";filename=\"" + newName + "\"" + end);
		ds.writeBytes(end);
		FileInputStream fStream = new FileInputStream(filePath);
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];
		int length = -1;
		while ((length = fStream.read(buffer)) != -1) {
			ds.write(buffer, 0, length);
		}
		ds.writeBytes(end);
		ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
		fStream.close();
		ds.flush();
		InputStream is = con.getInputStream();
		int ch;
		StringBuffer b = new StringBuffer();
		while ((ch = is.read()) != -1) {
			b.append((char) ch);
		}
		ds.close();
		return b.toString();
	}
	/*************************************************
	 *@description： 文件下载
	*************************************************/
	public static void DownLoadFile(String urlString)
	{
//		String urlStr="http://172.17.54.91:8080/download/1.mp3";
		String path="file";
		String fileName="2.mp3";
		OutputStream output=null;
		try {
			URL url=new URL(urlString);
			HttpURLConnection conn=(HttpURLConnection)url.openConnection();
			//取得inputStream，并将流中的信息写入SDCard
			String SDCard= Environment.getExternalStorageDirectory()+"";
			String pathName=SDCard+"/"+path+"/"+fileName;//文件存储路径
			File file=new File(pathName);
			InputStream input=conn.getInputStream();
			if(file.exists()){
				System.out.println("exits");
				return;
			}else{
				String dir=SDCard+"/"+path;
				new File(dir).mkdir();//新建文件夹
				file.createNewFile();//新建文件
				output=new FileOutputStream(file);
				//读取大文件
				byte[] buffer=new byte[4*1024];
				while(input.read(buffer)!=-1){
					output.write(buffer);
				}
				output.flush();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				output.close();
				System.out.println("success");
			} catch (IOException e) {
				System.out.println("fail");
				e.printStackTrace();
			}
		}
	}
	/*************************************************
	 *@description： 图片预览
	*************************************************/
    public static Bitmap returnBitMap(String url){
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}


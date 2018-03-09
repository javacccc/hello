package com.bjw.Common;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static com.bjw.Common.StaticConfig.FlagOfConnectTimeOut;

/**
 * Created by asus on 2017/4/21.
 */

public class Connection {
    public static void getConnection(final URL url, final Handler handler) {
        //创建线程，连接服务器
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection conn=null;
                    trustAllHosts();
                    HttpsURLConnection https = (HttpsURLConnection)url.openConnection();
                    https.setHostnameVerifier(DO_NOT_VERIFY);
                    conn=https;
                    conn.setRequestMethod("GET");//声明请求方式默认get
                    conn.setConnectTimeout(10000);
                    int code = conn.getResponseCode();
                    //判断连接是否成功
                    if (code == 200) {
                        InputStream in = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }//通过in得到网页源代码存入response中
                        Message msg = Message.obtain();//减少消息创建的数量
                        msg.obj = response;
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    FlagOfConnectTimeOut=1;
                    e.printStackTrace();
                }
            }
        }).start();
    }
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
    private static void trustAllHosts() {
        final String TAG = "trustAllHosts";
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                Log.i(TAG, "checkClientTrusted");
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                Log.i(TAG, "checkServerTrusted");
            }
        }};
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

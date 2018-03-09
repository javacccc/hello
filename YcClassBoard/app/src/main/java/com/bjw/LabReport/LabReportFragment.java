package com.bjw.LabReport;

/**
 * 创建人：wxdn
 * 创建时间：2018/02/05
 * 功能描述：
 */
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bjw.R;
import com.bjw.bean.LabReport;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LabReportFragment extends Fragment {
    LineChart mLineChart;
    /*************************************************
     *@description： 处理获取到的数据进行相应的数据更新
    *************************************************/
    public Handler handlerForlabReport=new Handler() {
        public void handleMessage(Message msg) {
            String response = msg.obj.toString();
            String labReports=null;
            try {
                JSONObject jsonObject = new JSONObject(response.toString());
                labReports = jsonObject.getString("result");
            } catch (JSONException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
            if(labReports!=null) {
                Gson gson = new Gson();
                JsonParser parser = new JsonParser();
                List<LabReport> labReportList =new ArrayList<>();
                JsonArray Jarray = parser.parse(labReports).getAsJsonArray();
                LabReport labReport = null;
                for (JsonElement obj : Jarray) {
                    labReport = gson.fromJson(obj, LabReport.class);
                    labReportList.add(labReport);
                }
                ChartUtils.initChart(mLineChart);
                List<List<Entry>> listArrayList = new ArrayList<>();
                List<Entry> values = new ArrayList<>();
                for(int i = 0; i< labReportList.size(); i++)
                {
                    values.add(new Entry(i, labReportList.get(i).getWeekStudentNum()));
                }
                listArrayList.add(values);
                ChartUtils.notifyDataSetChanged(mLineChart, listArrayList, ChartUtils.dayValue);
            }else{
                Toast.makeText(getContext(), "当前没有数据", Toast.LENGTH_SHORT).show();
            }
        }
    };
    /*************************************************
     *@description： 假数据做测试使用
    *************************************************/
    public Handler handler=new Handler() {
        public void handleMessage(Message msg) {
            List<LabReport> labReportList =new ArrayList<>();
            LabReport labReport1 =new LabReport("第一周",15,921);
            LabReport labReport2 =new LabReport("第二周",16,921);
            LabReport labReport3 =new LabReport("第三周",17,921);
            LabReport labReport4 =new LabReport("第四周",20,921);
            LabReport labReport5 =new LabReport("第五周",35,921);
            LabReport labReport6 =new LabReport("第六周",18,921);
            LabReport labReport7 =new LabReport("第七周",70,921);
            LabReport labReport8 =new LabReport("第八周",10,921);
            LabReport labReport9 =new LabReport("第九周",50,921);
            LabReport labReport10 =new LabReport("第十周",15,921);
            LabReport labReport11 =new LabReport("第十一周",45,921);
            labReportList.add(labReport1);
            labReportList.add(labReport2);
            labReportList.add(labReport3);
            labReportList.add(labReport4);
            labReportList.add(labReport5);
            labReportList.add(labReport6);
            labReportList.add(labReport7);
            labReportList.add(labReport8);
            labReportList.add(labReport9);
            labReportList.add(labReport10);
            labReportList.add(labReport11);
            ChartUtils.initChart(mLineChart);
            List<List<Entry>> listArrayList = new ArrayList<>();
            List<Entry> values = new ArrayList<>();
            List<Entry> values1 = new ArrayList<>();
            int totalnumber=0;
            for(int i = 0; i< labReportList.size(); i++)
            {
                values.add(new Entry(i, labReportList.get(i).getWeekStudentNum()));
                totalnumber=totalnumber+ labReportList.get(i).getWeekStudentNum();
            }
            listArrayList.add(values);
            int averageNum=totalnumber/ labReportList.size();
            for(int j = 0; j< labReportList.size(); j++)
            {
                values1.add(new Entry(j,averageNum));
            }
            listArrayList.add(values1);
            ChartUtils.notifyDataSetChanged(mLineChart, listArrayList, ChartUtils.dayValue);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View chatView = inflater.inflate(R.layout.fragment_labreport, container,false);
        return chatView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }
    public void onStart()
    {
        super.onStart();
        test test1=new test();
        test1.start();
        findViewId();
        /*************************************************
         *@description： 去服务器端获取数据
        *************************************************/
//        try {
//            URL url = new URL(remoteUrl + "labRoomInfo/LabReportFragment/" + LabID);
//            getConnection(url, handlerForlabReport);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
    }
    private void findViewId()
    {
        mLineChart = (LineChart) getActivity().findViewById(R.id.chart);
    }
    private class test extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(1);
                /*************************************************
                 *@description： 当前时间是周一的话，去服务器端获取相应的数据
                *************************************************/
//                try {
//                    URL url = new URL(remoteUrl + "labRoomInfo/LabReportFragment/" + LabID);
//                    getConnection(url, handlerForlabReport);
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }
}

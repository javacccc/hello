package com.bjw.DynamicInfo;

/*************************************************
 *@date：2017/11/17
 *@author： zxj
 *@description： 通知的碎片
*************************************************/

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bjw.R;
import com.bjw.bean.LabMessage;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bjw.Common.Connection.getConnection;
import static com.bjw.Common.StaticConfig.LabID;
import static com.bjw.Common.StaticConfig.remoteUrl;

public class MessageFragment extends Fragment {
    ListView listView;
    public static List <LabMessage>labMessageList=new ArrayList<>();
    BroadcastReceiverToReFreshMes broadcastReceiverToReFreshMes;
    /*************************************************
     *@description： 获取消息数据
    *************************************************/
    public Handler handlerForGetLabMesInfo = new Handler() {
        public void handleMessage(Message msg) {
                String response = msg.obj.toString();
                String labMessages = null;
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    labMessages = jsonObject.getString("result");
                } catch (JSONException e) {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                }
                if (labMessages != null) {
                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();
                    JsonArray Jarray = parser.parse(labMessages).getAsJsonArray();
                    LabMessage labMessage = null;
                    for (JsonElement obj : Jarray) {
                        labMessage = gson.fromJson(obj, LabMessage.class);
                        labMessageList.add(labMessage);
                    }
                    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                    for (int i = 0; i < labMessageList.size(); i++) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("siple_tv1", "通知" + (i + 1) + "：");
                        map.put("siple_tv2", labMessageList.get(i).getTitle());
                        map.put("siple_tv3", labMessageList.get(i).getSubmit_time());
                        list.add(map);
                    }
                    String[] from = {"siple_tv1", "siple_tv2","siple_tv3"};
                    int[] to = {R.id.messagename, R.id.messagecontent,R.id.messagetime};
                    SimpleAdapter simpleAdapter1 = new SimpleAdapter(getActivity(), list, R.layout.item_right_message_list, from, to);
                    listView.setAdapter(simpleAdapter1);
                } else {
                    Toast.makeText(getContext(), "当前无该实验室的信息", Toast.LENGTH_SHORT).show();
                }
        }
    };
    /*************************************************
     *@description： 动态更新获取消息数据（由刷新的服务里面的传入）
     *************************************************/
    public Handler handlerForReFreshLabMes = new Handler() {
        public void handleMessage(Message msg) {
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                for (int i = 0; i < labMessageList.size(); i++) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("siple_tv1", "通知" + (i + 1) + "：");
                    map.put("siple_tv2", labMessageList.get(i).getTitle());
                    map.put("siple_tv3", labMessageList.get(i).getSubmit_time());
                    list.add(map);
                }
                String[] from = {"siple_tv1", "siple_tv2","siple_tv3"};
                int[] to = {R.id.messagename, R.id.messagecontent,R.id.messagetime};
                SimpleAdapter simpleAdapter1 = new SimpleAdapter(getActivity(), list, R.layout.item_right_message_list, from, to);
                listView.setAdapter(simpleAdapter1);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View chatView = inflater.inflate(R.layout.fragment_dynamic_message, container,false);
        return chatView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }
    public void onStart() {
        super.onStart();
        findById();
        //注册动态更新数据的广播
        registerForRefresh();
        //第一次进入，获取数据
        try {
            labMessageList.clear();
            URL url = new URL(remoteUrl + "labRoomInfo/listNotice/" + LabID);
            getConnection(url, handlerForGetLabMesInfo);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            }
        //点击时间的监听
        listView.setOnItemClickListener(LvListener);
    }
    /*************************************************
     *@description： 数据绑定
    *************************************************/
    public void findById()
    {
        listView=(ListView)getActivity().findViewById(R.id.listviewformessage);
    }
    /*************************************************
     *@description： ListView的监听器
    *************************************************/
    AdapterView.OnItemClickListener LvListener=(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            final View layout = inflater.inflate(R.layout.dialog_messagedetails, null);//获取自定义布局
            builder.setView(layout);
            builder.setTitle(labMessageList.get(i).getTitle());//设置标题内容
            TextView TvMescontent = (TextView) layout.findViewById(R.id.mescontent);
            TextView TvSubmitter = (TextView) layout.findViewById(R.id.submitter);
            TextView TvTime = (TextView) layout.findViewById(R.id.time);
            TvMescontent.setText("\n\t\t"+labMessageList.get(i).getContent()+"\n");
            if(labMessageList.get(i).getSubmit_by()!=null) {
                TvSubmitter.setText("发布人:"+labMessageList.get(i).getSubmit_by());
            }
            TvTime.setText("发布时间:"+labMessageList.get(i).getSubmit_time());
            builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            final AlertDialog dia = builder.create();
            dia.show();
        }

    });

    /*************************************************
     *@description： 注册更新消息列表的广播
     *************************************************/
    public void registerForRefresh()
    {
        broadcastReceiverToReFreshMes=new BroadcastReceiverToReFreshMes();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(7);
        filter.addAction("com.RefreshForMes");
        getActivity().registerReceiver(broadcastReceiverToReFreshMes, filter);
    }
    /*************************************************
     *@description： 更新的广播接收器
     *************************************************/
    public class BroadcastReceiverToReFreshMes extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            handlerForReFreshLabMes.sendEmptyMessage(1);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

package com.bjw.DynamicInfo;

/*************************************************
 *@date：2017/11/17
 *@author： zxj
 *@description： 课表的碎片
 *************************************************/

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bjw.Adapter.LessonTableListAdapter;
import com.bjw.R;

import java.util.ArrayList;

import static com.bjw.Common.StaticConfig.lessonTables;

public class TableFragment extends Fragment {
    private ListView listView;
    private ArrayList<String> list1;
    ArrayList<ArrayList<String>> lists = new ArrayList<ArrayList<String>>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View chatView = inflater.inflate(R.layout.fragment_dynamic_table, container, false);
        return chatView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        findViewId();
        if (lists.size() == 0) {
            ArrayList<String> list = new ArrayList<String>();
            list.add("课程");
            list.add("教师");
            list.add("上课时间");
            list.add("上课班级");
            list.add("实验项目");
            list.add("应到人数");
            //上述是第一行
            lists.add(list);
            for (int i = 0; i < lessonTables.size(); i++) {
//                这就是相应的数据
                list1 = new ArrayList<>();
                list1.add(lessonTables.get(i).getCourse_name());
                list1.add(lessonTables.get(i).getCourse_teacher_name());
                list1.add(lessonTables.get(i).getCourse_beginning_time() + "-" + lessonTables.get(i).getCourse_ending_time());
                list1.add(lessonTables.get(i).getClasses_number());
                list1.add(lessonTables.get(i).getCourse_program());
                list1.add("" + lessonTables.get(i).getCourse_present_people());
                lists.add(list1);
            }
        }
        LessonTableListAdapter adapter = new LessonTableListAdapter(getActivity(), lists);
        listView.setAdapter(adapter);
    }

    /*************************************************
     *@description： 控件绑定
     *************************************************/
    private void findViewId() {
        listView = (ListView) getActivity().findViewById(R.id.listview);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

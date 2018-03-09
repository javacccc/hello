package com.bjw.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

/**
 * 创建人：wxdn
 * 创建时间：2017/11/17
 * 功能描述：
 */

public class LessonBelowAdapter extends SimpleAdapter {

    public LessonBelowAdapter(Context context, List<Map<String, Object>> items, int resource, String[] from, int[] to) {
        super(context, items, resource, from, to);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        return view;
    }
}

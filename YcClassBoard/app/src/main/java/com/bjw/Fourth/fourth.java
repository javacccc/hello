package com.bjw.Fourth;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bjw.R;

/**
 * 创建人：wxdn
 * 创建时间：2017/11/2
 * 功能描述：
 */

public class fourth extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View chatView = inflater.inflate(R.layout.fragment_fourth, container,false);
        return chatView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }
    public void onStart()
    {
        super.onStart();
        findViewId();
    }
    private void findViewId()
    {
    }
}
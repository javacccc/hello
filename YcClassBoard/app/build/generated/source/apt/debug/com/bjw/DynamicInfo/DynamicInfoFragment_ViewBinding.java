// Generated code from Butter Knife. Do not modify!
package com.bjw.DynamicInfo;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.bjw.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class DynamicInfoFragment_ViewBinding implements Unbinder {
  private DynamicInfoFragment target;

  @UiThread
  public DynamicInfoFragment_ViewBinding(DynamicInfoFragment target, View source) {
    this.target = target;

    target.img = Utils.findRequiredViewAsType(source, R.id.mainimg, "field 'img'", ImageView.class);
    target.underimg1 = Utils.findRequiredViewAsType(source, R.id.under_img1, "field 'underimg1'", ImageView.class);
    target.underimg2 = Utils.findRequiredViewAsType(source, R.id.under_img2, "field 'underimg2'", ImageView.class);
    target.underimg3 = Utils.findRequiredViewAsType(source, R.id.under_img3, "field 'underimg3'", ImageView.class);
    target.lessonTv = Utils.findRequiredViewAsType(source, R.id.lesson, "field 'lessonTv'", TextView.class);
    target.tableTv = Utils.findRequiredViewAsType(source, R.id.table, "field 'tableTv'", TextView.class);
    target.messageTv = Utils.findRequiredViewAsType(source, R.id.message, "field 'messageTv'", TextView.class);
    target.datetime = Utils.findRequiredViewAsType(source, R.id.datetime, "field 'datetime'", TextView.class);
    target.weathertime = Utils.findRequiredViewAsType(source, R.id.weathertime, "field 'weathertime'", TextView.class);
    target.labname = Utils.findRequiredViewAsType(source, R.id.labname, "field 'labname'", TextView.class);
    target.lab_introduct = Utils.findRequiredViewAsType(source, R.id.lab_introduct, "field 'lab_introduct'", TextView.class);
    target.rightmPageVp = Utils.findRequiredViewAsType(source, R.id.change_right_tab, "field 'rightmPageVp'", ViewPager.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    DynamicInfoFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.img = null;
    target.underimg1 = null;
    target.underimg2 = null;
    target.underimg3 = null;
    target.lessonTv = null;
    target.tableTv = null;
    target.messageTv = null;
    target.datetime = null;
    target.weathertime = null;
    target.labname = null;
    target.lab_introduct = null;
    target.rightmPageVp = null;
  }
}

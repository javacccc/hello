// Generated code from Butter Knife. Do not modify!
package com.bjw.MainFragment;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.GridView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.bjw.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ChooseLabActivity_ViewBinding implements Unbinder {
  private ChooseLabActivity target;

  @UiThread
  public ChooseLabActivity_ViewBinding(ChooseLabActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ChooseLabActivity_ViewBinding(ChooseLabActivity target, View source) {
    this.target = target;

    target.gview = Utils.findRequiredViewAsType(source, R.id.gview, "field 'gview'", GridView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ChooseLabActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.gview = null;
  }
}

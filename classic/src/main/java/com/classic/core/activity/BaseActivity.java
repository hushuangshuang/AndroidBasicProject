package com.classic.core.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import butterknife.ButterKnife;
import com.classic.core.event.EventUtil;
import com.classic.core.fragment.BaseFragment;
import com.classic.core.interfaces.I_Activity;
import com.classic.core.interfaces.I_Register;
import com.classic.core.utils.KeyBoardUtil;
import com.classic.core.utils.SharedPreferencesUtil;

/**
 * 父类Activitiy
 * @author 续写经典
 * @date 2015/11/7
 */
public abstract class BaseActivity extends AppCompatActivity
  implements View.OnClickListener,I_Activity,I_Register{

  /**
   * Activity状态
   */
  public int activityState = DESTROY;
  protected BaseFragment currentFragment;
  protected Activity activity;
  private SharedPreferencesUtil spUtil;
  public static final String SP_NAME = "firstConfig";

  protected abstract boolean configButterKnife();
  protected abstract boolean configEventBus();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activity = this;
    initPre();
    BaseActivityStack.getInstance().addActivity(this);
    setContentView(setLayoutResId());
    if(configButterKnife()){
      ButterKnife.bind(this);
    }
    if(configEventBus()){
      EventUtil.registerEventBus(this);
    }
    spUtil = new SharedPreferencesUtil(this,SP_NAME);
    final String simpleName = this.getClass().getSimpleName();
    if(spUtil.getBooleanValue(simpleName, true)){
      onFirst();
      spUtil.putBooleanValue(simpleName, false);
    }
    initData();
    initToolbar();
    initView();
    register();
  }

  @Override public void initPre() { }
  @Override public void onFirst(){ }
  @Override public void initData() { }
  @Override public void initToolbar() { }
  @Override public void initView() { }
  @Override public void viewClick(View v) { }
  @Override public void showProgress() { }
  @Override public void hideProgress() { }
  @Override public void register() { }
  @Override public void unRegister() { }
  @Override public void onClick(View v) {
    viewClick(v);
  }

  @Override public void skipActivity(Activity aty, Class<?> cls) {
    startActivity(aty, cls);
    aty.finish();
  }

  @Override public void skipActivity(Activity aty, Intent it) {
    startActivity(aty, it);
    aty.finish();
  }

  @Override public void skipActivity(Activity aty, Class<?> cls, Bundle extras) {
    startActivity(aty, cls, extras);
    aty.finish();
  }

  @Override public void startActivity(Activity aty, Class<?> cls) {
    Intent intent = new Intent();
    intent.setClass(aty, cls);
    aty.startActivity(intent);
  }

  @Override public void startActivity(Activity aty, Intent it) {
    aty.startActivity(it);
  }

  @Override public void startActivity(Activity aty, Class<?> cls, Bundle extras) {
    Intent intent = new Intent();
    intent.putExtras(extras);
    intent.setClass(aty, cls);
    aty.startActivity(intent);
  }

  /**
   * 用Fragment替换视图
   *
   * @param resView        将要被替换掉的视图
   * @param targetFragment 用来替换的Fragment
   */
  public void changeFragment(int resView, BaseFragment targetFragment) {
    if (targetFragment.equals(currentFragment)) {
      return;
    }
    android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager()
        .beginTransaction();
    if (!targetFragment.isAdded()) {
      transaction.add(resView, targetFragment, targetFragment.getClass()
          .getName());
    }
    if (targetFragment.isHidden()) {
      transaction.show(targetFragment);
      targetFragment.onChange();
    }
    if (currentFragment != null
        && currentFragment.isVisible()) {
      transaction.hide(currentFragment);
    }
    currentFragment = targetFragment;
    transaction.commit();
  }

  @Override protected void onResume() {
    super.onResume();
    activityState = RESUME;
  }

  @Override protected void onPause() {
    super.onPause();
    activityState = PAUSE;
  }

  @Override protected void onStop() {
    super.onStop();
    activityState = STOP;
  }

  @Override protected void onDestroy() {
    unRegister();
    super.onDestroy();
    activityState = DESTROY;
    BaseActivityStack.getInstance().finishActivity(this);
    if(configButterKnife()){
      ButterKnife.unbind(this);
    }
    if(configEventBus()){
      EventUtil.unRegisterEventBus(this);
    }
  }

  @Override public void finish() {
    KeyBoardUtil.hide(getWindow().getDecorView());
    super.finish();
  }

}

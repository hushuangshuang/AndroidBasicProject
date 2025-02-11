package com.classic.core.activity;

import android.app.Activity;
import android.content.Context;
import com.classic.core.interfaces.I_Activity;
import java.util.Stack;

/**
 * Activity栈管理
 * @author 续写经典
 * @date 2015/11/4
 */
public final class BaseActivityStack {
  private static Stack<I_Activity> activityStack;
  private static final BaseActivityStack instance = new BaseActivityStack();
  private BaseActivityStack(){}
  public static BaseActivityStack getInstance(){ return instance; }

  /**
   * 获取当前Activity栈中元素个数
   */
  public int getCount() {
    return activityStack.size();
  }

  /**
   * 添加Activity到栈
   */
  public void addActivity(I_Activity activity) {
    if (activityStack == null) {
      activityStack = new Stack<I_Activity>();
    }
    activityStack.add(activity);
  }

  /**
   * 获取当前Activity（栈顶Activity）
   */
  public Activity topActivity() {
    if (activityStack == null) {
      throw new NullPointerException("Activity stack is Null");
    }
    if (activityStack.isEmpty()) {
      return null;
    }
    I_Activity activity = activityStack.lastElement();
    return (Activity) activity;
  }

  /**
   * 获取当前Activity（栈顶Activity） 没有找到则返回null
   */
  public Activity findActivity(Class<?> cls) {
    I_Activity activity = null;
    for (I_Activity aty : activityStack) {
      if (aty.getClass().equals(cls)) {
        activity = aty;
        break;
      }
    }
    return null == activity ? null : (Activity) activity;
  }

  /**
   * 结束当前Activity（栈顶Activity）
   */
  public void finishActivity() {
    I_Activity activity = activityStack.lastElement();
    finishActivity((Activity) activity);
  }

  /**
   * 结束指定的Activity(重载)
   */
  public void finishActivity(Activity activity) {
    if (activity != null) {
      activityStack.remove(activity);
      // activity.finish();//此处不用finish
      activity = null;
    }
  }

  /**
   * 结束指定的Activity(重载)
   */
  public void finishActivity(Class<?> cls) {
    for (I_Activity activity : activityStack) {
      if (activity.getClass().equals(cls)) {
        finishActivity((Activity) activity);
      }
    }
  }

  /**
   * 关闭除了指定activity以外的全部activity 如果cls不存在于栈中，则栈全部清空
   *
   * @param cls
   */
  public void finishOthersActivity(Class<?> cls) {
    for (I_Activity activity : activityStack) {
      if (!(activity.getClass().equals(cls))) {
        finishActivity((Activity) activity);
      }
    }
  }

  /**
   * 结束所有Activity
   */
  public void finishAllActivity() {
    for (int i = 0, size = activityStack.size(); i < size; i++) {
      if (null != activityStack.get(i)) {
        ((Activity) activityStack.get(i)).finish();
      }
    }
    activityStack.clear();
  }

  /**
   * 应用程序退出
   *
   */
  public void appExit(Context context) {
    try {
      finishAllActivity();
      Runtime.getRuntime().exit(0);
    } catch (Exception e) {
      Runtime.getRuntime().exit(-1);
    }
  }
}

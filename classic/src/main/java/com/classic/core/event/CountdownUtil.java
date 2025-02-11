package com.classic.core.event;

import java.util.Timer;
import java.util.TimerTask;
import org.simple.eventbus.EventBus;

/**
 * 基于Eventbus的简单倒计时工具类
 * @author 续写经典
 * @date 2015/11/4
 */
public class CountdownUtil
{
	private boolean isRun;
	private int totalTime;
	private String countdownTag;

	/**
	 * 启动倒计时广播
	 * @param time 倒计时长（单位：秒）
	 * @param countdownTag 倒计时事件标示
	 */
	public void start(int time,String countdownTag){
		if(isRun) return;
		this.countdownTag = countdownTag;
		totalTime = time;
		if(null==timer) timer = new Timer();
		isRun = true;
		timer.schedule(task, 100, 1000); // 0.1s后执行task,经过1s再次执行
	}
	private Timer timer;
	private final TimerTask task = new TimerTask() {

        @Override
        public void run() {
					totalTime--;
        	EventBus.getDefault().post(totalTime, countdownTag);
        	if(totalTime==0){
        		timer.cancel();
						isRun = false;
        	}
        }
    };
}

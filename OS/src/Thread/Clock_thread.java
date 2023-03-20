package Thread;

import java.util.concurrent.locks.LockSupport;

import View.MainForm;

//系统时钟线程类
public class Clock_thread extends Thread{
	
	public static int COUNTTIME=0;//系统时间
	public static Boolean STOP=false;//暂停标志
	
	public Clock_thread(int p) {
		setPriority(p);
	}
	
	//重写run方法
	public void run() {
		while(true) {
			try {
				if(STOP)
					Thread.sleep(1);
				else {
					Thread.sleep(MainForm.inputValue);
					COUNTTIME++;
					if(Clock_thread.COUNTTIME%10==0)
						LockSupport.unpark(MainForm.jobIn);//唤醒作业请求线程
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

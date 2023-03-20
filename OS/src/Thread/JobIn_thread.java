package Thread;

import java.util.concurrent.locks.LockSupport;

import Register.JCB;
import Register.JOB;
import Register.Process;

//作业请求判断线程类
public class JobIn_thread extends Thread{
		
	public JobIn_thread(int p) {
		setPriority(p);
	}
	
	//重写run方法
	public void run() {
		while(true) {
			LockSupport.park();//等待唤醒
			synchronized(Process.Lock) {
				CheckJob();//每10秒执行一次作业请求判断函数
			}
		}
	}
	
	//作业请求判断函数
	public void CheckJob() {
		for(JCB jcb:JOB.jobList_NoRequest) {
			if(jcb.InTimes<=Clock_thread.COUNTTIME && !jcb.IntoJobList) {
				Process.JobIntoRequestedList(jcb);//有作业到达时将作业加入到作业后备队列并判断是否可以为其创建新进程
			}
		}
	}
}

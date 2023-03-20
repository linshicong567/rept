package Thread;

import Register.Process;
import View.MainForm;
import View.Show;
import Register.CPU2023;

//外设输入中断线程类
public class InputBlock_thread extends Thread{
	
	private int CLOCK=0;//与系统时钟同步的时间
	
	public InputBlock_thread(int p) {
		setPriority(p);
	}
	
	//重写run方法
	public void run() {
		while(true) {
			try {
				if(Clock_thread.STOP)
					Thread.sleep(1);
				else if((Clock_thread.COUNTTIME-CLOCK)==1){
					synchronized(Process.Lock) {
						CheckBlockQueue();//检测阻塞队列中的进程是否执行完毕
					}
					CLOCK++;
				}
				else {
					Thread.sleep(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
		
	//检查当前阻塞队列中的进程是否被唤醒
	public void CheckBlockQueue() {
		if(!Process.blockQueue_1.isEmpty()) {
			for(int i=0;i<Process.blockQueue_1.size();i++) {
				//阻塞队列中的进程IO指令执行结束
				if((Clock_thread.COUNTTIME-Process.blockQueue_1.get(i).BqTimes)>=Process.blockQueue_1.get(i).instructions.get(Process.blockQueue_1.get(i).IR-1).InRunTimes) {
					//如果指令结束运行
					if(Process.blockQueue_1.get(i).IR==Process.blockQueue_1.get(i).InstrucNum) {
						CPU2023.endRun(Process.blockQueue_1.get(i));
						Process.blockQueue_1.get(i).RunTimes+=Process.blockQueue_1.get(i).instructions.get(Process.blockQueue_1.get(i).IR-1).InRunTimes;
						Process.blockQueue_1.get(i).PSW=-2;
						CPU2023.updatePCBPool(Process.blockQueue_1.get(i));
						Process.blockQueue_1.remove(i);//删除阻塞队列中该进程的信息
						//就绪队列为空则停止执行，保存记录
						if(Process.readyQueue.isEmpty() && Process.BlockQueue_AllIsEmpty() && CPU2023.PSW==0) {
							Show.SavaProcessLog();//保存作业运行记录
							MainForm.textArea_RunProcess.append("进程运行信息已保存到"+Process.FileOutputPath+"\\ProcessResults-"+Clock_thread.COUNTTIME+"-JTYX.txt]\n");
							Clock_thread.STOP=true;
							MainForm.ContinueButtonChange();
							MainForm.Show_All();
							MainForm.clearCheck();
						}
					}
					else {
						//更新总运行时间
						Process.blockQueue_1.get(i).RunTimes+=Process.blockQueue_1.get(i).instructions.get(Process.blockQueue_1.get(i).IR-1).InRunTimes;
						//放入就绪队列
						synchronized(Process.Lock) {
							CPU2023.intoReadyQueue(Process.blockQueue_1.get(i));
						}
						//删除阻塞队列中该进程的信息
						Process.blockQueue_1.remove(i);
					}
					
				}
			}
		}
	}
}

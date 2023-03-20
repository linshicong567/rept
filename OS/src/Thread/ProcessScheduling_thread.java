package Thread;

import Register.Process;
import View.MainForm;
import View.Show;

import Register.CPU2023;

//进程调度算法线程类
public class ProcessScheduling_thread extends Thread{
	
	private int times=3;//时间片大小
	private int CLOCK=0;//与系统时钟同步的时间
	
	public ProcessScheduling_thread(int p) {
		setPriority(p);
	}
	
	public void run() {
		while(true) {
			try {
				if((Clock_thread.COUNTTIME-CLOCK)==1){
					synchronized(Process.Lock) {
						ProcessScheduling();
					}
					MainForm.Show_All();
					CLOCK++;
				}
				else
					Thread.sleep(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	//调度时间函数,当times=0时对times重新赋值
	public void CheckSchedulingTime() {
		times=3;//重置时间片
	}
	
	//进程调度函数
	public void ProcessScheduling() {
		
		//CPU空闲
		if(CPU2023.PSW==0) {
			//就绪队列非空
			if(!Process.readyQueue.isEmpty()) {
				CheckSchedulingTime();
				CPU2023.CPU_REC(Process.readyQueue.get(0));//给就绪队列中优先级最高的进程分配CPU并运行
				times--;
			}
			else {
				Show.Show_RunProcess(Clock_thread.COUNTTIME+":[CPU空闲]\n");
			}
		}
		//CPU忙
		else{
			//就绪队列为空
			if(Process.readyQueue.isEmpty()) {
				//当前进程指令执行结束
				if(CPU2023.CPU_PCB.InstrucNum == CPU2023.IR) {
					CPU2023.endRun(CPU2023.CPU_PCB);//进程运行结束
					Show.SavaProcessLog();//保存作业运行记录
					MainForm.clearCheck();//清除MMU信息
					MainForm.textArea_RunProcess.append("进程运行信息已保存到"+Process.FileOutputPath+"\\ProcessResults-"+Clock_thread.COUNTTIME+"-JTYX.txt]\n");
					Clock_thread.STOP=true;
					MainForm.ContinueButtonChange();
				}
				else if(times==0) {
					CheckSchedulingTime();//更新时间片
					CPU2023.runProcess();
					times--;
				}
				else {
					CPU2023.runProcess();//进程运行一条指令
					times--;
				}
			}
			//就绪队列不为空
			else {
				//当前进程指令执行结束
				if(CPU2023.CPU_PCB.InstrucNum == CPU2023.IR) {
					CPU2023.endRun(CPU2023.CPU_PCB);//进程运行结束
					CheckSchedulingTime();//更新时间片
				}
				//时间片到，进行进程调度
				else if(times==0) {
					CPU2023.CPU_PRO();//现场保护
					CheckSchedulingTime();//更新时间片
				}
				else {
					CPU2023.runProcess();//进程运行一条指令
					times--;
				}
			}
		}
	}
}

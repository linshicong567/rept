package Register;

import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.locks.LockSupport;

import Thread.Clock_thread;
import View.MainForm;
import View.Show;

public class CPU2023 {
	
	public static int PC=0;//程序计数器，下一条要执行的指令
	public static int IR=0;//指令寄存器，当前执行的指令
	public static int PSW=0;//状态寄存器，CPU当前的状态（0空闲/1运行）
	public static PCB CPU_PCB;//保存进程信息的PCB
	
	//CPU现场保护,将CPU当前的状态保存到正在运行的进程的PCB中，进程进入就绪队列
	public static void CPU_PRO() {
		CPU_PCB.PC=PC;
		CPU_PCB.IR=IR;
		PSW=0;//更新CPU状态信息
		intoReadyQueue(CPU_PCB);//进程放入就绪队列并排序
		CPU_PCB.pageTable=null;
	}
	
	//CPU现场恢复，将即将占用CPU资源的进程的PCB中的信息保存到CPU中
	public static void CPU_REC(PCB pcb) {
		
		PCB pcb_new=new PCB(pcb);
		pcb_new.PSW=1;//进程状态改为运行态
		CPU_PCB=pcb_new;//保存PCB信息
		PC=pcb_new.PC;//恢复PCB状态
		IR=pcb_new.IR;
		PSW=1;//更新CPU状态
		Process.readyQueue.remove(pcb);//将分得CPU的进程移出就绪队列
		updatePCBPool(CPU_PCB);//更新PCB池的信息
		runProcess();
	}
	
	//进程运行结束
	public static void endRun(PCB pcb) {
		PCB pcb_new=new PCB(pcb);
		pcb_new.EndTimes=Clock_thread.COUNTTIME;//结束时间
		pcb_new.TurnTimes=Clock_thread.COUNTTIME-pcb_new.EnterTimes;//周转时间
		//进程由运行态结束
		if(pcb.PSW==1) {
			//更新CPU状态信息
			PC=0;
			IR=0;
			PSW=0;
			CPU_PCB.pageTable=null;
		}
		pcb_new.PSW=-2;//更新PCB状态
		updatePCBPool(pcb_new);//更新PCB池的信息
		Show.Show_RunProcess(Clock_thread.COUNTTIME+":[终止进程"+pcb_new.ProID+"]\n");
		Show.RunProcessLog_State.add(Clock_thread.COUNTTIME+":["+pcb_new.ProID+":"+pcb_new.EnterTimes+"+"+pcb_new.InTimes+"+"+pcb_new.TurnTimes+"]\n");
		pcb_new.returnMemory();//释放内存资源
		if(JOB.getNoCreateProcessJob()!=null)
			Process.CreatProcess(JOB.getNoCreateProcessJob());//创建一个进程
	}
	
	//进程运行一条指令
	public static void runProcess() {
		
		PC++;
		IR++;
		//当前指令不在页表或在页表中但不在内存中
		if(!CPU_PCB.isInPageTableExist(CPU_PCB.instructions.get(IR-1).L_Address)) {
			//缺页中断
			Show.PPP.add(CPU_PCB.ProID+"/"+Clock_thread.COUNTTIME);
			LockSupport.unpark(MainForm.pageInterruption);//唤醒缺页中断线程
			MainForm.chooseCheck(false);//显示缺页中断
			LockSupport.park();//阻塞进程调度的线程
			MainForm.showAddressExchange(CPU_PCB.instructions.get(IR-1).L_Address*1024,CPU_PCB.getPageFrameNum(CPU_PCB.instructions.get(IR-1).L_Address)*1024);
			CPU_PCB.setVisitNum(CPU_PCB.instructions.get(IR-1).L_Address);//更新访问位
			Show.Show_RunProcess(Clock_thread.COUNTTIME+":[缺页中断:"+CPU_PCB.ProID+","+CPU_PCB.PageOut+","+CPU_PCB.PageIn+"]\n");
		}
		//当前指令在页表中
		else {
			MainForm.chooseCheck(true);//显示MMU信息
			MainForm.showAddressExchange(CPU_PCB.instructions.get(IR-1).L_Address*1024,CPU_PCB.getPageFrameNum(CPU_PCB.instructions.get(IR-1).L_Address)*1024);
			CPU_PCB.setVisitNum(CPU_PCB.instructions.get(IR-1).L_Address);//更新访问位
		}
			
		if(CPU_PCB.instructions.get(IR-1).Instruc_State==1 || CPU_PCB.instructions.get(IR-1).Instruc_State==0) {
			CPU_PCB.RunTimes++;
			updatePCBPool(CPU_PCB);//更新PCB池信息
			CPU_PCB.instructions.get(IR-1).InRunTimes--;//需要运行时间减一
			Show.Show_RunProcess(Clock_thread.COUNTTIME+":[运行进程:"+CPU_PCB.ProID+","+IR+","+CPU_PCB.instructions.get(IR-1).Instruc_State+","+CPU_PCB.instructions.get(IR-1).L_Address*1024+","+CPU_PCB.getPageFrameNum(CPU_PCB.instructions.get(IR-1).L_Address)*1024+"]\n");
			if(CPU_PCB.instructions.get(IR-1).InRunTimes!=0) {
				IR--;
				PC--;
			}
		}
		else{
			CPU_PCB.PC=PC;
			CPU_PCB.IR=IR;
			PSW=0;
			intoBlockQueue(CPU_PCB);//进程进入阻塞队列
			CPU_PCB.pageTable=null;
		}
	}
	
	//进程进入就绪队列
	public static void intoReadyQueue(PCB pcb) {
		PCB pcb_new=new PCB(pcb);
		Process.readyQueue.add(pcb_new);
		//对就绪队列中的进程按优先级（高——低）排序
		Collections.sort(Process.readyQueue, new Comparator<PCB>() {
            public int compare(PCB o1, PCB o2) {
                return Integer.valueOf(o1.Priority).compareTo(Integer.valueOf(o2.Priority));
            }
        });
		
		pcb_new.PSW=0;//进程状态置为就绪态
		pcb_new.RqTimes=Clock_thread.COUNTTIME;//进入就绪队列时间
		pcb_new.RqNum=Process.readyQueue.indexOf(pcb_new);//获取排序后进程在就绪队列的位置
		if(pcb.PSW==2)
			Show.Show_RunProcess(Clock_thread.COUNTTIME+":[进入就绪队列:"+pcb_new.ProID+","+(pcb_new.InstrucNum-pcb_new.IR)+"]\n");
		else
			Show.Show_RunProcess(Clock_thread.COUNTTIME+":[重新进入就绪队列:"+pcb_new.ProID+","+(pcb_new.InstrucNum-pcb_new.IR)+"]\n");
		updatePCBPool(pcb_new);//更新PCB池信息
		Process.readyQueue.set(Process.readyQueue.indexOf(pcb_new),pcb_new);//将进程在就绪队列的位置信息保存到进程的PCB中
	}
	
	//进程进入阻塞队列
	public static void intoBlockQueue(PCB pcb) {
		PCB pcb_new=new PCB(pcb);
		int blockQueueNum=pcb_new.instructions.get(pcb_new.IR-1).Instruc_State-1;//阻塞队列编号
		pcb_new.PSW=-1;//进程状态置为阻塞态
		pcb_new.BqTimes=Clock_thread.COUNTTIME;//进入阻塞队列时间
		switch(blockQueueNum) {
			case 1:{
				Process.blockQueue_1.add(pcb_new);
				pcb_new.BqNum=Process.blockQueue_1.indexOf(pcb_new);
				Process.blockQueue_1.set(pcb_new.BqNum,pcb_new);
				Show.BB1.add(""+pcb_new.ProID+"/"+Clock_thread.COUNTTIME);
				break;
			}
			case 2:{
				Process.blockQueue_2.add(pcb_new);
				pcb_new.BqNum=Process.blockQueue_2.indexOf(pcb_new);
				Process.blockQueue_2.set(pcb_new.BqNum,pcb_new);
				Show.BB2.add(""+pcb_new.ProID+"/"+Clock_thread.COUNTTIME);
				break;
			}
			case 3:{
				Process.blockQueue_3.add(pcb_new);
				pcb_new.BqNum=Process.blockQueue_3.indexOf(pcb_new);
				Process.blockQueue_3.set(pcb_new.BqNum,pcb_new);
				Show.BB3.add(""+pcb_new.ProID+"/"+Clock_thread.COUNTTIME);
				break;
			}
			case 4:{
				Process.blockQueue_4.add(pcb_new);
				pcb_new.BqNum=Process.blockQueue_4.indexOf(pcb_new);
				Process.blockQueue_4.set(pcb_new.BqNum,pcb_new);
				Show.BB4.add(""+pcb_new.ProID+"/"+Clock_thread.COUNTTIME);
				break;
			}
			case 5:{
				Process.blockQueue_5.add(pcb_new);
				pcb_new.BqNum=Process.blockQueue_5.indexOf(pcb_new);
				Process.blockQueue_5.set(pcb_new.BqNum,pcb_new);
				Show.BB5.add(""+pcb_new.ProID+"/"+Clock_thread.COUNTTIME);
				break;
			}
		}
		updatePCBPool(pcb_new);//更新PCB池信息
		Show.Show_RunProcess(Clock_thread.COUNTTIME+":[阻塞进程:"+blockQueueNum+","+pcb_new.ProID+"]\n");
	}
	
	//更新PCB池中的进程信息
	public static void updatePCBPool(PCB pcb) {
		for(int i=0;i<Process.pcbPool.size();i++) {
			if(Process.pcbPool.get(i).ProID==pcb.ProID) {
				Process.pcbPool.get(i).PSW=pcb.PSW;
				Process.pcbPool.get(i).Priority=pcb.Priority;
				Process.pcbPool.get(i).IR=pcb.IR;
				Process.pcbPool.get(i).PC=pcb.PC;
				Process.pcbPool.get(i).InstrucNum=pcb.InstrucNum;
				Process.pcbPool.get(i).InTimes=pcb.InTimes;
				Process.pcbPool.get(i).EndTimes=pcb.EndTimes;
				Process.pcbPool.get(i).RunTimes=pcb.RunTimes;
				Process.pcbPool.get(i).TurnTimes=pcb.TurnTimes;
				Process.pcbPool.get(i).BqNum=pcb.BqNum;
				Process.pcbPool.get(i).BqTimes=pcb.BqTimes;
				Process.pcbPool.get(i).RqNum=pcb.RqNum;
				Process.pcbPool.get(i).RqTimes=pcb.RqTimes;
				Process.pcbPool.get(i).EnterTimes=pcb.EnterTimes;
				Process.pcbPool.get(i).instructions=pcb.instructions;
				Process.pcbPool.get(i).pageTable=pcb.pageTable;
				break;
			}
		}
	}
}

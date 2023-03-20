package Register;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import External.EnternalMemory;
import Thread.Clock_thread;
import View.MainForm;
import View.Show;

public class Process {
	
	public static ArrayList<PCB> readyQueue=new ArrayList<PCB>();//就绪队列
	public static ArrayList<PCB> blockQueue_1=new ArrayList<PCB>();//阻塞队列1，键盘输入
	public static ArrayList<PCB> blockQueue_2=new ArrayList<PCB>();//阻塞队列2，屏幕输出
	public static ArrayList<PCB> blockQueue_3=new ArrayList<PCB>();//阻塞队列3，打印操作
	public static ArrayList<PCB> blockQueue_4=new ArrayList<PCB>();//阻塞队列4，磁盘读取
	public static ArrayList<PCB> blockQueue_5=new ArrayList<PCB>();//阻塞队列5，磁盘写入
	public static ArrayList<PCB> pcbPool=new ArrayList<PCB>();//PCB池
	public static Object Lock=new Object();//锁
	
	public static int Num=1;//进程/作业编号
	public static String FilePath=null;//运行文件路径
	public static String FileInputPath=null;//打开文件路径
	public static String FileOutputPath=null;//文件保存路径
	
	//将作业加入到后备作业列表
	public static void JobIntoRequestedList(JCB jcb) {
		jcb.IntoJobList=true;
		JCB jcb_new=new JCB(jcb);
		JOB.jobList_Requested.add(jcb_new);//将作业放入作业后备队列
		Show.Show_RunProcess(Clock_thread.COUNTTIME+":[新增作业:"+jcb.JobsID+","+jcb.InTimes+","+jcb.InstrucNum+"]\n");
		//判断是否可以创建进程,并发进程数最大为4
		if(Clock_thread.COUNTTIME>=jcb.InTimes && getRunProcessNum()<4)
			CreatProcess(jcb_new);//为该作业创建进程
	}
	
	//创建进程
	public static void CreatProcess(JCB jcb) {
		PCB pcb=new PCB();//创建一个空白PCB
		jcb.CreatePCB=true;//更新作业已创建进程的信息
		pcb.ProID=Num;//进程编号
		pcb.Priority=jcb.Priority;//进程优先级
		pcb.InTimes=Clock_thread.COUNTTIME;//进程创建时间为当前时钟时间
		pcb.InstrucNum=jcb.InstrucNum;//进程包含的指令条数
		pcb.PSW=2;//进程状态设为未进入就绪队列
		pcb.RunTimes=0;//进程运行时间
		pcb.RqTimes=Clock_thread.COUNTTIME;//进入就绪队列的时间
		pcb.PC=1;//程序计数器指向第一条指令
		pcb.IR=0;
		pcb.EnterTimes=jcb.InTimes;
		
		GetInstruction(pcb,jcb);//添加指令序列
		EnternalMemory.saveToEnternalMemory(pcb);//信息保存到磁盘
		pcbPool.add(pcb);//将此pcb信息加入到pcb池中
		Show.Show_RunProcess(Clock_thread.COUNTTIME+":[创建进程:"+pcb.ProID+"]\n");
		CPU2023.intoReadyQueue(pcb);//将进程放入就绪队列
		Num++;//进程编号加一
	}
	
	//实时添加作业
	public static void AddJob() {
		Random random=new Random();
		int option=random.nextInt(7)+1;
		//使用副本进行实时作业添加
		for(JCB jcb:JOB.jobList_NoRequest) {
			if(jcb.JobsID==option) {
				JCB jcb_new=new JCB(jcb);
				jcb_new.CreatePCB=false;
				jcb_new.IntoJobList=true;
				jcb_new.InTimes=Clock_thread.COUNTTIME;//修改作业添加时间
				JOB.jobList_Requested.add(jcb_new);//新生成的作业添加到后备队列
				Show.Show_RunProcess(Clock_thread.COUNTTIME+":[新增作业:"+jcb_new.JobsID+","+jcb_new.InTimes+","+jcb_new.InstrucNum+"]\n");
				if(getRunProcessNum()<4)
					CreatProcess(jcb_new);//为该作业创建进程
				break;
			}
		}
	}
	
	//添加作业对应进程的指令序列到PCB中
	public static void GetInstruction(PCB pcb,JCB jcb) {
		String absolutePath=FileInputPath+"\\"+jcb.JobsID+".txt";//文件的绝对路径
		try {
			FileInputStream fin = new FileInputStream(absolutePath);
			InputStreamReader rin = new InputStreamReader(fin);
			@SuppressWarnings("resource")
			BufferedReader bin = new BufferedReader(rin);
			String str = "";//用来保vc存每次读取的一行数据
			while((str=bin.readLine())!=null) {
				//将首尾及中间的空格去掉并用‘，’分割中间的数据
				String[] data=str.trim().replaceAll(" ","").split(","); 
				int[] jobdata=new int[3];
				//将字符串类型全部转化为整型
				for(int i=0;i<data.length;i++) {
					jobdata[i]=Integer.parseInt(data[i]);
				}
				Instruc instruc=new Instruc(jobdata[0],jobdata[1],jobdata[2]);
				pcb.instructions.add(instruc);//将指令信息加入到PCB中
			}
		} catch (Exception e) {
			MainForm.textArea_RunProcess.setText("添加作业指令序列失败！！！\n");
		}
	}
	
	//判断阻塞队列是否都为空
	public static Boolean BlockQueue_AllIsEmpty() {
		if(blockQueue_1.isEmpty() && blockQueue_2.isEmpty() && blockQueue_3.isEmpty() && blockQueue_4.isEmpty() && blockQueue_5.isEmpty())
			return true;
		return false;
	}
	
	//返回当前并发进程的数量
	public static int getRunProcessNum() {
		int num=0;
		for(PCB pcb:Process.pcbPool) {
			if(pcb.PSW!=-2)
				num++;
		}
		return num;
	}
}

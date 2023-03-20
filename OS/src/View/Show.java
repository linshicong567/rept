package View;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import Internal.MMU;
import Internal.Page;
import Register.CPU2023;
import Register.JOB;
import Register.Process;
import Thread.Clock_thread;

public class Show {
	
	public static ArrayList<String> RunProcessLog=new ArrayList<String>();//运行记录
	public static ArrayList<String> RunProcessLog_State=new ArrayList<String>();//状态统计记录
	public static ArrayList<String> BB1=new ArrayList<String>();//阻塞队列1状态统计记录
	public static ArrayList<String> BB2=new ArrayList<String>();//阻塞队列2状态统计记录
	public static ArrayList<String> BB3=new ArrayList<String>();//阻塞队列3状态统计记录
	public static ArrayList<String> BB4=new ArrayList<String>();//阻塞队列4状态统计记录
	public static ArrayList<String> BB5=new ArrayList<String>();//阻塞队列5状态统计记录
	public static ArrayList<String> PPP=new ArrayList<String>();//缺页中断状态统计记录

	public static Object ShowLock=new Object();//显示锁
	
	//显示PCB池中的信息
	public static void Show_PCBPool(JTable jTable,DefaultTableModel tableModel) {
		tableModel.setRowCount(0);
		for(int i=0;i<Process.pcbPool.size();i++) {
			String proID = ""+Process.pcbPool.get(i).ProID;
			String priority = ""+Process.pcbPool.get(i).Priority;
			String inTimes = ""+Process.pcbPool.get(i).InTimes;
			String pSW = Process.pcbPool.get(i).PSW>0 ? "运行态":Process.pcbPool.get(i).PSW==0 ? "就绪态":Process.pcbPool.get(i).PSW==-1 ? "阻塞态":"结束态";
			String runTimes = ""+Process.pcbPool.get(i).RunTimes;
			String instrucNum = ""+Process.pcbPool.get(i).InstrucNum;
			tableModel.addRow(new String[] {proID,priority,inTimes,runTimes,instrucNum,pSW});
		}
		jTable.setModel(tableModel);
	}
	
	//显示当前时间
	public static void Show_Time(JTextField jTextField) {
		jTextField.setText(""+Clock_thread.COUNTTIME);
	}
	
	//追加显示运行过程信息
	public static void Show_RunProcess(String str) {
		MainForm.textArea_RunProcess.setCaretPosition(MainForm.textArea_RunProcess.getText().length());//跟随文本显示
		MainForm.textArea_RunProcess.append(str);
		RunProcessLog.add(str);//保存运行记录
	}
	
	//显示就绪队列信息
	public static void Show_ReadyQueue(JTable jTable,DefaultTableModel tableModel) {
		tableModel.setRowCount(0);
		for(int i=0;i<Process.readyQueue.size();i++) {
			String proID = ""+Process.readyQueue.get(i).ProID;
			String priority = ""+Process.readyQueue.get(i).Priority;
			tableModel.addRow(new String[] {proID,priority});
		}
		jTable.setModel(tableModel);
	}
	
	//显示阻塞队列1信息
	public static void Show_BlockQueue_1(JTable jTable,DefaultTableModel tableModel) {
		tableModel.setRowCount(0);
		for(int i=0;i<Process.blockQueue_1.size();i++) {
			String proID = ""+Process.blockQueue_1.get(i).ProID;
			String priority = ""+Process.blockQueue_1.get(i).Priority;
			tableModel.addRow(new String[] {proID,priority});
		}
		jTable.setModel(tableModel);
	}
	
	//显示阻塞队列2信息
	public static void Show_BlockQueue_2(JTable jTable,DefaultTableModel tableModel) {
		tableModel.setRowCount(0);
		for(int i=0;i<Process.blockQueue_2.size();i++) {
			String proID = ""+Process.blockQueue_2.get(i).ProID;
			String priority = ""+Process.blockQueue_2.get(i).Priority;
			tableModel.addRow(new String[] {proID,priority});
		}
		jTable.setModel(tableModel);
	}
	
	//显示阻塞队列3信息
	public static void Show_BlockQueue_3(JTable jTable,DefaultTableModel tableModel) {
		tableModel.setRowCount(0);	
		for(int i=0;i<Process.blockQueue_3.size();i++) {
			String proID = ""+Process.blockQueue_3.get(i).ProID;
			String priority = ""+Process.blockQueue_3.get(i).Priority;
			tableModel.addRow(new String[] {proID,priority});
		}
		jTable.setModel(tableModel);
	}
	
	//显示阻塞队列4信息
	public static void Show_BlockQueue_4(JTable jTable,DefaultTableModel tableModel) {
		tableModel.setRowCount(0);	
		for(int i=0;i<Process.blockQueue_4.size();i++) {
			String proID = ""+Process.blockQueue_4.get(i).ProID;
			String priority = ""+Process.blockQueue_4.get(i).Priority;
			tableModel.addRow(new String[] {proID,priority});
		}
		jTable.setModel(tableModel);
	}
	
	//显示阻塞队列5信息
	public static void Show_BlockQueue_5(JTable jTable,DefaultTableModel tableModel) {
		tableModel.setRowCount(0);
		for(int i=0;i<Process.blockQueue_5.size();i++) {
			String proID = ""+Process.blockQueue_5.get(i).ProID;
			String priority = ""+Process.blockQueue_5.get(i).Priority;
			tableModel.addRow(new String[] {proID,priority});
		}
		jTable.setModel(tableModel);
	}
	
	//显示内存条信息
	public static void Show_InteneralMemory(JProgressBar progressBar) {
		int num=0;
		for(Page p:MMU.internalMemory) {
			if(!p.isFree)
				num++;
		}
		progressBar.setValue(num);
	}
	
	//显示CPU状态信息
	public static void Show_CPU(JTable jTable,DefaultTableModel tableModel) {
		tableModel.setRowCount(0);
		String IR=CPU2023.PSW==0 ? "0" : ""+CPU2023.IR;
		String PC=CPU2023.PSW==0 ? "0" : ""+CPU2023.PC;
		String ID= CPU2023.PSW==0 ? "空闲" :""+CPU2023.CPU_PCB.ProID;
		tableModel.addRow(new String[] {ID,IR,PC});
		jTable.setModel(tableModel);
	}
	
	//显示作业队列信息
	public static void Show_JobList(JTable jTable,DefaultTableModel tableModel) {
		tableModel.setRowCount(0);
		for(int i=0;i<JOB.jobList_Requested.size();i++) {
			String jobsId = ""+JOB.jobList_Requested.get(i).JobsID;
			String priority = ""+JOB.jobList_Requested.get(i).Priority;
			String instrucNum=""+JOB.jobList_Requested.get(i).InstrucNum;
			String createPCB = JOB.jobList_Requested.get(i).CreatePCB ? "是" : "否";
			String inTimes = ""+JOB.jobList_Requested.get(i).InTimes;
			tableModel.addRow(new String[] {jobsId,priority,instrucNum,createPCB,inTimes});
		}
		jTable.setModel(tableModel);
	}
	
	//显示当前进程的页表信息
	public static void Show_PageTable(JTable jTable,DefaultTableModel tableModel) {
		tableModel.setRowCount(0);
		if(CPU2023.PSW==1) {
			for(int i=0;i<CPU2023.CPU_PCB.pageTable.size();i++) {
				String pageNum = ""+CPU2023.CPU_PCB.pageTable.get(i).pageNum;
				String pageFrameNum = ""+CPU2023.CPU_PCB.pageTable.get(i).pageFrameNum;
				String isExist = CPU2023.CPU_PCB.pageTable.get(i).isExist ? "在内存" : "不在内存";
				tableModel.addRow(new String[] {pageNum,pageFrameNum,isExist});
			}
			jTable.setModel(tableModel);
		}
	}
	//保存进程运行信息
	public static void SavaProcessLog() {
		try {
			File f=new File(Process.FileOutputPath+"\\ProcessResults-"+Clock_thread.COUNTTIME+"-JTYX.txt");
			if(!f.exists())
				f.getParentFile().mkdirs();
			FileOutputStream fout = new FileOutputStream(f);
			OutputStreamWriter rout = new OutputStreamWriter(fout);
			BufferedWriter bin = new BufferedWriter(rout);
			String str1 = "作业/进程调度事件：\n";
			String str2="";
			String str3="PPPP:[PageInterruption:";
			for(String Log:RunProcessLog)
				str1+=Log;
			str1+="\n\n状态统计信息：\n";
			for(String LogState:RunProcessLog_State)
				str1+=LogState;
			str2+="BB1:[阻塞队列1，键盘输入:";
			for(int i=0;i<BB1.size();i++) {
				if(i==(BB1.size()-1))
					str2+=""+BB1.get(i);
				else 
					str2+=""+BB1.get(i)+",";
			}
			str2+="]\nBB2:[阻塞队列2，屏幕显示:";
			for(int i=0;i<BB2.size();i++) {
				if(i==(BB2.size()-1))
					str2+=""+BB2.get(i);
				else 
					str2+=""+BB2.get(i)+",";
			}
			str2+="]\nBB3:[阻塞队列3，打印任务:";
			for(int i=0;i<BB3.size();i++) {
				if(i==(BB3.size()-1))
					str2+=""+BB3.get(i);
				else 
					str2+=""+BB3.get(i)+",";
			}
			str2+="]\nBB4:[阻塞队列4，磁盘读文件:";
			for(int i=0;i<BB4.size();i++) {
				if(i==(BB4.size()-1))
					str2+=""+BB4.get(i);
				else 
					str2+=""+BB4.get(i)+",";
			}
			str2+="]\nBB5:[阻塞队列5，磁盘写文件:";
			for(int i=0;i<BB5.size();i++) {
				if(i==(BB5.size()-1))
					str2+=""+BB5.get(i);
				else 
					str2+=""+BB5.get(i)+",";
			}
			str2+="]\n\n";
			for(int i=0;i<PPP.size();i++) {
				if(i==(PPP.size()-1))
					str3+=""+PPP.get(i);
				else
					str3+=""+PPP.get(i)+",";
			}
			str3+="]\n";
			bin.write(str1+str2+str3);
			bin.close();
		} catch (Exception e) {
			MainForm.textArea_RunProcess.setText("保存进程运行信息失败！！！\n");
		}
	}
}

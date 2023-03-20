package Thread;

import java.util.concurrent.locks.LockSupport;

import Internal.MMU;
import Internal.PageTableItem;
import Register.CPU2023;
import View.MainForm;

//缺页中断线程类
public class PageInterruption_thread extends Thread{
	
	//重写run方法
	public void run() {
		while(true) {
			LockSupport.park();//等待唤醒
			if(CPU2023.CPU_PCB.PageNum>=4)
				PageOut();//淘汰一页
			PageIn();//装入一页
			LockSupport.unpark(MainForm.processScheduling);//唤醒线程调度的进程
		}
	}
	
	//页装入函数
	public static void PageIn() {
		int pageFrameNum=MMU.getFreePageIndex();//空闲的页帧号
		//页表项已存在就只改变其页帧号
		if(CPU2023.CPU_PCB.isInPageTable(CPU2023.CPU_PCB.instructions.get(CPU2023.IR-1).L_Address)) {
			for(PageTableItem pti:CPU2023.CPU_PCB.pageTable) {
				if(pti.pageNum==CPU2023.CPU_PCB.instructions.get(CPU2023.IR-1).L_Address) {
					pti.pageFrameNum=pageFrameNum;
					pti.isExist=true;//更新存在内存标志
					CPU2023.CPU_PCB.PageIn=pti.pageNum;
				}
			}
		}
		//页表项不存在则新建页表项
		else {
			PageTableItem pageTableItem=new PageTableItem(CPU2023.CPU_PCB.instructions.get(CPU2023.IR-1).L_Address,pageFrameNum);
			CPU2023.CPU_PCB.pageTable.add(pageTableItem);//将页表项信息加入到该进程的页表中
			CPU2023.CPU_PCB.PageIn=CPU2023.CPU_PCB.instructions.get(CPU2023.IR-1).L_Address;
		}
		CPU2023.CPU_PCB.PageNum++;
	}
	
	//页淘汰函数(最近最久未访问)
	public static void PageOut() {
		int visitNum=0;//访问位
		int index=0;//需要淘汰的页的索引号
		int pageFrameNum=0;//需要淘汰的页帧号
		//找出最近最久未被访问的页的索引
		for(int i=0;i<CPU2023.CPU_PCB.pageTable.size();i++) {
			if(CPU2023.CPU_PCB.pageTable.get(i).isExist) {
				if(CPU2023.CPU_PCB.pageTable.get(i).visitNum>=visitNum) {
					visitNum=CPU2023.CPU_PCB.pageTable.get(i).visitNum;
					index=i;
					pageFrameNum=CPU2023.CPU_PCB.pageTable.get(i).pageFrameNum;
				}
			}
		}
		CPU2023.CPU_PCB.PageOut=CPU2023.CPU_PCB.pageTable.get(index).pageNum;
		CPU2023.CPU_PCB.pageTable.get(index).isExist=false;//淘汰最近最久未被访问的页
		CPU2023.CPU_PCB.pageTable.get(index).visitNum=0;//将淘汰页的访问位置为0
		CPU2023.CPU_PCB.PageNum--;//进程拥有的页数减一
		MMU.internalMemory[pageFrameNum].isFree=true;//释放内存空间
	}
}

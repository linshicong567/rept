package Register;

import java.util.ArrayList;

import Internal.MMU;
import Internal.PageTableItem;

public class PCB{

	public int ProID;//(1)进程编号
	public int Priority;//(2)进程优先数
	public int InTimes;//(3)进程创建时间
	public int EndTimes;//(4)进程结束时间
	public int PSW;//(5)进程状态（2未进入就绪队列/1运行/0就绪/-1阻塞/-2结束态）
	public int RunTimes;//(6)进程运行时间
	public int TurnTimes;//(7)进程周转时间统计
	public int InstrucNum;//(8)进程包含的指令数目
	public int PC;//(9)程序计数器信息
	public int IR;//(10)指令寄存器信息
	public int RqNum;//(11)在就绪队列的位置编号
	public int RqTimes;//(12)进入就绪队列时间
	public int BqNum;//(13)在阻塞队列的位置编号
	public int BqTimes;//(14)进入阻塞队列时间
	public int EnterTimes;//(15)作业请求时间
	public int PageNum;//(16)当前进程拥有的内存块数
	public int PageIn;//(17)装入的页号
	public int PageOut;//(18)淘汰的页号
	
	public ArrayList<Instruc> instructions=new ArrayList<Instruc>();//进程的指令序列
	public ArrayList<PageTableItem> pageTable=new ArrayList<PageTableItem>();//页表
	
	//构造函数
	public PCB() {
		
	}
	
	//构造函数
	public PCB(PCB pcb) {
		this.ProID=pcb.ProID;
		this.Priority=pcb.Priority;
		this.PC=pcb.PC;
		this.PSW=pcb.PSW;
		this.InTimes=pcb.InTimes;
		this.InstrucNum=pcb.InstrucNum;
		this.EndTimes=pcb.EndTimes;
		this.BqNum=pcb.BqNum;
		this.BqTimes=pcb.BqTimes;
		this.IR=pcb.IR;
		this.RqNum=pcb.RqNum;
		this.RqTimes=pcb.RqTimes;
		this.RunTimes=pcb.RunTimes;
		this.TurnTimes=pcb.TurnTimes;
		this.EnterTimes=pcb.EnterTimes;
		this.PageNum=pcb.PageNum;
		this.instructions=pcb.instructions;
		this.pageTable=pcb.pageTable;
	}
	
	//通过页号获取页框号
	public int getPageFrameNum(int pageNum) {
		for(PageTableItem pageItem:this.pageTable) {
			if(pageNum==pageItem.pageNum)
				return pageItem.pageFrameNum;
		}
		return -1;//没有找到返回-1
	}
	
	//判断当前指令内存信息是否在页表中且是否在内存中
	public Boolean isInPageTableExist(int L_Address) {
		if(!this.pageTable.isEmpty()) {
			for(PageTableItem pageItem:this.pageTable) {
				if(pageItem.pageNum==L_Address && pageItem.isExist)
					return true;
			}
		}
		return false;
	}
	
	//判断当前指令内存信息是否在页表中
	public Boolean isInPageTable(int L_Address) {
		if(!this.pageTable.isEmpty()) {
			for(PageTableItem pageItem:this.pageTable) {
				if(pageItem.pageNum==L_Address)
					return true;
			}
		}
		return false;
	}
	
	//将指定指令访问位置为0,其他位加一
	public void setVisitNum(int L_address) {
		for(PageTableItem pti:this.pageTable) {
			if(pti.isExist) {
				if(pti.pageNum==L_address)
					pti.visitNum=0;
				else
					pti.visitNum++;
			}
		}
	}
	
	//回收进程占有的内存资源
	public void returnMemory() {
		for(PageTableItem pti:this.pageTable)
			if(pti.isExist)
				MMU.internalMemory[pti.pageFrameNum].isFree=true;
	}
}

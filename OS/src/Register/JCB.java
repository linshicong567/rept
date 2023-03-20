package Register;

public class JCB {
	
	public int JobsID;//作业序号
	public int Priority;//作业优先级
	public int InTimes;//作业请求时间
	public int InstrucNum;//作业包含的指令条数
	
	public Boolean IntoJobList=false;//是否进入作业队列
	public Boolean CreatePCB=false;//是否创建进程,默认没有创建
	
	//构造函数
	public JCB() {
		
	}
	
	//参数形式的构造函数
	public JCB(int jobsID,int priority,int inTimes,int instrucNum) {
		this.JobsID=jobsID;
		this.Priority=priority;
		this.InTimes=inTimes;
		this.InstrucNum=instrucNum;
	}
	
	//构造函数
	public JCB(JCB jcb) {
		this.JobsID=jcb.JobsID;
		this.Priority=jcb.Priority;
		this.InTimes=jcb.InTimes;
		this.InstrucNum=jcb.InstrucNum;
		this.CreatePCB=jcb.CreatePCB;
		this.IntoJobList=jcb.IntoJobList;
	}
}

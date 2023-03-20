package Register;

public class Instruc {
	
	public int Instruc_ID;//指令编号
	public int Instruc_State;//指令类型
	public int L_Address;//逻辑地址
	public int InRunTimes;//需要的运行时间
	
	//构造函数
	public Instruc() {
		
	}
	
	//构造函数
	public Instruc(int instruc_ID,int instruc_State,int l_Address) {
		this.Instruc_ID=instruc_ID;
		this.Instruc_State=instruc_State;
		this.L_Address=l_Address;
		switch(instruc_State) {
			case 0:this.InRunTimes=1;break;
			case 4:this.InRunTimes=1;break;
			default: this.InRunTimes=2;break;
		}
	}
}

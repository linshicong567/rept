package Internal;

//页
public class Page {
	public int ownerPro;//拥有该页面的进程号
	public Boolean isFree;//是否空闲
	public int index;//页面索引/页面编号
	public int externalAddress;//页框号
	public int instrucID;//保存的指令编号
	
	public Page() {
		this.ownerPro=-1;
		this.isFree=true;
		this.index=-1;
		this.externalAddress=-1;
		this.instrucID=-1;
	}
	
	//构造函数
	public Page(Page page) {
		isFree = page.isFree;
		externalAddress = page.externalAddress;
		index = page.index;
		ownerPro = page.ownerPro;
		instrucID=page.instrucID;
	}
}

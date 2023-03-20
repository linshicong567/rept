package Internal;

public class MMU {
		
	public static Page[] internalMemory=new Page[16];//内存
	public static Page[] externalMemory=new Page[16];//外存
	
	//初始化内存/外存信息
	public static void InitMemory() {
		for(int i=0;i<16;i++) {
			Page inMemory=new Page();
			Page exMemory=new Page();
			internalMemory[i]=inMemory;
			externalMemory[i]=exMemory;
		}
	}
	
	//返回空闲页框的索引号
	public static int getFreePageIndex() {
		for(int i=0;i<internalMemory.length;i++) {
			if(internalMemory[i].isFree) {
				internalMemory[i].isFree=false;
				return i;
			}
		}
		return -1;//没有空闲页就返回-1
	}
}

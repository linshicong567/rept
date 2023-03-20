package Internal;

//页表项
public class PageTableItem{	
	public int pageNum; 				//页号
	public int pageFrameNum; 			//页帧号
	public boolean isExist; 			//页面是否在内存中
	public int visitNum; 			//最近是否被访问
	public int externalPosition;		//该页在外存中的地址

	public PageTableItem() {
		
	}
	
	public PageTableItem(int pageNum,int pageFrameNum) {
		this.pageNum=pageNum;
		this.pageFrameNum=pageFrameNum;
		this.isExist=true;
		this.visitNum=0;
	}
}

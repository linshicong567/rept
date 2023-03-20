package Register;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import View.MainForm;

public class JOB {
	
	public static ArrayList<JCB> jobList_Requested=new ArrayList<JCB>();//已提交的作业
	public static ArrayList<JCB> jobList_NoRequest=new ArrayList<JCB>();//未提交的作业 

	
	public static void getFileInformation(File file) {
		try {
			FileInputStream fin = new FileInputStream(file);
			InputStreamReader rin = new InputStreamReader(fin);
			@SuppressWarnings("resource")
			BufferedReader bin = new BufferedReader(rin);
			String str = "";//用来保存每次读取的一行数据
			while((str=bin.readLine())!=null) {
				//将首尾及中间的空格去掉并用‘，’分割中间的数据
				String[] data=str.trim().replaceAll(" ","").split(","); 
				if(data.length!=4)throw new Exception();//文件内容不正确抛出异常
				int[] jobdata=new int[4];
				//将字符串类型全部转化为整型
				for(int i=0;i<data.length;i++) {
					jobdata[i]=Integer.parseInt(data[i]);
				}
				JCB jcb=new JCB(jobdata[0],jobdata[1],jobdata[2],jobdata[3]);
				jobList_NoRequest.add(jcb);//将作业信息加入到作业列表
			}
			MainForm.textArea_RunProcess.setText("文件解析成功，有"+jobList_NoRequest.size()+"个作业待提交！！！\n");
			bin.close();
		} catch (Exception e) {
			MainForm.textArea_RunProcess.setText("文件（内容/格式）错误!!!");
		}
	}
	
	//取出一个还没有创建进程的作业
	public static JCB getNoCreateProcessJob() {
		for(JCB jcb:jobList_Requested) {
			if(!jcb.CreatePCB)
				return jcb;
		}
		return null;//没有进程可创建就返回null
	}
}

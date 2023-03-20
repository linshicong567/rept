package External;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import Register.PCB;
import Register.Process;

public class EnternalMemory {
	
	//将指定进程的指令信息保存到外存上
	public static void saveToEnternalMemory(PCB pcb) {
		String url=Process.FilePath+"\\Disk\\track_"+pcb.ProID+"\\";
		for(int i=0;i<pcb.InstrucNum;i++) {
			try {
				File f=new File(url+(i+1)+".txt");
				if(!f.exists())
					f.getParentFile().mkdirs();
				FileOutputStream fout = new FileOutputStream(f);
				OutputStreamWriter rout = new OutputStreamWriter(fout);
				BufferedWriter bin = new BufferedWriter(rout);
				String str = "";
				str+=pcb.instructions.get(i).Instruc_ID+",";
				str+=pcb.instructions.get(i).Instruc_State+",";
				str+=pcb.instructions.get(i).L_Address;
				bin.write(str);
				bin.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}

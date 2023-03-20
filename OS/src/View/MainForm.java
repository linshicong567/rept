package View;

import javax.swing.JFrame;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import Internal.MMU;
import Register.JOB;
import Register.Process;
import Thread.Clock_thread;
import Thread.DiskReadBlock_thread;
import Thread.DiskWriteBlock_thread;
import Thread.InputBlock_thread;
import Thread.JobIn_thread;
import Thread.OutputBlock_thread;
import Thread.PageInterruption_thread;
import Thread.PrintBlock_thread;
import Thread.ProcessScheduling_thread;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.border.EtchedBorder;
import javax.swing.JProgressBar;
import javax.swing.border.LineBorder;
import java.awt.Color;

public class MainForm extends JFrame{
	
	/**
	 * 
	 */
	public static int inputValue;//时钟频率
	private static final long serialVersionUID = 1L;
	public static PageInterruption_thread pageInterruption=new PageInterruption_thread();
	public static ProcessScheduling_thread processScheduling=new ProcessScheduling_thread(1);
	public static JobIn_thread jobIn=new JobIn_thread(10);

	private static DefaultTableModel dtm_table_JobList;
	private static DefaultTableModel dtm_table_ReadyQueue;
	private static DefaultTableModel dtm_table_CPU;
	private static DefaultTableModel dtm_table_PCBPool;
	private static DefaultTableModel dtm_table_BlockQueue_1;
	private static DefaultTableModel dtm_table_BlockQueue_2;
	private static DefaultTableModel dtm_table_BlockQueue_3;
	private static DefaultTableModel dtm_table_BlockQueue_4;
	private static DefaultTableModel dtm_table_BlockQueue_5;
	private static DefaultTableModel dtm_table_PageTable;

	private static JButton InputFileButton;
	private static JButton RunProcessButton;
	private static JButton AddProcessButton;
	private static JButton StopButton;
	private static JButton ContinueButton;
	private static JButton ExitButton;
	
	private static JTable table_JobList;//作业队列显示
	private static JTable table_ReadyQueue;//就绪队列显示
	private static JTable table_CPU;//CPU状态显示
	private static JTable table_PCBPool;//PCB池显示
	private static JTable table_BlockQueue_1;//阻塞队列
	private static JTable table_BlockQueue_2;
	private static JTable table_BlockQueue_3;
	private static JTable table_BlockQueue_4;
	private static JTable table_BlockQueue_5;
	public static JTable table_PageTable;//页表信息
	
	public static JTextArea textArea_RunProcess;//运行过程信息
	public static JTextField textField_Time;//运行时间
	public static JProgressBar progressBar;//内存条
	public static JCheckBox pageIn_CheckBox;//页表命中
	public static JCheckBox pageOut_CheckBox;//缺页中断
	public static JLabel L_Address_NewLabel;//逻辑地址
	public static JLabel P_Address_NewLabel;//物理地址
	
	private JScrollPane scrollPane_CPU;
	private JLabel lblNewLabel_3;
	
	private DefaultTableCellRenderer dc;//表格居中渲染器
	
	
	public MainForm() {
		this.setTitle("操作系统课程设计");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1216, 734);
		setResizable(false);//界面不可变
		getContentPane().setLayout(null);
		
		InputFileButton = new JButton("加载文件");
		InputFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InputFile();
			}
		});
		InputFileButton.setFont(new Font("微软雅黑", Font.PLAIN, 15));
		InputFileButton.setBounds(1014, 499, 150, 30);
		getContentPane().add(InputFileButton);
		
		RunProcessButton = new JButton("运    行");
		RunProcessButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RunProcess();
			}
		});
		RunProcessButton.setFont(new Font("微软雅黑", Font.PLAIN, 15));
		RunProcessButton.setBounds(1014, 539, 150, 30);
		getContentPane().add(RunProcessButton);
		RunProcessButton.setEnabled(false);
		
		AddProcessButton = new JButton("添加作业");
		AddProcessButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AddProcess();
			}
		});
		AddProcessButton.setFont(new Font("微软雅黑", Font.PLAIN, 15));
		AddProcessButton.setBounds(1014, 579, 150, 30);
		getContentPane().add(AddProcessButton);
		AddProcessButton.setEnabled(false);
		
		StopButton = new JButton("暂    停");
		StopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StopButton();
			}
		});
		StopButton.setFont(new Font("微软雅黑", Font.PLAIN, 15));
		StopButton.setBounds(1014, 619, 150, 30);
		getContentPane().add(StopButton);
		StopButton.setEnabled(false);
		
		ContinueButton = new JButton("继    续");
		ContinueButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ContinueButton();
			}
		});
		ContinueButton.setFont(new Font("微软雅黑", Font.PLAIN, 15));
		ContinueButton.setBounds(1014, 619, 150, 30);
		getContentPane().add(ContinueButton);
		ContinueButton.setVisible(false);
		
		ExitButton = new JButton("退    出");
		ExitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		ExitButton.setFont(new Font("微软雅黑", Font.PLAIN, 15));
		ExitButton.setBounds(1014, 659, 150, 30);
		getContentPane().add(ExitButton);
		
		JScrollPane scrollPane_JobList = new JScrollPane();
		scrollPane_JobList.setBounds(302, 504, 656, 185);
		getContentPane().add(scrollPane_JobList);
		
		table_JobList = new JTable();
		table_JobList.setRowHeight(20);
		dc=new DefaultTableCellRenderer();
		dc.setHorizontalAlignment(JLabel.CENTER);
		table_JobList.setDefaultRenderer(Object.class, dc);
		dtm_table_JobList = new DefaultTableModel(null,new String[] {"作业号","优先级","指令条数","创建PCB","请求时间"});
		table_JobList.setModel(dtm_table_JobList);
		scrollPane_JobList.setViewportView(table_JobList);
		
		JScrollPane scrollPane_RunProcess = new JScrollPane();
		scrollPane_RunProcess.setBounds(10, 52, 282, 637);
		getContentPane().add(scrollPane_RunProcess);
		
		textArea_RunProcess = new JTextArea();
		scrollPane_RunProcess.setViewportView(textArea_RunProcess);
		textArea_RunProcess.setLineWrap(false);
		
		JLabel lblNewLabel = new JLabel("作业后备队列");
		lblNewLabel.setBounds(604, 479, 88, 15);
		getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("运行过程");
		lblNewLabel_1.setBounds(120, 24, 54, 15);
		getContentPane().add(lblNewLabel_1);
		
		JScrollPane scrollPane_ReadyQueue = new JScrollPane();
		scrollPane_ReadyQueue.setBounds(302, 52, 101, 159);
		getContentPane().add(scrollPane_ReadyQueue);
		
		table_ReadyQueue = new JTable();
		table_ReadyQueue.setRowHeight(20);
		dc=new DefaultTableCellRenderer();
		dc.setHorizontalAlignment(JLabel.CENTER);
		table_ReadyQueue.setDefaultRenderer(Object.class, dc);
		dtm_table_ReadyQueue = new DefaultTableModel(null,new String[] {"进程ID","优先级"});
		table_ReadyQueue.setModel(dtm_table_ReadyQueue);
		scrollPane_ReadyQueue.setViewportView(table_ReadyQueue);
		
		JLabel lblNewLabel_2 = new JLabel("就绪队列");
		lblNewLabel_2.setBounds(325, 24, 54, 15);
		getContentPane().add(lblNewLabel_2);
		
		scrollPane_CPU = new JScrollPane();
		scrollPane_CPU.setBounds(968, 446, 226, 43);
		getContentPane().add(scrollPane_CPU);
		
		table_CPU = new JTable();
		table_CPU.setRowHeight(20);
		dc=new DefaultTableCellRenderer();
		dc.setHorizontalAlignment(JLabel.CENTER);
		table_CPU.setDefaultRenderer(Object.class, dc);
		dtm_table_CPU = new DefaultTableModel(null,new String[] {"ID","IR","PC"});
		table_CPU.setModel(dtm_table_CPU);
		scrollPane_CPU.setViewportView(table_CPU);
		
		lblNewLabel_3 = new JLabel("CPU信息");
		lblNewLabel_3.setBounds(1065, 421, 54, 15);
		getContentPane().add(lblNewLabel_3);
		
		JLabel lblNewLabel_4 = new JLabel("时间");
		lblNewLabel_4.setBounds(10, 20, 40, 15);
		getContentPane().add(lblNewLabel_4);
		
		textField_Time = new JTextField();
		textField_Time.setBounds(38, 12, 30, 30);
		textField_Time.setHorizontalAlignment(JTextField.CENTER);
		getContentPane().add(textField_Time);
		textField_Time.setColumns(10);
		textField_Time.setEditable(false);
		
		JScrollPane scrollPane_PCBPool = new JScrollPane();
		scrollPane_PCBPool.setBounds(302, 311, 656, 158);
		getContentPane().add(scrollPane_PCBPool);
		
		table_PCBPool = new JTable();
		table_PCBPool.setRowHeight(20);
		dc=new DefaultTableCellRenderer();
		dc.setHorizontalAlignment(JLabel.CENTER);
		table_PCBPool.setDefaultRenderer(Object.class, dc);
		dtm_table_PCBPool = new DefaultTableModel(null,new String[] {"ID","优先级","创建时间","运行时间","指令数目","状态"});
		table_PCBPool.setModel(dtm_table_PCBPool);
		scrollPane_PCBPool.setViewportView(table_PCBPool);
		
		JLabel lblNewLabel_5 = new JLabel("PCB信息");
		lblNewLabel_5.setBounds(620, 286, 54, 15);
		getContentPane().add(lblNewLabel_5);
		
		JScrollPane scrollPane_BlockQueue_1 = new JScrollPane();
		scrollPane_BlockQueue_1.setBounds(413, 52, 101, 159);
		getContentPane().add(scrollPane_BlockQueue_1);
		
		table_BlockQueue_1 = new JTable();
		table_BlockQueue_1.setRowHeight(20);
		dc=new DefaultTableCellRenderer();
		dc.setHorizontalAlignment(JLabel.CENTER);
		table_BlockQueue_1.setDefaultRenderer(Object.class, dc);
		dtm_table_BlockQueue_1 = new DefaultTableModel(null,new String[] {"ID","优先级"});
		table_BlockQueue_1.setModel(dtm_table_BlockQueue_1);
		scrollPane_BlockQueue_1.setViewportView(table_BlockQueue_1);
		
		JScrollPane scrollPane_BlockQueue_2 = new JScrollPane();
		scrollPane_BlockQueue_2.setBounds(524, 52, 101, 159);
		getContentPane().add(scrollPane_BlockQueue_2);
		
		table_BlockQueue_2 = new JTable();
		table_BlockQueue_2.setRowHeight(20);
		dc=new DefaultTableCellRenderer();
		dc.setHorizontalAlignment(JLabel.CENTER);
		table_BlockQueue_2.setDefaultRenderer(Object.class, dc);
		dtm_table_BlockQueue_2 = new DefaultTableModel(null,new String[] {"ID","优先级"});
		table_BlockQueue_2.setModel(dtm_table_BlockQueue_2);
		scrollPane_BlockQueue_2.setViewportView(table_BlockQueue_2);
		
		JScrollPane scrollPane_BlockQueue_3 = new JScrollPane();
		scrollPane_BlockQueue_3.setBounds(635, 52, 101, 159);
		getContentPane().add(scrollPane_BlockQueue_3);
		
		table_BlockQueue_3 = new JTable();
		table_BlockQueue_3.setRowHeight(20);
		dc=new DefaultTableCellRenderer();
		dc.setHorizontalAlignment(JLabel.CENTER);
		table_BlockQueue_3.setDefaultRenderer(Object.class, dc);
		dtm_table_BlockQueue_3 = new DefaultTableModel(null,new String[] {"ID","优先级"});
		table_BlockQueue_3.setModel(dtm_table_BlockQueue_3);
		scrollPane_BlockQueue_3.setViewportView(table_BlockQueue_3);
		
		JScrollPane scrollPane_BlockQueue_4 = new JScrollPane();
		scrollPane_BlockQueue_4.setBounds(746, 52, 101, 159);
		getContentPane().add(scrollPane_BlockQueue_4);
		
		table_BlockQueue_4 = new JTable();
		table_BlockQueue_4.setRowHeight(20);
		dc=new DefaultTableCellRenderer();
		dc.setHorizontalAlignment(JLabel.CENTER);
		table_BlockQueue_4.setDefaultRenderer(Object.class, dc);
		dtm_table_BlockQueue_4 = new DefaultTableModel(null,new String[] {"ID","优先级"});
		table_BlockQueue_4.setModel(dtm_table_BlockQueue_4);
		scrollPane_BlockQueue_4.setViewportView(table_BlockQueue_4);
		
		JScrollPane scrollPane_BlockQueue_5 = new JScrollPane();
		scrollPane_BlockQueue_5.setBounds(857, 52, 101, 159);
		getContentPane().add(scrollPane_BlockQueue_5);
		
		table_BlockQueue_5 = new JTable();
		table_BlockQueue_5.setRowHeight(20);
		dc=new DefaultTableCellRenderer();
		dc.setHorizontalAlignment(JLabel.CENTER);
		table_BlockQueue_5.setDefaultRenderer(Object.class, dc);
		dtm_table_BlockQueue_5 = new DefaultTableModel(null,new String[] {"ID","优先级"});
		table_BlockQueue_5.setModel(dtm_table_BlockQueue_5);
		scrollPane_BlockQueue_5.setViewportView(table_BlockQueue_5);
		
		JLabel lblNewLabel_2_1 = new JLabel("阻塞队列1");
		lblNewLabel_2_1.setBounds(437, 24, 77, 15);
		getContentPane().add(lblNewLabel_2_1);
		
		JLabel lblNewLabel_2_1_1 = new JLabel("阻塞队列2");
		lblNewLabel_2_1_1.setBounds(546, 24, 77, 15);
		getContentPane().add(lblNewLabel_2_1_1);
		
		JLabel lblNewLabel_2_1_2 = new JLabel("阻塞队列3");
		lblNewLabel_2_1_2.setBounds(659, 24, 79, 15);
		getContentPane().add(lblNewLabel_2_1_2);
		
		JLabel lblNewLabel_2_1_3 = new JLabel("阻塞队列4");
		lblNewLabel_2_1_3.setBounds(768, 24, 79, 15);
		getContentPane().add(lblNewLabel_2_1_3);
		
		JLabel lblNewLabel_2_1_4 = new JLabel("阻塞队列5");
		lblNewLabel_2_1_4.setBounds(884, 24, 74, 15);
		getContentPane().add(lblNewLabel_2_1_4);
		
		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		panel.setBounds(968, 287, 226, 124);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		pageIn_CheckBox = new JCheckBox("页表命中");
		pageIn_CheckBox.setBounds(24, 31, 90, 23);
		panel.add(pageIn_CheckBox);
		pageIn_CheckBox.setSelected(false);
		
		pageOut_CheckBox = new JCheckBox("缺页中断");
		pageOut_CheckBox.setBounds(139, 31, 81, 23);
		panel.add(pageOut_CheckBox);
		pageOut_CheckBox.setSelected(false);
		
		JLabel lblNewLabel_6 = new JLabel("MMU信息");
		lblNewLabel_6.setBounds(87, 10, 54, 15);
		panel.add(lblNewLabel_6);
		
		JLabel lblNewLabel_7 = new JLabel("逻辑地址");
		lblNewLabel_7.setBounds(31, 70, 54, 15);
		panel.add(lblNewLabel_7);
		
		JLabel lblNewLabel_7_1 = new JLabel("物理地址");
		lblNewLabel_7_1.setBounds(149, 70, 54, 15);
		panel.add(lblNewLabel_7_1);
		
		JLabel lblNewLabel_7_1_1 = new JLabel("——>");
		lblNewLabel_7_1_1.setBounds(104, 70, 63, 15);
		panel.add(lblNewLabel_7_1_1);
		
		L_Address_NewLabel = new JLabel("0");
		L_Address_NewLabel.setBounds(31, 95, 54, 15);
		panel.add(L_Address_NewLabel);
		
		P_Address_NewLabel = new JLabel("0");
		P_Address_NewLabel.setBounds(149, 95, 54, 15);
		panel.add(P_Address_NewLabel);
		
		JScrollPane scrollPane_PageTable = new JScrollPane();
		scrollPane_PageTable.setBounds(968, 52, 226, 225);
		getContentPane().add(scrollPane_PageTable);
		
		table_PageTable = new JTable();
		table_PageTable.setRowHeight(20);
		dc=new DefaultTableCellRenderer();
		dc.setHorizontalAlignment(JLabel.CENTER);
		table_PageTable.setDefaultRenderer(Object.class, dc);
		dtm_table_PageTable = new DefaultTableModel(null,new String[] {"页号","帧号","状态"});
		table_PageTable.setModel(dtm_table_PageTable);
		scrollPane_PageTable.setViewportView(table_PageTable);
		
		JLabel lblNewLabel_2_1_4_1 = new JLabel("当前进程的页表信息");
		lblNewLabel_2_1_4_1.setBounds(1031, 24, 119, 15);
		getContentPane().add(lblNewLabel_2_1_4_1);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(Color.LIGHT_GRAY));
		panel_1.setBounds(302, 246, 656, 30);
		getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		progressBar = new JProgressBar(0,16);
		progressBar.setBounds(302, 246, 656, 30);
		getContentPane().add(progressBar);
		
		JLabel lblNewLabel_8 = new JLabel("内存条");
		lblNewLabel_8.setBounds(620, 221, 72, 15);
		getContentPane().add(lblNewLabel_8);
		
		this.setVisible(true);
	}

	//继续按钮动作
	protected void ContinueButton() {
		Show_All();
		Clock_thread.STOP=false;
		ContinueButton.setVisible(false);
		StopButton.setVisible(true);
		
	}

	//暂停按钮动作
	protected void StopButton() {
		Show_All();
		Clock_thread.STOP=true;
		StopButton.setVisible(false);
		ContinueButton.setVisible(true);
	}

	//添加作业按钮动作
	protected void AddProcess() {
		Process.AddJob();
	}

	//重置继续按钮
	public static void ContinueButtonChange() {
		StopButton.setVisible(false);
		ContinueButton.setVisible(true);
	}
	
	//运行进程按钮动作
	protected void RunProcess() {
		MMU.InitMemory();//初始化内存信息
		Clock_thread clock=new Clock_thread(10);
		InputBlock_thread inputBlock=new InputBlock_thread(5);
		OutputBlock_thread outputBlock=new OutputBlock_thread(5);
		PrintBlock_thread printBlock=new PrintBlock_thread(5);
		DiskReadBlock_thread diskReadBlock=new DiskReadBlock_thread(5);
		DiskWriteBlock_thread diskWriteBlock=new DiskWriteBlock_thread(5);
		clock.start();
		jobIn.start();
		processScheduling.start();
		inputBlock.start();
		outputBlock.start();
		printBlock.start();
		diskReadBlock.start();
		diskWriteBlock.start();
		pageInterruption.start();
		StopButton.setEnabled(true);
		AddProcessButton.setEnabled(true);	
	}

	//加载文件按钮动作
	protected void InputFile() {
		JFileChooser chooser=new JFileChooser();
		chooser.showOpenDialog(this);
		File file=chooser.getSelectedFile();
		Process.FilePath=file.getParentFile().getParent();//保存当前文件的父目录的根路径
		Process.FileInputPath=file.getParent();//保存打开文件路径
		String path[]=file.getParent().split("\\\\");
		String id[]=path[path.length-1].split("");
		Process.FileOutputPath=file.getParentFile().getParent() + (Integer.valueOf(id[id.length-1])==1  ? "\\output1" : "\\output2");
		textArea_RunProcess.setText("");
		JOB.getFileInformation(file);//获取作业信息
		Show.Show_JobList(table_JobList, dtm_table_JobList);//显示作业后备队列信息
		RunProcessButton.setEnabled(true);
		inputValue = Integer.parseInt((JOptionPane.showInputDialog("请输入每秒间隔的时间/(ms)"))); 
	}
	
	//显示作业队列信息
	public static void Show_All() {
		Show.Show_CPU(table_CPU, dtm_table_CPU);//显示CPU信息
		Show.Show_JobList(table_JobList, dtm_table_JobList);//显示作业队列信息
		Show.Show_ReadyQueue(table_ReadyQueue, dtm_table_ReadyQueue);//显示就绪队列信息
		Show.Show_Time(textField_Time);//显示当前时间
		Show.Show_PCBPool(table_PCBPool, dtm_table_PCBPool);//显示PCB池的信息
		Show.Show_BlockQueue_1(table_BlockQueue_1,dtm_table_BlockQueue_1);//阻塞队列信息
		Show.Show_BlockQueue_2(table_BlockQueue_2,dtm_table_BlockQueue_2);
		Show.Show_BlockQueue_3(table_BlockQueue_3,dtm_table_BlockQueue_3);
		Show.Show_BlockQueue_4(table_BlockQueue_4,dtm_table_BlockQueue_4);
		Show.Show_BlockQueue_5(table_BlockQueue_5,dtm_table_BlockQueue_5);
		Show.Show_PageTable(table_PageTable, dtm_table_PageTable);//显示当前页表信息
		Show.Show_InteneralMemory(progressBar);//显示内存信息
	}
	
	
	//清空选项
	public static void clearCheck() {
		pageIn_CheckBox.setSelected(false);
		pageOut_CheckBox.setSelected(false);
		L_Address_NewLabel.setText("0");
		P_Address_NewLabel.setText("0");
	}
	
	//选择选项
	public static void chooseCheck(Boolean pageIn) {
		if(pageIn) {
			pageIn_CheckBox.setSelected(true);
			pageOut_CheckBox.setSelected(false);
		}
		else {
			pageIn_CheckBox.setSelected(false);
			pageOut_CheckBox.setSelected(true);
		}
	}
	
	//显示物理地址和逻辑地址
	public static void showAddressExchange(int L_Address,int P_Address) {
		L_Address_NewLabel.setText(""+L_Address);
		P_Address_NewLabel.setText(""+P_Address);

	}
}

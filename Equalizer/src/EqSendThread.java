import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class EqSendThread extends Thread{
	public static String feedback;
	Socket socketc;
	String useropt;
	BufferedReader is;
	PrintWriter os;
	String serverId;
	int port;
	int i;
	
	public EqSendThread(int serverId,String useropt,int i){
		this.useropt = useropt;
		this.i = i;
		try{
			this.serverId = Integer.toString(serverId);
			
			port = (serverId == 0)?4700:5700;
			socketc = new Socket("127.0.0.1",port);
			
			is = new BufferedReader(new InputStreamReader(socketc.getInputStream()));
			os = new PrintWriter(socketc.getOutputStream());
			
		}catch(UnknownHostException e){
			e.printStackTrace();
		}catch(IOException e){
				if(serverId == 0)
					Equalizer.s0c = 100;
				else
					Equalizer.s1c = 100;
				if(!useropt.equals("logupdate"))
					System.out.println(serverId+"号服务器掉线...\n转到另一个服务器");
				serverId = (serverId==0)?1:0;
				this.serverId = Integer.toString(serverId);
				
				port = (serverId == 0)?4700:5700;
				try{
					socketc = new Socket("127.0.0.1",port);
					is = new BufferedReader(new InputStreamReader(socketc.getInputStream()));
					os = new PrintWriter(socketc.getOutputStream());
				}catch(UnknownHostException f){
					f.printStackTrace();
				}catch(IOException f){
					f.printStackTrace();
				}
		}
	}
	public void run(){
		try{
			if(useropt.equals("logupdate")){
				LogUpdate();
				sleep(500);
				LogUpdate();
			}
				
			else{
				SendAndLog();
			}
				
			os.close();
			is.close();
			
		}catch(Exception e){
			System.out.println("run wrong");
			e.printStackTrace();
		}
	}
	
	private void SendAndLog() {
		
		os.println(useropt);
		os.flush();
		
		String[] serverDone;
		
		try {
			serverDone = is.readLine().trim().split(",");
			if(serverDone[0].equals("0"))
				Equalizer.s0c--;
			else if(serverDone[0].equals("1"))
				Equalizer.s1c--;
			
			FileWriter fw = new FileWriter(new File("src/log.txt"),true);   
		    PrintWriter pw=new PrintWriter(fw); 
		        
		    String flag = (serverDone[1].equals("1"))?"Succeed":"Fail";
		        
		    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		    pw.println(serverDone[0]+" "+useropt+" "+flag+" "+serverDone[2]+" "+df.format(new Date()));
		    pw.close () ;
		    fw.close () ;
		    
		    System.out.println("Operation"+i+"已完成，正在同步服务器...");
		    LogUpdate();
		    feedback = flag+"! Your balance now is "+serverDone[2];
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private void LogUpdate() {
		while(Equalizer.lock == 1)
			try {
				sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		Equalizer.lock = 1;
		
		os.println("logupdate");
		os.flush();
		
		String dateflag = null;
		RandomAccessFile rf = null;
		try {
			rf = new RandomAccessFile("src/log.txt", "r");
			long len = rf.length();
			long start = rf.getFilePointer();
			long nextend = start + len - 1;
			String line;
			rf.seek(nextend);
			int c = -1;
			while (nextend > start) {
				c = rf.read();
				if (c == '\n' || c == '\r') {
					line = rf.readLine();					
					
					if (line != null) {
						String[] term = line.trim().split("[\\p{Space}]+");
						String datenow = term[6]+" "+term[7];
						if(dateflag == null)
							dateflag = datenow;
						
						Date dateF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateflag);
						Date dateN = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datenow);
						long interval = (dateF.getTime() - dateN.getTime())/1000;
						if(interval <= 1.5){
							os.println(line);							
							os.flush();
							
							dateflag = datenow;
						}
						else break;
					} 
					nextend--;
				}
				nextend--;
				rf.seek(nextend);
				if (nextend == 0) {// 当文件指针退至文件开始处，读取第一行
					String head = rf.readLine();
					String[] term = head.trim().split("[\\p{Space}]+");
					String datenow = term[6]+" "+term[7];
					if(dateflag == null)
						dateflag = datenow;
					
					Date dateF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateflag);
					Date dateN = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datenow);
					long interval = (dateF.getTime() - dateN.getTime())/1000;
					if(interval <= 1.5){
						os.println(head);
						os.flush();
					}
				}
			}
			os.println("logupend");
			os.flush();
			
			Equalizer.lock = 0;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (rf != null)
					rf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		/*RandomAccessFile rf = null;
		try{
			rf = new RandomAccessFile(new File("src/log.txt"),"r");
			long len = rf.length();
			long start = rf.getFilePointer();
			long nextend = start + len -1;
			String line;
			rf.seek(nextend);
			int c = -1;
			while(nextend > start){
				c = rf.read();
				if(c=='\n' || c=='\r'){
					line = rf.readLine();
					if(line != null)
						os.println(new String(line.getBytes("ISO-8859-1"),"utf-8"));
					nextend--;
				}
				nextend--;
				rf.seek(nextend);
				if(nextend == 0){
					os.println(new String(rf.readLine().getBytes("ISO-8859-1"),"utf-8"));
				}
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				if(rf != null)
					rf.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}*/
		
		
		/*try{
			while(Equalizer.lock == 1) sleep(500);
			Equalizer.lock = 1;
			
			os.println("logupdate");
			os.flush();
			
			Scanner in = new Scanner(new File("src/log.txt"));			
			
			while(in.hasNext()){
				String logterm = null;
				logterm = in.nextLine().trim();
				//String[] logs = logterm.split("[\\p{Space}]+");			
				
				PrintWriter os = new PrintWriter(socketc.getOutputStream());
				
				if((logterm != null) && (!logterm.equals(""))){
					os.println(logterm);
					os.flush();
				}
			}	
			
			os.println("logupend");
			os.flush();
			
			Equalizer.lock = 0;
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(UnknownHostException e){
			e.printStackTrace();
		}catch(IOException e){
			System.out.println(serverId+"号服务器掉线...");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/		
	}
}

import java.io.*;
import java.net.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
 

public class ServerThread extends Thread{
	Socket socket;
	BufferedReader is;
	PrintWriter os;
	public ServerThread(Socket s){
		try{
			socket = s;
			is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			os = new PrintWriter(socket.getOutputStream());
			
		}catch(Exception e){
			System.out.println("Error:"+e);
		}
	}
	public void run(){	    
		try{	
			String get;
			while((get = is.readLine())!=null){
				get = get.trim();
				
				if(get.equals("logupdate"))
					LogUpdate();
				
				else 
					Process(get);
			}
			is.close();
		}catch(Exception e){
			System.out.println("Error:"+e);
		}
	}
	
	private void Process(String get) {
		try{
			System.out.println("---------Operation:"+get+"---------");
			if(get != null) {
				int optMon;
				int balance = 0;
				int flag=1;
				String str = null;
				String add = null;	
					
				String[] opt = get.split("[\\p{Space}]+");
				optMon = Integer.parseInt(opt[2]);
				
				while(SocketServer0.lock == 1) sleep(500);
				SocketServer0.lock = 1;
				
				BufferedReader br=new BufferedReader(new FileReader( new File("src/userlist.txt")));
				StringBuffer sb=new StringBuffer(4096);
				str = br.readLine();
					
				while((str!=null)&&(!str.equals("\n"))&&(!str.equals(""))){
					String[] user = str.trim().split("[\\p{Space}]+");
						
					if(user[0].equals(opt[0])){
						balance = Integer.parseInt(user[1]);
						if(opt[1].equals("w")){
							if(balance >= optMon) balance -= optMon;
							else {
								System.out.println(user[0]+":余额不足");
								flag = 0;
							}
						}
						else if(opt[1].equals("d"))
							balance += optMon;
						
						add = user[0]+" "+balance;
						if(flag == 1)
							System.out.println(user[0]+"'s balance changes from "+user[1]+" to "+balance);
						else
							System.out.println(user[0]+"'s balance dosn't change");
						str = br.readLine();
						continue;
					}
					sb.append(str).append("\r\n");
					str = br.readLine();
				}
					
				br.close();
				BufferedWriter bw=new BufferedWriter(new FileWriter(new File("src/userlist.txt")));
				bw.write(sb.toString());
				bw.close();	
					
				try {   
					FileWriter fw = new FileWriter(new File("src/userlist.txt"),true);   
					PrintWriter pw=new PrintWriter(fw);   
					pw.println(add);
					pw.close () ;
					fw.close () ;  
					} catch (IOException e) {   
						e.printStackTrace();   
					} 
				
				SocketServer0.lock = 0;
				
				os.println("0,"+flag+","+balance);
				os.flush();
			}
			System.out.println("-------------------------------------------\n");
		}catch(Exception e){
			System.out.println("Error:"+e);
		}
	}
	private void LogUpdate() {
		try{
			String logterm;
			while(!(logterm = is.readLine()).equals("logupend")){
				logterm = logterm.trim();
				if(logterm != null) {
					String str = null;
					String add = null;	
					
					String[] log = logterm.split("[\\p{Space}]+");
					String realMon = log[5];
					
					while(SocketServer0.lock == 1) sleep(500);
					SocketServer0.lock = 1;
					
					BufferedReader br=new BufferedReader(new FileReader( new File("src/userlist.txt")));
					StringBuffer sb=new StringBuffer(4096);
					str = br.readLine();
						
					while((str!=null)&&(!str.equals("\n"))&&(!str.equals(""))){
						String[] user = str.trim().split("[\\p{Space}]+");
							
						if(user[0].equals(log[1])){
							add = user[0]+" "+realMon;
							str = br.readLine();
							continue;
						}
						sb.append(str).append("\r\n");
						str = br.readLine();
					}
						
					br.close();
					BufferedWriter bw=new BufferedWriter(new FileWriter(new File("src/userlist.txt")));
					bw.write(sb.toString());
					bw.close();	
						
					try {   
						FileWriter fw = new FileWriter(new File("src/userlist.txt"),true);   
						PrintWriter pw=new PrintWriter(fw);   
						pw.println(add);
						pw.close () ;
						fw.close () ;  
						} catch (IOException e) {   
							e.printStackTrace();   
						} 
					
					SocketServer0.lock = 0;
				}
			}
			
		}catch(Exception e){
			System.out.println("Error:"+e);
		}
	}
}

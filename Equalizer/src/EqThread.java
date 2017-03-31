import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class EqThread extends Thread{
	Socket socket;
	
	public EqThread(Socket socket){
		this.socket = socket;
	}
	public void run(){
		String get;
		int serverId;
		BufferedReader is;
		try {
			is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			get = is.readLine();
			
			if(get.equals("faultrec0")){
				System.out.println("恢复服务器0信息...");
				Equalizer.s0c = 0;
				FaultRecover(socket);
			}
			else if(get.equals("faultrec1")){
				System.out.println("恢复服务器1信息...");
				Equalizer.s1c = 1;
				FaultRecover(socket);
			}
			else{
				LogUpdate();
				
				int i = 0;
				while(true){
					
					PrintWriter os = new PrintWriter(socket.getOutputStream());
					
					if(get!= null){
						if(Equalizer.s0c <= Equalizer.s1c){
							serverId = 0;
							Equalizer.s0c++;
						}
						else{
							serverId = 1;
							Equalizer.s1c++;
						}
						System.out.println("ClientOpration"+i+":"+get+"---go to Server"+serverId);
						
						EqSendThread a = new EqSendThread(serverId,get,i);
						a.start();
						//a.join();
						
						os.println("正在处理");
						os.flush();
						
						//a.join();
						//LogUpdate();
					}
					else {
						is.close();
						os.close();
						socket.close();
						break;
					}
					get = is.readLine();
					i++;
				}
				//LogUpdate();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static void FaultRecover(Socket socket) {
		try {
			PrintWriter os = new PrintWriter(socket.getOutputStream());

			os.println("logupdate");
			os.flush();

			Scanner in = new Scanner(new File("src/log.txt"));

			while (in.hasNext()) {
				String logterm = null;
				logterm = in.nextLine().trim();
				// String[] logs = logterm.split("[\\p{Space}]+");

				if ((logterm != null) && (!logterm.equals(""))) {
					os.println(logterm);
					os.flush();
				}
			}

			os.println("logupend");
			os.flush();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		LogUpdate();
	}
	
	private static void LogUpdate() {
		System.out.println("...用户信息一致ing...");
		EqSendThread a = new EqSendThread(0,"logupdate",1);
		EqSendThread b = new EqSendThread(1,"logupdate",1);
		a.start();
		b.start();
		try {
			a.join();
			b.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

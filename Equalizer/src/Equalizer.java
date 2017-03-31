import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Equalizer {
	public static int s0c = 0;
	public static int s1c = 0;
	public static int lock = 0;
	public static String feedback = null;
	public static void main(String args[]){
		try{
			ServerSocket server = null;
			try{
				server = new ServerSocket(7700);
			}catch(Exception e){
				System.out.println("Can not listen to:"+e);
			}
			
			Socket socket = null;			
			
			while(true){
				try{
					socket = server.accept();
					new EqThread(socket).start();
				}catch(Exception e){
					System.out.println("Error:"+e);
				}
			}
		}catch(Exception e){
			System.out.println("Error:"+e);
		}
	}
	
	private static void FaultRecover(Socket socket) {
		try{
			PrintWriter os = new PrintWriter(socket.getOutputStream());

			
			os.println("logupdate");
			os.flush();
			
			Scanner in = new Scanner(new File("src/log.txt"));			
			
			while(in.hasNext()){
				String logterm = null;
				logterm = in.nextLine().trim();
				//String[] logs = logterm.split("[\\p{Space}]+");			
								
				if((logterm != null) && (!logterm.equals(""))){
					os.println(logterm);
					os.flush();
				}
			}
			
			os.println("logupend");
			os.flush();
			
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(UnknownHostException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}

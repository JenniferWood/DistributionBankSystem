import java.io.*;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketClient {
	public static void main(String args[]){
		String send = null;
		String reply = null;		
		try{
			Socket socket = new Socket("127.0.0.1",7700);
			Scanner in = new Scanner(new File("src/useropt.txt"));			
			
			while(in.hasNext()){
				send = null;
				send = in.nextLine().trim();
				System.out.println(send);
				//String[] user = str.split("[\\p{Space}]+");				
				
				BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter os = new PrintWriter(socket.getOutputStream());
				
				os.println(send);
				os.flush();
				
				System.out.println("Server:"+is.readLine());
				
				
			}			
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(UnknownHostException e){
			e.printStackTrace();
		}catch(IOException e){
			System.out.println("Ouch!!!!!!!");
		}
		
	}
}

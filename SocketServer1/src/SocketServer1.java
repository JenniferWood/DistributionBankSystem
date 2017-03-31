import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.applet.Applet;

public class SocketServer1{  
	public static int lock = 0;
	public static void main(String args[]){
		try{
			ServerSocket server = null;
			try{
				server = new ServerSocket(5700);
			}catch(Exception e){
				System.out.println("Can not listen to:"+e);
			}
			Socket socket = null;
			while(true){
				try{
					socket = server.accept();
					new ServerThread(socket).start();
					//QueueProcess(socket);					
				}catch(Exception e){
					System.out.println("Error:"+e);
				}				
			}
			
		}catch(Exception e){
			System.out.println("Error:"+e);
		}
	}
}
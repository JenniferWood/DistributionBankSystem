import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.applet.Applet;

public class SocketServer0{ 
	public static int lock = 0;
	public static void main(String args[]){
		try{
			ServerSocket server = null;
			try{
				server = new ServerSocket(4700);
			}catch(Exception e){
				System.out.println("Can not listen to:"+e);
			}
			Socket socket = null;
			while(true){
				try{
					socket = server.accept();
					new ServerThread(socket).start();
					//QueueProcess(socket);
					TimerThread a = new TimerThread("down",5,server);
					a.start();
					a.join();
					System.out.println("服务器掉线...");
					
				}catch(Exception e){
					System.out.println("Error:"+e);					
					break;
				}				
			}
			TimerThread b = new TimerThread("on",3000,server);
			b.start();
		}catch(Exception e){
			System.out.println("Error:"+e);
		}
	}
}
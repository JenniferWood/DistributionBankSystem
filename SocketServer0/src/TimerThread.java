import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;


public class TimerThread extends Thread{
	ServerSocket server;
	String state;
	int time;
	public TimerThread(String state,int time,ServerSocket server){
		this.state = state;
		this.time = time;
		this.server = server;
	}
	public void run(){
		try {
			sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(state.equals("down")) {
			System.out.println("自定义故障");
        	try {
            	
    			server.close();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			System.out.println("Ouch!!!!");
    		}
		}
		else if(state.equals("on")){
			System.out.println("故障恢复ing...");
        	
        	try {
        		//timer.wait(100);
        		Socket socket = new Socket("127.0.0.1",7700);

        		
        		/*ServerThread a = new ServerThread(socket);
        		a.start();
        		a.join();
        		socket.close();
        		System.out.println("恢复完成");*/
        		
        		ServerSocket newserver = new ServerSocket(4700);
        		PrintWriter os = new PrintWriter(socket.getOutputStream());
        		os.println("faultrec0");
        		os.flush();
        		
        		new ServerThread(socket).start();
        		
        		Socket newsocket = null;
    			while(true){
    				newsocket = newserver.accept();
    				new ServerThread(newsocket).start();
    			}
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			System.out.println("Ouch!!!!");
    		} 
		}
	}
}

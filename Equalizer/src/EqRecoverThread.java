import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class EqRecoverThread extends Thread {
	Socket socketc;
	String useropt;
	PrintWriter os;
	int port;
	String get;
	int serverID;

	public EqRecoverThread(Socket socket,String get) {
		socketc = socket;
	}

	public void run() {

		//FaultRecover(socketc);

	}
}

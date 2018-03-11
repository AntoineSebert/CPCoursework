package customer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

import common.Protocol;
import common.ServerProperties;
import common.Utility;
import javafx.application.Application;
import javafx.stage.Stage;

public class Client /*extends Application*/ {
	static private Date connectionDate;
	static private Socket mySocket;
	static private PrintWriter out;
	static private BufferedReader in;

	public static void main(String[] args) {
		//launch(args);
		if(connectToServer()) {
			send(Protocol.clientTags.BID_SUBMIT, 100);
			disconnect();
		}
	}
	/*
	@Override
	public void start(Stage primaryStage) throws Exception {
	
	}
	*/
	static private boolean connectToServer() {
		connectionDate = new Date();
		try {
			mySocket = new Socket("localhost", ServerProperties.portNumber);
			out = new PrintWriter(mySocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		println("Connection established");
		
		// receive
		
		return true;
	}
	
	static private void disconnect() {
		try {
			out.println(Protocol.clientTags.CLOSE_CONNECTION);
			mySocket.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void send(Protocol.clientTags tag, Object data) {
		System.out.println("Sending " + tag + ' ' + data.toString() + " to server");
		out.println(tag);
		out.println(data);
	}

	public static void println(String data) {
		Utility.println("[CLIENT]> " + data);
	}
}

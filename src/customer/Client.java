package customer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

import auctioneer.Server;
import common.ServerProperties;
import javafx.application.Application;
import javafx.stage.Stage;

public class Client /*extends Application*/ {
	static private Date connectionDate;
	static private String name;
	static private Socket mySocket;
	static private PrintWriter out;
	static private BufferedReader in;

	public static void main(String[] args) {
		//launch(args);
		name = "client1";
		try {
			mySocket = new Socket("localhost", ServerProperties.portNumber);
			out = new PrintWriter(mySocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
			/*
			String inputLine, outputLine;
			while ((inputLine = in.readLine()) != null) {
				outputLine = kkp.processInput(inputLine);
				out.println(outputLine);
					if (outputLine.equals("Bye."))
						break;
			}
			*/
			if(connectToServer(new Server())) {
				sendBid(100);
				disconnect();
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	/*
	@Override
	public void start(Stage primaryStage) throws Exception {
	
	}
	*/
	static private boolean connectToServer(Server server) {
		connectionDate = new Date();
		System.out.println("Connection established on " + connectionDate);
		
		return true;
	}
	
	static private void sendBid(int bid) {
		
	}
	
	static private void disconnect() {
		try {
			mySocket.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}

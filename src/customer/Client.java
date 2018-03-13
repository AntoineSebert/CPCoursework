package customer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

import auctioneer.Server;
import common.Protocol;
import common.ServerProperties;
import common.Utility;
import javafx.application.Application;
import javafx.stage.Stage;

public class Client /*extends Application*/ {
	private static Date connectionDate;
	private static Socket mySocket;
	private static PrintWriter out;
	private static BufferedReader in;
	// static private current highest bid + client id
	static private int id;

	public static void main(String[] args) {
		//launch(args);
		if(start()) {
			send(Protocol.clientTags.BID_SUBMIT, new Object[] { 100 });
			receive();
			stop();
		}
	}
	/*
	@Override
	public void start(Stage primaryStage) throws Exception {
	
	}
	*/
	static private boolean start() {
		connectionDate = new Date();
		try {
			mySocket = new Socket("localhost", ServerProperties.portNumber);
			println("Connection established on" + connectionDate);
			out = new PrintWriter(mySocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
			return true;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	static private void stop() {
		try {
			out.println(Protocol.clientTags.CLOSE_CONNECTION);
			mySocket.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void send(Protocol.clientTags tag, Object data[]) {
		System.out.println("Sending " + tag + ' ' + data.toString() + " to server");
		out.println(tag);
		out.println(data);
	}
	
	public static void receive() {
		try {
			Protocol.serverTags tag = Protocol.serverTags.valueOf(in.readLine());
			switch(tag) {
				case SERVER_STATUS:
					println("Server status is " + in.readLine());
					break;
				case PRODUCT_DESCRIPTION:
					println("The product is " + in.readLine());
					break;
				case TIME_REMAINING:
					println("Time remaining : " + in.readLine());
					break;
				case HIGHEST_UPDATE:
					println("Highest bid is now " + in.readLine());
					break;
				case CLOSE_BIDDING:
					println("The auction has been closed");
					break;
				case NOT_HIGHER:
					println("The bid must be higher than the current highest bid");
					break;
				case WINNING_BID:
					println("The winning bid is" + in.readLine());
					break;
				case CLOSE_CONNECTION:
					println("Closing connection with the server...");
					stop();
					break;
				default:
					println("Unknown instruction :" + tag + ", value :" + in.readLine());
					break;
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	public static void println(String data) {
		Utility.println("[CLIENT]> " + data);
	}
}

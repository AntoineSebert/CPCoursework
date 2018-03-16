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

public class Client extends Application {
	// server communication
		static private Date connectionDate;
		static Socket mySocket;
		static private int id = -1;
		static private PrintWriter out;
		static private BufferedReader in;
		static private double updateInterval = 0.1;
	// current auction
		static private int currentHighestBidAmount;
		static private int currentHighestBidId;

	public static void main(String[] args) {
		//launch(args);
		if(start()) {
			while(true) {
				send(Protocol.clientTags.BID_SUBMIT, 100);
				receive();
			}
			//stopClient();
		}
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
	
	}

	static private boolean start() {
		connectionDate = new Date();
		try {
			mySocket = new Socket("localhost", ServerProperties.portNumber);
			println("Connection established on " + connectionDate);
			println("Refreshing fields every " + updateInterval + " seconds");
			out = new PrintWriter(mySocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
			return true;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static void stopClient() {
		try {
			out.println(Protocol.clientTags.CLOSE_CONNECTION);
			mySocket.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	public static void send(Protocol.clientTags tag, Object... data) {
		println("Sending " + tag + ' ' + data + " to server");
		out.println(tag);
		for(Object element : data)
			out.println(element);
	}

	public static void receive() {
		try {
			Protocol.serverTags tag = Protocol.serverTags.valueOf(in.readLine());
			switch(tag) {
				case SERVER_STATUS:
					println("Server status is " + in.readLine());
					break;
				case SEND_ID:
					if(id == -1) {
						id = Integer.parseInt(in.readLine());
						println("New id is " + id);
					}
					else
						send(Protocol.clientTags.ERROR, "Id already assigned");
					break;
				case PRODUCT_DESCRIPTION:
					println("The product is " + in.readLine());
					println('\t' + in.readLine());
					println('\t' + in.readLine());
					break;
				case TIME_REMAINING:
					println("Time remaining : " + in.readLine());
					break;
				case HIGHEST_UPDATE:
					currentHighestBidAmount = Integer.parseInt(in.readLine());
					currentHighestBidId = Integer.parseInt(in.readLine());
					println("Highest bid is " + currentHighestBidAmount + " from client " + currentHighestBidId );
					break;
				case CLOSE_BIDDING:
					println("The auction has been closed");
					cleanAuctionAttributes();
					break;
				case NOT_HIGHER:
					println("The bid must be higher than the current highest bid");
					break;
				case WINNING_BID:
					println("The winning bid is" + in.readLine());
					break;
				case CLOSE_CONNECTION:
					println("Closing connection with the server");
					cleanAuctionAttributes();
					mySocket.close();
					break;
				case ERROR:
					println("Error : " + in.readLine());
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

	public static void println(String data) { Utility.println("[CLIENT]> " + data); }
	
	public static void cleanAuctionAttributes() {
		currentHighestBidAmount = 0;
		currentHighestBidId = -1;
	}
}

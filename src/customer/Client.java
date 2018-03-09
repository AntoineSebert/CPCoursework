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

public class Client extends Application {
	private Date connectionDate;
	private String name;
	private int id;
	Socket mySocket;
	PrintWriter out;
	BufferedReader in;

	public void main(String[] args) {
		launch(args);
		if(connectToServer(new Server())) {
			sendBid(100);
			disconnect();
		}
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			mySocket = new Socket("knockknockserver.example.com", ServerProperties.portNumber);
			out = new PrintWriter(mySocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
			/*
			while ((inputLine = in.readLine()) != null) {
				outputLine = kkp.processInput(inputLine);
				out.println(outputLine);
					if (outputLine.equals("Bye."))
						break;
			}
			*/
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean connectToServer(Server server) {
		connectionDate = new Date();
		System.out.println("Connection established on " + connectionDate);
		return true;
	}
	
	private void sendBid(int bid) {
		
	}
	
	private void disconnect() {
		try {
			mySocket.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}

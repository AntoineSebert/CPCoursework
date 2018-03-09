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
	private Date currentDate;
	private String name;
	private int id;

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			Socket kkSocket = new Socket("knockknockserver.example.com", ServerProperties.portNumber);
			PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
			/*
			
			KnockKnockProtocol kkp = new KnockKnockProtocol();
			outputLine = kkp.processInput(null);
			out.println(outputLine);

			while ((inputLine = in.readLine()) != null) {
				outputLine = kkp.processInput(inputLine);
				out.println(outputLine);
					if (outputLine.equals("Bye."))
						break;
			}
			*/
		}
		catch(IOException e) {
			
		}
	}
	
	private boolean connectToServer(Server server) {
		return true;
	}
	
	private void sendBid(int bid) {
		
	}
	
	private void disconnect() {
		
	}

}

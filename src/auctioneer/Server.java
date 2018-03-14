package auctioneer;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import common.Auction;
import common.Protocol;
import common.ServerProperties;
import common.ServerStatus;
import common.Utility;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Server extends Application {
	private static String serverStartDate;
	private static ServerStatus serverStatus = ServerStatus.STOPPED;
	private static ServerSocket serverSocket;
	private static ArrayList<ClientImage> clientsQueue = new ArrayList<ClientImage>();
	private static Auction currentAuction;
	private static ArrayList<Auction> auctionHistory = new ArrayList<Auction>();
	private int statusBroadcastInterval = 1;

	public static void main(String[] args) {
		//launch(args);
		if (start()) {
			while (true) {
				// accept a connection
				// create a thread to deal with the client
				try {
					clientsQueue.add(new ClientImage(serverSocket.accept(), ClientImage.totalClients));
					broadcast(Protocol.serverTags.SERVER_STATUS, new Object[]{ serverStatus });
				}
				catch(IOException e) {
					e.printStackTrace();
				}
				//break;
			}
			//stop();
		}
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		/*
		primaryStage.setTitle("auctioneer");
		Button btn = new Button();
		btn.setText("Invoke Satan");
		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				println("Server started on " + serverStartDate);
			}
		});
		
		StackPane root = new StackPane();
		root.getChildren().add(btn);
		primaryStage.setScene(new Scene(root, 300, 250));
		primaryStage.show();
		 */
	}
	
	protected static boolean start() {
		serverStartDate = Utility.getStringDate();
		try {
			serverSocket = new ServerSocket(ServerProperties.portNumber);
			println("Server started on " + serverStartDate);
			serverStatus = ServerStatus.RUNNING;
			return true;
		}
		catch(IOException e) {
			stopServer();
			e.printStackTrace();
		}
		return false;
	}

	public static void stopServer() {
		for(ClientImage client : clientsQueue) {
			client.send(Protocol.serverTags.CLOSE_CONNECTION, null);
			println("Closing connection with client " + client.getId());
			clientsQueue.remove(client);
		}
		try {
			serverSocket.close();
			println("Server stopped");
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void broadcast(Protocol.serverTags tag, Object data[]) {
		for(ClientImage client : clientsQueue)
			client.send(tag, data);
	}
	
	public static void removeClient(ClientImage client) {
		clientsQueue.remove(client);
	}

	public static void println(String data) {
		Utility.println("[SERVER]> " + data);
	}
	
	public static void beginAuction() {
		if (currentAuction != null)
			auctionHistory.add(currentAuction);

		ZonedDateTime start = ZonedDateTime.parse("15/03/2018 17:00:00", DateTimeFormatter.ofPattern("dd MM yyyy HH:mm:ss"));
		ZonedDateTime deadline = ZonedDateTime.parse("15/03/2018 18:00:00", DateTimeFormatter.ofPattern("dd MM yyyy HH:mm:ss"));		
		currentAuction = new Auction(start, deadline,"Memories of Green", "A beautiful music from Blade Runner", 1982);
		
		broadcast(Protocol.serverTags.PRODUCT_DESCRIPTION, new Object[]{
				currentAuction.getProductName(),
				currentAuction.getProductDescription(), currentAuction.getInitialPrice()
		});
		
		broadcast(Protocol.serverTags.TIME_REMAINING, new Object[] {
				Utility.difference(Utility.getDate(), currentAuction.getDeadline())
		});
	}
	
	public static void addBid(ClientImage client, int amount) {
		if (amount < currentAuction.getHighestBid().getKey())
			clientsQueue.get(clientsQueue.indexOf(client)).send(Protocol.serverTags.ERROR, new Object[] {
					"The bid must be higher than the actual highest bid."
			});
			// send ERROR
		currentAuction.addBid(client.getId(), amount);
	}
	
}

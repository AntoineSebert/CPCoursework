package auctioneer;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.Duration;
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
	// server
		private static String serverStartDate;
		private static ServerStatus serverStatus = ServerStatus.STOPPED;
		private static ServerSocket serverSocket;
	// clients
		private static ArrayList<ClientImage> clientsQueue = new ArrayList<ClientImage>();
	// auction
		private static Auction currentAuction;
		private static ArrayList<Auction> auctionHistory = new ArrayList<Auction>();
	// other
		private int statusBroadcastInterval = 1;

	public static void main(String[] args) {
		//launch(args);
		if (start()) {
			while (true) {
				// accept a connection
				// create a thread to deal with the client
				// send to client product desc & stuff
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
		if(tag != Protocol.serverTags.NOT_HIGHER && tag != Protocol.serverTags.SEND_ID) {
			for(ClientImage client : clientsQueue)
				client.send(tag, data);
		}
	}

	public static void removeClient(ClientImage client) { clientsQueue.remove(client); }

	public static void println(String data) { Utility.println("[SERVER]> " + data); }

	public static void beginAuction() {
		if (currentAuction != null)
			auctionHistory.add(currentAuction);

		currentAuction = new Auction(
			ZonedDateTime.parse("15/03/2018 17:00:00", DateTimeFormatter.ofPattern("dd MM yyyy HH:mm:ss")),
			ZonedDateTime.parse("15/03/2018 18:00:00", DateTimeFormatter.ofPattern("dd MM yyyy HH:mm:ss")),
			"Memories of Green",
			"A beautiful music from Blade Runner",
			1982
		);

		broadcast(Protocol.serverTags.PRODUCT_DESCRIPTION, getProductInfo());

		broadcast(Protocol.serverTags.TIME_REMAINING, new Object[] {
			Utility.difference(Utility.getDate(), currentAuction.getDeadline())
		});
	}

	public static void addBid(ClientImage client, int amount) {
		if (amount < currentAuction.getHighestBid().getKey())
			clientsQueue.get(clientsQueue.indexOf(client)).send(Protocol.serverTags.ERROR, new Object[] {
					"The bid must be higher than the actual highest bid."
			});
		currentAuction.addBid(client.getId(), amount);
	}

	public static Duration getTimeRemaining() { return Utility.difference(currentAuction.getDeadline(), Utility.getDate()); }

	public static String[] getProductInfo() {
		return new String[] {
				currentAuction.getProductName(),
				currentAuction.getProductDescription(),
				currentAuction.getHighestBid().toString()
		};
	}

	public static boolean isInProgress() {
		if (currentAuction == null)
			return false;
		return !currentAuction.isDealineOver();
	}
}

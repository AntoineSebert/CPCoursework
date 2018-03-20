package auctioneer;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
		private static double broadcastUpdateInterval = 1.0;
		private final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		final static Runnable statusNotifier = new Runnable() {
			public void run() {
				if(isInProgress() && atLeastOneClientConnected())
					broadcast(Protocol.serverTags.HIGHEST_UPDATE, auctions.get(currentAuctionIndex).getHighestBid());
			}
		};
		final static ScheduledFuture<?> statusNotifierHandle = scheduler.scheduleWithFixedDelay(statusNotifier, 1, 1, TimeUnit.SECONDS);
	// clients
		private static ArrayList<ClientHandler> clientsQueue = new ArrayList<ClientHandler>();
		private static ArrayList<ClientHandler> disconnectedClients = new ArrayList<ClientHandler>();
	// auction
		private static int currentAuctionIndex = -1;
		private static ArrayList<Auction> auctions = new ArrayList<Auction>();
		private static boolean automaticProcess = true;

	public static void main(String[] args) {
		Thread.currentThread().setPriority(Thread.NORM_PRIORITY + 1);
		//launch(args);
		if (start()) {
			broadcast(Protocol.serverTags.SERVER_STATUS, serverStatus);
			addAuction();
			nextAuction();
			while (true) {
				ClientHandler worker = null;
				try {
					worker = new ClientHandler(serverSocket.accept(), ClientHandler.totalClients);
				}
				catch(IOException e) {
					e.printStackTrace();
				}
				clientsQueue.add(worker);
				worker.start();
				try {
					worker.join();
				}
				catch(InterruptedException e) {
					e.printStackTrace();
				}
				if(automaticProcess && currentAuctionIndex != -1) {
					if(auctions.get(currentAuctionIndex).isDealineOver())
						nextAuction();
				}
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
			println("Broadcasting update every " + broadcastUpdateInterval + " seconds");
			serverStatus = ServerStatus.RUNNING;
			scheduler.schedule(new Runnable() {
				public void run() { statusNotifierHandle.cancel(true); }
			}, (long)(36000 * broadcastUpdateInterval), TimeUnit.MILLISECONDS);
			return true;
		}
		catch(IOException e) {
			stopServer();
			e.printStackTrace();
		}
		return false;
	}

	public static void stopServer() {
		serverStatus = ServerStatus.STOPPED;
		for(ClientHandler client : clientsQueue) {
			client.send(Protocol.serverTags.CLOSE_CONNECTION);
			println("Closing connection with client " + client.getId());
			try {
				client.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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

	private static void broadcast(Protocol.serverTags tag, Object... data) {
		if(tag == Protocol.serverTags.NOT_HIGHER || tag == Protocol.serverTags.SEND_ID) {
			println(tag + " cannot be broadcasted");
			return;
		}
		for(ClientHandler client : clientsQueue)
			client.send(tag, data);
	}

	public static synchronized void removeClient(ClientHandler client) {
		disconnectedClients.add(client);
		clientsQueue.remove(client);
	}

	public static void println(String data) { Utility.println("[SERVER]> " + data); }

	public static void addAuction() {
		auctions.add(new Auction(
			ZonedDateTime.parse("2007-12-03T10:15:30+01:00[Europe/Paris]", DateTimeFormatter.ISO_ZONED_DATE_TIME),
			ZonedDateTime.parse("2018-12-12T10:15:30+01:00[Europe/Paris]", DateTimeFormatter.ISO_ZONED_DATE_TIME),
			"Memories of Green",
			"A beautiful music from Blade Runner",
			1982
		));
		println("New auction added to queue");
	}

	public static void nextAuction() {
		if (currentAuctionIndex < auctions.size()) {
			currentAuctionIndex++;
			broadcast(Protocol.serverTags.PRODUCT_DESCRIPTION, (Object[])getProductInfo());
			broadcast(
					Protocol.serverTags.TIME_REMAINING,
				Utility.difference(Utility.getDate(), auctions.get(currentAuctionIndex).getDeadline())
			);
		}
		else
			println("There is no next auction");
	}

	public static synchronized void addBid(ClientHandler client, int amount) {
		if(!isInProgress()) {
			println("No auction is in progress, cannot add bid from client " + client.getId());
			return;
		}
		if (amount < auctions.get(currentAuctionIndex).getHighestBid().getKey())
			clientsQueue.get(clientsQueue.indexOf(client)).send(
				Protocol.serverTags.ERROR,
				"The bid must be higher than the actual highest bid."
			);
		auctions.get(currentAuctionIndex).addBid(client.getClientId(), amount);
	}

	public static synchronized Duration getTimeRemaining() {
		if(!isInProgress()) {
			println("No auction is progress, cannot get time remaining");
			return null;
		}
		return Utility.difference(auctions.get(currentAuctionIndex).getDeadline(), Utility.getDate());
	}

	public static synchronized String[] getProductInfo() {
		if(!isInProgress()) {
			println("No auction is progress, cannot get product information");
			return null;
		}
		return new String[] {
			auctions.get(currentAuctionIndex).getProductName(),
			auctions.get(currentAuctionIndex).getProductDescription(),
			auctions.get(currentAuctionIndex).getHighestBid().getValue().toString()
		};
	}

	public static synchronized boolean isInProgress() {
		if(currentAuctionIndex == -1)
			return false;
		return !auctions.get(currentAuctionIndex).isDealineOver();
	}

	public static synchronized Map.Entry<Integer, Integer> getHighestBid() { return auctions.get(currentAuctionIndex).getHighestBid(); }

	public static void connectionsInfo() {
		// do not use thread.isAlive() because of the interval between thread.start() and thread.isAlive() == true
		for(ClientHandler client : clientsQueue) {
			println(client.getConnectionDate().toString());
			if(client.getState() == Thread.State.TERMINATED)
				println(client.getDisconnectionDate().toString());
		}
	}

	public static int connectedClients() {
		int count = 0;
		for(ClientHandler client : clientsQueue) {
			if(client.getState() != Thread.State.TERMINATED)
				count++;
		}
		return count;
	}

	public static boolean atLeastOneClientConnected() {
		for(ClientHandler client : clientsQueue) {
			if(client.getState() != Thread.State.TERMINATED)
				return true;
		}
		return false;
	}
}

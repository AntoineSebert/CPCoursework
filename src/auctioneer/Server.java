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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import common.Auction;
import common.Protocol;
import common.ServerProperties;
import common.ServerStatus;
import common.Utility;

public class Server {
	/* attributes */
		// server
			private static String serverStartDate;
			private static ServerStatus serverStatus = ServerStatus.STOPPED;
			private static ServerSocket serverSocket;
			private static double broadcastUpdateInterval = 1.0;
			private final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
			final static Runnable statusNotifier = new Runnable() {
				public /*synchronized*/ void run() {
					ServerGUI.updateTime();
					if(isInProgress() && atLeastOneClientConnected())
						broadcast(Protocol.serverTags.HIGHEST_UPDATE, auctions.get(currentAuctionIndex.get()).get().getHighestBid());
				}
			};
			final static ScheduledFuture<?> statusNotifierHandle = scheduler.scheduleWithFixedDelay(statusNotifier, 1, 1, TimeUnit.SECONDS);
		// clients
			private static ArrayList<AtomicReference<ClientHandler>> clientsQueue = new ArrayList<AtomicReference<ClientHandler>>();
			private static ArrayList<AtomicReference<ClientHandler>> disconnectedClients = new ArrayList<AtomicReference<ClientHandler>>();
			private static ReentrantLock myLock = new ReentrantLock(true);
		// auction
			private static ArrayList<AtomicReference<Auction>> auctions = new ArrayList<AtomicReference<Auction>>();
			private static AtomicInteger currentAuctionIndex = new AtomicInteger(-1);
			private static boolean automaticProcess = true;
		// graphics
			private static Thread graphicInterface;
	/* methods */
		// mains
			public static void main(String[] args) {
				Thread.currentThread().setPriority(Thread.NORM_PRIORITY + 1);
				if (start()) {
					graphicInterface = new Thread(new ServerGUI(args));
					graphicInterface.start();
					addAuction();
					nextAuction();
					while (serverStatus == ServerStatus.RUNNING) {
						if(graphicInterface.getState() == Thread.State.TERMINATED)
							stopServer();
						try {
							ClientHandler worker = new ClientHandler(serverSocket.accept(), ClientHandler.totalClients);
							clientsQueue.add(new AtomicReference<ClientHandler>(worker));
							worker.start();
						}
						catch(IOException e) {
							if(!e.getMessage().equals("socket closed") && !e.getMessage().equals("socket is closed"))
								e.printStackTrace();
						}
						if(automaticProcess && currentAuctionIndex.get() != -1)
							if(auctions.get(currentAuctionIndex.get()).get().isDealineOver())
								nextAuction();
					}
				}
				println("finishing...");
			}
		// connection
			private static boolean start() {
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
				// change status
					serverStatus = ServerStatus.STOPPED;
				// close graphic interface
					try {
						println("joining...");
						println(graphicInterface.getState().toString());
						graphicInterface.join();
						println("joined");
					}
					catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					finally {
						// close server socket
						try {
							serverSocket.close();
						}
						catch(IOException e) {
							e.printStackTrace();
						}
						finally {
							// close client handlers
							for(AtomicReference<ClientHandler> client : clientsQueue) {
								println("Closing connection with client " + client.get().getId());
								client.get().terminate();
								try {
									client.get().join();
								}
								catch(InterruptedException e) {
									e.printStackTrace();
								}
							}
							println("Server stopped");
						}
					}
			}
			public static void broadcast(Protocol.serverTags tag, Object... data) {
				if(tag == Protocol.serverTags.NOT_HIGHER || tag == Protocol.serverTags.SEND_ID) {
					println(tag + " cannot be broadcasted");
					return;
				}
				for(AtomicReference<ClientHandler> client : clientsQueue)
					client.get().send(tag, data);
			}
		// modifiers
			public static /*synchronized*/ void removeClient(ClientHandler myClient) {
				disconnectedClients.add(new AtomicReference<ClientHandler>(myClient));
				for(AtomicReference<ClientHandler> client : clientsQueue) {
					if(client.get() == myClient) {
						clientsQueue.remove(client);
						break;
					}
				}
			}
			private static void addAuction() {
				/*
				try {
					myLock.lock();
				*/
					auctions.add(
						new AtomicReference<Auction>(
								new Auction(
									ZonedDateTime.parse("2017-12-03T10:15:30+01:00[Europe/Paris]", DateTimeFormatter.ISO_ZONED_DATE_TIME),
									ZonedDateTime.parse("2018-12-12T10:15:30+01:00[Europe/Paris]", DateTimeFormatter.ISO_ZONED_DATE_TIME),
									"Memories of Green",
									"A beautiful music from Blade Runner",
									1982
								)
						)
					);
						println("New auction added to queue");
				/*
				}
				finally {
					myLock.unlock();
				}
				*/
			}
			private static void nextAuction() {
				/*
				try {
					myLock.lock();
					*/
					if (currentAuctionIndex.get() < auctions.size()) {
						currentAuctionIndex.getAndIncrement();
						broadcast(Protocol.serverTags.PRODUCT_DESCRIPTION, (Object[])getProductInfo());
						broadcast(
								Protocol.serverTags.TIME_REMAINING,
							Utility.difference(Utility.getDate(), auctions.get(currentAuctionIndex.get()).get().getDeadline())
						);
					}
					else
						println("There is no next auction");
				/*
				}
				finally {
					myLock.unlock();
				}
				*/
			}
			public static /*synchronized*/ void addBid(ClientHandler client, int amount) {
				if(!isInProgress()) {
					println("No auction is in progress, cannot add bid from client " + client.getId());
					return;
				}
				if (amount < auctions.get(currentAuctionIndex.get()).get().getHighestBid().getKey())
					client.send(Protocol.serverTags.ERROR, "The bid must be higher than the actual highest bid.");
				else
					auctions.get(currentAuctionIndex.get()).get().addBid(client.getClientId(), amount);
			}
		// getters
			public static Duration getTimeRemaining() {
				if(!isInProgress()) {
					println("No auction is progress, cannot get time remaining");
					return null;
				}
				return Utility.difference(auctions.get(currentAuctionIndex.get()).get().getDeadline(), Utility.getDate());
			}
			public static String[] getProductInfo() {
				if(!isInProgress()) {
					println("No auction is progress, cannot get product information");
					return null;
				}
				return new String[] {
					auctions.get(currentAuctionIndex.get()).get().getProductName(),
					auctions.get(currentAuctionIndex.get()).get().getProductDescription(),
					auctions.get(currentAuctionIndex.get()).get().getHighestBid().getValue().toString()
				};
			}
			public static int getConnectedClientsCount() {
				int count = 0;
				for(AtomicReference<ClientHandler> client : clientsQueue)
					if(client.get().getState() != Thread.State.TERMINATED)
						count++;
				return count;
			}
			public static ArrayList<AtomicReference<ClientHandler>> getConnectedClients() {
				ArrayList<AtomicReference<ClientHandler>> connectedClients = new ArrayList<AtomicReference<ClientHandler>>();
				for(AtomicReference<ClientHandler> client : clientsQueue)
					if(client.get().getState() != Thread.State.TERMINATED)
						connectedClients.add(client);
				return connectedClients;
			}
			public static /*synchronized*/ Map.Entry<Integer, Integer> getHighestBid() {
				return auctions.get(currentAuctionIndex.get()).get().getHighestBid();
			}
			public static ServerStatus getStatus() { return serverStatus; }
		// accessors
			public static boolean isInProgress() {
				return (currentAuctionIndex.get() == -1 ? false : !auctions.get(currentAuctionIndex.get()).get().isDealineOver());
			}
			private static boolean atLeastOneClientConnected() {
				for(AtomicReference<ClientHandler> client : clientsQueue)
					if(client.get().getState() != Thread.State.TERMINATED)
						return true;
				return false;
			}
		// display
			private static void println(String data) { Utility.println("[SERVER]> " + data); }
			private static void connectionsInfo() {
				// do not use thread.isAlive() because of the interval between thread.start() and thread.isAlive() == true
				for(AtomicReference<ClientHandler> client : clientsQueue) {
					println(client.get().getConnectionDate().toString());
					if(client.get().getState() == Thread.State.TERMINATED)
						println('\t' + client.get().getDisconnectionDate().toString());
				}
			}
}
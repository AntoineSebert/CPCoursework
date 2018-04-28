package auctioneer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Date;
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
/*
 * @author Anthony Sébert
 * Set a connection point, create and manage ClientHandler objects and Auction objects,
 * update and hold a ServerGUI object, regularly notify clients about time remaining and current highest bid.
 */
public class Server {
	/* attributes */
		// server
			private static String serverStartDate;
			private static ServerStatus serverStatus = ServerStatus.STOPPED;
			private static ServerSocket serverSocket;
		// background task
			private static double broadcastUpdateInterval = 1.0;
			private final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
			final static Runnable statusNotifier = new Runnable() {
				public void run() {
					ServerGUI.updateTime();
					// broadcast time and highest bid to clients
					if(isInProgress() && atLeastOneClientConnected()) {
						broadcast(
							Protocol.serverTags.HIGHEST_UPDATE,
							auctions.get(currentAuctionIndex.get()).get().getHighestBid().getKey(),
							auctions.get(currentAuctionIndex.get()).get().getHighestBid().getValue()
						);
						broadcast(Protocol.serverTags.TIME_REMAINING, getTimeRemaining());
					}
				}
			};
			// running one second after initialization, calls runnable statusNotifier every 1 second (see below)
			final static ScheduledFuture<?> statusNotifierHandle = scheduler.scheduleWithFixedDelay(
				statusNotifier, 1, (long)broadcastUpdateInterval, TimeUnit.SECONDS
			);
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
		// main
			public static void main(String[] args) {
				// server has highest priority than ClientHandlers
				Thread.currentThread().setPriority(Thread.NORM_PRIORITY + 1);
				if (start()) {
					graphicInterface = new Thread(new ServerGUI(args));
					graphicInterface.start();
					// sample auction
					addAuction(
						Utility.stringToDate("2017/12/03 05:52:23"),
						Utility.stringToDate("2018/12/03 05:52:23"),
						"Memories of Green",
						"A beautiful music from Blade Runner",
						1982
					);
					nextAuction();
					while (serverStatus == ServerStatus.RUNNING) {
						if(graphicInterface.getState() == Thread.State.TERMINATED)
							stopServer();
						// wait for incoming connections
						try {
							ClientHandler worker = new ClientHandler(serverSocket.accept(), ClientHandler.totalClients);
							clientsQueue.add(new AtomicReference<ClientHandler>(worker));
							worker.start();
						}
						catch(IOException e) {
							if(!e.getMessage().equals("socket closed") && !e.getMessage().equals("socket is closed"))
								e.printStackTrace();
						}
						// go to next auction is the current one is terminated
						if(automaticProcess && currentAuctionIndex.get() != -1)
							if(auctions.get(currentAuctionIndex.get()).get().isDealineOver())
								nextAuction();
					}
				}
				println("finishing...");
			}
		// connection
			public static boolean start() {
				serverStartDate = Utility.getStringDate();
				// create connection point, start the scheduler; if an exception occur the server is stopped
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
			public static boolean stopServer() {
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
				return true;
			}
			public static void broadcast(Protocol.serverTags tag, Object... data) {
				// check if tag can be broadcasted
				if(tag == Protocol.serverTags.NOT_HIGHER || tag == Protocol.serverTags.SEND_ID) {
					println(tag + " cannot be broadcasted");
					return;
				}
				for(AtomicReference<ClientHandler> client : clientsQueue)
					client.get().send(tag, data);
			}
		// modifiers
			public static void removeClient(ClientHandler myClient) {
				disconnectedClients.add(new AtomicReference<ClientHandler>(myClient));
				for(AtomicReference<ClientHandler> client : clientsQueue) {
					if(client.get() == myClient) {
						clientsQueue.remove(client);
						break;
					}
				}
			}
			public static void addAuction(Date begin, Date end, String name, String description, int startPrice) {
				auctions.add(new AtomicReference<Auction>(new Auction(begin, end, name, description, startPrice)));
				println("New auction added to queue");
			}
			private static void nextAuction() {
				// check if there is another auction to run, in that case, broadcast auction informations
				if (currentAuctionIndex.get() < auctions.size()) {
					currentAuctionIndex.getAndIncrement();
					broadcast(Protocol.serverTags.PRODUCT_DESCRIPTION, (Object[])getProductInfo());
					broadcast(
						Protocol.serverTags.TIME_REMAINING,
						Utility.difference(new Date(), auctions.get(currentAuctionIndex.get()).get().getDeadline(), TimeUnit.SECONDS)
					);
				}
				else
					println("There is no next auction");
				broadcast(Protocol.serverTags.WINNING_BID, getHighestBid().getKey(), getHighestBid().getValue());
			}
			public static void addBid(ClientHandler client, int amount) {
				if(!isInProgress()) {
					println("No auction is in progress, cannot add bid from client " + client.getId());
					return;
				}
				// if the bid is not higher, send an error message to the client, otherwise update
				if(amount < auctions.get(currentAuctionIndex.get()).get().getHighestBid().getKey())
					client.send(Protocol.serverTags.ERROR, "The bid must be higher than the actual highest bid.");
				else {
					auctions.get(currentAuctionIndex.get()).get().addBid(client.getClientId(), amount);
					ServerGUI.updateHighestBid();
					Server.broadcast(Protocol.serverTags.HIGHEST_UPDATE, amount, client.getId());
				}
			}
		// getters
			public static long getTimeRemaining() {
				if(!isInProgress()) {
					println("No auction is progress, cannot get time remaining");
					return (long)0.0;
				}
				return Utility.difference(auctions.get(currentAuctionIndex.get()).get().getDeadline(), new Date(), TimeUnit.SECONDS);
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
			public static int getConnectedClientsCount() { return clientsQueue.size(); }
			public static ArrayList<AtomicReference<ClientHandler>> getConnectedClients() { return clientsQueue; }
			public static Map.Entry<Integer, Integer> getHighestBid() {
				return auctions.get(currentAuctionIndex.get()).get().getHighestBid();
			}
			public static ServerStatus getStatus() { return serverStatus; }
		// accessors
			public static boolean isInProgress() {
				return (currentAuctionIndex.get() == -1 ? false : !auctions.get(currentAuctionIndex.get()).get().isDealineOver());
			}
			private static boolean atLeastOneClientConnected() { return clientsQueue.isEmpty(); }
		// display
			private static void println(String data) {
				Utility.println("[SERVER]> " + data);
				ServerGUI.printConsole("[SERVER_UI]> " + data);
			}
			private static void connectionsInfo() {
				// do not use thread.isAlive() because of the interval between thread.start() and thread.isAlive() == true
				for(AtomicReference<ClientHandler> client : clientsQueue) {
					println(client.get().getConnectionDate().toString());
					if(client.get().getState() == Thread.State.TERMINATED)
						println('\t' + client.get().getDisconnectionDate().toString());
				}
			}
}
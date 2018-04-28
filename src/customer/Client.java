package customer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import common.Protocol;
import common.ServerProperties;
import common.Utility;
import javafx.application.Application;
import javafx.stage.Stage;
/*
 * @author Anthony Sébert
 * Connect to a connection point, send and receive information, update and hold a ClientGUI object.
 */
public class Client extends Application {
	/* attributes */
		// client
			static private boolean mainCondition = true;
			static private ClientGUI ui;
		// server communication
			static private Date connectionDate;
			static private Socket mySocket;
			static private int id = -1;
			static private PrintWriter out;
			static private BufferedReader in;
			static private double updateInterval = 0.1;
		// current auction
			static private int currentHighestBidAmount;
			static private int currentHighestBidId;
			static private long timeRemaining = -1;
		// graphics
			static private Thread graphicInterface;
		// background task
			private static double connectionAttemptInterval = 1.0, timeUpdateInterval = 1.0;
			private final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
			final static Runnable timeUpdate = new Runnable() {
				public void run() {
					// update GUI time and time remaining
					ui.updateTime(timeRemaining);
				}
			};
			final static Runnable connectionAttempt = new Runnable() {
				public void run() {
					// if the server is not responding, attempt a connection every one second
					if(mySocket == null) {
						println("Connection attempt");
						try {
							mySocket = new Socket(ServerProperties.serverAddress, ServerProperties.portNumber);
							println("Connection established on " + connectionDate);
							println("Refreshing fields every " + updateInterval + " seconds");
							out = new PrintWriter(mySocket.getOutputStream(), true);
							in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
						}
						catch(IOException e) {
							if(e.getClass() != ConnectException.class)
								e.printStackTrace();
						}
					}
				}
			};
			// running one second after initialization, calls runnables every 1 second (see above)
			final static ScheduledFuture<?> connectionAttemptHandler = scheduler.scheduleWithFixedDelay(
				connectionAttempt,
				1,
				(long) connectionAttemptInterval,
				TimeUnit.SECONDS
			);
			final static ScheduledFuture<?> timeUpdateHandler = scheduler.scheduleWithFixedDelay(
					timeUpdate,
				1,
				(long) timeUpdateInterval,
				TimeUnit.SECONDS
			);
	/* members */
		// main
			public static void main(String[] args) {
				if(start()) {
					ui  = new ClientGUI(args);
					graphicInterface = new Thread(ui);
					graphicInterface.start();
					//send(Protocol.clientTags.BID_SUBMIT, 100);
					while(mainCondition) {
						if(mySocket != null) {
							try {
								Thread.sleep(1000);
							}
							catch(InterruptedException e) {
								e.printStackTrace();
							}
							receive();
						}
					}
					stopClient();
				}
			}
		// graphic display
			@Override
			public void start(Stage primaryStage) throws Exception {
				// start the schedulers
				scheduler.schedule(new Runnable() {
					public void run() { connectionAttemptHandler.cancel(true); }
				}, (long)(36000 * connectionAttemptInterval), TimeUnit.MILLISECONDS);
				scheduler.schedule(new Runnable() {
					public void run() { timeUpdateHandler.cancel(true); }
				}, (long)(36000 * timeUpdateInterval), TimeUnit.MILLISECONDS);
			}
		// connection
			static private boolean start() {
				connectionDate = new Date();
				return true;
			}
			private static void stopClient() {
				try {
					graphicInterface.join();
				}
				catch(InterruptedException e1) {
					e1.printStackTrace();
				}
				try {
					out.println(Protocol.clientTags.CLOSE_CONNECTION);
					mySocket.close();
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
			public static void send(Protocol.clientTags tag, Object... data) {
				println("Sending " + tag + " to server :");
				out.println(tag);

				for(Object element : data) {
					println("\t" + element);
					out.println(element);
				}
				Thread.yield();
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
								println("New id is : " + Integer.toString(id));
							}
							else
								send(Protocol.clientTags.ERROR, "Id already assigned");
							break;
						case PRODUCT_DESCRIPTION:
							println("The product is :");
							String name = in.readLine(), description = in.readLine();
							println('\t' + name + "\n\t\t\t" + description + "\n\t\t\t" + in.readLine() + '\n');
							ui.updateProductInfo(name, description);
							break;
						case TIME_REMAINING:
							timeRemaining = Long.parseLong(in.readLine());
							ui.updateTime(timeRemaining);
							println("Time remaining : " + timeRemaining);
							break;
						case HIGHEST_UPDATE:
							currentHighestBidAmount = Integer.parseInt(in.readLine());
							currentHighestBidId = Integer.parseInt(in.readLine());
							ui.updateHighestBid(currentHighestBidId, currentHighestBidAmount);
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
							disconnect();
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
			public static void disconnect() {
				cleanAuctionAttributes();
				if(mySocket != null) {
					if(!mySocket.isClosed()) {
						try {
							mySocket.close();
						}
						catch(IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		// modifiers
			public static void cleanAuctionAttributes() {
				currentHighestBidAmount = 0;
				currentHighestBidId = -1;
			}
		// display
			public static void println(String data) { Utility.println("[CLIENT]> " + data); }
}

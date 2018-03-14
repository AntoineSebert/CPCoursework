package auctioneer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Date;

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

	private static Date startDate;
	private static int statusBroadcastInterval = 1;
	private static String product;
	private static Date deadline;

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
		serverStartDate = Utility.getDate();
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
	
	public static void beginAuction(String productName, int initialPrice) {
		
	}
}

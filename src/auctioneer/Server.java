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

public class Server /*extends Application*/ {
	private static Date deadline;
	private static String serverStartDate;
	private static int statusBroadcastinterval;
	private static String product = "test";
	static ArrayList<ClientImage> clientsQueue = new ArrayList<ClientImage>(); // order clients by bid, but check if no bids
	private Date startDate = null;
	static ServerStatus serverStatus = ServerStatus.STOPPED;
	static ServerSocket serverSocket = null;

	public static void main(String[] args) {
		//launch(args);
		statusBroadcastinterval = 1;
		if (startServer()) {
			while (true) {
				// accept a connection
				// create a thread to deal with the client
				try {
					ClientImage newClient;
					newClient = new ClientImage(serverSocket.accept(), clientsQueue.size());
					clientsQueue.add(newClient);
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				broadcast(Protocol.serverTags.SERVER_STATUS, serverStatus);
				clientsQueue.get(0).send(Protocol.serverTags.PRODUCT_DESCRIPTION, product);
				break;
			}
			stopServer();
		}
	}
/*
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("auctioneer");
		Button btn = new Button();
		btn.setText("Invoke Satan");
		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Server started on " + serverStartDate);
			}
		});
		
		StackPane root = new StackPane();
		root.getChildren().add(btn);
		primaryStage.setScene(new Scene(root, 300, 250));
		primaryStage.show();
	}
*/
	public static boolean startServer() {
		serverStartDate = Utility.getDate();
		try {
			serverSocket = new ServerSocket(ServerProperties.portNumber);
			System.out.println("Server started on " + serverStartDate);
			return true;
		}
		catch(IOException e) {
			stopServer();
			e.printStackTrace();
		}
		return false;
	}

	public static void stopServer() {
		try {
			for(ClientImage client : clientsQueue) {
				// envoyer à client message déconnexion
				// déconnecter client
				client.toString();
			}
			serverSocket.close();
			System.out.println("Server stopped");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void broadcast(Protocol.serverTags tag, Object data) {
		for(ClientImage client : clientsQueue)
			client.send(tag, data);
	}
	
	public static void removeClient(int index) {
		clientsQueue.remove(index);
	}
}

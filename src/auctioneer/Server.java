package auctioneer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
	static PrintWriter out;
	static BufferedReader in;
	static ArrayList<ClientImage> clientsQueue = new ArrayList<ClientImage>(); // order clients by bid, but check if no bids
	Date startDate = new Date();
	Date currentDate = new Date();
	static ServerStatus serverStatus = ServerStatus.STOPPED;
	static ServerSocket serverSocket = null;

	public static void main(String[] args) {
		//launch(args);
		statusBroadcastinterval = 1;

		if (startServer()) {
			clientsQueue.add(new ClientImage());
			broadcast(Protocol.serverTags.SERVER_STATUS, serverStatus);
			try {
				clientsQueue.get(0).socket = serverSocket.accept();
				out = new PrintWriter(clientsQueue.get(0).socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(clientsQueue.get(0).socket.getInputStream()));
				clientsQueue.get(0).send(Protocol.serverTags.PRODUCT_DESCRIPTION, product);
				clientsQueue.get(0).send(Protocol.serverTags.TIME_REMAINING, deadline);
			}
			catch (IOException e) {
				e.printStackTrace();
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
			System.out.println("Server started");
			return true;
		}
		catch(IOException e) {
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void broadcast(Protocol.serverTags tag, Object data) {
		for(ClientImage client : clientsQueue)
			System.out.println("Sending " + tag + data.toString() + " to " + client.toString());
	}
}

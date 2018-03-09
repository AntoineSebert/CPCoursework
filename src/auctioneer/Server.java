package auctioneer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Date;

import common.Protocol;
import common.ServerProperties;
import common.Utility;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Server extends Application implements AbstractServer {
	private Date deadline;
	private String serverStartDate;
	private int statusBroadcastinterval;
	private ServerSocket serverSocket;
	private String product = "test";

	public void main(String[] args) {
		launch(args);
		
		statusBroadcastinterval = 1;
		startServer();
		
		for (int i = 0; i < 20; i++) {
			broadcast(Protocol.serverTags.SERVER_STATUS, serverStatus);
			send(clientsQueue.iterator().next(), Protocol.serverTags.PRODUCT_DESCRIPTION, product);
			send(clientsQueue.iterator().next(), Protocol.serverTags.TIME_REMAINING, deadline);
		}
		
		stopServer();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("auctioneer");
		Button btn = new Button();
		btn.setText("Invoke Satan");
		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("Gort ! Klaatu barada nikto !");
			}
		});
		
		StackPane root = new StackPane();
		root.getChildren().add(btn);
		primaryStage.setScene(new Scene(root, 300, 250));
		primaryStage.show();
	}

	@Override
	public void startServer() {
		serverStartDate = Utility.getDate();
		try {
			serverSocket = new ServerSocket(ServerProperties.portNumber);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stopServer() {
		try {
			for(ClientImage client : clientsQueue) {
				// envoyer à client message déconnexion
				// déconnecter client
				client.toString();
			}
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void broadcast(Protocol.serverTags tag, Object data) {
		for(ClientImage client : clientsQueue)
			System.out.println("Sending " + tag + data.toString() + " to " + client.toString());
	}
	
	private void send(ClientImage client, Protocol.serverTags tag, Object data) {
		System.out.println("Sending " + tag + data.toString() + " to " + client.toString());
	}
	
	private void receive() {
		Protocol.clientTags test = Protocol.clientTags.BID_SUBMIT;
		switch(test) {
			case BID_SUBMIT:
				System.out.println(test.toString() + " received");
				break;
			default:
				break;
		}
	}

}

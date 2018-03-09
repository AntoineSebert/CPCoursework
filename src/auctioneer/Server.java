package auctioneer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Date;

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

	public void main(String[] args) {
		launch(args);
		
		startServer();
		
		for (int i = 0; i < 20; i++) {
			broadcastProduct();
			broadcastStatus();
			broadcastWinningBid();
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
				// envoyer � client message d�connexion
				// d�connecter client
				client.toString();
			}
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void broadcast(String tag, Object data) {
		for(ClientImage client : clientsQueue)
			System.out.println("Sending " + tag + data.toString() + " to " + client.toString());
	}
	
	private void send(ClientImage client, String tag, Object data) {
		System.out.println("Sending " + tag + data.toString() + " to " + client.toString());
	}
	

}

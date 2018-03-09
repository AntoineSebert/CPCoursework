package auctioneer;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import common.ServerProperties;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Server extends Application implements AbstractServer {
	private Date deadline;
	private Date serverStartDate;
	private int statusBroadcastinterval;
	private ServerSocket serverSocket;

	public void main(String[] args) {
		ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
		System.out.println("DATETIME = " + utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
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
		try {
			serverSocket = new ServerSocket(ServerProperties.portNumber);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stopServer() {
		for(ClientImage client : clientsQueue) {
			// envoyer à client message déconnexion
			// déconnecter client
			client.toString();
		}
	}
	
	private void broadcastProduct() {
		
	}
	
	private void broadcastStatus() {
		
	}
	
	private void broadcastWinningBid() {
		for(ClientImage client : clientsQueue) {
			client.toString();
		}
	}
	
	private void sendStatus(ClientImage receiver) {
		System.out.println(serverStatus);
		receiver.toString();
	}

}

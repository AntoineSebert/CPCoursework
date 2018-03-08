package auctioneer;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;

import common.ServerStatus;
import customer.Client;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Server extends Application {
	private HashSet<Client> clients; // order clients by bid, but check if no bids
	private Date deadline;
	private Date currentDate;
	private int statusBroadcastinterval;
	private ServerStatus serverStatus;

	public static void main(String[] args) {
		ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
		System.out.println("DATETIME = " + utc.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		launch(args);
		
		// program loop
		
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
	
	private void broadcastProduct() {
		
	}
	
	private void broadcastStatus() {
		
	}
	
	private void broadcastWinningBid() {
		
	}
	
	private void sendStatus(Client receiver) {
		
	}

}

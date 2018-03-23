package auctioneer;

import common.Utility;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ServerGUI extends Application implements Runnable {
	/* attributes */
		private String[] args;
	/* members */
		// constructor
			public ServerGUI(String[] args) {
				this.args = args;
			}
		// thread
			@Override
			public void run() {
				launch(args);
			}
		// javafx
			@Override
			public void start(Stage primaryStage) throws Exception {
				primaryStage.setTitle("auctioneer");
				Button btn = new Button();
				btn.setText("Invoke Satan");
				btn.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						println("Gort ! Klaatu barada nikto !");
					}
				});
				StackPane root = new StackPane();
				root.getChildren().add(btn);
				primaryStage.setScene(new Scene(root, 300, 250));
				primaryStage.show();
			}
		// display
			private static void println(String data) { Utility.println("[SERVER_UI]> " + data); }
}

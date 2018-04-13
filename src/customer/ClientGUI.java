package customer;

import common.Protocol;
import common.Utility;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class ClientGUI extends Application implements Runnable {
	/* attributes */
		// application
			private String[] args;
		// graphic elements
			private static BorderPane root;
			private static VBox bidPanel = new VBox();
	/* members */
		// constructor
			public ClientGUI() {}
			public ClientGUI(String[] args) { this.args = args; }
		// thread
			@Override
			public void run() { launch(args); }
		// javafx
			@Override
			public void start(Stage primaryStage) throws Exception {
				primaryStage.setTitle("client");
				root = new BorderPane();
				// TOP
					root.setTop(Utility.createText("Time remaining: -1", 300, 15.0, TextAlignment.LEFT));
				// LEFT
					bidPanel.getChildren().addAll(
						Utility.createEditableTextField("Bid"),
						Utility.createButton("Send bid", new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								String input = ((TextField)bidPanel.getChildren().get(0)).getText();
								if(input != "") {
									Client.send(Protocol.clientTags.BID_SUBMIT, input);
									((TextField)bidPanel.getChildren().get(0)).clear();
								}
							}
						})
					);
					root.setLeft(bidPanel);
				// CENTER
					// product info
				// RIGHT
					// value of the highest bid received to date
				// BOTTOM
					// disconnect
				primaryStage.setScene(new Scene(root, 1200, 600));
				primaryStage.show();
			}
			@Override
			public void stop() {
				println("Closing user interface...");
				try {
					super.stop();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				println("User interface closed");
			}
		// client-side events
			public static void updateTimeRemaining(long timeRemaining) {
				((Text)root.getTop()).setText(
					"time remaining: hours: " + String.valueOf(Math.floor(timeRemaining / 360))
					+ " minutes: " + String.valueOf(Math.floor(timeRemaining / 60))
					+ " seconds: " + String.valueOf(Math.floor(timeRemaining % 60))
				);
			}
		// display
			private static void println(String data) {
				Utility.println("[CLIENT_UI]> " + data);
			}
}

package customer;

import auctioneer.Server;
import common.Protocol;
import common.ServerStatus;
import common.Utility;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
/*
 * @author Anthony Sébert
 * A user interface for the Server class, composed of various javaFX components.
 * Displays current time, the time remaining, the current highest bid and the current product description,
 * allows user to create and send a new bid, and disconnect.
 */
public class ClientGUI extends Application implements Runnable {
	/* attributes */
		// application
			private static String[] args;
		// graphic elements
			private static BorderPane root;
			private static VBox bidPanel = new VBox();
			private static VBox highestBidPanel = new VBox();
			private static HBox topbar = new HBox();
			private static TilePane actionsPanel = new TilePane();
			private static VBox productPanel = new VBox();
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
					topbar.getChildren().addAll(
						Utility.createText("current time: " + Utility.getStringDate() + '\n', 300, 15.0, TextAlignment.LEFT),
						Utility.createText(
							" hours: " + String.valueOf(Math.floor(Server.getTimeRemaining() / 360))
							+ " minutes: " + String.valueOf(Math.floor(Server.getTimeRemaining() / 60))
							+ " seconds: " + String.valueOf(Math.floor(Server.getTimeRemaining() % 60)) + '\n',
							350, 15.0, TextAlignment.LEFT
						)
					);
					root.setTop(topbar);
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
					productPanel.getChildren().addAll(
						Utility.createText(
							"product name: " + ""
							+ "\nproduct description: " + "" + '\n',
							350, 15.0, TextAlignment.LEFT
						)
					);
					root.setCenter(productPanel);
				// RIGHT
					highestBidPanel.getChildren().addAll(
						Utility.createText(
							"Highest bid\n" +
							"Client id: " + ""
							+ "\namount: " + "" + '\n',
							350, 15.0, TextAlignment.RIGHT
						)
					);
					root.setRight(highestBidPanel);
				// BOTTOM
					actionsPanel.getChildren().addAll(
						Utility.createButton("Disconnect", new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								Client.disconnect();
							}
						})
					);
					root.setBottom(actionsPanel);
				primaryStage.setScene(new Scene(root, 1200, 600));
				primaryStage.show();
				println(String.valueOf(topbar.getChildren().isEmpty()));
			}
			@Override
			public void stop() {
				println("Closing user interface...");
				try {
					super.stop();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				println("User interface closed");
			}
		// client-side events
			public void updateTime(long timeRemaining) {
				((Text)topbar.getChildren().get(0)).setText("current time: " + Utility.getStringDate() + '\n');
				((Text)topbar.getChildren().get(1)).setText(
					"time remaining: hours: " + String.valueOf(Math.floor(timeRemaining / 360))
					+ " minutes: " + String.valueOf(Math.floor(timeRemaining / 60))
					+ " seconds: " + String.valueOf(Math.floor(timeRemaining % 60))
					+ '\n'
				);
			}
			public void updateProductInfo(String name, String description) {
				((Text)actionsPanel.getChildren().get(0)).setText(
					"product name: " + name + "\nproduct description: " + description + "\n"
				);
			}
			public void updateHighestBid(int clientIndex, int bid) {
				((Text)highestBidPanel.getChildren().get(0)).setText(
					"Highest bid" + "Client id: " + String.valueOf(clientIndex) + "\namount: " + String.valueOf(bid) + '\n'
				);
			}
		// display
			private void println(String data) {
				Utility.println("[CLIENT_UI]> " + data);
			}
}

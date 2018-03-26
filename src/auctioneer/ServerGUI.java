package auctioneer;

import common.Protocol;
import common.ServerStatus;
import common.Utility;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ServerGUI extends Application implements Runnable {
	/* attributes */
		private String[] args;
		Server parent;
	/* members */
		// constructor
			public ServerGUI() {}
			public ServerGUI(String[] args) {
				this.args = args;
			}
		// thread
			@Override
			public void run() { launch(args); }
		// javafx
			@Override
			public void start(Stage primaryStage) throws Exception {
				primaryStage.setTitle("auctioneer");
				BorderPane root = new BorderPane();
				// TOP
					HBox topbar = new HBox();
					topbar.getChildren().addAll(
						createImmutableTextField(Utility.getStringDate(), 1, 1),
						createImmutableTextField(Server.getTimeRemaining().toString(), 100, 100)
					);
					root.setTop(topbar);
				// LEFT
					VBox productPanel = new VBox();
					productPanel.getChildren().addAll(
						createEditableTextField("Name", 0, 0),
						createEditableTextField("Description", 0, 0),
						createEditableTextField("Price", 0, 0)
					);
					root.setLeft(productPanel);
				// CENTER
					TilePane actionsPanel = new TilePane();
					actionsPanel.getChildren().addAll(
						createButton("Start/Stop", new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								println((Server.getStatus() == ServerStatus.RUNNING ? "Stop" : "Start"));
							}
						}),
						createButton("Send product info", new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								Server.broadcast(Protocol.serverTags.PRODUCT_DESCRIPTION, (Object[])Server.getProductInfo());
							}
						}),
						createButton("Send time remaining", new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								Server.broadcast(Protocol.serverTags.TIME_REMAINING, Server.getTimeRemaining());
							}
						})
					);
					root.setCenter(actionsPanel);
				// RIGHT
					VBox highestBidPanel = new VBox();
					highestBidPanel.getChildren().addAll(
						createImmutableTextField((Server.getHighestBid().getKey().toString()), 1, 1),
						createImmutableTextField(Server.getHighestBid().getValue().toString(), 1, 1)
					);
					root.setRight(highestBidPanel);
				// BOTTOM
					root.setBottom(createImmutableTextField("Console", 0, 0));
				primaryStage.setScene(new Scene(root, 600, 600));
				primaryStage.show();
			}
		// javafx components
			private TextField createImmutableTextField(String content, int posx, int posy) {
				TextField newTextField = new TextField(content);
				newTextField.setEditable(false);
				newTextField.setLayoutX(posx);
				newTextField.setLayoutY(posy);
				return newTextField;
			}
			private TextField createEditableTextField(String hint, int posx, int posy) {
				TextField newTextField = new TextField();
				newTextField.setPromptText(hint);
				newTextField.setLayoutX(posx);
				newTextField.setLayoutY(posy);
				return newTextField;
			}
			private Button createButton(String text, EventHandler<ActionEvent> action) {
				Button newButton = new Button(text);
				newButton.setOnAction(action);
				return newButton;
			}
		// display
			private static void println(String data) { Utility.println("[SERVER_UI]> " + data); }
}
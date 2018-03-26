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
import javafx.scene.layout.StackPane;
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
				StackPane root = new StackPane();
				Button btn = new Button("Invoke Satan");
				btn.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						println("Gort ! Klaatu barada nikto !");
					}
				});
				root.getChildren().add(btn);
				//
				root.getChildren().add(createImmutableTextField(Utility.getStringDate(), 0, 0));
				root.getChildren().add(createImmutableTextField(Server.getTimeRemaining().toString(), 100, 100));
				root.getChildren().add(createButton("Start/Stop", new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						println((Server.getStatus() == ServerStatus.RUNNING ? "Stop" : "Start"));
					}
				}));
				root.getChildren().add(createEditableTextField("Name", 0, 0));
				root.getChildren().add(createEditableTextField("Description", 0, 0));
				root.getChildren().add(createEditableTextField("Price", 0, 0));
				root.getChildren().add(createButton("Send product info", new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						Server.broadcast(Protocol.serverTags.PRODUCT_DESCRIPTION, (Object[])Server.getProductInfo());
					}
				}));
				root.getChildren().add(createButton("Send time remaining", new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						Server.broadcast(Protocol.serverTags.TIME_REMAINING, Server.getTimeRemaining());
					}
				}));
				root.getChildren().add(createImmutableTextField(Server.getHighestBid().getKey().toString(), 0, 0));
				root.getChildren().add(createImmutableTextField(Server.getHighestBid().getValue().toString(), 0, 0));
				root.getChildren().add(createImmutableTextField("Console", 0, 0));
				//
				primaryStage.setScene(new Scene(root, 300, 250));
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
				return new Button();
			}
		// display
			private static void println(String data) { Utility.println("[SERVER_UI]> " + data); }
}
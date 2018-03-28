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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class ServerGUI extends Application implements Runnable {
	/* attributes */
		private String[] args;
		Server parent;
		BorderPane root;
		static Text currentTime;
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
				root = new BorderPane();
				// TOP
					HBox topbar = new HBox();
					currentTime = createText("current time: " + Utility.getStringDate() + '\n', 200, 15.0, TextAlignment.LEFT);
					topbar.getChildren().addAll(
						currentTime,
						createText(
							"days " + Server.getTimeRemaining().toDays()
							+ " hours: " + Server.getTimeRemaining().toHours() % 24
							+ " minutes: " + Server.getTimeRemaining().toMinutes() % 60
							+ " seconds: " + Server.getTimeRemaining().toSeconds() % 60
							+ '\n',
							200, 15.0, TextAlignment.LEFT
						)
					);
					root.setTop(topbar);
				// LEFT
					VBox productPanel = new VBox();
					productPanel.getChildren().addAll(
						createText("Product creation\n", 200, 20.0, TextAlignment.LEFT),
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
						createText("Current highest bid\n", 200, 20.0, TextAlignment.RIGHT),
						createText(Server.getHighestBid().getKey().toString() + '\n', 200, 15.0, TextAlignment.RIGHT),
						createText(Server.getHighestBid().getValue().toString() + '\n', 200, 15.0, TextAlignment.RIGHT)
					);
					root.setRight(highestBidPanel);
				// BOTTOM
					root.setBottom(createImmutableTextField("Console", 0, 0));
				primaryStage.setScene(new Scene(root, 600, 600));
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
		// javafx components
			static private TextField createImmutableTextField(String content, int posx, int posy) {
				TextField newTextField = new TextField(content);
				newTextField.setEditable(false);
				newTextField.setLayoutX(posx);
				newTextField.setLayoutY(posy);
				
				return newTextField;
			}
			static private TextField createEditableTextField(String hint, int posx, int posy) {
				TextField newTextField = new TextField();
				newTextField.setPromptText(hint);
				newTextField.setLayoutX(posx);
				newTextField.setLayoutY(posy);
				
				return newTextField;
			}
			static private Button createButton(String text, EventHandler<ActionEvent> action) {
				Button newButton = new Button(text);
				newButton.setOnAction(action);
				
				return newButton;
			}
			static private Text createText(String text, int width, double fontSize, TextAlignment alignment) {
				Text newText = new Text(text);
				newText.setFont(new Font(fontSize));
				newText.setWrappingWidth(width);
				newText.setTextAlignment(alignment);
				
				return newText;
			}
		// server-side events
			static public void updateTime() {
				currentTime = createText("current time: " + Utility.getStringDate() + '\n', 200, 15.0, TextAlignment.LEFT);
			}
		// display
			private static void println(String data) { Utility.println("[SERVER_UI]> " + data); }
}
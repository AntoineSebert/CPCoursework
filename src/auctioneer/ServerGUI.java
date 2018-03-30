package auctioneer;

import common.Protocol;
import common.ServerStatus;
import common.Utility;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
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
		// application
			private String[] args;
		// graphic elements
			private static BorderPane root;
			private static HBox topbar = new HBox();
			private static VBox productPanel = new VBox();
			private static TilePane actionsPanel = new TilePane();
			private static VBox highestBidPanel = new VBox();
			private static ScrollPane scrollableConsole;
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
					topbar.getChildren().addAll(
						createText("current time: " + Utility.getStringDate() + '\n', 300, 15.0, TextAlignment.LEFT),
						createText(
							" hours: " + String.valueOf(Math.floor(Server.getTimeRemaining() / 360))
							+ " minutes: " + String.valueOf(Math.floor(Server.getTimeRemaining() / 60))
							+ " seconds: " + String.valueOf(Math.floor(Server.getTimeRemaining() % 60))
							+ '\n',
							350, 15.0, TextAlignment.LEFT
						)
					);
					root.setTop(topbar);
				// LEFT
					productPanel.getChildren().addAll(
						createText("Product creation\n", 300, 20.0, TextAlignment.LEFT),
						createEditableTextField("Name"),
						createEditableTextField("Description"),
						createEditableTextField("Price"),
						createEditableTextField("Start datetime " + "yyyy/MM/dd HH:mm:ss"),
						createEditableTextField("End datetime " + "yyyy/MM/dd HH:mm:ss"),
						createButton("Add auction", new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								if(checkNewAuctionFields()) {
									Server.addAuction(
										Utility.stringToDate(((TextField)productPanel.getChildren().get(4)).getText()),
										Utility.stringToDate(((TextField)productPanel.getChildren().get(5)).getText()),
										((TextField)productPanel.getChildren().get(1)).getText(),
										((TextField)productPanel.getChildren().get(2)).getText(),
										Integer.parseInt(((TextField)productPanel.getChildren().get(3)).getText())
									);
									clearNewAuctionFields();
								}
							}
						})
					);
					root.setLeft(productPanel);
				// CENTER
					actionsPanel.getChildren().addAll(
						createButton("Start/Stop", new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								if(Server.getStatus() == ServerStatus.RUNNING)
									Server.stopServer();
								else
									Server.start();
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
					highestBidPanel.getChildren().addAll(
						createText("Current highest bid\n", 200, 20.0, TextAlignment.RIGHT),
						createText(Server.getHighestBid().getKey().toString() + '\n', 200, 15.0, TextAlignment.RIGHT),
						createText(Server.getHighestBid().getValue().toString() + '\n', 200, 15.0, TextAlignment.RIGHT)
					);
					root.setRight(highestBidPanel);
				// BOTTOM
					scrollableConsole = new ScrollPane();
					scrollableConsole.setPrefSize(600, 320);
					scrollableConsole.setContent(createText("init\n", 500, 12, TextAlignment.LEFT));
					root.setBottom(scrollableConsole);
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
		// javafx components
			static private TextField createEditableTextField(String hint) {
				TextField newTextField = new TextField();
				newTextField.setPromptText(hint);

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
			public static void updateTime() {
				((Text)topbar.getChildren().get(0)).setText("current time: " + Utility.getStringDate() + '\n');
				((Text)topbar.getChildren().get(1)).setText(
					" hours: " + String.valueOf(Math.floor(Server.getTimeRemaining() / 360))
					+ " minutes: " + String.valueOf(Math.floor(Server.getTimeRemaining() / 60))
					+ " seconds: " + String.valueOf(Math.floor(Server.getTimeRemaining() % 60))
					+ '\n'
				);
			}
			public static void updateHighestBid() {
				((Text)highestBidPanel.getChildren().get(1)).setText(Server.getHighestBid().getKey().toString());
				((Text)highestBidPanel.getChildren().get(2)).setText(Server.getHighestBid().getValue().toString());
			}
			public static void printConsole(String data) {
				try {
					Text console = (Text)scrollableConsole.getContent();
					console.setText(console.getText() + '[' + Utility.getStringTime() + "]" + data + '\n');
				}
				catch(NullPointerException e) {
					Utility.println("[SERVER_UI]> Graphic console not available");
				}
			}
		// other
			private boolean checkNewAuctionFields() {
				return ((TextField)productPanel.getChildren().get(4)).getText() != ""
					&& ((TextField)productPanel.getChildren().get(5)).getText() != ""
					&& ((TextField)productPanel.getChildren().get(1)).getText() != ""
					&& ((TextField)productPanel.getChildren().get(2)).getText() != ""
					&& ((TextField)productPanel.getChildren().get(3)).getText() != "";
			}
			private void clearNewAuctionFields() {
				((TextField)productPanel.getChildren().get(1)).clear();
				((TextField)productPanel.getChildren().get(2)).clear();
				((TextField)productPanel.getChildren().get(3)).clear();
				((TextField)productPanel.getChildren().get(4)).clear();
				((TextField)productPanel.getChildren().get(5)).clear();
			}
		// display
			private static void println(String data) {
				Utility.println("[SERVER_UI]> " + data);
				printConsole("[SERVER_UI]> " + data);
			}
}
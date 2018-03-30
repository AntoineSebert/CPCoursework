package customer;

import common.Utility;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ClientGUI extends Application implements Runnable {
	/* attributes */
		// application
			private String[] args;
		// graphic elements
			private static BorderPane root;
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
					// the time left (in seconds) to the end-of-bidding deadline
				// LEFT
					// text field to allow the client to enter a new bid
					// Button to submit a new bid 
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
			public static void printConsole(String data) {
				try {
					Text console = (Text)scrollableConsole.getContent();
					console.setText(console.getText() + '[' + Utility.getStringTime() + "]" + data + '\n');
				}
				catch(NullPointerException e) {
					Utility.println("[CLIENT]> Graphic console not available");
				}
			}
		// display
			private static void println(String data) {
				Utility.println("[CLIENT]> " + data);
				printConsole("[CLIENT]> " + data);
			}
}

package auctioneer;

import javafx.application.Application;
import javafx.stage.Stage;

public class ServerGUI extends Application implements Runnable {
	private String[] args;
	
	public ServerGUI(String[] args) {
		this.args = args;
	}

	@Override
	public void run() {
		launch(args);
		
	}

	@Override
	public void start(Stage args) throws Exception {
		// TODO Auto-generated method stub
		
	}

}

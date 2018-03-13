package common;

import javafx.stage.Stage;

public interface NetworkNode {
	public void main(String[] args);
	public void start(Stage primaryStage) throws Exception;
	public boolean start();
	public void stop();
}

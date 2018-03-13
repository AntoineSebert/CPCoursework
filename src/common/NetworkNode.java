package common;

import javafx.stage.Stage;

public interface NetworkNode {
	public static boolean start() { return false; }
	public static void stop() {}
	public static void start(Stage primaryStage) throws Exception {}
	public static void println(String data) {}
}

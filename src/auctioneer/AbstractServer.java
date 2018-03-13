package auctioneer;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Date;

import common.NetworkNode;
import common.ServerStatus;
import common.Utility;

public interface AbstractServer extends NetworkNode {
	static String serverStartDate = Utility.getDate();
	static ArrayList<ClientImage> clientsQueue = new ArrayList<ClientImage>();
	static Date startDate = null;
	static ServerStatus serverStatus = ServerStatus.STOPPED;
	static ServerSocket serverSocket = null;
}

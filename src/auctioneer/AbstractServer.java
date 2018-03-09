/**
 * 
 */
package auctioneer;

import java.util.Date;
import java.util.HashSet;

import common.ServerStatus;
import customer.Client;

/**
 * @author i
 *
 */
public interface AbstractServer {
	static HashSet<Client> clientsQueue = new HashSet<Client>(); // order clients by bid, but check if no bids
	static Date startDate = new Date();
	static Date currentDate = new Date();
	static ServerStatus serverStatus = ServerStatus.STOPPED;
	

	void startServer();
	
	void stopServer();
	
}

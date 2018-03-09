/**
 * 
 */
package auctioneer;

import java.util.Date;
import java.util.HashSet;

import common.ServerStatus;

/**
 * @author i
 *
 */
public interface AbstractServer {
	HashSet<ClientImage> clientsQueue = new HashSet<ClientImage>(); // order clients by bid, but check if no bids
	Date startDate = new Date();
	Date currentDate = new Date();
	ServerStatus serverStatus = ServerStatus.STOPPED;

	void startServer();
	
	void stopServer();
	
}

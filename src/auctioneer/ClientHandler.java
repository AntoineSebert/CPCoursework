package auctioneer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

import common.Protocol;
import common.Utility;
/*
 * @author Anthony Sébert
 * Holds and manage server-side client data, talk with client.
 */
public class ClientHandler extends Thread {
	/* attributes */
		// static
			static public int totalClients = 0;
		// connection
			private PrintWriter out;
			private BufferedReader in;
			private Socket socket;
		// instance data
			private int id;
			private Date connectionDate;
			private Date disconnectionDate = null;
	/* members */
		// constructor
			public ClientHandler(Socket newSocket, int id) {
				connectionDate = new Date();
				totalClients++;
				this.id = id;
				socket = newSocket;
				println("Connexion established with client " + id + " on " + connectionDate);
				try {
					out = new PrintWriter(socket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					send(Protocol.serverTags.PRODUCT_DESCRIPTION, (Object[])Server.getProductInfo());
					receive();
				}
				catch(IOException e) {
					e.printStackTrace();
				}
			}
		// connection
			public void start() {
				while(disconnectionDate == null)
					receive();
			}
			public void send(Protocol.serverTags tag, Object... data) {
				// check is there is an auction in progress; some tags can be unavailable
				if(!Server.isInProgress() && (
						tag == Protocol.serverTags.PRODUCT_DESCRIPTION
						|| tag == Protocol.serverTags.TIME_REMAINING
						|| tag == Protocol.serverTags.HIGHEST_UPDATE
						|| tag == Protocol.serverTags.CLOSE_BIDDING
				)) {
					println("No auction in progress, cannot send " + tag);
					return;
				}
		
				println("Sending " + tag + " to client " + id + ':');
				out.println(tag);

				for(Object element : data) {
					println("\t" + element);
					out.println(element);
				}
				yield();
			}
			private void receive() {
				try {
					Protocol.clientTags tag = Protocol.clientTags.valueOf(in.readLine());
					switch(tag) {
						case BID_SUBMIT:
							int amount = Integer.parseInt(in.readLine());
							println(tag.toString() + " received : " + amount);
							Server.addBid(this, amount);
							break;
						case ASK_REMAINING:
							println(tag.toString() + " received");
							send(Protocol.serverTags.TIME_REMAINING, Server.getTimeRemaining());
							break;
						case ASK_PRODUCT:
							println(tag.toString() + " received");
							send(Protocol.serverTags.TIME_REMAINING, (Object[])Server.getProductInfo());
							break;
						case ASK_HIGHEST:
							println(tag.toString() + " received");
							send(Protocol.serverTags.HIGHEST_UPDATE, Server.getHighestBid().getKey(), Server.getHighestBid().getValue());
							break;
						case CLOSE_CONNECTION:
							terminate();
							break;
						case ERROR:
							println(tag.toString() + " : " + in.readLine());
							break;
						default:
							println("Unknown instruction :" + tag + ", value :" + in.readLine());
							break;
					}
				}
				catch(IOException e) {
					e.printStackTrace();
					terminate();
				}
				yield();
			}
			public void terminate() {
				println("Closing client_" + id + " connection on " + disconnectionDate);
				send(Protocol.serverTags.CLOSE_CONNECTION);
				try {
					socket.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				finally {
					Server.removeClient(this);
					disconnectionDate = new Date();
				}
				yield();
			}
		// getters
			public int getClientId() { return id; }
			public Date getConnectionDate() { return connectionDate; }
			public Date getDisconnectionDate() { return disconnectionDate; }
		// display
			private void println(String data) {
				Utility.println("[SERVER_" + id + "]> " + data);
				ServerGUI.printConsole("[SERVER]> " + data);
			}
}

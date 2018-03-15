package auctioneer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import common.Protocol;
import common.Utility;

public class ClientImage {
	static public int totalClients = 0;
	private PrintWriter out;
	private BufferedReader in;
	private Socket socket;
	private int id;
	
	public ClientImage(Socket newSocket, int id) {
		totalClients++;
		this.id = id;
		socket = newSocket;
		println("Connexion established with client ");
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		receive();
	}
	
	public void send(Protocol.serverTags tag, Object data[]) {
		System.out.println("Sending " + tag + ':' + data.toString() + " to client " + id);
		out.println(tag);
		out.println(data);
	}
	
	public void receive() {
		try {
			Protocol.clientTags tag = Protocol.clientTags.valueOf(in.readLine());
			switch(tag) {
				case BID_SUBMIT:
					if(Server.isInProgress()) {
						String amountString = in.readLine();
						println(tag.toString() + " received :" + amountString);
						Server.addBid(this, Integer.parseInt(amountString));
					}
					else
						send(Protocol.serverTags.CLOSE_BIDDING, new Object[] {});
					break;
				case ASK_REMAINING:
					println(tag.toString() + " received");
					send(Protocol.serverTags.TIME_REMAINING, new Object[] { Server.getTimeRemaining() });
					break;
				case ASK_PRODUCT:
					println(tag.toString() + " received");
					send(Protocol.serverTags.TIME_REMAINING, Server.getProductInfo());
					break;
				case ASK_HIGHEST:
					println(tag.toString() + " received");
					send(Protocol.serverTags.HIGHEST_UPDATE, new Object[] {});
					break;
				case CLOSE_CONNECTION:
					Server.removeClient(this);
					println(tag.toString() + " : closing client_" + id + " connection");
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
		}
	}

	public int getId() {
		return id;
	}

	public void println(String data) {
		Utility.println("[SERVER_" + id + "]> " + data);
	}
}

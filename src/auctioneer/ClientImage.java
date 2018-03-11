package auctioneer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import common.Protocol;
import common.Utility;

public class ClientImage {
	private PrintWriter out;
	private BufferedReader in;
	private Socket socket;
	private int id;
	
	public ClientImage(Socket newSocket, int id) {
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
	
	public void send(Protocol.serverTags tag, Object data) {
		System.out.println("Sending " + tag + ':' + data.toString() + " to client " + id);
		out.println(tag);
		out.println(data);
	}
	
	public void receive() {
		try {
			Protocol.clientTags tag = Protocol.clientTags.valueOf(in.readLine());
			println("test");
			switch(tag) {
				case BID_SUBMIT:
					println("BID_SUBMIT received :" + in.readLine());
					break;
				case CLOSE_CONNECTION:
					Server.removeClient(id);
					println("Connection with client " + id + " closed");
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

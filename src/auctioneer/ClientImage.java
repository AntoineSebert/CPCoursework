package auctioneer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import common.Protocol;

public class ClientImage {
	PrintWriter out;
	BufferedReader in;
	Socket socket;
	
	public ClientImage(ServerSocket serverSocket) {
		try {
			socket = serverSocket.accept();
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send(Protocol.serverTags tag, Object data) {
		System.out.println("Sending " + tag + ':' + data.toString() + " to " + toString());
		out.println(tag);
		out.println(data.toString());
	}
	
	public void receive() {
		try {
			switch(Protocol.clientTags.valueOf(in.readLine())) {
				case BID_SUBMIT:
					System.out.println("BID_SUBMIT received :" + in.readLine());
					break;
				default:
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

package main;

public class Auctioneer {
	private static Auctioneer auctioneer = null;
	
	protected Auctioneer() {
		// Exists only to defeat instantiation
	}
	public synchronized static Auctioneer getInstance() {
		if(auctioneer == null)
			auctioneer = new Auctioneer();
		return auctioneer;
	}
}

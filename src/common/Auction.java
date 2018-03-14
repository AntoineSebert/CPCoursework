/**
 * 
 */
package common;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * @author i
 *
 */
public class Auction {
	private ZonedDateTime start;
	private ZonedDateTime deadline;
	private String productName;
	private int initialPrice;
	private NavigableMap<Integer, Integer> bids = new TreeMap<Integer, Integer>();

	public Auction(ZonedDateTime start, ZonedDateTime deadline, String productName, int initialPrice) {
		this.start = start;
		this.deadline = deadline;
		this.productName = productName;
		this.initialPrice = initialPrice;
	}
	
	public ZonedDateTime getStart() { return start; }
	public ZonedDateTime getDeadline() { return deadline; }
	public String getProductName() { return productName; }
	public int getInitialPrice() { return initialPrice; }

	public Map.Entry<Integer, Integer> getHighestBid() { return bids.lastEntry(); }
	public boolean isDealineOver() { return Utility.compareDates(start, deadline) < 0; }
	
	public void addBid(int clientId, int amount) { bids.put(clientId, amount); }
}

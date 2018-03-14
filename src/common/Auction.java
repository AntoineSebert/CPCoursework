/**
 * 
 */
package common;

import java.util.Date;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * @author i
 *
 */
public class Auction {
	private Date start;
	private Date deadline;
	private String productName;
	private int initialPrice;
	private NavigableMap<Integer, Integer> bids = new TreeMap<Integer, Integer>();

	public Auction(Date start, Date deadline, String productName, int initialPrice) {
		this.start = start;
		this.deadline = deadline;
		this.productName = productName;
		this.initialPrice = initialPrice;
		
	}
	
	public Date getStart() { return start; }
	public Date getDeadline() { return deadline; }
	public String getProductName() { return productName; }
	public int getInitialPrice() { return initialPrice; }

	public Map.Entry<Integer, Integer> getHighestBid() { return bids.lastEntry(); }
	public boolean isDealineOver() { return deadline.compareTo(Utility.getDate()) < 0; }
	
	public void addBid(int clientId, int amount) { bids.put(clientId, amount); }
}

package common;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * @author i
 *
 */
public class Auction {
	// attributes
		private ZonedDateTime start;
		private ZonedDateTime deadline;
		private String productName;
		private String productDescription;
		private int initialPrice;
		private NavigableMap<Integer, Integer> bids = new TreeMap<Integer, Integer>();
	// methods
		// constructor
			public Auction(ZonedDateTime start, ZonedDateTime deadline, String productName, String productDescription, int initialPrice) {
				this.start = start;
				this.deadline = deadline;
				this.productName = productName;
				this.productDescription = productDescription;
				this.initialPrice = initialPrice;
				bids.put(-1, initialPrice);
			}
		// getters
			public ZonedDateTime getStart() { return start; }
			public ZonedDateTime getDeadline() { return deadline; }
			public String getProductName() { return productName; }
			public String getProductDescription() { return productDescription; }
			public int getInitialPrice() { return initialPrice; }
		// other accessors
			public Map.Entry<Integer, Integer> getHighestBid() {
				return Collections.max(bids.entrySet(), Comparator.comparingInt(Map.Entry::getValue));
			}
			public boolean isDealineOver() { return Utility.compareDates(start, deadline) > 0; }
		// mutators
			public void addBid(int clientId, int amount) {
				if(0 < getHighestBid().getValue().compareTo(amount))
					bids.put(clientId, amount);
			}
}
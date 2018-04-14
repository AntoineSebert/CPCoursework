package common;

import java.util.Date;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
/*
 * @author Anthony Sébert
 * The object representation of an auction. It includes a container to manage the related bids
 * and a member to check if the deadline for this auction is over.
 */
public class Auction {
	// attributes
		private Date start;
		private Date deadline;
		private String productName;
		private String productDescription;
		private int initialPrice;
		private NavigableMap<Integer, Integer> bids = new TreeMap<Integer, Integer>();
	// methods
		// constructor
			public Auction(Date start, Date deadline, String productName, String productDescription, int initialPrice) {
				this.start = start;
				this.deadline = deadline;
				this.productName = productName;
				this.productDescription = productDescription;
				this.initialPrice = initialPrice;
				bids.put(-1, initialPrice);
			}
		// getters
			public Date getStart() { return start; }
			public Date getDeadline() { return deadline; }
			public String getProductName() { return productName; }
			public String getProductDescription() { return productDescription; }
			public int getInitialPrice() { return initialPrice; }
		// other accessors
			public Map.Entry<Integer, Integer> getHighestBid() {
				return Collections.max(bids.entrySet(), Comparator.comparingInt(Map.Entry::getValue));
			}
			public boolean isDealineOver() { return Utility.difference(new Date(), deadline, TimeUnit.SECONDS) < 0; }
		// mutators
			public void addBid(int clientId, int amount) {
				if(0 < getHighestBid().getValue().compareTo(amount))
					bids.put(clientId, amount);
			}
}
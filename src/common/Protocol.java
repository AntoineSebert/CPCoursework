package common;

public class Protocol {
	public static enum serverTags {
		SERVER_STATUS,
		PRODUCT_DESCRIPTION,
		TIME_REMAINING,
		HIGHEST_UPDATE,
		CLOSE_BIDDING,
		WINNING_BID
	}
	
	public static enum clientTags {
		BID_SUBMIT
	}
}

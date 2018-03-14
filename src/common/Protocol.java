package common;

public class Protocol {
	// ajouter attribution id
	public static enum serverTags {
		// server status
		SERVER_STATUS,
		// description of a production (name, description, price)
		PRODUCT_DESCRIPTION,
		// time remaining
		TIME_REMAINING,
		// update of the highest bid
		HIGHEST_UPDATE,
		// closing of the current auction
		CLOSE_BIDDING,
		// bid is not higher than previous bid
		NOT_HIGHER,
		// bid that have won the auction
		WINNING_BID,
		// closing connection with the client
		CLOSE_CONNECTION
	}
	
	// ajouter ask_id, ask_desc, ask_highest, ask_remaining
	public static enum clientTags {
		// submitting a new bid
		BID_SUBMIT,
		// closing connection with the server
		CLOSE_CONNECTION
	}
}

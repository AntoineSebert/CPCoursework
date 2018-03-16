package common;

public class Protocol {
	public static enum serverTags {
		// server status
		SERVER_STATUS,
		// send id to client
		SEND_ID, /* NO BROADCAST */
		// product (name, description, price)
		PRODUCT_DESCRIPTION, /* ONLY IN PROGESS */
		// time remaining
		TIME_REMAINING, /* ONLY IN PROGESS */
		// update of the highest bid
		HIGHEST_UPDATE, /* ONLY IN PROGESS */
		// closing of the current auction
		CLOSE_BIDDING, /* ONLY IN PROGESS */
		// bid is not higher than previous bid
		NOT_HIGHER, /* NO BROADCAST */
		// bid that have won the auction
		WINNING_BID,
		// closing connection with the client
		CLOSE_CONNECTION,
		// generic error
		ERROR
	}

	public static enum clientTags {
		// submitting a new bid
		BID_SUBMIT,
		// ask remaining time
		ASK_REMAINING,
		// ask product information
		ASK_PRODUCT,
		// ask current highest bid
		ASK_HIGHEST,
		// closing connection with the server
		CLOSE_CONNECTION,
		// generic error
		ERROR
	}
}

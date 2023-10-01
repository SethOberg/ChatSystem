package assignment5;

/**
 * An interface implemented by MessageServer and MessageClient to send a message when send button is clicked
 */
public interface MessageListener {

	public void sendMessage(String message);
	
}

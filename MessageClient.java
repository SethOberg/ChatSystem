package assignment5;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 * Client that connects to the threadpool server
 */

public class MessageClient implements MessageListener {
	private GUIChat clientGUI;
	private Socket clientSocket;
	private int port; 
	private String ip; 
	private Thread clientThread;
	
	private DataInputStream dis;
	private DataOutputStream dos;
	
	public MessageClient(String ip, int port) throws UnknownHostException, IOException {
		this.ip = ip;
		this.port = port;
		
		//start gui
		clientGUI = new GUIChat("Client");
		clientGUI.Start();
		clientGUI.addMessageListener(this);
		
		//create connection
		clientThread = new Thread(new runnableClass());
		clientThread.start();
	}
	
	
	
	/**
	 * Connect to MessageServer and open input/outputStreams
	 */
	public void connectToServer() {
		
		
		try {
			clientSocket = new Socket(ip, port);
			dis = new DataInputStream(clientSocket.getInputStream());
			dos = new DataOutputStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			disconnectFromServer();
		}
		
		
	}
	
	
	/**
	 * Disconnect client from server
	 */
	public void disconnectFromServer() {
		
		try {
			dis.close();
			dos.flush();
			dos.close();
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

	//Send a String object to server
	public void sendMessage(String message) {
		try {
			dos.writeUTF(message);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Thread reading from inputstream in a while loop 
	 * @author sethoberg
	 */
	private class runnableClass implements Runnable {
		String newMessage = "";
		
		public void run() {
			
			connectToServer();
			
				try {
					while(true) {
						newMessage = dis.readUTF();
						clientGUI.updateTextArea(newMessage);
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				disconnectFromServer();
			
			
		}
		
	}
	
	
	public static void main(String [] args) throws UnknownHostException, IOException {
		MessageClient client = new MessageClient(InetAddress.getLocalHost().getHostName(),3461);
		
	}


}

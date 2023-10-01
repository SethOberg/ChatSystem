package assignment5;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Server that handles multiple clients with a threadpool
 */

public class MessageServer implements Runnable, MessageListener {
	private GUIChat serverGUI;
	private ServerSocket serverSocket;
	private int port;
	private LinkedList<ClientHandler> connectedClients = new LinkedList<>(); 
//	private ExecutorService threadPool = Executors.newFixedThreadPool(3);
	private ExecutorService threadPool = Executors.newCachedThreadPool();
	
	
	/**
	 * Creates a server with a port number
	 * @param port
	 */
	public MessageServer(int port) {
		serverGUI = new GUIChat("Server");
		serverGUI.Start();
		serverGUI.addMessageListener(this);
		this.port = port; 
		threadPool.execute(this);
	}
	
	
	/**
	 * Start the server 
	 */
	public void startServer() {
		
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Server startad");
		} catch (IOException e) {
			closeServer();
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Send message
	 */
	public void sendMessage(String message) {
		
		for(int i = 0; i < connectedClients.size(); i++) {
			connectedClients.get(i).sendMessage(message);
		}
		
	}
	
	
	/**
	 * Look for new clients in a while true loop
	 * When new client is connected they are added to the threadPool
	 * A new instance of ClientHandler is created for each connected client
	 */
	public void run() {
		startServer();
		while(true) {
			Socket clientSocket;
			ClientHandler newClient;
			try {
				clientSocket = serverSocket.accept();
				newClient = new ClientHandler(clientSocket);
				threadPool.execute(newClient);
				connectedClients.add(newClient);
				
			} catch (IOException e) {
				threadPool.shutdown();
				closeServer();
				e.printStackTrace();
			}
		}
		
		
	}

	
	/**
	 * Close server
	 */
	public void closeServer() {
		
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	/**
	 * Class for handling new clients connected to the server
	 */
	private class ClientHandler implements Runnable {
		private Socket clientSocket;
		private DataInputStream dis;
		private DataOutputStream dos;
		private volatile boolean isConnected = false;
		
		private ClientHandler(Socket socket) {
			clientSocket = socket;
		}
		
		
		/**
		 * Open streams for connected client
		 */
		public void getClientStreams() {
			
			try {
				dis = new DataInputStream (clientSocket.getInputStream());
				dos = new DataOutputStream( clientSocket.getOutputStream());
				isConnected = true;
				
			} catch (IOException e) {
				disconnectClient();
				e.printStackTrace();
			}
		}
		
		public void disconnectClient() {
			
			try {
				System.out.println("Client disconnected");
				isConnected = false;
				dos.flush();
				dos.close();
				dis.close();
				clientSocket.close();
				connectedClients.remove(this); //ny lör 21:19
				System.out.println("Client disconnected from server");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}

		/**
		 * Run method reads in a while true loop
		 * while the inputstream object is available
		 */
		public void run() {
			String newClientMessage = "";
			getClientStreams(); 
			

			try {
				while(isConnected == true) {
					if(dis.available() > 0) {
						newClientMessage = dis.readUTF();
						serverGUI.updateTextArea(newClientMessage);	
					}
					
				}
				
				} catch (IOException e) {
					disconnectClient();
					e.printStackTrace();
				}
				
		}
		
		public void sendMessage(String message) {
			
			try {
				dos.writeUTF(message);
				dos.flush();
			} catch (IOException e) {
				disconnectClient();
				e.printStackTrace();
			}
			
		}
		
	}
	
	
	public static void main(String [] args) {
		MessageServer server = new MessageServer(3461);
	}

}

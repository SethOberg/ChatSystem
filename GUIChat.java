
package assignment5;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

/**
 * GUI for chat
 */
public class GUIChat
{
	private JFrame frame;				// The Main window
	private JTextField txt;				// Input for text to send
	private JButton btnSend;			// Send text in txt
	private JTextArea lstMsg;			// The logger listbox
	private ArrayList<MessageListener> listeners = new ArrayList<>();
	
	
	private String frameName;
	
	private ButtonListener btnListener = new ButtonListener();
	
	/**
	 * Constructor
	 */
	public GUIChat(String name)
	{
		frameName = name;
	}
	
	/**
	 * Starts the application
	 */
	public void Start()
	{
		frame = new JFrame(frameName);
		frame.setBounds(100, 100, 300,300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
		frame.setTitle(frameName);
		InitializeGUI();
		frame.setVisible(true);
		frame.setResizable(false);
	}
	
	/**
	 * Sets up the GUI with components
	 */
	private void InitializeGUI()
	{
		txt = new JTextField();
		txt.setBounds(13,  13, 177, 23);
		frame.add(txt);
		btnSend = new JButton("Send");
		btnSend.setBounds(197, 13, 75, 23);
		frame.add(btnSend);
		lstMsg = new JTextArea();
		lstMsg.setEditable(false);
		JScrollPane pane = new JScrollPane(lstMsg);
		pane.setBounds(12, 51, 260, 199);
		pane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		frame.add(pane);
		
		btnSend.addActionListener(btnListener);
	}
	
	
	public void addMessageListener(MessageListener listener) {
		listeners.add(listener);
	}
	
	
	/**
	 * Remove text written by client/server 
	 */
	public void clearTextField() {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				txt.setText("");
			}
			
		});
	}
	
	
	/**
	 * Get text written by client/server
	 * @return
	 */
	public String getTextFieldText() {
		return txt.getText();
	}
	
	
	/**
	 * Update the JTextArea for client/server with new text
	 * @param newText
	 */
	public synchronized void updateTextArea(String newText) {
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				lstMsg.append(newText + "\n");
			}
			
		});
	}
	
	
	/**
	 * Sends text from JTextField when send button is clicked
	 */
	
	private class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			
			if(e.getSource() == btnSend) {
				listeners.get(0).sendMessage(getTextFieldText());
			}
			
		}
		
	}
	
	
}

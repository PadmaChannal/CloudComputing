package ds.thesaurus;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Name: Riten Jayantilal Vithalani
 * Student ID: 1001444167
 * 
 * References: 
 * 1. http://www.oracle.com/webfolder/technetwork/tutorials/obe/java/SocketProgramming/SocketProgram.html 
 * 2. http://docs.oracle.com/javase/tutorial/uiswing/components/menu.html 
 */
public class Server extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private static final int PORT_NUMBER = 8005;
	private static final int FRAME_WIDTH = 600;
	private static final int FRAME_HEIGHT = 600;
	private static final String TITLE = "Thesaurus Server Application";
	private static final int MAX_ALLOWED_CLIENTS = 2;
	
	private static JTextArea console;
	
	public Server() {
		setLayout(new BorderLayout()); 
		initializeServerConsole();
		initializeExitButton();
	}
	
	/**
	 * Initialize the exit button to close the server application
	 * 
	 */
	private void initializeExitButton() {
		JButton exitButton = new JButton("Exit");
		exitButton.setSize(40, 10);
		
		exitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		add(exitButton, BorderLayout.SOUTH);
	}

	/**
	 * Initialize the textArea to track the interactions on the server.
	 * 
	 */
	private void initializeServerConsole() {
		// Create a text area with 10 rows and 200 columns
		console = new JTextArea(32, 200);
		console.setLineWrap(true);
		console.setWrapStyleWord(true);
		console.setEditable(false);
		// Give 10px padding from all sides
		console.setBorder(BorderFactory.createCompoundBorder(console.getBorder(),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		
		JScrollPane scroll = new JScrollPane (console, 
				   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		// Add textArea in the JFrame
		add(scroll, BorderLayout.NORTH);
	}

	/**
	 * @param file
	 * 
	 * Create a server that will accept MAX_ALLOWED_CLIENTS number of client connections 
	 * 
	 */
	public static void createMultiThreadedServer() {
		ExecutorService executor = null;
		try (ServerSocket serverSocket = new ServerSocket(PORT_NUMBER);) {
			executor = Executors.newFixedThreadPool(MAX_ALLOWED_CLIENTS);
			console.append("Server started at port " + PORT_NUMBER + "\n");
			console.append("Waiting for clients to connect. Maximum "+ MAX_ALLOWED_CLIENTS +" clients can connect \n");
			
			while (true) {
				// The server socket will continuously be open to accept the client connection
				Socket clientSocket = serverSocket.accept();
				
				Runnable worker = new RequestHandler(clientSocket, console);
				
				// Will call the run() of the RequestHandler.java
				executor.execute(worker);
			}
		} catch (IOException e) {
			console.append("Exception caught when trying to listen on port " + PORT_NUMBER + " or listening for a connection \n");
			console.append(e.getMessage() + "\n");
		} finally {
			if (executor != null) {
				executor.shutdown();
			}
		}
	}

	/**
	 * Create a server window that will display the incoming client requests 
	 * 
	 */
	public static void initializeServerGUI() {
		Server serverGUI = new Server();
		
		// Exit the application when JFrame is closed
		serverGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Set the width and height of the JFrame
		serverGUI.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		serverGUI.setResizable(true);
		serverGUI.setTitle(TITLE);

		// Open the window at the center of the screen
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		serverGUI.setLocation(dimension.width / 2 - serverGUI.getSize().width / 2,
				dimension.height / 2 - serverGUI.getSize().height / 2);
		serverGUI.setVisible(true);
	}
	
	public static void main(String[] args) throws IOException {
		initializeServerGUI();
		createMultiThreadedServer();
	}
}

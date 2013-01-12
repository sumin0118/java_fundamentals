/**
 * Source code is from 
 * http://ashishmyles.com/tutorials/tcpchat/
 * and modified by sumin0118
 * **/
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.SwingUtilities;

public class Tool implements Runnable {
	private volatile static ConnectionInfo info;

	public Tool() {

	}

	static Tool tcpObj = new Tool();
	public final static String END_CHAT_SESSION = new Character((char) 0)
			.toString(); // Indicates the end of a session

	// TCP Components
	public static ServerSocket hostServer = null;
	public static Socket socket = null;
	public static BufferedReader in = null;
	public static PrintWriter out = null;

	// static ToolUI gui = new ToolUI();

	// The thread-safe way to change the GUI components while
	// changing state
	private static void changeStatusTS(int newConnectStatus, boolean noError) {

		// Change state if valid state
		if (newConnectStatus != ConnectionStatus.NULL) {
			info.connectionStatus = newConnectStatus;
		}

		// If there is no error, display the appropriate status message
		if (noError) {
			info.statusString = info.statusMessages[info.connectionStatus];
		}
		// Otherwise, display error message
		else {
			info.statusString = info.statusMessages[ConnectionStatus.NULL];
		}

		// Call the run() routine (Runnable interface) on the
		// error-handling and GUI-update thread
		SwingUtilities.invokeLater(tcpObj);
	}

	// Thread-safe way to append to the chat box
	private static void appendToChatBox(String s) {
		synchronized (info.toAppend) {
			info.toAppend.append(s);
		}
	}

	// Add text to send-buffer
	private static void sendString(String s) {
		synchronized (info.toSend) {
			info.toSend.append(s + "\n");
		}
	}

	// Cleanup for disconnect
	private static void cleanUp() {
		try {
			if (hostServer != null) {
				hostServer.close();
				hostServer = null;
			}
		} catch (IOException e) {
			hostServer = null;
		}

		try {
			if (socket != null) {
				socket.close();
				socket = null;
			}
		} catch (IOException e) {
			socket = null;
		}

		try {
			if (in != null) {
				in.close();
				in = null;
			}
		} catch (IOException e) {
			in = null;
		}

		if (out != null) {
			out.close();
			out = null;
		}
	}

	// Checks the current state and sets the enables/disables
	// accordingly
	public void run() {

		ToolUI.run(info.connectionStatus, info);
	}

	// The main procedure
	public static void main(String args[]) {
		String s;
		// ConnectionInfo info = new ConnectionInfo();
		ToolUI.initGUI(info);

		while (true) {
			try { // Poll every ~10 ms
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}

			switch (info.connectionStatus) {
			case ConnectionStatus.BEGIN_CONNECT:
				try {
					// Try to set up a server if host
					if (info.isHost) {
						hostServer = new ServerSocket(info.port);
						socket = hostServer.accept();
					}

					// If guest, try to connect to the server
					else {
						socket = new Socket(info.hostIP, info.port);
					}

					in = new BufferedReader(new InputStreamReader(
							socket.getInputStream()));
					out = new PrintWriter(socket.getOutputStream(), true);
					changeStatusTS(ConnectionStatus.CONNECTED, true);
				}
				// If error, clean up and output an error message
				catch (IOException e) {
					cleanUp();
					changeStatusTS(ConnectionStatus.DISCONNECTED, false);
				}
				break;

			case ConnectionStatus.CONNECTED:
				try {
					// Send data
					if (info.toSend.length() != 0) {
						out.print(info.toSend);
						out.flush();
						info.toSend.setLength(0);
						changeStatusTS(ConnectionStatus.NULL, true);
					}

					// Receive data
					if (in.ready()) {
						s = in.readLine();
						if ((s != null) && (s.length() != 0)) {
							// Check if it is the end of a transmission
							if (s.equals(END_CHAT_SESSION)) {
								changeStatusTS(ConnectionStatus.DISCONNECTING,
										true);
							}

							// Otherwise, receive what text
							else {
								appendToChatBox("INCOMING: " + s + "\n");
								changeStatusTS(ConnectionStatus.NULL, true);
							}
						}
					}
				} catch (IOException e) {
					cleanUp();
					changeStatusTS(ConnectionStatus.DISCONNECTED, false);
				}
				break;

			case ConnectionStatus.DISCONNECTING:
				// Tell other chatter to disconnect as well
				out.print(END_CHAT_SESSION);
				out.flush();

				// Clean up (close all streams/sockets)
				cleanUp();
				changeStatusTS(ConnectionStatus.DISCONNECTED, true);
				break;

			default:
				break; // do nothing
			}
		}
	}
}

// Action adapter for easy event-listener coding
class ActionEventAdapter implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	}
}

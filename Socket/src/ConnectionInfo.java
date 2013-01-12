public class ConnectionInfo {
	// Other constants
	public final static String statusMessages[] = {
			" Error! Could not connect!", " Disconnected", " Disconnecting...",
			" Connecting...", " Connected" };

	// Connection satate info
	public static String hostIP = "localhost";
	public static int port = 1234;
	public static int connectionStatus = ConnectionStatus.DISCONNECTED;
	public static boolean isHost = true;
	public static String statusString = statusMessages[connectionStatus];
	public static StringBuffer toAppend = new StringBuffer("");
	public static StringBuffer toSend = new StringBuffer("");

	static void changeStatusNTS(int newConnectStatus, boolean noError) {
		// Change state if valid state
		if (newConnectStatus != ConnectionStatus.NULL) {
			connectionStatus = newConnectStatus;
		}

		// If there is no error, display the appropriate status message
		if (noError) {
			statusString = statusMessages[connectionStatus];
		}
		// Otherwise, display error message
		else {
			statusString = statusMessages[ConnectionStatus.NULL];
		}

		// Call the run() routine (Runnable interface) on the
		// current thread
		Thread t = new Thread(new Tool());
		t.start();
		// tcpObj.run( );
	}
	static void appendToChatBox(String s ) {
		synchronized (  toAppend) {
		 toAppend.append(s);
		}
	}

	// ///////////////////////////////////////////////////////////////

	// Add text to send-buffer
	static void sendString(String s ) {
		synchronized ( toSend) {
			 toSend.append(s + "\n");
		}
	}

}

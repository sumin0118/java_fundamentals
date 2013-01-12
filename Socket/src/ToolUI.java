import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ToolUI {
	// Various GUI components and info
	public static JFrame mainFrame = null;
	public static JTextArea chatText = null;
	public static JTextField chatLine = null;
	public static JPanel statusBar = null;
	public static JLabel statusField = null;
	public static JTextField statusColor = null;
	public static JTextField ipField = null;
	public static JTextField portField = null;
	public static JRadioButton hostOption = null;
	public static JRadioButton guestOption = null;
	public static JButton connectButton = null;
	public static JButton disconnectButton = null;

	private static JPanel initOptionsPane() {
		final ConnectionInfo info = new ConnectionInfo();
		JPanel pane = null;
		ActionEventAdapter buttonListener = null;

		// Create an options pane
		JPanel optionsPane = new JPanel(new GridLayout(4, 1));

		// IP address input
		pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pane.add(new JLabel("Host IP:"));
		ipField = new JTextField(10);
		ipField.setText(ConnectionInfo.hostIP);
		ipField.setEnabled(false);
		ipField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				ipField.selectAll();
				// Should be editable only when disconnected
				if (ConnectionInfo.connectionStatus != ConnectionStatus.DISCONNECTED) {
					ConnectionInfo.changeStatusNTS(ConnectionStatus.NULL, true);
				} else {
					ConnectionInfo.hostIP = ipField.getText();
				}
			}
		});
		pane.add(ipField);
		optionsPane.add(pane);

		// Port input
		pane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pane.add(new JLabel("Port:"));
		portField = new JTextField(10);
		portField.setEditable(true);
		portField.setText((new Integer(info.port)).toString());
		portField.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				// should be editable only when disconnected
				if (info.connectionStatus != ConnectionStatus.DISCONNECTED) {
					// changeStatusNTS(ConnectionStatus.NULL, true);
				} else {
					int temp;
					try {
						temp = Integer.parseInt(portField.getText());
						info.port = temp;
					} catch (NumberFormatException nfe) {
						portField.setText((new Integer(info.port)).toString());
						mainFrame.repaint();
					}
				}
			}
		});
		pane.add(portField);
		optionsPane.add(pane);

		// Host/guest option
		buttonListener = new ActionEventAdapter() {
			public void actionPerformed(ActionEvent e) {
				if (info.connectionStatus != ConnectionStatus.DISCONNECTED) {
					// changeStatusNTS(ConnectionStatus.NULL, true);
				} else {
					info.isHost = e.getActionCommand().equals("host");

					// Cannot supply host IP if host option is chosen
					if (info.isHost) {
						ipField.setEnabled(false);
						ipField.setText("localhost");
						info.hostIP = "localhost";
					} else {
						ipField.setEnabled(true);
					}
				}
			}
		};
		ButtonGroup bg = new ButtonGroup();
		hostOption = new JRadioButton("Host", true);
		hostOption.setMnemonic(KeyEvent.VK_H);
		hostOption.setActionCommand("host");
		hostOption.addActionListener(buttonListener);
		guestOption = new JRadioButton("Guest", false);
		guestOption.setMnemonic(KeyEvent.VK_G);
		guestOption.setActionCommand("guest");
		guestOption.addActionListener(buttonListener);
		bg.add(hostOption);
		bg.add(guestOption);
		pane = new JPanel(new GridLayout(1, 2));
		pane.add(hostOption);
		pane.add(guestOption);
		optionsPane.add(pane);

		// Connect/disconnect buttons
		JPanel buttonPane = new JPanel(new GridLayout(1, 2));
		buttonListener = new ActionEventAdapter() {
			public void actionPerformed(ActionEvent e) {
				// Request a connection initiation
				if (e.getActionCommand().equals("connect")) {
					ConnectionInfo.changeStatusNTS(
							ConnectionStatus.BEGIN_CONNECT, true);
				}
				// Disconnect
				else {
					ConnectionInfo.changeStatusNTS(
							ConnectionStatus.DISCONNECTING, true);
				}
			}
		};
		connectButton = new JButton("Connect");
		connectButton.setMnemonic(KeyEvent.VK_C);
		connectButton.setActionCommand("connect");
		connectButton.addActionListener(buttonListener);
		connectButton.setEnabled(true);
		disconnectButton = new JButton("Disconnect");
		disconnectButton.setMnemonic(KeyEvent.VK_D);
		disconnectButton.setActionCommand("disconnect");
		disconnectButton.addActionListener(buttonListener);
		disconnectButton.setEnabled(false);
		buttonPane.add(connectButton);
		buttonPane.add(disconnectButton);
		optionsPane.add(buttonPane);

		return optionsPane;
	}

	public static void run(int connectionStatus, ConnectionInfo info) {
		switch (connectionStatus) {
		case ConnectionStatus.DISCONNECTED:
			connectButton.setEnabled(true);
			disconnectButton.setEnabled(false);
			ipField.setEnabled(true);
			portField.setEnabled(true);
			hostOption.setEnabled(true);
			guestOption.setEnabled(true);
			chatLine.setText("");
			chatLine.setEnabled(false);
			statusColor.setBackground(Color.red);
			break;

		case ConnectionStatus.DISCONNECTING:
			connectButton.setEnabled(false);
			disconnectButton.setEnabled(false);
			ipField.setEnabled(false);
			portField.setEnabled(false);
			hostOption.setEnabled(false);
			guestOption.setEnabled(false);
			chatLine.setEnabled(false);
			statusColor.setBackground(Color.orange);
			break;

		case ConnectionStatus.CONNECTED:
			connectButton.setEnabled(false);
			disconnectButton.setEnabled(true);
			ipField.setEnabled(false);
			portField.setEnabled(false);
			hostOption.setEnabled(false);
			guestOption.setEnabled(false);
			chatLine.setEnabled(true);
			statusColor.setBackground(Color.green);
			break;

		case ConnectionStatus.BEGIN_CONNECT:
			connectButton.setEnabled(false);
			disconnectButton.setEnabled(false);
			ipField.setEnabled(false);
			portField.setEnabled(false);
			hostOption.setEnabled(false);
			guestOption.setEnabled(false);
			chatLine.setEnabled(false);
			chatLine.grabFocus();
			statusColor.setBackground(Color.orange);
			break;
		}

		// Make sure that the button/text field states are consistent
		// with the internal states
		ipField.setText(info.hostIP);
		portField.setText((new Integer(info.port)).toString());
		hostOption.setSelected(info.isHost);
		guestOption.setSelected(!info.isHost);
		statusField.setText(info.statusString);
		chatText.append(info.toAppend.toString());
		info.toAppend.setLength(0);

		mainFrame.repaint();
	}

	// Initialize all the GUI components and display the frame
	static void initGUI(ConnectionInfo info) {
		// Set up the status bar
		statusField = new JLabel();
		statusField
				.setText(ConnectionInfo.statusMessages[ConnectionStatus.DISCONNECTED]);
		statusColor = new JTextField(1);
		statusColor.setBackground(Color.red);
		statusColor.setEditable(false);
		statusBar = new JPanel(new BorderLayout());
		statusBar.add(statusColor, BorderLayout.WEST);
		statusBar.add(statusField, BorderLayout.CENTER);

		// Set up the options pane
		JPanel optionsPane = initOptionsPane( );

		// Set up the chat pane
		JPanel chatPane = new JPanel(new BorderLayout());
		chatText = new JTextArea(10, 20);
		chatText.setLineWrap(true);
		chatText.setEditable(false);
		chatText.setForeground(Color.blue);
		JScrollPane chatTextPane = new JScrollPane(chatText,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		chatLine = new JTextField();
		chatLine.setEnabled(false);
		chatLine.addActionListener(new ActionEventAdapter() {
			public void actionPerformed(ActionEvent e) {
				String s = chatLine.getText();
				if (!s.equals("")) {
					ConnectionInfo.appendToChatBox("OUTGOING: " + s + "\n");
					chatLine.selectAll();

					// Send the string
					ConnectionInfo.sendString(s);
				}
			}
		});
		chatPane.add(chatLine, BorderLayout.SOUTH);
		chatPane.add(chatTextPane, BorderLayout.CENTER);
		chatPane.setPreferredSize(new Dimension(200, 200));

		// Set up the main pane
		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(statusBar, BorderLayout.SOUTH);
		mainPane.add(optionsPane, BorderLayout.WEST);
		mainPane.add(chatPane, BorderLayout.CENTER);

		// Set up the main frame
		mainFrame = new JFrame("Tool");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setContentPane(mainPane);
		mainFrame.setSize(mainFrame.getPreferredSize());
		mainFrame.setLocation(200, 200);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}

}

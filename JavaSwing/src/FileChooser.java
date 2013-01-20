import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class FileChooser extends JFrame {
	private static JButton button = new JButton("Open");
	private static JFileChooser fileChooser = new JFileChooser();

	public FileChooser() {
		add(button);
		setSize(400, 200);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	public static void main(String[] args) {

		/* Enabling Multiple Selection */
		fileChooser.setMultiSelectionEnabled(true);

		/* Setting Current Directory */
		fileChooser.setCurrentDirectory(new File("C:\\Documents and Settings"));

		/* Adding action listener to open file */
		button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				String command = event.getActionCommand();
				if (command.equals("Open")) {
					fileChooser.showDialog(new FileChooser(),
							"File Chooser Example");
				}
			}
		});

		/* Running the Application */
		new FileChooser();
	}
}
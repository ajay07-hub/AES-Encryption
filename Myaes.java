// imports
import java.awt.*;
import java.awt.event.*;
import java.awt.GridBagConstraints;
import javax.swing.*;
// import javax.swing.filechooser.*;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;

// Create a GUI to encrypt and decrypt files
public class Myaes extends JPanel {
	private JPanel keyPanel, keyPanel2, inPanel, buttonPanel, encryptPanel, decryptPanel;
	private JLabel keyLabel, inLabel, statusLabel, headLabel;
	private JTextArea keyArea, inArea;
	private JScrollPane keyPane, inPane;
	private JButton inButton, encryptButton, decryptButton;
	private File inFile, outFile;
	// private FileWriter fw;
	// private BufferedWriter bw;

	private static JButton createSimpleButton(String text) {
		JButton button = new JButton(text);
		button.setForeground(Color.white);
		button.setBackground(Color.BLACK);
		Border line = new LineBorder(Color.BLACK);
		Border margin = new EmptyBorder(0, 14, 0, 14);
		Border compound = new CompoundBorder(line, margin);
		button.setBorder(compound);
		return button;
	}

	private static JButton createSimpleButton2(String text) {
		JButton button = new JButton(text);
		button.setForeground(Color.white);
		button.setBackground(Color.decode("#363232"));
		Border line = new LineBorder(Color.BLACK);
		Border margin = new EmptyBorder(4, 15, 4, 15);
		Border compound = new CompoundBorder(line, margin);
		button.setBorder(compound);
		return button;
	}

	// create GridBagConstraints
	private GridBagConstraints createGBC(int x, int y) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = x;
		c.gridy = y;
		c.ipadx = 14;
		c.ipady = 8;
		c.fill = GridBagConstraints.HORIZONTAL;
		return c;
	}

	// create Myaes
	public Myaes() {
		// init files
		this.inFile = null;
		this.outFile = null;

		// init panels
		setLayout(new GridBagLayout());
		setBorder(new EmptyBorder(10, 16, 14, 16));
		this.keyPanel = new JPanel(new GridBagLayout());
		this.keyPanel2 = new JPanel(new BorderLayout());
		this.inPanel = new JPanel(new GridBagLayout());
		this.buttonPanel = new JPanel(new GridLayout(1, 2));
		this.encryptPanel = new JPanel(new FlowLayout());
		this.decryptPanel = new JPanel(new FlowLayout());

		// init labels
		this.keyLabel = new JLabel("Key:");
		this.inLabel = new JLabel("Input File:");
		this.statusLabel = new JLabel("Status: Waiting", JLabel.CENTER);
		this.headLabel = new JLabel("AES File Encryption Software ", JLabel.CENTER);

		// init textareas
		this.keyArea = new JTextArea(1, 26);
		this.inArea = new JTextArea(1, 20);
		this.inArea.setEditable(false);

		// init scrollpanes
		this.keyPane = new JScrollPane(this.keyArea, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.inPane = new JScrollPane(this.inArea, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		// init buttons
		// this.inButton = new JButton("Select");
		this.inButton = createSimpleButton(" Select ");
		this.encryptButton = createSimpleButton2(" Encrypt ");
		this.decryptButton = createSimpleButton2(" Decrypt ");

		this.inButton.addActionListener(new InListener());
		this.encryptButton.addActionListener(new EncryptListener());
		this.decryptButton.addActionListener(new DecryptListener());

		// assemble
		GridBagConstraints c;

		c = createGBC(0, 0);
		this.keyPanel.add(this.keyLabel, c);

		c = createGBC(0, 1);
		this.keyPanel.add(this.keyPane, c);

		this.keyPanel2.add(this.keyPanel, BorderLayout.WEST);

		c = createGBC(0, 0);
		add(this.headLabel, c);

		c = createGBC(0, 1);
		add(keyPanel2, c);

		c = createGBC(0, 1);
		this.inPanel.add(this.inLabel, c);

		c = createGBC(0, 2);
		this.inPanel.add(this.inPane, c);

		c = createGBC(1, 2);
		inButton.setSize(5, 20);
		this.inPanel.add(this.inButton, c);

		c = createGBC(0, 2);
		add(this.inPanel, c);

		this.encryptPanel.add(this.encryptButton);
		this.decryptPanel.add(this.decryptButton);

		this.buttonPanel.add(this.encryptPanel);
		this.buttonPanel.add(this.decryptPanel);
		c = createGBC(0, 3);
		add(this.buttonPanel, c);

		c = createGBC(0, 4);
		add(this.statusLabel, c);

		// log
		log("Initialize Session");
	}

	private static void log(String s) {
		try {
			File logFile = new File("log.txt");
			logFile.createNewFile();
			FileWriter fw = new FileWriter(logFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("[" + LocalDateTime.now() + "] " + s + System.getProperty("line.separator"));
			bw.close();
		} catch (IOException ioe) {
		}
	}

	// get file name without extension
	private static String getNameNoExtension(File f) {
		String s = f.getAbsolutePath();
		int sep = s.lastIndexOf(File.separator) + 1;
		String ss = s.substring(sep);
		int dot = ss.indexOf(".");
		return ss.substring(0, dot);
	}

	// get file extension
	private static String getExtension(File f) {
		String s = f.getAbsolutePath();
		int dot = s.lastIndexOf(".") + 1;
		return s.substring(dot);
	}

	// get file path
	private static String getPath(File f) {
		String s = f.getAbsolutePath();
		int sep = s.lastIndexOf(File.separator);
		return s.substring(0, sep) + File.separator;
	}

	// sets error messages
	private static String getError(Exception e) {
		if (e.getClass().getSimpleName().equals("CustomException")) {
			String s = e.getMessage();
			switch (s) {
				case "BadPaddingException":
					return "Incorrect Key";
				case "IllegalBlockSizeException":
					return "File Not Encrypted";
				default:
					return s;
			}
		} else {
			String s = e.getClass().getSimpleName();
			switch (s) {
				case "NullPointerException":
					return "No Files Selected";
				default:
					return s;
			}
		}
	}

	// listener for the input file button
	private class InListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// init JFileChooser
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			int val = fc.showOpenDialog(Myaes.this);
			if (val == JFileChooser.APPROVE_OPTION) {
				Myaes.this.inFile = fc.getSelectedFile();
				Myaes.this.inArea.setText(Myaes.this.inFile.getAbsolutePath());
			}
		}
	}

	// listener for the encrypt button
	private class EncryptListener implements ActionListener {
		private int count;

		public void actionPerformed(ActionEvent e) {
			try {
				// file
				if (Myaes.this.inFile.isFile()) {
					// check file
					if (!Myaes.this.inFile.getName().contains("enc")) {
						// get output file
						Myaes.this.outFile = new File(
								Myaes.getPath(Myaes.this.inFile) + Myaes.getNameNoExtension(Myaes.this.inFile)
										+ ".enc." + Myaes.getExtension(Myaes.this.inFile));

						// encrypt file
						Aesutils.encrypt(Myaes.this.keyArea.getText(), Myaes.this.inFile, Myaes.this.outFile);

						// status report
						Myaes.this.statusLabel.setText("Status: Encrypting...");
						Timer timer = new Timer(1000, new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								Myaes.this.statusLabel.setText("Status: 1 File Encrypted");
							}
						});
						timer.setRepeats(false);
						timer.start();

						// log
						Myaes.log("Encrypt: " + Myaes.this.inFile.getName());

						// delete
						Myaes.this.inFile.delete();

						// reset path
						Myaes.this.inFile = Myaes.this.outFile;
						Myaes.this.inArea.setText(Myaes.this.inFile.getAbsolutePath());
					} else {
						Myaes.this.statusLabel.setText("Status: File Already Encrypted");
					}

					// directory
				} else if (Myaes.this.inFile.isDirectory()) {
					File[] fileList = Myaes.this.inFile.listFiles();
					this.count = 0;
					for (int i = 0; i < fileList.length; i++) {
						if (!fileList[i].getName().contains("enc")) {
							// bump count
							this.count++;

							// get output file
							Myaes.this.outFile = new File(
									Myaes.getPath(fileList[i]) + Myaes.getNameNoExtension(fileList[i])
											+ ".enc." + Myaes.getExtension(fileList[i]));

							// encrypt file
							Aesutils.encrypt(Myaes.this.keyArea.getText(), fileList[i], Myaes.this.outFile);

							// status report
							Myaes.this.statusLabel.setText("Status: Encrypting...");

							// log
							Myaes.log("Encrypt: " + fileList[i].getName());

							// delete
							fileList[i].delete();
						}
					}
					// status report
					Timer timer = new Timer(1000, new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							Myaes.this.statusLabel
									.setText("Status: " + EncryptListener.this.count + " File(s) Encrypted");
						}
					});
					timer.setRepeats(false);
					timer.start();
				}
			} catch (Exception ex) {
				// status report
				String errorMessage = Myaes.getError(ex);
				Myaes.this.statusLabel.setText("Status: " + errorMessage);

				// log
				Myaes.log("Error: " + errorMessage);

			}
		}
	}

	// listener for the decrypt button
	private class DecryptListener implements ActionListener {
		private int count;

		public void actionPerformed(ActionEvent e) {
			try {
				// file
				if (Myaes.this.inFile.isFile()) {
					// check file
					if (Myaes.this.inFile.getName().contains("enc")) {
						// get output file
						Myaes.this.outFile = new File(
								Myaes.getPath(Myaes.this.inFile) + Myaes.getNameNoExtension(Myaes.this.inFile)
										+ "." + Myaes.getExtension(Myaes.this.inFile));

						// decrypt file
						Aesutils.decrypt(Myaes.this.keyArea.getText(), Myaes.this.inFile, Myaes.this.outFile);

						// status report
						Myaes.this.statusLabel.setText("Status: Decrypting...");
						Timer timer = new Timer(1000, new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								Myaes.this.statusLabel.setText("Status: 1 File Decrypted");
							}
						});	
						timer.setRepeats(false);
						timer.start();

						// log
						Myaes.log("Decrypt: " + Myaes.this.inFile.getName());

						// delete
						Myaes.this.inFile.delete();

						// reset path
						Myaes.this.inFile = Myaes.this.outFile;
						Myaes.this.inArea.setText(Myaes.this.inFile.getAbsolutePath());
					} else {
						Myaes.this.statusLabel.setText("Status: File Not Encrypted");
					}

					// directory
				} else if (Myaes.this.inFile.isDirectory()) {
					File[] fileList = Myaes.this.inFile.listFiles();
					this.count = 0;
					for (int i = 0; i < fileList.length; i++) {
						if (fileList[i].getName().contains("enc")) {
							// bump count
							this.count++;

							// get output file
							Myaes.this.outFile = new File(
									Myaes.getPath(fileList[i]) + Myaes.getNameNoExtension(fileList[i]) + "."
											+ Myaes.getExtension(fileList[i]));

							// decrypt file
							Aesutils.decrypt(Myaes.this.keyArea.getText(), fileList[i], Myaes.this.outFile);

							// status report
							Myaes.this.statusLabel.setText("Status: Decrypting...");

							// log
							Myaes.log("Decrypt: " + fileList[i].getName());

							// delete
							fileList[i].delete();
						}
					}
					// status report
					Timer timer = new Timer(1000, new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							Myaes.this.statusLabel
									.setText("Status: " + DecryptListener.this.count + " File(s) Decrypted");
						}
					});
					timer.setRepeats(false);
					timer.start();
				}
			} catch (Exception ex) {
				// status report
				String errorMessage = Myaes.getError(ex);
				Myaes.this.statusLabel.setText("Status: " + errorMessage);

				// log
				Myaes.log("Error: " + errorMessage);
			}
		}
	}

	public static void onExit() {
		Myaes.log("Close Session");
		System.exit(0);
	}

	// run
	public static void main(String[] args) {
		// create gui
		JFrame frame = new JFrame("AES File Encryptor ");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onExit();
			}
		});
		frame.setResizable(true);
		frame.add(new Myaes());
		frame.pack();
		frame.setVisible(true);
	}
}

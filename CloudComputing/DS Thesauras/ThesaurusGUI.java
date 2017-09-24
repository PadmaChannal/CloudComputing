package ds.thesaurus;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * References:
 * 1. http://www.oracle.com/webfolder/technetwork/tutorials/obe/java/SocketProgramming/SocketProgram.html
 * 2. http://docs.oracle.com/javase/tutorial/uiswing/components/menu.html
 */
public class ThesaurusGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final int FRAME_WIDTH = 600;
	private static final int FRAME_HEIGHT = 400;
	private static final String SEPERATOR = ",";
	private static final String TITLE = "Thesaurus Application";
	private static final String HOST_NAME = "127.0.0.1";
	private static final int PORT_NUMBER = 8005;

	private JTextArea textArea;

	public ThesaurusGUI() {
		setLayout(new BorderLayout());
		initTextArea();
		initializeExitButton();
	}

	/**
	 * Initialize the exit button to close the client application
	 *
	 */
	private void initializeExitButton() {
		JButton exitButton = new JButton("Exit");

		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		add(exitButton, BorderLayout.SOUTH);
	}

	/**
	 * Initialize the text area for the thesaurus application
	 *
	 */
	private void initTextArea() {
		// Create a text area with 20 rows and 200 columns
		textArea = new JTextArea(getTextBlock(), 20, 200);

		// Wrap line by words instead of characters
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);

		// Give 10px padding from all sides
		textArea.setBorder(BorderFactory.createCompoundBorder(textArea.getBorder(),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		// Add textArea in the JFrame
		add(textArea, BorderLayout.NORTH);

		textArea.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// For right mouse click
				if (SwingUtilities.isRightMouseButton(e)) {

					// Identify the selected word
					final Word word = identifySelectedWord(e);

					// Connect to server to get the word meanings
					final String synonyms = retrieveSynonymsFromServer(word);

					// Create a popup menu showing the word meanings
					createPopup(word, synonyms, e);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}
		});
	}

	/**
	 * Get the synonyms of the word from the server
	 *
	 * @param word
	 * @return the comma-separated list of synonyms for the input word and "" if
	 *         synonyms does not exist
	 *
	 */
	private String retrieveSynonymsFromServer(final Word word) {
		String synonyms = null;

		// Create and open a client socket. Create input and output streams to communicate with the server
		try (Socket echoSocket = new Socket(HOST_NAME, PORT_NUMBER);
				PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()))) {

			// Send the word to the server
			out.println(word.getWord());

			// Receive the synonyms from the server
			synonyms = in.readLine();
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + HOST_NAME);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " + HOST_NAME);
			e.printStackTrace();
		}

		return null != synonyms ? synonyms : "";
	}


	/**
	 * Right click on the word to receive the synonyms for that word from the server.
	 * This method creates the popup to display the synonyms receiveed from the server
	 *
	 * @param word
	 * @param synonyms
	 * @param mouseEvent
	 */
	private void createPopup(final Word word, final String synonyms, final MouseEvent mouseEvent) {
		JPopupMenu jPopupMenu = new JPopupMenu();
		JMenuItem jMenuItem = null;

		if (!"".equals(synonyms)) {
			String[] synonymsArr = synonyms.split(SEPERATOR);

			for (String synonym : synonymsArr) {
				jMenuItem = new JMenuItem(synonym);

				jMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// Replace the word in the text block
						replaceWordWithSynonym(e);
					}

					private void replaceWordWithSynonym(final ActionEvent e) {
						StringBuffer textBlock = new StringBuffer(textArea.getText());
						textBlock.replace(word.getStartIndex() + 1, word.getEndIndex(), e.getActionCommand());
						textArea.setText(textBlock.toString());
					}
				});

				if (null != jMenuItem)
					jPopupMenu.add(jMenuItem);
			}
		}

		jPopupMenu.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
	}


	/**
	 * Identify the word based on the current mouse position
	 *
	 * @param mouseEvent
	 * @return
	 */
	private Word identifySelectedWord(final MouseEvent mouseEvent) {

		// Get the position of the mouse in the textArea
		int pos = textArea.viewToModel(new Point(mouseEvent.getX(), mouseEvent.getY()));
		final String textBlock = textArea.getText();

		Word selectedWord = null;

		// Identify the word using the mouse position
		if (textBlock.charAt(pos) != ' ') {
			int startPos = pos;
			int endPos = pos;

			while (textBlock.charAt(startPos) != ' ' && startPos > 0) {
				startPos--;
			}
			while (textBlock.charAt(endPos) != ' ' && endPos < textBlock.length() - 1) {
				endPos++;
			}

			selectedWord = new Word(textBlock.substring(startPos + 1, endPos).replaceAll("[^A-Za-z]", ""), startPos,
					endPos);
		}

		return selectedWord;
	}

	private String getTextBlock() {
		return "A Boy was given permission to put his hand into a pitcher to get some filberts. But he took such a great fistful that he could not draw his hand out again. There he stood, unwilling to give up a single filbert and yet unable to get them all out at once. Vexed and disappointed he began to cry. My boy, said his mother, be satisfied with half the nuts you have taken and you will easily get your hand out. Then perhaps you may have some more filberts some other time. Do not attempt too much at once.";
	}

	public static void main(String args[]) {
		ThesaurusGUI thesaurusGUI = new ThesaurusGUI();

		// Exit the application when JFrame is closed
		thesaurusGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set the width and height of the JFrame
		thesaurusGUI.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		thesaurusGUI.setResizable(true);
		thesaurusGUI.setTitle(TITLE);

		// Open the window at the center of the screen
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		thesaurusGUI.setLocation(dimension.width / 2 - thesaurusGUI.getSize().width / 2,
				dimension.height / 2 - thesaurusGUI.getSize().height / 2);
		thesaurusGUI.setVisible(true);
	}
}

package ds.thesaurus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JTextArea;

/**
 *
 * References:
 * 1. http://www.oracle.com/webfolder/technetwork/tutorials/obe/java/SocketProgramming/SocketProgram.html
 * 2. http://docs.oracle.com/javase/tutorial/uiswing/components/menu.html
 */
public class RequestHandler implements Runnable {
	private static final String FILE_PATH = "./DS_lab1_synonyms.txt";
	private static final String SEPARATOR = ":";
	private Socket client;
	private JTextArea console;

	public RequestHandler(Socket client, JTextArea console) {
		this.client = client;
		this.console = console;
	}

	/**
	 * Will be invoked when executor.execute(worker) method in the Server class
	 * is called
	 */
	@Override
	public void run() {
		// Create reader and writer to read and write the data from
		try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));) {
			console.append("New thread started with name:" + Thread.currentThread().getName() + "\n");
			String word;
			while ((word = in.readLine()) != null) {
				console.append("Request received on thread " + Thread.currentThread().getName() + "\n User selected word: "
						+ word + "\n");
				String synonyms = getSynonymsForWord(word);
				writer.write(synonyms);
				writer.newLine();
				writer.flush();
			}
		} catch (IOException e) {
			console.append("I/O exception: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception ex) {
			console.append("Exception in Thread Run. Exception : " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	/**
	 * Retrieves the synonyms for the word from the synonyms file
	 *
	 * @param userSelectedWord
	 * @return comma-separated list of synonyms of the given word if it exists in the file otherwise return ""
	 */
	public String getSynonymsForWord(final String userSelectedWord) {
		String line = null;
		String wordMeaning = "";

		try {
			// FileReader reads text files in the default encoding.
			final FileReader fileReader = new FileReader(FILE_PATH);

			// Always wrap FileReader in BufferedReader.
			final BufferedReader bufferedReader = new BufferedReader(fileReader);
			while ((line = bufferedReader.readLine()) != null) {
				final String[] splittedLine = line.split(SEPARATOR);
				final String word = splittedLine[0];

				if (word.equalsIgnoreCase(userSelectedWord)) {
					wordMeaning = splittedLine[1];
					break;
				}
			}

			// Always close files.
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			console.append("FileNotFoundException: " + ex.getMessage());
			ex.printStackTrace();
		} catch (IOException ex) {
			console.append("I/O exception: " + ex.getMessage());
			ex.printStackTrace();
		}

		return wordMeaning;
	}

}

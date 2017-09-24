package ds.thesaurus;

/**
 *
 * References:
 * 1. http://www.oracle.com/webfolder/technetwork/tutorials/obe/java/SocketProgramming/SocketProgram.html
 * 2. http://docs.oracle.com/javase/tutorial/uiswing/components/menu.html
 */
public class Word {
	private String word;
	private int startIndex;
	private int endIndex;

	public Word(String word) {
		super();
		this.word = word;
	}
	public Word(String word, int startIndex, int endIndex) {
		super();
		this.word = word;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}
	public String getWord() {
		return word;
	}
	public int getStartIndex() {
		return startIndex;
	}
	public int getEndIndex() {
		return endIndex;
	}
}

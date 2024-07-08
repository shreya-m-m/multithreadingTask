package threadsTaskVersion2;

import javax.swing.Spring;

public class FileProcesserResult {
	private String fileName;
	private int wordCount;
	
	
	public FileProcesserResult(String fileName, int wordCount) {
		super();
		this.fileName = fileName;
		this.wordCount = wordCount;
	}


	public String getFileName() {
		return fileName;
	}


	public int getWordCount() {
		return wordCount;
	}
	
	
	

}

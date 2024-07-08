package threadsTaskVersion2;

import java.util.ArrayList;
import java.util.List;

public class FolderProcessor {
	private  String folderName;
	private List<FileProcesserResult> fileresults;
	private int totalWordCount;
	
	public FolderProcessor(String folderName) {
		this.folderName = folderName;
		this.fileresults = new ArrayList<>();
		this.totalWordCount = 0;
	}
	
	public String getFolderName() {
		return folderName;
	}
	
	public List<FileProcesserResult> getFileresults() {
		return fileresults;
	}
	
	public int getTotalWordCount() {
		return totalWordCount;
	}
	
	public void addProcessedFileResult(FileProcesserResult fileresult) {
		fileresults.add(fileresult);
		totalWordCount += fileresult.getWordCount();
	}
	
	

}

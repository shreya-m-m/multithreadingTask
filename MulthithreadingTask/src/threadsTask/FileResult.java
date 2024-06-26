package threadsTask;

import java.util.ArrayList;
import java.util.List;

public class FileResult {
	 private final String folderName;
	    private final String keyword;
	    private final List<String> fileNames;
	    private final List<Integer> counts;
	    private int totalCount;

	    public FileResult(String folderName, String keyword) {
	        this.folderName = folderName;
	        this.keyword = keyword;
	        this.fileNames = new ArrayList<>();
	        this.counts = new ArrayList<>();
	        this.totalCount = 0;
	    }

	    public void addFileCount(String fileName, int count) {
	        fileNames.add(fileName);
	        counts.add(count);
	        totalCount += count;
	    }

	    public String getFolderName() {
	        return folderName;
	    }

	    public String getKeyword() {
	        return keyword;
	    }

	    public List<String> getFileNames() {
	        return fileNames;
	    }

	    public List<Integer> getCounts() {
	        return counts;
	    }

	    public int getTotalCount() {
	        return totalCount;
	    }

}

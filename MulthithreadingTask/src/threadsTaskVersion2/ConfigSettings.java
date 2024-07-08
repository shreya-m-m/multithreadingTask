package threadsTaskVersion2;

public class ConfigSettings {
	  private final String folderPath;
	    private final String outputPath;
	    private final int batchSize;

	    public ConfigSettings(String folderPath, String outputPath, int batchSize) {
	        this.folderPath = folderPath;
	        this.outputPath = outputPath;
	        this.batchSize = batchSize;
	    }

	    public String getFolderPath() {
	        return folderPath;
	    }

	    public String getOutputPath() {
	        return outputPath;
	    }

	    public int getBatchSize() {
	        return batchSize;
	    }

}

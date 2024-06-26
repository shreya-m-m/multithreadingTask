package threadsTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileProcessor extends Thread{
	  private final File file;
	    private final String keyword;
	    private int fileCount; // Count of occurrences in the current file

	    public FileProcessor(File file, String keyword) {
	        this.file = file;
	        this.keyword = keyword;
	        this.fileCount = 0;
	    }
	    
	    @Override
	    public void run() {
	        System.out.println(Thread.currentThread().getName() + " is processing " + file.getName());
	        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                // Split line into words and count occurrences of keyword
	                String[] words = line.split("\\s+");
	                for (String word : words) {
	                    if (word.equalsIgnoreCase(keyword)) {
	                        fileCount++;
	                    }
	                }
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
	    }
	    public int getFileCount() {
	        return fileCount;
	    }

	    public String getFileName() {
	        return file.getName();
	    }
	}
	    



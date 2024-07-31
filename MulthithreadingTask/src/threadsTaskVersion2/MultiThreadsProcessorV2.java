package threadsTaskVersion2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.BufferedReader;
import threadsTask.FileResult;

public class MultiThreadsProcessorV2 {

	public static void main(String[] args) {
		
		 // Getting the configuration file path
        String configFilePath ="configureFile.txt";

        // Read and parse configuration settings
        ConfigSettings configSettings = readConfigSettings(configFilePath);

        if (configSettings == null) {
            System.out.println("Error reading configuration settings.");
            return;
        }
        
        //Get User input for the word to Count the occurrences
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter the word to be checked ");
		String keyword = scanner.next();
		
		scanner.close();	
		String foldersPath = configSettings.getFolderPath();
		String outputFile = configSettings.getOutputPath();
		int batchSize = configSettings.getBatchSize();
		
		//IniTialize a Concurrent Map to Store results
		ConcurrentHashMap<String, FolderProcessor> results = new ConcurrentHashMap<>();
	
		// Getting all the files inside the main directory
		
		File path = new File(foldersPath);
		File[] folders= path.listFiles(File::isDirectory);
		
		if(folders!= null) {
			// Calculate number of threads needed based on batchSize
            int threadsNum = (int) Math.ceil((double) folders.length / batchSize);
			ExecutorService exeservice = Executors.newFixedThreadPool(threadsNum);
			AtomicInteger activeThreads = new AtomicInteger(0);
		
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			
			for(int i=0; i< threadsNum; i++) {
				int startId = i * batchSize;
				int endId = Math.min(startId + batchSize, folders.length);
				  File[] foldersBatch = Arrays.copyOfRange(folders, startId, endId);
				  
				  exeservice.submit(() -> {
					  activeThreads.incrementAndGet();
					  long threadStartTime = System.currentTimeMillis();
					  System.out.println(Thread.currentThread().getName()+" started at: " + dateFormat.format(new Date(threadStartTime)));
					  folderToProcess(foldersBatch, keyword, results);
					  long threadEndTime = System.currentTimeMillis();
					  System.out.println(Thread.currentThread().getName()+ " ended at: " + dateFormat.format(new Date(threadEndTime)));
					  System.out.println(Thread.currentThread().getName()+ " total time taken: "+ (threadEndTime - threadStartTime)+ " ms");
					  activeThreads.decrementAndGet();
					  
				  });
			}
			exeservice.shutdown();
			
			try {
				exeservice.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
			
				e.printStackTrace();
			}
			long exeStartTime = System.currentTimeMillis();
			long exeEndTime = System.currentTimeMillis();
			System.out.println(" Total execution time: " + ( exeEndTime - exeStartTime ) + " ms");
			
			System.out.println(" Total Number of Threads Used " + threadsNum);
			
			System.out.println(" All folders are processed");
			
			results.forEach((folderName, folderResult) -> {
				System.out.println(" Folder: "+ folderName);
				System.out.println(" Total count of word " + keyword + " : "+ folderResult.getTotalWordCount());
				
				List<FileProcesserResult> fileResults = folderResult.getFileresults();
				System.out.println(" File-Wise Occurance ");
				
				for(FileProcesserResult fileResult : fileResults) {
					System.out.println(" File: "+ fileResult.getFileName() + " Occurrence: " + fileResult.getWordCount());
				}
				System.out.println("------------------------------");
			});
			
			//Write Result into CSV File
			writeResultToCsv(results, keyword, outputFile);
		} else {
			System.out.println("File not found outputFile");
			
		}
	}
	
	private static ConfigSettings readConfigSettings(String configFilePath) {
		
		Properties properties = new Properties();
		  try (FileInputStream input = new FileInputStream(configFilePath)) {
	            properties.load(input);
	            String folderPath = properties.getProperty("folderPath");
	            String outputPath = properties.getProperty("outputPath");
	            int batchSize = Integer.parseInt(properties.getProperty("batchSize"));

	            return new ConfigSettings(folderPath, outputPath, batchSize);
	        } catch (IOException | NumberFormatException e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	
	 private static void   folderToProcess(File[] folders, String keyword, ConcurrentHashMap<String, FolderProcessor> results) {
		 for(File folder: folders) {
			 System.out.println(Thread.currentThread().getName() + " now processing the folder: " + folder.getName());
			 
			 //Getting all text files in the the folder
			   File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
			   
			   if (files != null) {
				   FolderProcessor folderResult = new FolderProcessor(folder.getName());
				   for( File file : files) {
					   int fileWordCount = wordOccuranceCount(file, keyword);
					   folderResult.addProcessedFileResult(new FileProcesserResult(file.getName(), fileWordCount));
							   
				   }
				   //Store the result in the Concurrent map
				   results.put(folder.getName(),folderResult);
			   }else {
				   System.out.println("No Text file found in the folder " + folder.getName());
			   }
		 }
		 
	 }
	private static int wordOccuranceCount(File file, String keyword) {
		int count = 0;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				//Split line into words and count occurrences of the specified word
				String[] words = line.trim().toLowerCase().split("\\s+");
				for(String w : words) {
					if(w.equals(keyword)) {
						count++;
					}
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("File Not Found " + file.getName());
		} catch (IOException e) {
			System.err.println("Error reading file: " + file.getName());
		}
		return count;
	}

	private static void writeResultToCsv(ConcurrentHashMap<String, FolderProcessor> results, String keyword,String outputFile) {
		  try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
	            writer.println("Folder,Keyword,File,Count");

	            for (FolderProcessor folderResult : results.values()) {
	                String folderName = folderResult.getFolderName();

	                for (FileProcesserResult fileResult : folderResult.getFileresults()) {
	                    String fileName = fileResult.getFileName();
	                    int wordCount = fileResult.getWordCount();
	                    writer.printf("%s,%s,%s,%d\n", folderName, keyword, fileName, wordCount);
	                }
	            }
			System.out.println(" Result saved into CSV file: " + outputFile);
		} catch (IOException e) {
			System.out.println("Error while saving result into CSV: " + outputFile);
			e.printStackTrace();
		}
		
		
	}

	

	
	

	private static Object processingFolders(File[] folderBatch, String keyword,
			ConcurrentHashMap<String, FolderProcessor> results) {
		// TODO Auto-generated method stub
		return null;
	}

}

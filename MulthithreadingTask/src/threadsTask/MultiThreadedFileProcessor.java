package threadsTask;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class MultiThreadedFileProcessor {
	
	private static final int THREAD_POOL_SIZE= 5;
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		
		//User Input
		System.out.println("Enter the keyword to be checked ");
		String keyword = scanner.nextLine();
		scanner.close();// close the scanner after the user input 
		
		String rootDirectory = "C:/Users/nichiuser/Desktop/threads"; // Root where the folder and text files are present 
        String outputFilePath = "C:/Users/nichiuser/Desktop/threadsresult.csv";   //Output file where the result is Stored
        processFiles(rootDirectory, outputFilePath, keyword); 
		

	}
	private static void processFiles(String rootDirectory, String outputFilePath, String keyword) {
	    List<FileResult> results = new ArrayList<>();

	    try {
	        Files.createDirectories(Paths.get(outputFilePath).getParent()); // Create directories if they don't exist

	        // ExecutorService for managing threads
	        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

	        // Process files and collect results
	        Map<String, List<Path>> groupedFiles = Files.walk(Paths.get(rootDirectory))
	                .filter(Files::isRegularFile)
	                .collect(Collectors.groupingBy(path -> path.getParent().toString()));

	        for (Map.Entry<String, List<Path>> entry : groupedFiles.entrySet()) {
	            String folderPath = entry.getKey();
	            List<Path> fileList = entry.getValue();

	            FileResult folderResult = new FileResult(getFolderName(folderPath), keyword);

	            List<Future<Void>> futures = new ArrayList<>();

	            // Submit tasks to ExecutorService
	            fileList.forEach(path -> {
	                File file = path.toFile();
	                Callable<Void> task = () -> {
	                    FileProcessor processor = new FileProcessor(file, keyword);
	                    processor.start(); // Start processing in a separate thread
	                    processor.join(); // Wait for completion

	                    // Record filename and count in FileResult
	                    folderResult.addFileCount(processor.getFileName(), processor.getFileCount());
	                    return null;
	                };
	                futures.add(executor.submit(task));
	            });

	            // Wait for all tasks to complete
	            for (Future<Void> future : futures) {
	                try {
	                    future.get(); // Wait for task completion
	                } catch (InterruptedException | ExecutionException e) {
	                    e.printStackTrace();
	                }
	            }

	            // Add result to list
	            results.add(folderResult);

	            // Print results for the current folder
	            printFolderResults(folderResult);
	        }

	        // Shutdown ExecutorService
	        executor.shutdown();

	        // Write results to output CSV file
	        writeResultsToCsv(results, outputFilePath);

	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}


	    private static void printFolderResults(FileResult result) {
	        System.out.println("Folder: " + result.getFolderName());
	        System.out.println("Keyword: " + result.getKeyword());
	        System.out.println("Total Count: " + result.getTotalCount());

	        List<String> fileNames = result.getFileNames();
	        List<Integer> counts = result.getCounts();

	        System.out.println("File-wise counts:");
	        for (int i = 0; i < fileNames.size(); i++) {
	            System.out.println(fileNames.get(i) + ": " + counts.get(i));
	        }

	        System.out.println(); // Empty line for separation
	    }

	    private static String getFolderName(String folderPath) {
	        return Paths.get(folderPath).getFileName().toString();
	    }

	    private static void writeResultsToCsv(List<FileResult> results, String outputFilePath) {
	        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath))) {
	            writer.println("Folder,Keyword,File,Count");

	            for (FileResult result : results) {
	                String folderName = result.getFolderName();
	                String keyword = result.getKeyword();
	                List<String> fileNames = result.getFileNames();
	                List<Integer> counts = result.getCounts();

	                for (int i = 0; i < fileNames.size(); i++) {
	                    writer.printf("%s,%s,%s,%d\n", folderName, keyword, fileNames.get(i), counts.get(i));
	                }
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }

}

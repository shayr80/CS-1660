package com.shayr.searchEngineGUI;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.api.gax.paging.Page;
import com.google.cloud.dataproc.v1.HadoopJob;
import com.google.cloud.dataproc.v1.Job;
import com.google.cloud.dataproc.v1.JobControllerClient;
import com.google.cloud.dataproc.v1.JobControllerSettings;
import com.google.cloud.dataproc.v1.JobMetadata;
import com.google.cloud.dataproc.v1.JobPlacement;
import com.google.cloud.dataproc.v1.JobStatus;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


// Author : Shay Rounsville

public class searchEngineGUI extends JFrame implements ActionListener {
	
	// enum for gui pages, used to keep track of current page
	enum Pages {
    	Main,
    	Selection,
    	Search,
    	TopN,
    	SearchResult,
    	TopNResult
    }
	
	// Variables for gui
	Font defaultFont;
    Font loadingFont;
    JLabel titleLabel;
    JTextPane selectedFilesDisplay;
    StyledDocument selectedFilesDoc;
    SimpleAttributeSet center;
    JButton chooseFilesButton;
    JFileChooser fileChooser;
    File[] files;
    JButton constructIndicesButton;
    JLabel loadingLabel;
    JButton closeButton;
    JLabel selectionLabel;
    JLabel selectionActionLabel;
    JButton searchForTermButton;
    JButton topNFrequentTermsButton;
    JLabel searchLabel;
    JTextField searchField;
    JButton submitSearchButton;
    JButton backToSelectionButton;
    JLabel topNLabel;
    JTextField topNField;
    JButton submitTopNButton;
    JLabel searchResultLabel;
    JScrollPane searchScroll;
    JButton backToSearchButton;
    JLabel topNResultLabel;
    JScrollPane topNScroll;
    JButton backToTopNButton;
    JLabel invalidEntryLabel;
    JLabel indicesTimeTakenLabel;
    JLabel searchTimeTakenLabel;
    
    // Used to store time taken for jobs
    long indicesTimeTaken;
    long searchTimeTaken;
    
    // Used to keep track of current page to determine which gui elements need to be hidden when navigating to different page
    Pages currentPage;
    
    // GCP variables
    String projectId = "";
    String clusterName = "";
    String bucketName = "";
    String bucketPath = "gs://" + bucketName;
    Storage storage;
    String region = "";
    String endpoint = String.format("%s-dataproc.googleapis.com:443", region);

    public searchEngineGUI() {
    	// Initializing elements and setting up page layouts upon creating an instance of the program (which is done in main)
        initializeElements();
        setLayout();
    }

    void initializeElements() {
    	// Initializing elements to set up user interface and storage access
    	System.out.println("\nInitializing elements");
        defaultFont = new Font("Arial", Font.PLAIN, 30);
        loadingFont = new Font("Arial", Font.PLAIN, 16);

        titleLabel = new JLabel("Mini Search Engine", SwingConstants.CENTER);
        titleLabel.setFont(defaultFont);

        selectedFilesDisplay = new JTextPane();
        selectedFilesDisplay.setFont(defaultFont);
        selectedFilesDisplay.setEditable(false);
        selectedFilesDisplay.setOpaque(false);
        
        center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);

        chooseFilesButton = new JButton("Choose Files");
        chooseFilesButton.setFont(defaultFont);
        chooseFilesButton.addActionListener(this);

        fileChooser = new JFileChooser();

        constructIndicesButton = new JButton("Construct Inverted Indices");
        constructIndicesButton.setFont(defaultFont);
        constructIndicesButton.addActionListener(this);
        constructIndicesButton.setEnabled(false);

        loadingLabel = new JLabel("Uploading files to GCP, please wait...", SwingConstants.SOUTH_EAST);
        loadingLabel.setFont(loadingFont);
        
        closeButton = new JButton("X");
        closeButton.setFont(loadingFont);
        closeButton.addActionListener(this);

        selectionLabel = new JLabel("Inverted Indices Constructed", SwingConstants.CENTER);
        selectionLabel.setFont(defaultFont);
        
        selectionActionLabel = new JLabel("Select Action Below", SwingConstants.CENTER);
        selectionActionLabel.setFont(defaultFont);
        
        searchForTermButton = new JButton("Search for Term");
        searchForTermButton.setFont(defaultFont);
        searchForTermButton.addActionListener(this);

        topNFrequentTermsButton = new JButton("Top-N Frequent Terms");
        topNFrequentTermsButton.setFont(defaultFont);
        topNFrequentTermsButton.addActionListener(this);

        searchLabel = new JLabel("Enter Term Below", SwingConstants.CENTER);
        searchLabel.setFont(defaultFont);

        searchField = new JTextField();
        searchField.setFont(defaultFont);
        searchField.setForeground(Color.GRAY);

        submitSearchButton = new JButton("Search");
        submitSearchButton.setFont(defaultFont);
        submitSearchButton.addActionListener(this);

        backToSelectionButton = new JButton("Back to Selection");
        backToSelectionButton.setFont(loadingFont);
        backToSelectionButton.addActionListener(this);

        topNLabel = new JLabel("Enter N Value Below", SwingConstants.CENTER);
        topNLabel.setFont(defaultFont);
        
        topNField = new JTextField();
        topNField.setFont(defaultFont);
        topNField.setForeground(Color.GRAY);

        submitTopNButton = new JButton("Search");
        submitTopNButton.setFont(defaultFont);
        submitTopNButton.addActionListener(this);

        searchResultLabel = new JLabel("Search Results for ", SwingConstants.CENTER);
        searchResultLabel.setFont(defaultFont);
        
        searchScroll = new JScrollPane();

        backToSearchButton = new JButton("Back to Search");
        backToSearchButton.setFont(loadingFont);
        backToSearchButton.addActionListener(this);

        topNResultLabel = new JLabel("Top-N Results for N = ", SwingConstants.CENTER);
        topNResultLabel.setFont(defaultFont);
        
        topNScroll = new JScrollPane();

        backToTopNButton = new JButton("Back to Top-N");
        backToTopNButton.setFont(loadingFont);
        backToTopNButton.addActionListener(this);
        
        invalidEntryLabel = new JLabel("Invalid term, please try again", SwingConstants.CENTER);
        invalidEntryLabel.setFont(loadingFont);
        invalidEntryLabel.setForeground(Color.RED);
        
        indicesTimeTakenLabel = new JLabel("Inverted indices constructed in ", SwingConstants.CENTER);
        indicesTimeTakenLabel.setFont(loadingFont);
        
        searchTimeTakenLabel = new JLabel("Your search was executed in ", SwingConstants.CENTER);
        searchTimeTakenLabel.setFont(loadingFont);
        
        // Initialize storage
		storage = StorageOptions.newBuilder()
                .setProjectId(projectId)
                .build()
                .getService();
        
        System.out.println("\nElements initialized");
    }

    void setLayout() {
    	// Setting up jframe
    	System.out.println("\nSetting page layouts");
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	setLayout(null);
    	setSize(800, 600);
    	setVisible(true);
    	
        // Setting up layout for starting page
    	titleLabel.setBounds(250, 75, 300, 50);
    	add(titleLabel);
    	chooseFilesButton.setBounds(250, 200, 300, 50);
    	add(chooseFilesButton);
    	selectedFilesDisplay.setBounds(150, 260, 500, 150);
    	add(selectedFilesDisplay);
    	constructIndicesButton.setBounds(150, 420, 500, 50);
    	add(constructIndicesButton);
    	loadingLabel.setBounds(0, 550, 790, 50);
    	loadingLabel.setVisible(false);
    	add(loadingLabel);
    	closeButton.setBounds(750, 0, 50, 50);
    	add(closeButton);
    	
    	// Setting up layout for selection page, hiding elements to start
    	selectionLabel.setBounds(150, 50, 500, 50);
    	selectionLabel.setVisible(false);
    	add(selectionLabel);
    	selectionActionLabel.setBounds(250, 100, 300, 50);
    	selectionActionLabel.setVisible(false);
    	add(selectionActionLabel);
    	indicesTimeTakenLabel.setBounds(0, 175, 800, 50);
    	indicesTimeTakenLabel.setVisible(false);
    	add(indicesTimeTakenLabel);
    	searchForTermButton.setBounds(250, 250, 300, 50);
    	searchForTermButton.setVisible(false);
    	add(searchForTermButton);
    	topNFrequentTermsButton.setBounds(200, 350, 400, 50);
    	topNFrequentTermsButton.setVisible(false);
    	add(topNFrequentTermsButton);
    	
    	// Setting up layout for search page, hiding elements to start
        searchLabel.setBounds(250, 50, 300, 50);
        searchLabel.setVisible(false);
        add(searchLabel);
        searchField.setBounds(250, 200, 300, 50);
        searchField.setVisible(false);
        add(searchField);
        submitSearchButton.setBounds(250, 350, 300, 50);
        submitSearchButton.setVisible(false);
        add(submitSearchButton);
        backToSelectionButton.setBounds(275, 425, 250, 50);
        backToSelectionButton.setVisible(false);
        add(backToSelectionButton);
        invalidEntryLabel.setBounds(0, 275, 800, 50);
        invalidEntryLabel.setVisible(false);
        add(invalidEntryLabel);
        
        // Setting up layout for top-n page, hiding elements to start
        topNLabel.setBounds(225, 50, 350, 50);
        topNLabel.setVisible(false);
        add(topNLabel);
        topNField.setBounds(250, 200, 300, 50);
        topNField.setVisible(false);
        add(topNField);
        submitTopNButton.setBounds(250, 350, 300, 50);
        submitTopNButton.setVisible(false);
        add(submitTopNButton);
        
        // Setting up layout for search result page, hiding elements to start
        searchResultLabel.setBounds(0, 10, 800, 50);
        searchResultLabel.setVisible(false);
        add(searchResultLabel);
        searchTimeTakenLabel.setBounds(0, 50, 800, 40);
        searchTimeTakenLabel.setVisible(false);
        add(searchTimeTakenLabel);
        backToSearchButton.setBounds(275, 525, 250, 50);
        backToSearchButton.setVisible(false);
        add(backToSearchButton);
        
        // Setting up layout for top-n result page, hiding elements to start
        topNResultLabel.setBounds(0, 10, 800, 50);
        topNResultLabel.setVisible(false);
        add(topNResultLabel);
        backToTopNButton.setBounds(275, 525, 250, 50);
        backToTopNButton.setVisible(false);
        add(backToTopNButton);
        
        currentPage = Pages.Main;
        
        System.out.println("\nPage layouts set");
    }
    
    public void actionPerformed(ActionEvent e) {
    	JButton button = (JButton) e.getSource();
    	
    	// Handling Button Clicks
    	if(button == chooseFilesButton) {
    		System.out.println("\nChoosing files");
    		openFileChooser();
    	}
    	if(button == constructIndicesButton) {
    		System.out.println("\nSubmitting inverted indices job to GCP");
    		button.setEnabled(false);
    		clickIndicesButton();
    	}
    	if(button == searchForTermButton || button == backToSearchButton) {
    		System.out.println("\nNavigating to search page");
    		showSearchPage();
    	}
    	if(button == topNFrequentTermsButton || button == backToTopNButton) {
    		System.out.println("\nNavigating to top-N page");
    		showTopNPage();
    	}
    	if(button == backToSelectionButton) {
    		System.out.println("\nNavigating to selection page");
    		invalidEntryLabel.setVisible(false);
    		showSelectionPage();
    	}
    	if(button == submitSearchButton) {
    		clickSubmitSearchButton();
    	}
    	if(button == submitTopNButton) {
    		clickSubmitTopNButton();
    	}
    	if(button == closeButton) {
    		System.out.println("\nClosing application");
    		dispose();
    	}
    }
    
    public void openFileChooser() {
    	// Setting up file chooser, showing/hiding relevant GUI elements
    	selectedFilesDisplay.setVisible(false);
    	selectedFilesDisplay.setText("Selected Files:");
    	selectedFilesDoc = selectedFilesDisplay.getStyledDocument();
    	fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    	fileChooser.setMultiSelectionEnabled(true);
    	fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
    	fileChooser.showOpenDialog(this);
    	files = fileChooser.getSelectedFiles();
	   	
    	// Creating a new thread to upload selected files to GCP storage
	   	Thread uploadThread = new Thread() {
	   		public void run() {
	   			// Setting up gui before uploading files
	   			SwingUtilities.invokeLater(new Runnable() {
	   	    		public void run() {
	   	    			for(File f : files) {
	   	    				System.out.println("\nSelecting file: " + f.getName());
	   	    				try {
	   	    					selectedFilesDoc.insertString(selectedFilesDoc.getLength(), "\n" + f.getName(), center);
	   	    				} catch (BadLocationException e) {
	   	    					e.printStackTrace();
	   	    				}
	   	    			}
	   	    	    	selectedFilesDoc.setParagraphAttributes(0, selectedFilesDoc.getLength(), center, false);
	   	    			selectedFilesDisplay.setVisible(true);
	   	    			loadingLabel.setText("Uploading file(s) to GCP, please wait...");
	   	    		   	loadingLabel.setVisible(true);
	   	    		   	System.out.println("\nUploading selected files to GCP storage bucket, please wait...");
	   	    		}
	   	    	});
	   			
	   			// Checking if files are in storage bucket or uploading files to storage bucket if they don't already exist
	   			uploadFiles(storage, files, "");
	   			
	   			// Updating gui after files have been checked/uploaded
	   			SwingUtilities.invokeLater(new Runnable() {
	   				public void run() {
	   					loadingLabel.setVisible(false);
	   					constructIndicesButton.setEnabled(true);
	   					System.out.println("\nFiles uploaded to GCP storage bucket, ready to construct inverted indices.");
	   				}
	   			});
	   		}
	   	};
	   	
	   	// Starting the above thread
	   	uploadThread.start();
    }
    
    // Uploading files to GCP storage bucket if they don't already exist
    public void uploadFiles(Storage storage, File[] files, String folder) {
    	for(File file : files) {
    		if(file.isDirectory()) {
    			// Note: when running on local machine, must use:
    			//String[] localPath = file.getAbsolutePath().split("\\\\");
    			// But must use the below command when running through docker container
    			String[] localPath = file.getAbsolutePath().split("/");
    			String localFolder = localPath[localPath.length - 1];
    			String absFolder;
    			if(!folder.isEmpty()) {
        			absFolder = folder + localFolder + "/";
    			} else {
    				absFolder = localFolder + "/";
    			}
    			uploadFiles(storage, file.listFiles(), absFolder);
    		} else {
    			try {
    				// Checking if object exists in storage bucket, if not then uploading it
					if(!checkIfObjectExists(storage, "Data/" + folder + file.getName())) {
					    BlobId blobId = BlobId.of(bucketName, "Data/" + folder + file.getName());
					    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
					    storage.create(blobInfo, Files.readAllBytes(Paths.get(file.getAbsolutePath())));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
    		}
    	}
    }
    
    // Helper method to check if a file exists in the GCP storage bucket
    public boolean checkIfObjectExists(Storage storage, String objectName) throws IOException {
    	Blob blob = storage.get(bucketName, objectName);
    	if(blob != null && blob.exists()) {
    		return true;
    	}
    	return false;
    }
    
    public void clickIndicesButton() {
    	// Storing start time to calculate time taken for constructing inverted indices, including communication between client and GCP
    	long startTime = System.currentTimeMillis();
    	
    	// Creating a new thread to submit job to GCP
    	Thread indicesThread = new Thread() {
	   		public void run() {
	   			// Setting up gui before running job
	   			SwingUtilities.invokeLater(new Runnable() {
	   	    		public void run() {
	   	    			loadingLabel.setText("Running inverted indices job, please wait...");
	   	    		   	loadingLabel.setVisible(true);
	   	    		   	System.out.println("\nRunning inverted indices job, please wait...");
	   	    		}
	   	    	});
	   			
	   			// Running the job to construct inverted indices
	   			String content = constructInvertedIndices();
	   			
	   			// Updating gui after job is finished
	   			SwingUtilities.invokeLater(new Runnable() {
	   	    		public void run() {
	   	    			// Calculating time taken for inverted indices job
	   	    			indicesTimeTaken = System.currentTimeMillis() - startTime;
	   	    			
	   	    			// Checking result of job, exiting if unsuccessful, moving to selection page if successful
	   	    			if(content == null || content.isEmpty()) {
	   	    				System.out.println("\nError fetching data from inverted indices job. Exiting...");
	   	    				System.exit(-1);
	   	    			}
	   	    			else {
	   	    				System.out.println("\nInverted indices constructed, ready to search");
		   	    			loadingLabel.setVisible(false);
		   	    			showSelectionPage();
	   	    			}
	   	    		}
	   	    	});
	   		}
	   	};
	   	
	   	// Starting the above thread
	   	indicesThread.start();
    }
    
    private String constructInvertedIndices() {
    	try {
    		// Configuring JobControllerSettings with endpoint
			JobControllerSettings jobControllerSettings = JobControllerSettings.newBuilder()
					.setEndpoint(endpoint)
					.build();
			
			// Creating JobControllerClient using above JobControllerSettings
			JobControllerClient jobControllerClient = JobControllerClient.create(jobControllerSettings);
			
			// Creating JobPlacement with clusterName set to the name of my dataproc cluster
			JobPlacement jobPlacement = JobPlacement.newBuilder()
					.setClusterName(clusterName)
					.build();
			
			// Deleting inverted indices output if it exists in cloud storage, since hadoop job will fail if output "folder" already exists
			if(storage.get(BlobId.of(bucketName, "Indices/Output/")) != null) {
				Page<Blob> blobs = storage.list(bucketName, Storage.BlobListOption.prefix("Indices/Output/"));
				Iterator<Blob> blobsIterable = blobs.iterateAll().iterator();
				while(blobsIterable.hasNext()) {
					Blob blob = blobsIterable.next();
					storage.delete(blob.getBlobId());
				}
			}
			
			// Creating HadoopJob with jar file from cloud storage and input and output folders in storage bucket
			HadoopJob hadoopJob = HadoopJob.newBuilder()
					.setMainJarFileUri(bucketPath + "/Jar/InvertedIndex.jar")
					.addArgs(bucketPath + "/Data/")
					.addArgs(bucketPath + "/Indices/Output/")
					.build();
			
			// Configuring job to be sent to dataproc cluster
			Job job = Job.newBuilder()
					.setPlacement(jobPlacement)
					.setHadoopJob(hadoopJob)
					.build();
					
			// Submitting async request to run job
			OperationFuture<Job, JobMetadata> submitJobAsOperationAsyncRequest = jobControllerClient.submitJobAsOperationAsync(projectId, region, job);
			
			// Waiting for job to complete
			Job response = submitJobAsOperationAsyncRequest.get();
			
			// Checking to see if indices were created successfully
			BlobId blobId = BlobId.of(bucketName, "Indices/Output/part-r-00000");
			byte[] blobContent = storage.readAllBytes(blobId);
			return new String(blobContent, StandardCharsets.UTF_8);
		} catch (IOException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
    	return "";
    }
    
    public void showSelectionPage() {
    	// Hiding gui elements from current page
    	switch (currentPage) {
    		case Main:
	    		// Hiding elements from start page
	        	titleLabel.setVisible(false);
	        	chooseFilesButton.setVisible(false);
	        	selectedFilesDisplay.setVisible(false);
	        	constructIndicesButton.setVisible(false);
        		break;
    		case Search:
	    		// Hiding elements from search page
	        	searchLabel.setVisible(false);
	        	searchField.setVisible(false);
	        	submitSearchButton.setVisible(false);
	        	backToSelectionButton.setVisible(false);
	        	break;
    		case TopN:
	    		// Hiding elements from top-n page
	        	topNLabel.setVisible(false);
	        	topNField.setVisible(false);
	        	submitTopNButton.setVisible(false);
	        	backToSelectionButton.setVisible(false);
	        	break;
        	default:
        		// Should never reach here
        		break;
    	}
    	
    	// Formatting time taken for job label
    	long secondsTaken = indicesTimeTaken / 1000;
    	if(secondsTaken >= 60) {
    		int minutes = (int) (secondsTaken / 60);
    		String minutesString = (minutes == 1) ? " minute" : " minutes";
    		long seconds  = secondsTaken % 60;
    		String secondsString = ((int)seconds == 1) ? " second" : " seconds";
    		indicesTimeTakenLabel.setText("Inverted indices constructed in " + minutes + minutesString + " and " + Long.toString(seconds) + secondsString);
    	} else {
    		String secondsString = ((int)secondsTaken == 1) ? " second" : " seconds";
    		indicesTimeTakenLabel.setText("Inverted indices constructed in " + Long.toString(secondsTaken) + secondsString);
    	}
    	
    	
    	// Showing elements for selection page
    	selectionLabel.setVisible(true);
    	selectionActionLabel.setVisible(true);
    	searchForTermButton.setVisible(true);
    	topNFrequentTermsButton.setVisible(true);
    	indicesTimeTakenLabel.setVisible(true);
    	
    	// Setting current page
    	currentPage = Pages.Selection;
    }
    
    public void showSearchPage() {
    	// Hiding gui elements from current page
    	switch (currentPage) {
	    	case Selection:
	    		// Hiding elements from selection page
	        	selectionLabel.setVisible(false);
	        	selectionActionLabel.setVisible(false);
	        	searchForTermButton.setVisible(false);
	        	topNFrequentTermsButton.setVisible(false);
	        	indicesTimeTakenLabel.setVisible(false);
	        	break;
	    	case SearchResult:
	        	// Hiding elements from search result page
	        	searchResultLabel.setVisible(false);
	        	searchTimeTakenLabel.setVisible(false);
	        	searchScroll.setVisible(false);
	        	backToSearchButton.setVisible(false);
	        	break;
        	default:
        		// Should never reach here
        		break;
    	}
    	
    	// Showing elements for search page
    	searchLabel.setVisible(true);
    	searchField.setVisible(true);
    	submitSearchButton.setVisible(true);
    	backToSelectionButton.setVisible(true);
    	
    	// Setting current page
    	currentPage = Pages.Search;
    }
    
    public void showTopNPage() {
    	// Hiding gui elements from current page
    	switch (currentPage) {
	    	case Selection:
	    		// Hiding elements from selection page
	        	selectionLabel.setVisible(false);
	        	selectionActionLabel.setVisible(false);
	        	searchForTermButton.setVisible(false);
	        	topNFrequentTermsButton.setVisible(false);
	        	indicesTimeTakenLabel.setVisible(false);
	        	break;
	    	case TopNResult:
	        	// Hiding elements from top-n result page
	        	topNResultLabel.setVisible(false);
	        	searchTimeTakenLabel.setVisible(false);
	        	topNScroll.setVisible(false);
	        	backToTopNButton.setVisible(false);
	        	break;
        	default:
        		// Should never reach here
        		break;
    	}
    	
    	// Showing elements for top-n page
    	topNLabel.setVisible(true);
    	topNField.setVisible(true);
    	submitTopNButton.setVisible(true);
    	backToSelectionButton.setVisible(true);
    	
    	// Setting current page
    	currentPage = Pages.TopN;
    }
    
    public void showSearchResultPage() {
    	// Hiding elements from search page
    	searchLabel.setVisible(false);
    	searchField.setVisible(false);
    	submitSearchButton.setVisible(false);
    	backToSelectionButton.setVisible(false);
    	
    	// Formatting time taken for job label
    	long secondsTaken = searchTimeTaken / 1000;
    	if(secondsTaken >= 60) {
    		int minutes = (int) (secondsTaken / 60);
    		String minutesString = (minutes == 1) ? " minute" : " minutes";
    		long seconds  = secondsTaken % 60;
    		String secondsString = ((int)seconds == 1) ? " second" : " seconds";
    		searchTimeTakenLabel.setText("Your search was executed in " + minutes + minutesString + " and " + Long.toString(seconds) + secondsString);
    	} else {
    		String secondsString = ((int)secondsTaken == 1) ? " second" : " seconds";
    		searchTimeTakenLabel.setText("Your search was executed in " + Long.toString(secondsTaken) + secondsString);
    	}
    	
    	// Showing elements for search result page
    	searchResultLabel.setVisible(true);
    	searchTimeTakenLabel.setVisible(true);
    	searchScroll.setVisible(true);
    	backToSearchButton.setVisible(true);
    	
    	// Setting current page
    	currentPage = Pages.SearchResult;
    }
    
    public void showTopNResultPage() {
    	// Hiding elements from top-n page
    	topNLabel.setVisible(false);
    	topNField.setVisible(false);
    	submitTopNButton.setVisible(false);
    	backToSelectionButton.setVisible(false);
    	
    	// Formatting time taken for job label
    	long secondsTaken = searchTimeTaken / 1000;
    	if(secondsTaken >= 60) {
    		int minutes = (int) (secondsTaken / 60);
    		String minutesString = (minutes == 1) ? " minute" : " minutes";
    		long seconds  = secondsTaken % 60;
    		String secondsString = ((int)seconds == 1) ? " second" : " seconds";
    		searchTimeTakenLabel.setText("Your top-N search was executed in " + minutes + minutesString + " and " + Long.toString(seconds) + secondsString);
    	} else {
    		String secondsString = ((int)secondsTaken == 1) ? " second" : " seconds";
    		searchTimeTakenLabel.setText("Your top-N search was executed in " + Long.toString(secondsTaken) + secondsString);
    	}
    	
    	// Showing elements for top-n result page
    	topNResultLabel.setVisible(true);
    	searchTimeTakenLabel.setVisible(true);
    	topNScroll.setVisible(true);
    	backToTopNButton.setVisible(true);
    	
    	// Setting current page
    	currentPage = Pages.TopNResult;
    }
    
    public void clickSubmitSearchButton() {
    	// Storing start time to calculate time taken for executing search job, including communication between client and GCP
    	long startTime = System.currentTimeMillis();
    	
    	// Getting search term, creating new thread to submit job to GCP
       	String termToSearch = searchField.getText();
    	Thread searchThread = new Thread() {
	   		public void run() {
	   			
	   			// Setting up gui before running job
	   			SwingUtilities.invokeLater(new Runnable() {
	   	    		public void run() {
	   	    			submitSearchButton.setEnabled(false);
	   	    			loadingLabel.setText("Running search job, please wait...");
	   	    		   	loadingLabel.setVisible(true);
	   	    		   	invalidEntryLabel.setVisible(false);
	   	    		}
	   	    	});
	   			
	   			// Running Search job
	   			String content = submitSearch(termToSearch);
	   			
	   			// Updating gui after getting results of search job
	   			SwingUtilities.invokeLater(new Runnable() {
	   	    		public void run() {
	   	    			// Calculating time taken for search job
	   	    			searchTimeTaken = System.currentTimeMillis() - startTime;
	   	    			
	   	    			// If the content is null, and invalid term was entered by the user
		   	    		// If the content is empty, there are no files with the search term in it or an error occurred, 
	   	    			// If content isn't empty, display the files containing the term and their frequencies
		   	 			if(content == null) {
		   	 				System.out.println("\n'" + termToSearch + "'" + " is an invalid entry. Please enter a term containing only alphpabetical characters.");
		   	 				invalidEntryLabel.setText("'" + termToSearch + "'" + " is an invalid entry. Please enter a term containing only alphabetical characters.");
		   	 				invalidEntryLabel.setVisible(true);
		   	 				loadingLabel.setVisible(false);
		   	 				searchField.setText("");
		   	 			} else if (content.isEmpty()) {
			   	 			System.out.println("\nNo results found for term: " + termToSearch);
		   	 				searchResultLabel.setText("No results found for term: " + termToSearch);
			   	 			loadingLabel.setVisible(false);
			   	 			showSearchResultPage();
			   	 	    	searchField.setText("");
		   	 			} else {
		   	 				System.out.println("\nDisplaying results for term: " + termToSearch);
		   	 				searchResultLabel.setText("Search results for term '" + termToSearch + "'");
		   	 				
		   	 				// Formatting data to fit into table for display
		   	 				String[] termAndFiles = content.split("\t");
		   	 				String[] filesWithTerm = termAndFiles[1].split(" ");
		   	 				String[][] fileCountPairs = new String[filesWithTerm.length - 1][2];
		   	 				for(int i = 0; i < fileCountPairs.length; i++) {
		   	 					String[] fileAndCount = filesWithTerm[i].split(":");
		   	 					fileCountPairs[i][0] = fileAndCount[0];
		   	 					fileCountPairs[i][1] = fileAndCount[1];
		   	 				}
		   	 				
		   	 				// Sorting array so that files with highest frequency show up first
		   	 				Arrays.sort(fileCountPairs, (b,a) -> Integer.compare(Integer.parseInt(a[1]), Integer.parseInt(b[1])));
		   	 				
		   	 				// Creating table and configuring table settings, then showing results
		   	 				String[] headerLabels = { "File", "Frequency" };
		   	 				DefaultTableModel model = new DefaultTableModel(fileCountPairs, headerLabels);
		   	 				JTable table = new JTable(model);
		   	 				DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		   	 				renderer.setHorizontalAlignment(SwingConstants.CENTER);
		   	 				table.setDefaultRenderer(Object.class, renderer);
		   	 				JTableHeader header = table.getTableHeader();
		   	 				header.setFont(defaultFont);
		   	 				table.setRowHeight(25);
		   	 				table.setFont(loadingFont);
		   	 				searchScroll = new JScrollPane(table);
		   	 				searchScroll.setBackground(Color.WHITE);
		   	 		        searchScroll.setBounds(150, 100, 500, 410);
		   	 		        add(searchScroll);
			   	 		    loadingLabel.setVisible(false);
			   	 			showSearchResultPage();
			   	 	    	searchField.setText("");
		   	 			}
		   	 			submitSearchButton.setEnabled(true);
	   	    		}
	   	    	});
	   		}
	   	};
	   	// Starting the above thread
	   	searchThread.start();
    }
    
    private String submitSearch(String termToSearch) {
    	// Checking to make sure search term only contains alphabetical characters
    	termToSearch = termToSearch.toLowerCase();
    	Pattern p = Pattern.compile("[^a-zA-Z]");
    	Matcher m = p.matcher(termToSearch);
    	boolean invalidTerm = m.find();
    	
    	if(invalidTerm || termToSearch.isEmpty()) {
    		return null;
    	}
    	
    	System.out.println("\nSubmitting search job to GCP");
    	
    	try {
    		// Configuring JobControllerSettings with endpoint
			JobControllerSettings jobControllerSettings = JobControllerSettings.newBuilder()
					.setEndpoint(endpoint)
					.build();
			
			// Creating JobControllerClient using above JobControllerSettings
			JobControllerClient jobControllerClient = JobControllerClient.create(jobControllerSettings);
			
			// Creating JobPlacement with clusterName set to the name of my dataproc cluster
			JobPlacement jobPlacement = JobPlacement.newBuilder()
					.setClusterName(clusterName)
					.build();
			
			// Deleting search term output if it exists in cloud storage, since hadoop job will fail if output "folder" already exists
			if(storage.get(BlobId.of(bucketName, "Search/Output/")) != null) {
				Page<Blob> blobs = storage.list(bucketName, Storage.BlobListOption.prefix("Search/Output/"));
				Iterator<Blob> blobsIterable = blobs.iterateAll().iterator();
				while(blobsIterable.hasNext()) {
					Blob blob = blobsIterable.next();
					storage.delete(blob.getBlobId());
				}
			}
			
			// Creating HadoopJob with jar file from cloud storage, search term, and input and output folders in storage bucket
			HadoopJob hadoopJob = HadoopJob.newBuilder()
					.setMainJarFileUri(bucketPath + "/Jar/SearchIndices.jar")
					.addArgs(termToSearch)
					.addArgs(bucketPath + "/Indices/Output/")
					.addArgs(bucketPath + "/Search/Output/")
					.build();
			
			// Configuring job to be sent to dataproc cluster
			Job job = Job.newBuilder()
					.setPlacement(jobPlacement)
					.setHadoopJob(hadoopJob)
					.build();
					
			// Submitting async request to run job
			OperationFuture<Job, JobMetadata> submitJobAsOperationAsyncRequest = jobControllerClient.submitJobAsOperationAsync(projectId, region, job);
			
			System.out.println("\nRunning search job, please wait...");
			
			// Waiting for job to complete
			Job response = submitJobAsOperationAsyncRequest.get();
			
			// Fetching output of search job and reading contents
			BlobId blobId = BlobId.of(bucketName, "Search/Output/part-r-00000");
			byte[] blobContent = storage.readAllBytes(blobId);
			return new String(blobContent, StandardCharsets.UTF_8);
			
		} catch (IOException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
    	
    	return "";
    }
    
    public void clickSubmitTopNButton() {
    	// Storing start time to calculate time taken for executing top-n job, including communication between client and GCP
    	long startTime = System.currentTimeMillis();
    	
    	// Getting n value from input field and creating a new thread to submit job to GCP 
    	String nValue = topNField.getText();
    	Thread nThread = new Thread() {
	   		public void run() {
	   			
	   			// Setting up gui before running job
	   			SwingUtilities.invokeLater(new Runnable() {
	   	    		public void run() {
	   	    			submitTopNButton.setEnabled(false);
	   	    			loadingLabel.setText("Running top-N job, please wait...");
	   	    		   	loadingLabel.setVisible(true);
	   	    		   	invalidEntryLabel.setVisible(false);
	   	    		}
	   	    	});
	   			
	   			// Running the top-n job
	   			String content = submitTopN(nValue);
	   			
	   			// Updating gui after getting results of top-n job
	   			SwingUtilities.invokeLater(new Runnable() {
	   	    		public void run() {
	   	    			// Calculating time taken for top-n job
	   	    			searchTimeTaken = System.currentTimeMillis() - startTime;
	   	    			
	   	    			// If the content is null, an invalid n value was entered by the user
		   	    		// If the content is empty, there are no terms in the inverted indices or an error occurred, 
	   	    			// If content isn't empty, display the top-n terms and their frequencies
		   	 			if(content == null) {
		   	 				System.out.println("\n'" + nValue + "'" + " is an invalid entry. Please enter a positive number.");
		   	 				invalidEntryLabel.setText("'" + nValue + "'" + " is an invalid entry. Please enter a positive number.");
		   	 				invalidEntryLabel.setVisible(true);
		   	 				loadingLabel.setVisible(false);
		   	 				topNField.setText("");
		   	 			} else if (content.isEmpty()) {
			   	 			System.out.println("\nError completing job for N = " + nValue);
		   	 				topNResultLabel.setText("Error completing job for N = " + nValue);
			   	 			loadingLabel.setVisible(false);
			   	 			showTopNResultPage();
			   	 	    	topNField.setText("");
		   	 			} else {
		   	 				System.out.println("\nDisplaying results for Top-N with value: " + nValue);
		   	 				topNResultLabel.setText("Top-N results for N = " + nValue);
		   	 				
		   	 				// Formatting data to fit into table for display
		   	 				String[] topNTerms = content.split("\n");
		   	 				String[][] termCountPairs = new String[topNTerms.length - 1][2];
		   	 				for(int i = 0; i < termCountPairs.length; i++) {
		   	 					String[] termCounts = topNTerms[i].split("\t");
		   	 					termCountPairs[i][0] = termCounts[1];
		   	 					termCountPairs[i][1] = termCounts[0];
		   	 				}
		   	 				
		   	 				// Sorting array so that more frequent terms are displayed first
		   	 				Arrays.sort(termCountPairs, (b, a) -> Integer.compare(Integer.parseInt(a[1]), Integer.parseInt(b[1])));
		   	 				
		   	 				// Creating table and configuring table settings, then displaying results
		   	 				String[] headerLabels = { "Term", "Frequency" };
		   	 				DefaultTableModel model = new DefaultTableModel(termCountPairs, headerLabels);
		   	 				JTable table = new JTable(model);
		   	 				DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		   	 				renderer.setHorizontalAlignment(SwingConstants.CENTER);
		   	 				table.setDefaultRenderer(Object.class, renderer);
		   	 				JTableHeader header = table.getTableHeader();
		   	 				header.setFont(defaultFont);
		   	 				table.setRowHeight(25);
		   	 				table.setFont(loadingFont);
		   	 				topNScroll = new JScrollPane(table);
		   	 				topNScroll.setBackground(Color.WHITE);
		   	 		        topNScroll.setBounds(150, 100, 500, 410);
		   	 		        add(topNScroll);
			   	 		    loadingLabel.setVisible(false);
			   	 			showTopNResultPage();
			   	 	    	topNField.setText("");
		   	 			}
		   	 			submitTopNButton.setEnabled(true);
	   	    		}
	   	    	});
	   		}
	   	};
	   	// Starting the above thread
	    nThread.start();
    }
    
    private String submitTopN(String nValue) {
    	// Checking to make sure a valid n value was entered
    	Pattern p = Pattern.compile("[^0-9]");
    	Matcher m = p.matcher(nValue);
    	boolean invalidTerm = m.find();
    	
    	if(invalidTerm || nValue.isEmpty()) {
    		return null;
    	}
    	
    	System.out.println("\nSubmitting top-N job to GCP");
    	
    	try {
    		// Configuring JobControllerSettings with endpoint
			JobControllerSettings jobControllerSettings = JobControllerSettings.newBuilder()
					.setEndpoint(endpoint)
					.build();
			
			// Creating JobControllerClient using above JobControllerSettings
			JobControllerClient jobControllerClient = JobControllerClient.create(jobControllerSettings);
			
			// Creating JobPlacement with clusterName set to the name of my dataproc cluster
			JobPlacement jobPlacement = JobPlacement.newBuilder()
					.setClusterName(clusterName)
					.build();
			
			// Deleting search term output if it exists in cloud storage, since hadoop job will fail if output "folder" already exists
			if(storage.get(BlobId.of(bucketName, "TopN/Output/")) != null) {
				Page<Blob> blobs = storage.list(bucketName, Storage.BlobListOption.prefix("TopN/Output/"));
				Iterator<Blob> blobsIterable = blobs.iterateAll().iterator();
				while(blobsIterable.hasNext()) {
					Blob blob = blobsIterable.next();
					storage.delete(blob.getBlobId());
				}
			}
			
			// Creating HadoopJob with jar file from cloud storage, search term, and input and output folders in storage bucket
			HadoopJob hadoopJob = HadoopJob.newBuilder()
					.setMainJarFileUri(bucketPath + "/Jar/TopNTerms.jar")
					.addArgs(nValue)
					.addArgs(bucketPath + "/Indices/Output/")
					.addArgs(bucketPath + "/TopN/Output/")
					.build();
			
			// Configuring job to be sent to dataproc cluster
			Job job = Job.newBuilder()
					.setPlacement(jobPlacement)
					.setHadoopJob(hadoopJob)
					.build();
					
			// Submitting async request to run job
			OperationFuture<Job, JobMetadata> submitJobAsOperationAsyncRequest = jobControllerClient.submitJobAsOperationAsync(projectId, region, job);
			
			System.out.println("\nRunning top-N job, please wait...");
			
			// Waiting for job to complete
			Job response = submitJobAsOperationAsyncRequest.get();
			
			// Fetching output of search job and reading contents
			BlobId blobId = BlobId.of(bucketName, "TopN/Output/part-r-00000");
			byte[] blobContent = storage.readAllBytes(blobId);
			return new String(blobContent, StandardCharsets.UTF_8);
		} catch (IOException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
    	return "";
    }

    // Creating instance of script to open gui
    public static void main(String args[]) {
    	try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
			    	new searchEngineGUI();
			    }
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
    }
}
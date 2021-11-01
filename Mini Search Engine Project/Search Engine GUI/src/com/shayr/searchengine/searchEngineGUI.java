package com.shayr.searchengine;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class searchEngineGUI extends JFrame implements ActionListener {
    
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
    JLabel searchLoadLabel;
    JButton backToSelectionButton;
    JLabel topNLabel;
    JTextField topNField;
    JButton submitTopNButton;
    JLabel topNLoadLabel;
    JLabel searchResultLabel;
    JScrollPane searchScroll;
    JButton backToSearchButton;
    JLabel topNResultLabel;
    JScrollPane topNScroll;
    JButton backToTopNButton;

    public searchEngineGUI() {
        initializeGUIElements();
        setLayout();
    }

    void initializeGUIElements() {
    	System.out.println("\nInitializing GUI elements");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        defaultFont = new Font("Arial", Font.PLAIN, 30);
        loadingFont = new Font("Arial", Font.PLAIN, 22);

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

        loadingLabel = new JLabel("Loading...");
        loadingLabel.setFont(loadingFont);
        loadingLabel.setVisible(false);
        
        closeButton = new JButton("X");
        closeButton.setFont(new Font("Arial", Font.PLAIN, 16));
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

        searchLoadLabel = new JLabel("Waiting for Term Search Result...");
        searchLoadLabel.setFont(loadingFont);
        searchLoadLabel.setVisible(false);

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

        topNLoadLabel = new JLabel("Waiting for Top-N Result...");
        topNLoadLabel.setFont(loadingFont);
        topNLoadLabel.setVisible(false);

        searchResultLabel = new JLabel("Search Results for", SwingConstants.CENTER);
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
        System.out.println("\nGUI elements initialized");
    }

    void setLayout() {
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
    	loadingLabel.setBounds(600, 550, 200, 50);
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
        searchLoadLabel.setBounds(600, 550, 200, 50);
        searchLoadLabel.setVisible(false);
        add(searchLoadLabel);
        backToSelectionButton.setBounds(275, 425, 250, 50);
        backToSelectionButton.setVisible(false);
        add(backToSelectionButton);
        
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
        topNLoadLabel.setBounds(600, 550, 200, 50);
        topNLoadLabel.setVisible(false);
        add(topNLoadLabel);
        
        // Setting up layout for search result page, hiding elements to start
        searchResultLabel.setBounds(150, 10, 500, 50);
        searchResultLabel.setVisible(false);
        add(searchResultLabel);
        searchScroll.setBounds(150, 60, 500, 450);
        searchScroll.setVisible(false);
        add(searchScroll);
        backToSearchButton.setBounds(275, 525, 250, 50);
        backToSearchButton.setVisible(false);
        add(backToSearchButton);
        
        // Setting up layout for top-n result page, hiding elements to start
        topNResultLabel.setBounds(150, 10, 500, 50);
        topNResultLabel.setVisible(false);
        add(topNResultLabel);
        topNScroll.setBounds(150, 60, 500, 450);
        topNScroll.setVisible(false);
        add(topNScroll);
        backToTopNButton.setBounds(275, 525, 250, 50);
        backToTopNButton.setVisible(false);
        add(backToTopNButton);
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
    		System.out.println("\nConstructing inverted indices");
    		constructInvertedIndices();
    		showSelectionPage();
    	}
    	if(button == searchForTermButton || button == backToSearchButton) {
    		System.out.println("\nNavigating to search page");
    		showSearchPage();
    	}
    	if(button == topNFrequentTermsButton || button == backToTopNButton) {
    		System.out.println("\nNavigating to top-n page");
    		showTopNPage();
    	}
    	if(button == backToSelectionButton) {
    		System.out.println("\nNavigating to selection page");
    		showSelectionPage();
    	}
    	if(button == submitSearchButton) {
    		System.out.println("\nSubmitting term to GCP to complete job");
    		submitSearch();
    	}
    	if(button == submitTopNButton) {
    		System.out.println("\nSubmitting n value to GCP to complete job");
    		submitTopN();
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
    	loadingLabel.setVisible(true);
    	
    	// For each file selected, adding file name to selectedFiles text pane
    	for(File f : files) {
    		System.out.println("\nSelecting file: " + f.getName());
    		try {
    			selectedFilesDoc.insertString(selectedFilesDoc.getLength(), "\n" + f.getName(), center);
    			
    			// TODO: Check if files are in GCP storage bucket, if not then add them
    		} catch (BadLocationException e) {
    			e.printStackTrace();
    		}
    		selectedFilesDoc.setParagraphAttributes(0, selectedFilesDoc.getLength(), center, false);
    		selectedFilesDisplay.setVisible(true);
    		loadingLabel.setVisible(false);
    	}
    }
    
    public void constructInvertedIndices() {
    	// TODO: Contact GCP to Submit job
    	// TODO: On success, load selection panel
    	// TODO: On failure, print error and exit
    }
    
    public void showSelectionPage() {
    	// Hiding elements from start page
    	titleLabel.setVisible(false);
    	chooseFilesButton.setVisible(false);
    	selectedFilesDisplay.setVisible(false);
    	constructIndicesButton.setVisible(false);
    	
    	// Hiding elements from search page
    	searchLabel.setVisible(false);
    	searchField.setVisible(false);
    	submitSearchButton.setVisible(false);
    	searchLoadLabel.setVisible(false);
    	backToSelectionButton.setVisible(false);
    	
    	// Hiding elements from top-n page
    	topNLabel.setVisible(false);
    	topNField.setVisible(false);
    	submitTopNButton.setVisible(false);
    	topNLoadLabel.setVisible(false);
    	
    	// Showing elements for selection page
    	selectionLabel.setVisible(true);
    	selectionActionLabel.setVisible(true);
    	searchForTermButton.setVisible(true);
    	topNFrequentTermsButton.setVisible(true);
    }
    
    public void showSearchPage() {
    	// Hiding elements from selection page
    	selectionLabel.setVisible(false);
    	selectionActionLabel.setVisible(false);
    	searchForTermButton.setVisible(false);
    	topNFrequentTermsButton.setVisible(false);
    	
    	// Hiding elements from search result page
    	searchResultLabel.setVisible(false);
    	searchScroll.setVisible(false);
    	backToSearchButton.setVisible(false);
    	
    	// Showing elements for search page
    	searchLabel.setVisible(true);
    	searchField.setVisible(true);
    	submitSearchButton.setVisible(true);
    	//searchLoadLabel.setVisible(true);
    	backToSelectionButton.setVisible(true);
    }
    
    public void showTopNPage() {
    	// Hiding elements from selection page
    	selectionLabel.setVisible(false);
    	selectionActionLabel.setVisible(false);
    	searchForTermButton.setVisible(false);
    	topNFrequentTermsButton.setVisible(false);
    	
    	// Hiding elements from top-n result page
    	topNResultLabel.setVisible(false);
    	topNScroll.setVisible(false);
    	backToTopNButton.setVisible(false);
    	
    	// Showing elements for top-n page
    	topNLabel.setVisible(true);
    	topNField.setVisible(true);
    	submitTopNButton.setVisible(true);
    	//topNLoadLabel.setVisible(true);
    	backToSelectionButton.setVisible(true);
    }
    
    public void showSearchResultPage() {
    	// Hiding elements from search page
    	searchLabel.setVisible(false);
    	searchField.setVisible(false);
    	submitSearchButton.setVisible(false);
    	searchLoadLabel.setVisible(false);
    	backToSelectionButton.setVisible(false);
    	
    	// Showing elements for search result page
    	searchResultLabel.setVisible(true);
    	searchScroll.setVisible(true);
    	backToSearchButton.setVisible(true);
    }
    
    public void showTopNResultPage() {
    	// Hiding elements from top-n page
    	topNLabel.setVisible(false);
    	topNField.setVisible(false);
    	submitTopNButton.setVisible(false);
    	topNLoadLabel.setVisible(false);
    	backToSelectionButton.setVisible(false);
    	
    	// Showing elements for top-n result page
    	topNResultLabel.setVisible(true);
    	topNScroll.setVisible(true);
    	backToTopNButton.setVisible(true);
    }
    
    public void submitSearch() {
    	// TODO: Contact GCP to perform search
    	// TODO: On Success, show search result page
    	// TODO: On Fail, print error and either exit or return to search page
    	showSearchResultPage();
    }
    
    public void submitTopN() {
    	// TODO: Contact GCP to perform top-n
    	// TODO: On Success, show top-n result page
    	// TODO: On Fail, print error and either exit or return to top-n page
    	showTopNResultPage();
    }

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
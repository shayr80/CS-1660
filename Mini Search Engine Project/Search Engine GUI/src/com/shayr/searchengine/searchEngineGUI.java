package com.shayr.searchengine;

import java.io.File;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.Component;

public class searchEngineGUI extends JFrame implements ActionListener {
    
    Font defaultFont;
    Font loadingFont;
    JPanel startPanel;
    JLabel titleLabel;
    JTextPane selectedFilesDisplay;
    StyledDocument selectedFilesDoc;
    SimpleAttributeSet center;
    JButton chooseFilesButton;
    JFileChooser fileChooser;
    File[] files;
    JButton constructIndicesButton;
    JLabel loadingLabel;
    JPanel selectionPanel;
    JLabel selectionLabel;
    JLabel selectionActionLabel;
    JButton searchForTermButton;
    JButton topNFrequentTermsButton;
    JPanel searchPanel;
    JLabel searchLabel;
    JTextField searchField;
    JButton submitSearchButton;
    JLabel searchLoadLabel;
    JButton backToSelectionFromSearchButton;
    JPanel topNPanel;
    JLabel topNLabel;
    JTextField topNField;
    JButton submitTopNButton;
    JLabel topNLoadLabel;
    JButton backToSelectionFromNButton;
    JPanel searchResultPanel;
    JLabel searchResultLabel;
    JScrollPane searchScroll;
    JButton backToSearchButton;
    JPanel topNResultPanel;
    JLabel topNResultLabel;
    JScrollPane topNScroll;
    JButton backToTopNButton;

    public searchEngineGUI() {
        initializeGUIElements();
        setPanelLayouts();
    }

    void initializeGUIElements() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        defaultFont = new Font("Arial", Font.PLAIN, 30);
        loadingFont = new Font("Arial", Font.PLAIN, 22);

        startPanel = new JPanel();

        titleLabel = new JLabel("Mini Search Engine", SwingConstants.CENTER);
        titleLabel.setFont(defaultFont);

        selectedFilesDisplay = new JTextPane();
        selectedFilesDisplay.setFont(defaultFont);
        selectedFilesDisplay.setEditable(false);
        selectedFilesDisplay.setOpaque(false);

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

        selectionPanel = new JPanel();

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

        searchPanel = new JPanel();

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

        backToSelectionFromSearchButton = new JButton("Back to Selection");
        backToSelectionFromSearchButton.setFont(loadingFont);
        backToSelectionFromSearchButton.addActionListener(this);

        topNPanel = new JPanel();

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

        backToSelectionFromNButton = new JButton("Back to Selection");
        backToSelectionFromNButton.setFont(defaultFont);
        backToSelectionFromNButton.addActionListener(this);

        searchResultPanel = new JPanel();

        searchResultLabel = new JLabel("Search Results for", SwingConstants.CENTER);
        searchResultLabel.setFont(defaultFont);

        searchScroll = new JScrollPane();

        backToSearchButton = new JButton("Back to Search");
        backToSearchButton.setFont(loadingFont);
        backToSearchButton.addActionListener(this);

        topNResultPanel = new JPanel();

        topNResultLabel = new JLabel("Top-N Results for N = ", SwingConstants.CENTER);
        topNResultLabel.setFont(defaultFont);

        topNScroll = new JScrollPane();

        backToTopNButton = new JButton("Back to Top-N");
        backToTopNButton.setFont(loadingFont);
        backToTopNButton.addActionListener(this);
    }

    void setPanelLayouts() {
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        startPanel.setLayout(layout);
        int non = GridBagConstraints.NONE;
        int both = GridBagConstraints.BOTH;
        int cen = GridBagConstraints.CENTER;
        int right = GridBagConstraints.LINE_END;
        int loadPos = GridBagConstraints.LAST_LINE_END;
        
        // building start panel
        addComponentToPanel(startPanel, titleLabel, c, 1, 0, 2, 1, 0, 0.2, both, 0, 0, 0, 0, cen);
        addComponentToPanel(startPanel, chooseFilesButton, c, 1, 1, 2, 1, 0, 0, both, 0, 0, 0, 0, cen);
        addComponentToPanel(startPanel, selectedFilesDisplay, c, 1, 3, 2, 2, 0, 0, both, 0, 0, 0, 0, cen);
        addComponentToPanel(startPanel, constructIndicesButton, c, 1, 6, 2, 1, 0, 0, both, 0, 0, 0, 0, cen);
        addComponentToPanel(startPanel, loadingLabel, c, 3, 8, 1, 1, 0, 0, non, 0, 0, 10, 15, loadPos);
        addComponentToPanel(startPanel, Box.createHorizontalStrut(80), c, 0, 0, 1, 9, 0.4, 0, both, 0, 0, 0, 0, cen);
        addComponentToPanel(startPanel, Box.createHorizontalStrut(80), c, 3, 0, 1, 9, 0.4, 0, both, 0, 0, 0, 0, cen);
        addComponentToPanel(startPanel, Box.createGlue(), c, 0, 0, 1, 1, 0, 0.2, both, 0, 0, 0, 0, cen);
        addComponentToPanel(startPanel, Box.createGlue(), c, 0, 1, 1, 1, 0, 0.05, both, 0, 0, 0, 0, cen);
        addComponentToPanel(startPanel, Box.createGlue(), c, 0, 2, 1, 1, 0, 0.05, both, 0, 0, 0, 0, cen);
        addComponentToPanel(startPanel, Box.createGlue(), c, 0, 3, 1, 2, 0, 0.3, both, 0, 0, 0, 0, cen);
        addComponentToPanel(startPanel, Box.createGlue(), c, 0, 5, 1, 1, 0, 0.05, both, 0, 0, 0, 0, cen);
        addComponentToPanel(startPanel, Box.createGlue(), c, 0, 6, 1, 1, 0, 0.05, both, 0, 0, 0, 0, cen);
        addComponentToPanel(startPanel, Box.createGlue(), c, 0, 7, 1, 1, 0, 0.25, both, 0, 0, 0, 0, cen);
        addComponentToPanel(startPanel, Box.createGlue(), c, 1, 8, 2, 1, 0.2, 0.05, both, 0, 0, 0, 0, cen);

        // building selection panel
        selectionPanel.setLayout(layout);
        addComponentToPanel(selectionPanel, selectionLabel, c, 1, 1, 2, 1, 0, 0, both, 0, 0, 0, 0, cen);
        addComponentToPanel(selectionPanel, selectionActionLabel, c, 1, 2, 2, 1, 0, 0, both, 0, 0, 0, 0, cen);
        addComponentToPanel(selectionPanel, searchForTermButton, c, 1, 4, 2, 1, 0, 0, both, 0, 0, 0, 0, cen);
        addComponentToPanel(selectionPanel, topNFrequentTermsButton, c, 1, 6, 2, 1, 0, 0, both, 0, 0, 0, 0, cen);
        addComponentToPanel(selectionPanel, Box.createGlue(), c, 0, 0, 1, 1, 0, 0.05, both, 0, 0, 0, 0, cen);
        addComponentToPanel(selectionPanel, Box.createGlue(), c, 0, 1, 1, 1, 0, 0.1, both, 0, 0, 0, 0, cen);
        addComponentToPanel(selectionPanel, Box.createGlue(), c, 0, 2, 1, 1, 0, 0.1, both, 0, 0, 0, 0, cen);
        addComponentToPanel(selectionPanel, Box.createGlue(), c, 0, 3, 1, 1, 0, 0.2, both, 0, 0, 0, 0, cen);
        addComponentToPanel(selectionPanel, Box.createGlue(), c, 0, 4, 1, 1, 0, 0.05, both, 0, 0, 0, 0, cen);
        addComponentToPanel(selectionPanel, Box.createGlue(), c, 3, 5, 1, 1, 0.45, 0.2, both, 0, 0, 0, 0, cen);
        addComponentToPanel(selectionPanel, Box.createGlue(), c, 0, 6, 1, 1, 0.45, 0.05, both, 0, 0, 0, 0, cen);
        addComponentToPanel(selectionPanel, Box.createGlue(), c, 1, 7, 2, 1, 0.1, 0.25, both, 0, 0, 0, 0, cen);
        
        // building search panel
        searchPanel.setLayout(layout);
        addComponentToPanel(searchPanel, searchLabel, c, 1, 1, 2, 1, 0, 0, both, 0, 0, 0, 0, cen);
        addComponentToPanel(searchPanel, searchField, c, 1, 3, 2, 1, 0, 0, both, 0, 0, 0, 0, cen);
        addComponentToPanel(searchPanel, submitSearchButton, c, 1, 5, 2, 1, 0, 0, both, 0, 0, 0, 0, cen);
        addComponentToPanel(searchPanel, searchLoadLabel, c, 3, 8, 1, 1, 0, 0, non, 0, 0, 0, 0, loadPos);
        addComponentToPanel(searchPanel, backToSelectionFromSearchButton, c, 1, 7, 2, 1, 0, 0, both, 0, 0, 0, 0, cen);
        addComponentToPanel(searchPanel, Box.createGlue(), c, 0, 0, 1, 1, 0, 0.15, both, 0, 0, 0, 0, cen);
        addComponentToPanel(searchPanel, Box.createGlue(), c, 0, 1, 1, 1, 0, 0.1, both, 0, 0, 0, 0, cen);
        addComponentToPanel(searchPanel, Box.createGlue(), c, 0, 2, 1, 1, 0, 0.2, both, 0, 0, 0, 0, cen);
        addComponentToPanel(searchPanel, Box.createGlue(), c, 0, 3, 1, 1, 0, 0.05, both, 0, 0, 0, 0, cen);
        addComponentToPanel(searchPanel, Box.createGlue(), c, 0, 4, 1, 1, 0, 0.2, both, 0, 0, 0, 0, cen);
        addComponentToPanel(searchPanel, Box.createGlue(), c, 0, 5, 1, 1, 0, 0.05, both, 0, 0, 0, 0, cen);
        addComponentToPanel(searchPanel, Box.createGlue(), c, 3, 6, 1, 1, 0.45, 0.05, both, 0, 0, 0, 0, cen);
        addComponentToPanel(searchPanel, Box.createGlue(), c, 0, 7, 1, 1, 0.45, 0.05, both, 0, 0, 0, 0, cen);
        addComponentToPanel(searchPanel, Box.createGlue(), c, 1, 8, 2, 1, 0.1, 0.3, both, 0, 0, 0, 0, cen);
        
        // building top-n panel
        topNPanel.setLayout(layout);
        addComponentToPanel(topNPanel, topNLabel, c, 1, 1, 2, 1, 0, 0, both, 0, 0, 0, 0, cen);
        addComponentToPanel(topNPanel, topNField, c, 1, 3, 2, 1, 0, 0, both, 0, 0, 0, 0, cen);
        addComponentToPanel(topNPanel, submitTopNButton, c, 1, 5, 2, 1, 0, 0, both, 0, 0, 0, 0, cen);
        addComponentToPanel(topNPanel, topNLoadLabel, c, 3, 8, 1, 1, 0, 0, non, 0, 0, 0, 0, loadPos);
        //addComponentToPanel(topNPanel, backToSelectionButton, c, 1, 7, 2, 1, 0, 0, both, 0, 0, 0, 0, cen);
        addComponentToPanel(topNPanel, Box.createGlue(), c, 0, 0, 1, 1, 0, 0.15, both, 0, 0, 0, 0, cen);
        addComponentToPanel(topNPanel, Box.createGlue(), c, 0, 1, 1, 1, 0, 0.1, both, 0, 0, 0, 0, cen);
        addComponentToPanel(topNPanel, Box.createGlue(), c, 0, 2, 1, 1, 0, 0.2, both, 0, 0, 0, 0, cen);
        addComponentToPanel(topNPanel, Box.createGlue(), c, 0, 3, 1, 1, 0, 0.05, both, 0, 0, 0, 0, cen);
        addComponentToPanel(topNPanel, Box.createGlue(), c, 0, 4, 1, 1, 0, 0.2, both, 0, 0, 0, 0, cen);
        addComponentToPanel(topNPanel, Box.createGlue(), c, 0, 5, 1, 1, 0, 0.05, both, 0, 0, 0, 0, cen);
        addComponentToPanel(topNPanel, Box.createGlue(), c, 3, 6, 1, 1, 0.45, 0.05, both, 0, 0, 0, 0, cen);
        addComponentToPanel(topNPanel, Box.createGlue(), c, 0, 7, 1, 1, 0.45, 0.05, both, 0, 0, 0, 0, cen);
        addComponentToPanel(topNPanel, Box.createGlue(), c, 1, 8, 2, 1, 0.1, 0.3, both, 0, 0, 0, 0, cen);
        
        // building search result panel
        searchResultPanel.setLayout(layout);
        addComponentToPanel(searchResultPanel, searchResultLabel, c, 1, 1, 1, 1, 0, 0, both, 0, 0, 0, 0, cen);
        addComponentToPanel(searchResultPanel, searchScroll, c, 1, 2, 2, 1, 0, 0, both, 0, 0, 0, 0, cen);
        addComponentToPanel(searchResultPanel, backToSearchButton, c, 2, 1, 1, 1, 0, 0, non, 0, 0, 0, 0, right);
        addComponentToPanel(searchResultPanel, Box.createGlue(), c, 0, 0, 1, 1, 0, 0, both, 0, 0, 0, 0, cen);
        addComponentToPanel(searchResultPanel, Box.createGlue(), c, 3, 1, 1, 1, 0.1, 0.1, both, 0, 0, 0, 0, cen);
        addComponentToPanel(searchResultPanel, Box.createGlue(), c, 0, 2, 1, 1, 0.1, 0.85, both, 0, 0, 0, 0, cen);
        addComponentToPanel(searchResultPanel, Box.createGlue(), c, 1, 4, 2, 1, 0.8, 0.05, both, 0, 0, 0, 0, cen);

        // building top-n result panel
        topNResultPanel.setLayout(layout);
        addComponentToPanel(topNResultPanel, topNResultLabel, c, 1, 1, 1, 1, 0, 0, both, 0, 0, 0, 0, cen);
        addComponentToPanel(topNResultPanel, topNScroll, c, 1, 2, 2, 1, 0, 0, both, 0, 0, 0, 0, cen);
        addComponentToPanel(topNResultPanel, backToTopNButton, c, 2, 1, 1, 1, 0, 0, non, 0, 0, 0, 0, right);
        addComponentToPanel(topNResultPanel, Box.createGlue(), c, 0, 0, 1, 1, 0, 0, both, 0, 0, 0, 0, cen);
        addComponentToPanel(topNResultPanel, Box.createGlue(), c, 3, 1, 1, 1, 0.1, 0.1, both, 0, 0, 0, 0, cen);
        addComponentToPanel(topNResultPanel, Box.createGlue(), c, 0, 2, 1, 1, 0.1, 0.85, both, 0, 0, 0, 0, cen);
        addComponentToPanel(topNResultPanel, Box.createGlue(), c, 1, 4, 2, 1, 0.8, 0.05, both, 0, 0, 0, 0, cen);
        
        add(startPanel);
        this.setVisible(true);
        this.setSize(800, 600);
        //this.pack();
    }
    
    void addComponentToPanel(JPanel panel, Component comp, GridBagConstraints c, int col, int row, int width, int height,
    		double weightX, double weightY, int fill, int marginTop, int marginLeft, int marginBottom, int marginRight, int anchor) {
    	c.gridx = col;
    	c.gridy = row;
    	c.gridwidth = width;
    	c.gridheight = height;
    	c.weightx = weightX;
    	c.weighty = weightY;
    	c.fill = fill;
    	c.insets = new Insets(marginTop, marginLeft, marginBottom, marginRight);
    	c.anchor = anchor;
    	panel.add(comp, c);
    }
    
    public void actionPerformed(ActionEvent e) {
    	JButton button = (JButton) e.getSource();
    	
    	if(button == chooseFilesButton) {
    		openFileChooser();
    	}
    	if(button == constructIndicesButton) {
    		constructInvertedIndices();
    		showSelectionPage();
    	}
    	if(button == searchForTermButton || button == backToSearchButton) {
    		showSearchPage();
    	}
    	if(button == topNFrequentTermsButton || button == backToTopNButton) {
    		showTopNPage();
    	}
    	if(button == backToSelectionFromSearchButton || button == backToSelectionFromNButton) {
    		showSelectionPage();
    	}
    	if(button == submitSearchButton) {
    		submitSearch();
    	}
    	if(button == submitTopNButton) {
    		submitTopN();
    	}
    }
    
    public void openFileChooser() {
    	selectedFilesDisplay.setVisible(false);
    	selectedFilesDisplay.setText("Selected Files:");
    	selectedFilesDoc = selectedFilesDisplay.getStyledDocument();
    	loadingLabel.setVisible(true);
    	fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    	fileChooser.setMultiSelectionEnabled(true);
    	fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
    	fileChooser.showOpenDialog(this);
    	files = fileChooser.getSelectedFiles();
    	
    	// For each file selected, adding file name to selectedFiles text pane
    	for(File f : files) {
    		System.out.println("\nSelecting file: " + f.getName());
    		try {
    			selectedFilesDoc.insertString(selectedFilesDoc.getLength(), "\n" + f.getName(), center);
    			
    			// Check if files are in GCP, if not then add them
    		} catch (BadLocationException e) {
    			e.printStackTrace();
    		}
    		selectedFilesDoc.setParagraphAttributes(0, selectedFilesDoc.getLength(), center, false);
    		selectedFilesDisplay.setVisible(true);
    		loadingLabel.setVisible(false);
    	}
    }
    
    public void constructInvertedIndices() {
    	// Contact GCP to Submit job
    	// On success, load selection panel
    	// On failure, print error and exit
    }
    
    public void showSelectionPage() {
    	getContentPane().removeAll();
    	add(selectionPanel);
    	revalidate();
    	repaint();
    }
    
    public void showSearchPage() {
    	getContentPane().removeAll();
    	add(searchPanel);
    	revalidate();
    	repaint();
    }
    
    public void showTopNPage() {
    	getContentPane().removeAll();
    	add(topNPanel);
    	revalidate();
    	repaint();
    }
    
    public void showSearchResultPage() {
    	getContentPane().removeAll();
    	add(searchResultPanel);
    	revalidate();
    	repaint();
    }
    
    public void showTopNResultPage() {
    	getContentPane().removeAll();
    	add(topNResultPanel);
    	revalidate();
    	repaint();
    }
    
    public void submitSearch() {
    	// Contact GCP to perform search
    	// On Success, show search result page
    	// On Fail, print error and either exit or return to search page
    	showSearchResultPage();
    }
    
    public void submitTopN() {
    	// Contact GCP to perform top-n
    	// On Success, show top-n result page
    	// On Fail, print error and either exit or return to top-n page
    	showTopNResultPage();
    }

    public static void main(String args[]) {
        new searchEngineGUI();
    }
}
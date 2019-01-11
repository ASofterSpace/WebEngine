/**
 * Unlicensed code created by A Softer Space, 2019
 * www.asofterspace.com/licenses/unlicense.txt
 */
package com.asofterspace.webengine;

import com.asofterspace.toolbox.configuration.ConfigFile;
import com.asofterspace.toolbox.io.JSON;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;


public class GUI implements Runnable {

	private JFrame mainWindow;
	
	private String[] pageList;
	
	private List<PageTab> pageTabs;
	
	private JList<String> pageListComponent;
	
	private ConfigFile configuration;
	
	
	public GUI(ConfigFile config) {
		configuration = config;
	}
	
	@Override
	public void run() {
		
		createGUI();
		
		showGUI();
	}

	private void createGUI() {
		
		// Create the window
		mainWindow = new JFrame(Main.PROGRAM_TITLE);

		// Add content to the window
		createTopPanel(mainWindow);
		createMainPanel(mainWindow);
		createBottomPanel(mainWindow);
		
		// Stage everything to be shown
		mainWindow.pack();
		
		// Center the window
        mainWindow.setLocationRelativeTo(null);
        
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Actually display the whole jazz
        mainWindow.setVisible(true);
	}
	
	private JPanel createTopPanel(JFrame parent) {
		
	    JPanel topPanel = new JPanel();
	    topPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
	    
		JLabel label = new JLabel(Main.PROGRAM_TITLE + " version " + Main.VERSION_NUMBER + " from " + Main.VERSION_DATE);
		topPanel.add(label);

		parent.add(topPanel, BorderLayout.PAGE_START);
		
		return topPanel;
	}
	
	private JPanel createMainPanel(JFrame parent) {
		
	    JPanel mainPanel = new JPanel();
	    mainPanel.setPreferredSize(new Dimension(800, 500));
	    mainPanel.setLayout(new GridLayout(1, 2));

	    JPanel mainPanelRight = new JPanel();
	    String[] pageList = createPageTabs(mainPanelRight);
	    
		pageListComponent = new JList<String>(pageList);
		
		MouseListener pageListClickListener = new MouseListener() {
			
			@Override
		    public void mouseClicked(MouseEvent e) {
				showSelectedTab();
		    }

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				showSelectedTab();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				showSelectedTab();
			}
		};
		pageListComponent.addMouseListener(pageListClickListener);
		
		mainPanel.add(pageListComponent);
	    mainPanel.add(mainPanelRight);

		parent.add(mainPanel, BorderLayout.CENTER);
		
	    return mainPanel;
	}
	
	private void showSelectedTab() {

		String selectedItem = (String) pageListComponent.getSelectedValue();

		for (PageTab tab : pageTabs) {
			if (tab.isItem(selectedItem)) {
				tab.show();
			} else {
				tab.hide();
			}
		}
	}
	
	private String[] createPageTabs(JPanel parent) {

	    List<JSON> jsonPages = configuration.getAllContents().getArray("pages");

		pageList = new String[jsonPages.size()];
		pageTabs = new ArrayList<PageTab>();
	    
		int i = 0;
	    
	    for (JSON jsonPage : jsonPages) {
	    	
	    	String pageTitle = jsonPage.getString("title");
	    	
	    	pageList[i++] = pageTitle;

			PageTab tab = new PageTab(parent, pageTitle, jsonPage.getString("path"));
			
			pageTabs.add(tab);
	    }
	    
	    return pageList;
	}

	private JPanel createBottomPanel(JFrame parent) {
		
	    JPanel bottomPanel = new JPanel();
	    bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
	    
	    JButton closeButton = new JButton("Close");
	    closeButton.addActionListener(new ActionListener()
	    {
	      public void actionPerformed(ActionEvent e)
	      {
	        System.exit(0);
	      }
	    });
	    bottomPanel.add(closeButton);

		parent.add(bottomPanel, BorderLayout.PAGE_END);
		
	    return bottomPanel;
	}
	
	private void showGUI() {
		
		mainWindow.pack();
		mainWindow.setVisible(true);
	}

}

/**
 * Unlicensed code created by A Softer Space, 2019
 * www.asofterspace.com/licenses/unlicense.txt
 */
package com.asofterspace.webengine;

import com.asofterspace.toolbox.configuration.ConfigFile;
import com.asofterspace.toolbox.io.JsonParseException;
import com.asofterspace.toolbox.utils.Record;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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

	private String startupWebpage;

	private String startupGoal;


	public GUI(ConfigFile config, String startupWebpage, String startupGoal) {
		this.configuration = config;

		this.startupWebpage = startupWebpage;

		this.startupGoal = startupGoal;
	}

	@Override
	public void run() {

		createGUI();

		showGUI();

		if ((startupWebpage != null) && (startupGoal != null)) {
			PageTab tab = selectTab(startupWebpage);
			if (tab == null) {
				System.err.println("Could not find a webpage with the name '" + startupWebpage + "'!");
				return;
			}
			switch (startupGoal.toLowerCase()) {
				case "compile":
					if (!tab.performCompile()) {
						System.err.println("There was an error during regular compilation of the website!");
						System.exit(1);
					}
					break;
				case "upload":
					if (!tab.performUpload()) {
						System.err.println("There was an error during uploading to the website!");
						System.exit(1);
					}
					break;
				case "compileandupload":
					if (!tab.performCompile()) {
						System.err.println("There was an error during regular compilation of the website!");
						System.exit(1);
					}
					if (!tab.performUpload()) {
						System.err.println("There was an error during uploading to the website!");
						System.exit(1);
					}
					break;
				case "preview":
					if (!tab.performPreview()) {
						System.err.println("There was an error during preview compilation of the website!");
						System.exit(1);
					}
					break;
				default:
					System.err.println("Did not understand the goal '" + startupGoal + "' - please use " +
						"compile, upload, compileAndUpload or preview instead!");
					return;
			}
			System.out.println("Executed " + startupGoal + " for webpage '" + startupWebpage +
				"', shutting down...");
			System.exit(0);
		}
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
		mainPanel.setPreferredSize(new Dimension(1000, 600));
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

		selectTab(selectedItem);
	}

	private PageTab selectTab(String tabName) {

		PageTab result = null;

		for (PageTab tab : pageTabs) {
			if (tab.isItem(tabName)) {
				tab.show();
				result = tab;
			} else {
				tab.hide();
			}
		}

		return result;
	}

	private String[] createPageTabs(JPanel parent) {

		List<Record> recPages = configuration.getAllContents().getArray("pages");

		pageTabs = new ArrayList<PageTab>();

		for (Record recPage : recPages) {

			String pageTitle = recPage.getString("title");

			try {
				PageTab tab = new PageTab(parent, pageTitle, recPage.getString("path"));
				pageTabs.add(tab);

			} catch (JsonParseException e) {
				System.err.println("Loading the settings for " + pageTitle + " failed:");
				System.err.println(e);
				System.exit(1);
			}
		}

		int i = 0;

		pageList = new String[pageTabs.size()];

		for (PageTab tab : pageTabs) {
			pageList[i++] = tab.getTitle();
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

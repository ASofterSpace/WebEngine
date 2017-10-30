package com.asofterspace.webengine;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PageTab {

	JPanel parent;

	String title;
	
	String path;
	
	JPanel visualPanel;
	

	public PageTab(JPanel parentPanel, String page, String pathToPage) {

		parent = parentPanel;
		
		title = page;
		
		path = pathToPage;
		
		visualPanel = createVisualPanel();
		
		parent.add(visualPanel);
	}
	
	private JPanel createVisualPanel() {
		
		JPanel tab = new JPanel();
		tab.setLayout(new GridLayout(4, 1));

		JLabel titleLabel = new JLabel(title);
		tab.add(titleLabel);

		JLabel pathLabel = new JLabel("Path: " + path);
		tab.add(pathLabel);

	    JButton previewButton = new JButton("Preview");
	    previewButton.addActionListener(new ActionListener()
	    {
	      public void actionPerformed(ActionEvent e)
	      {
	        // TODO
	      }
	    });
	    tab.add(previewButton);

		tab.setVisible(false);

	    return tab;
	}

	public boolean isItem(String item) {

		if (title == null) {
			return false;
		}
		
		return title.equals(item);
	}

	public void show() {

		visualPanel.setVisible(true);
	}

	public void hide() {

		visualPanel.setVisible(false);
	}

}

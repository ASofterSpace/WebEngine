package com.asofterspace.webengine;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.asofterspace.toolbox.configuration.ConfigFile;

public class Main {
	
	private static JFrame mainWindow;
	

	public static void main(String[] args) {
		
		ConfigFile config = new ConfigFile("settings");
		
		createGUI();
		
		showGUI();
	}
	
	private static void createGUI() {
		
		// Create the window
		mainWindow = new JFrame("WebEngine");
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Add content to the window
		JLabel label = new JLabel("I am the webengine - meow!");
		mainWindow.getContentPane().add(label);
	}
	
	private static void showGUI() {
		
		mainWindow.pack();
		mainWindow.setVisible(true);
	}

}

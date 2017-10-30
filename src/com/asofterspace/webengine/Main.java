package com.asofterspace.webengine;

import javax.swing.SwingUtilities;

import com.asofterspace.toolbox.configuration.ConfigFile;

public class Main {
	
	public static void main(String[] args) {
		
		ConfigFile config = new ConfigFile("settings");
		
		SwingUtilities.invokeLater(new GUI(config));
	}
	
}

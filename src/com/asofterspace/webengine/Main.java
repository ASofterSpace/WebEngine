package com.asofterspace.webengine;

import javax.swing.SwingUtilities;

import com.asofterspace.toolbox.configuration.ConfigFile;
import com.asofterspace.toolbox.web.JSON;

public class Main {
	
	public static void main(String[] args) {
		
		ConfigFile config = new ConfigFile("settings");

		// create a default config file, if necessary
		if (config.getAllContents().isEmpty()) {

			config.setAllContents(new JSON("{\"pages\": []}"));
		}

		SwingUtilities.invokeLater(new GUI(config));
	}
	
}

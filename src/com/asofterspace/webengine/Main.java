/**
 * Unlicensed code created by A Softer Space, 2019
 * www.asofterspace.com/licenses/unlicense.txt
 */
package com.asofterspace.webengine;

import com.asofterspace.toolbox.configuration.ConfigFile;
import com.asofterspace.toolbox.io.JSON;
import com.asofterspace.toolbox.io.JsonParseException;
import com.asofterspace.toolbox.Utils;

import javax.swing.SwingUtilities;


public class Main {

	public final static String PROGRAM_TITLE = "WebEngine";
	public final static String VERSION_NUMBER = "0.0.0.8(" + Utils.TOOLBOX_VERSION_NUMBER + ")";
	public final static String VERSION_DATE = "4. October 2017 - 13. April 2019";

	public static void main(String[] args) {

		// let the Utils know in what program it is being used
		Utils.setProgramTitle(PROGRAM_TITLE);
		Utils.setVersionNumber(VERSION_NUMBER);
		Utils.setVersionDate(VERSION_DATE);

		ConfigFile config = null;

		try {
			config = new ConfigFile("settings");

			// create a default config file, if necessary
			if (config.getAllContents().isEmpty()) {
				config.setAllContents(new JSON("{\"pages\": []}"));
			}

		} catch (JsonParseException e) {
			System.err.println("Loading the settings failed:");
			System.err.println(e);
			System.exit(1);
		}

		SwingUtilities.invokeLater(new GUI(config));
	}

}

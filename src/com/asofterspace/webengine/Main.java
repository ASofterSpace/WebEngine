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
	public final static String VERSION_NUMBER = "0.0.1.0(" + Utils.TOOLBOX_VERSION_NUMBER + ")";
	public final static String VERSION_DATE = "4. October 2017 - 12. January 2025";

	public static void main(String[] args) {

		// let the Utils know in what program it is being used
		Utils.setProgramTitle(PROGRAM_TITLE);
		Utils.setVersionNumber(VERSION_NUMBER);
		Utils.setVersionDate(VERSION_DATE);

		if (args.length > 0) {
			if (args[0].equals("--version")) {
				System.out.println(Utils.getFullProgramIdentifierWithDate());
				return;
			}

			if (args[0].equals("--version_for_zip")) {
				System.out.println("version " + Utils.getVersionNumber());
				return;
			}
		}

		String webpage = null;
		String goal = null;

		if (args.length > 0) {
			if (args.length > 1) {
				System.err.println("Performing '" + args[1] + "' on webpage '" + args[0] + "'...");
				webpage = args[0];
				goal = args[1];
			} else {
				System.err.println("You told me to do something with '" + args[0] +
					"', but as second argument you should also tell me what to do with it (compile or preview.)");
			}
		} else {
			System.out.println("Not performing anything automatically (if you would like to do something " +
				"automatically, use the webpage name as first argument and the goal (compile or previs) " +
				"as second argument.)");
		}

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

		SwingUtilities.invokeLater(new GUI(config, webpage, goal));
	}

}

/**
 * Unlicensed code created by A Softer Space, 2019
 * www.asofterspace.com/licenses/unlicense.txt
 */
package com.asofterspace.webengine;

import com.asofterspace.toolbox.configuration.ConfigFile;
import com.asofterspace.toolbox.gui.WebPreviewer;
import com.asofterspace.toolbox.io.Directory;
import com.asofterspace.toolbox.io.File;
import com.asofterspace.toolbox.io.IoUtils;
import com.asofterspace.toolbox.io.JsonParseException;
import com.asofterspace.toolbox.io.TextFile;
import com.asofterspace.toolbox.utils.Record;
import com.asofterspace.toolbox.web.WebTemplateEngine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class PageTab {

	private final static String UPLOAD_STR_EN = "uploadStrEN";
	private final static String UPLOAD_STR_DE = "uploadStrDE";

	JPanel parent;

	String title;

	String path;

	JPanel visualPanel;

	ConfigFile configuration;

	WebTemplateEngine engine;


	public PageTab(JPanel parentPanel, String pageTitle, String pathToPage) throws JsonParseException {

		parent = parentPanel;

		title = pageTitle;

		path = pathToPage;

		visualPanel = createVisualPanel();

		parent.add(visualPanel);

		configuration = new ConfigFile(path + "/webengine.json");

		Directory origDir = new Directory(path);

		engine = new WebTemplateEngine(origDir, configuration.getAllContents());
	}

	private JPanel createVisualPanel() {

		JPanel tab = new JPanel();
		tab.setLayout(new GridLayout(4, 1));

		JLabel titleLabel = new JLabel(title);
		tab.add(titleLabel);

		JLabel pathLabel = new JLabel("Path: " + path);
		tab.add(pathLabel);

		JPanel buttonRow = new JPanel();
		GridLayout buttonRowLayout = new GridLayout(1, 2);
		buttonRowLayout.setHgap(10);
		buttonRow.setLayout(buttonRowLayout);
		tab.add(buttonRow);

		JButton previewButton = new JButton("Preview");
		previewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performPreview();
			}
		});
		buttonRow.add(previewButton);

		JButton compileButton = new JButton("Compile");
		compileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performCompile();
			}
		});
		buttonRow.add(compileButton);

		JButton compileUploadButton = new JButton("Compile and Upload");
		compileUploadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				performCompile();
				performUpload();
			}
		});
		buttonRow.add(compileUploadButton);

		tab.setVisible(false);

		return tab;
	}

	public boolean isItem(String item) {

		if (title == null) {
			return false;
		}

		return title.equals(item);
	}

	public String getTitle() {
		return title;
	}

	public void show() {

		visualPanel.setVisible(true);
	}

	public void hide() {

		visualPanel.setVisible(false);
	}

	private void increaseVersion() {

		Integer oldVersion = configuration.getInteger("version");

		configuration.set("version", oldVersion + 1);
	}

	public boolean performPreview() {

		increaseVersion();

		boolean result = engine.compileTo(new Directory(path + "/previews"), "", true);

		WebPreviewer.openLocalFileInBrowser(path + "/previews/index.htm");

		return result;
	}

	public boolean performCompile() {

		increaseVersion();

		boolean result = engine.compileTo(new Directory(path + "/compiled"), "", false);

		result = engine.compileTo(new Directory(path + "/compiledde"), "de", false) && result;

		return result;
	}

	public boolean performUpload() {

		Directory enDir = new Directory(path + "/compiled");
		Directory deDir = new Directory(path + "/compiledde");

		Record engineConf = engine.getConfig();

		List<String> files = engineConf.getArrayAsStringList("files");

		StringBuilder enBatch = new StringBuilder();
		StringBuilder deBatch = new StringBuilder();

		for (String file : files) {
			if (file.endsWith(".php")) {
				String folder = "";
				if (file.contains("/")) {
					folder = file.substring(0, file.lastIndexOf("/") + 1);
				}
				enBatch.append("cd /var/www/html/asofterspaceen/" + folder + "\r\n");
				enBatch.append("put ");
				enBatch.append((new File(enDir, file)).getAbsoluteFilename());
				enBatch.append("\r\n");
				deBatch.append("cd /var/www/html/asofterspacede/" + folder + "\r\n");
				deBatch.append("put ");
				deBatch.append((new File(deDir, file)).getAbsoluteFilename());
				deBatch.append("\r\n");
			}
		}

		enBatch.append("quit");
		deBatch.append("quit");

		Directory tempDir = new Directory(path + "/temp");
		tempDir.clear();

		TextFile enBatchFile = new TextFile(tempDir, "en.bat");
		enBatchFile.saveContent(enBatch);
		TextFile deBatchFile = new TextFile(tempDir, "de.bat");
		deBatchFile.saveContent(deBatch);

		System.out.println("Starting to upload PHP files both into DE and into EN directories on the server...");
		System.out.println("Starting with EN...");

		IoUtils.execute(engineConf.getString(UPLOAD_STR_EN) + enBatchFile.getAbsoluteFilename());

		System.out.println("Done with EN, continuing with DE...");

		IoUtils.execute(engineConf.getString(UPLOAD_STR_DE) + deBatchFile.getAbsoluteFilename());

		System.out.println("Uploaded all PHP files both into DE and into EN directories on the server!");

		return true;
	}
}

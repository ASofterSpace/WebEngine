/**
 * Unlicensed code created by A Softer Space, 2019
 * www.asofterspace.com/licenses/unlicense.txt
 */
 package com.asofterspace.webengine;

import com.asofterspace.toolbox.configuration.ConfigFile;
import com.asofterspace.toolbox.gui.WebPreviewer;
import com.asofterspace.toolbox.io.Directory;
import com.asofterspace.toolbox.io.File;
import com.asofterspace.toolbox.io.JsonParseException;
import com.asofterspace.toolbox.web.WebTemplateEngine;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class PageTab {

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
}

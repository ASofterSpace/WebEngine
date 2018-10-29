package com.asofterspace.webengine;

import com.asofterspace.toolbox.configuration.ConfigFile;
import com.asofterspace.toolbox.io.Directory;
import com.asofterspace.toolbox.io.File;
import com.asofterspace.toolbox.io.JSON;

import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class PageTab {

	JPanel parent;

	String title;

	String path;

	JPanel visualPanel;

	ConfigFile configuration;


	public PageTab(JPanel parentPanel, String page, String pathToPage) {

		parent = parentPanel;

		title = page;

		path = pathToPage;

		visualPanel = createVisualPanel();

		parent.add(visualPanel);

		configuration = new ConfigFile(path + "/webengine.json");
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
	    previewButton.addActionListener(new ActionListener()
	    {
	      public void actionPerformed(ActionEvent e)
	      {
	        performPreview();
	      }
	    });
	    buttonRow.add(previewButton);

	    JButton compileButton = new JButton("Compile");
	    compileButton.addActionListener(new ActionListener()
	    {
	      public void actionPerformed(ActionEvent e)
	      {
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

	public void show() {

		visualPanel.setVisible(true);
	}

	public void hide() {

		visualPanel.setVisible(false);
	}
	
	/**
	 * Returns true for PHP, Javascript, CSS, etc. files
	 * Returns false otherwise - e.g. for JPEG files
	 */
	private boolean isWebTextFile(File currentFile) {
	
		String path = currentFile.getFilename().toLowerCase();

		return path.endsWith(".php") ||
			   path.endsWith(".js") ||
			   path.endsWith(".json") ||
			   path.endsWith(".htm") ||
			   path.endsWith(".html") ||
			   path.endsWith(".css");
	}

	private void compileTo(String targetDir, boolean convertPhpToHtm) {

		JSON files = configuration.getAllContents().get("files");

		int fileAmount = files.getLength();

		for (int i = 0; i < fileAmount; i++) {

			String currentFile = files.getString(i);

			File indexIn = new File(path + "/" + currentFile);

			String newFileName = path + "/" + targetDir + "/" + currentFile;

			if (isWebTextFile(indexIn)) {

				String content = indexIn.getContent();

				if (currentFile.endsWith(".php")) {

					content = compilePhp(content);

					if (convertPhpToHtm) {
						content = removePhp(content);

						newFileName = newFileName.substring(0, newFileName.length() - 4) + ".htm";
					}
				}
				
				File indexOut = new File(newFileName);

				indexOut.saveContent(content);
				
			} else {

				indexIn.copyToDisk(new File(newFileName));
			}

		}
	}

	private void performPreview() {

		compileTo("previews", true);

		openPreviewInBrowser(path + "/previews/index.htm");
	}

	private void performCompile() {

		compileTo("compiled", false);
	}

	/**
	 * Perform the compilation / templating of a PHP file
	 * @param content  a string containing PHP source code
	 * @return the same string after templating
	 */
	private String compilePhp(String content) {
	
		// first of all, remove templating comments - so if someone has {{-- @include(bla) --}}, then do not even include
		content = removeTemplatingComments(content);

		// now perform the templating (meaning to actually follow up on those @includes)
		content = performTemplating(content);

		// aaaand remove comments again
		content = removeTemplatingComments(content);

		// remove whitespace and empty lines please!
		content = removeWhitespaceAndEmptyLines(content);

		// insert special content texts
		content = insertContentText(content);

		// insert version
		Integer oldVersion = configuration.getInteger("version");
		Integer newVersion = oldVersion + 1;
		content = insertNewVersion(content, newVersion);
		configuration.set("version", newVersion);

		// return result
		return content;
	}

	/**
	 * Removes all PHP tags from a PHP file to convert it into
	 * regular HTML, e.g. for local previews
	 * @param content  a string containing PHP source code
	 * @return the same string without the PHP tags and their contents
	 */
	private String removePhp(String content) {

		// TODO :: improve (right now, this ignores comments, strings etc.)
		
		while (content.contains("<?php")) {

			String contentBefore = content.substring(0, content.indexOf("<?php"));

			String contentAfter = content.substring(content.indexOf("<?php"));
			contentAfter = contentAfter.substring(contentAfter.indexOf("?>") + 2);

			content = contentBefore + contentAfter;
		}
		
		// also replace xyz.php links with xyz.htm links (such that local links also work within the preview)
		content = content.replaceAll(".php", ".htm");

		return content;
	}
	
	/**
	 * Takes a file content containing @include(xyz) and replaces
	 * this with the content of xyz; continues as long as @include
	 * is present (so this can be done recursively, however infinite
	 * loops are not checked and will result in infinite running
	 * without a regular way to abort... oops!)
	 */
	private String performTemplating(String content) {
	
		while (content.contains("@include(")) {
		
			String contentBefore = content.substring(0, content.indexOf("@include("));

			String contentAfter = content.substring(content.indexOf("@include("));
			String contentMiddle = contentAfter.substring(9);
			contentMiddle = contentMiddle.substring(0, contentMiddle.indexOf(")"));
			contentAfter = contentAfter.substring(contentAfter.indexOf(")") + 1);

			File includedFile = new File(path + "/" + contentMiddle);

			content = contentBefore + includedFile.getContent() + contentAfter;
		}
		
		return content;
	}

	/**
	 * Takes a string containing source code and removes
	 * {{-- comments like these --}}
	 * @param content  a string containing source code
	 * @return the same string, but without {{-- comments --}}
	 */
	private String removeTemplatingComments(String content) {

		while (content.contains("{{--")) {

			String contentBefore = content.substring(0, content.indexOf("{{--"));

			String contentAfter = content.substring(content.indexOf("{{--"));
			contentAfter = contentAfter.substring(contentAfter.indexOf("--}}") + 4);

			content = contentBefore + contentAfter;
		}

		return content;
	}

	/**
	 * Takes a string containing source code and removes starting and
	 * trailing spaces as well as excessive newlines
	 * @param content  a string containing source code
	 * @return the same string but without excessive whitespace
	 */
	private String removeWhitespaceAndEmptyLines(String content) {

		while (content.startsWith(" ")) {
			content = content.substring(1);
		}

		while (content.contains(" \n") || content.contains("\t\n")) {
			content = content.replaceAll(" \n", "\n");
			content = content.replaceAll("\t\n", "\n");
		}

		while (content.contains("\n ") || content.contains("\n\t")) {
			content = content.replaceAll("\n ", "\n");
			content = content.replaceAll("\n\t", "\n");
		}

		while (content.endsWith(" ")) {
			content = content.substring(0, content.length() - 1);
		}

		while (content.contains("\n\n")) {
			content = content.replaceAll("\n\n", "\n");
		}

		return content;
	}

	/**
	 * Takes a string containing source code and inserts text
	 * replacing @content(foobar) placeholders
	 * @param content  a string containing source code
	 * @return the same string, but with placeholders filled
	 */
	private String insertContentText(String content) {

		JSON contentConfig = configuration.getAllContents().get("content");

		while (content.contains("@content(")) {

			int atIndex = content.indexOf("@content(");

			String beforeContent = content.substring(0, atIndex);

			String contentKey = content.substring(atIndex + 9, content.length());

			atIndex = contentKey.indexOf(")");

			String afterContent = contentKey.substring(atIndex + 1);

			contentKey = contentKey.substring(0, atIndex);

			content = beforeContent + contentConfig.getString(contentKey) + afterContent;
		}

		return content;
	}

	/**
	 * Takes a string containing source code and inserts incrementing
	 * text for @version placeholders
	 * @param content  a string containing source code
	 * @return the same string, but with @version placeholders replaced
	 */
	private String insertNewVersion(String content, Integer version) {

		while (content.contains("@version")) {
			content = content.replaceAll("@version", ""+version);
		}

		return content;
	}

	private void openPreviewInBrowser(String previewFileName) {

		try {
			String absolutePath = new java.io.File(previewFileName).getCanonicalPath().toString().replace("\\", "/");

			if (Desktop.isDesktopSupported()) {
					Desktop.getDesktop().browse(new URI("file:///" + absolutePath));
			}
		} catch (IOException | URISyntaxException e) {
			System.err.println("[ERROR] trying to open the preview in a browser resulted in an I/O Exception - not quite inconceivable!");
		}
	}

}

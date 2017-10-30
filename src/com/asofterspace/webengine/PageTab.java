package com.asofterspace.webengine;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.asofterspace.toolbox.io.File;

public class PageTab {

	JPanel parent;

	String title;
	
	String path;
	
	Integer version;
	
	JPanel visualPanel;
	

	public PageTab(JPanel parentPanel, String page, String pathToPage, Integer versionOfPage) {

		parent = parentPanel;
		
		title = page;
		
		path = pathToPage;
		
		version = versionOfPage;
		
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
	        performPreview();
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
	
	private void performPreview() {
		
		File indexIn = new File(path + "/index.php");
		
		String content = indexIn.getContent();
		
		content = compilePhp(content);
		
		content = removePhp(content);
		
		File indexOut = new File(path + "/index.htm");
		
		indexOut.saveContent(content);
	}

	/**
	 * Perform the compilation / templating of a PHP file
	 * @param content  a string containing PHP source code
	 * @return the same string after templating
	 */
	private String compilePhp(String content) {

		content = removeTemplatingComments(content);
		
		content = removeWhitespaceAndEmptyLines(content);
		
		content = insertContentText(content);
		
		content = insertNewVersion(content);
		
		return content;
	}
	
	/**
	 * Removes all PHP tags from a PHP file to convert it into
	 * regular HTML, e.g. for local previews
	 * @param content  a string containing PHP source code
	 * @return the same string without the PHP tags and their contents
	 */
	private String removePhp(String content) {
		
		// TODO
		
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
		
		// TODO
		
		return content;
	}

	/**
	 * Takes a string containing source code and inserts incrementing
	 * text for @version placeholders
	 * @param content  a string containing source code
	 * @return the same string, but with @version placeholders replaced
	 */
	private String insertNewVersion(String content) {

		// TODO :: somehow increment this version, and store the
		// incremented value back in the configuration file
		
		while (content.contains("@version")) {
			content = content.replaceAll("@version", ""+version);
		}
		
		return content;
	}

}

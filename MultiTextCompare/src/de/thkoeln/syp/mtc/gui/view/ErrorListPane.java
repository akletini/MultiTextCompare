package de.thkoeln.syp.mtc.gui.view;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ErrorListPane extends JFrame {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -7789052817218164171L;
	private DefaultListModel<String> errorList;
	private JList<String> listFilePath;
	private JPanel panel;
	private JScrollPane scrollPane;

	public ErrorListPane() {
		panel = new JPanel();
		errorList = new DefaultListModel<String>();
		listFilePath = new JList<String>(errorList);

		scrollPane = new JScrollPane(listFilePath);
		panel.add(scrollPane, "cell 0 5 3 6,grow");
	}

	public DefaultListModel<String> getErrorList() {
		return errorList;
	}

	public void setErrorList(DefaultListModel<String> errorList) {
		this.errorList = errorList;
	}

}

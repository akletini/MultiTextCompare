package de.thkoeln.syp.mtc.gui.view;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.w3c.dom.ls.LSInput;

import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JButton;

public class ErrorListPane extends JFrame {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -7789052817218164171L;
	private DefaultListModel<String> errorList;
	private JList<String> listFilePath;
	private JPanel panel;
	private JLabel labelTitle;
	private JButton btnShowError;
	private JScrollPane scrollPane;

	public ErrorListPane() {
		panel = new JPanel();
		errorList = new DefaultListModel<String>();
//		errorList.addElement("Test1");
//		errorList.addElement("Test2");
		panel.setLayout(new MigLayout("", "[grow,fill]", "[30px][grow,fill][30px]"));
		
		labelTitle = new JLabel("Parse Error Overview");
		labelTitle.setFont(new Font("Tahoma", Font.BOLD, 13));
		panel.add(labelTitle, "cell 0 0");
		
		scrollPane = new JScrollPane();
		scrollPane.setBackground(Color.WHITE);
		listFilePath = new JList<String>(errorList);
		listFilePath.setBackground(Color.WHITE);
		scrollPane = new JScrollPane(listFilePath);
		panel.add(scrollPane, "flowx,cell 0 1");
		
		btnShowError = new JButton("Show error");
		panel.add(btnShowError, "cell 0 2");
		
		add(panel);
		setMinimumSize(new Dimension(400,700));
		setSize(new Dimension(400,500));
		setLocationRelativeTo(null);
	}

	public DefaultListModel<String> getErrorList() {
		return errorList;
	}

	public void setErrorList(DefaultListModel<String> errorList) {
		this.errorList = errorList;
	}

}

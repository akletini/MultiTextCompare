package de.thkoeln.syp.mtc.gui.view;

import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;
import de.thkoeln.syp.mtc.gui.control.FileSelectionController;
import de.thkoeln.syp.mtc.gui.control.Management;

public class FileSelectionView extends JFrame {
	private Management management;
	private JPanel panel;
	private JScrollPane scrollPane;
	private JTextField textFieldFileName;
	private JLabel lblRoot, lblRootPath, lblDateiname, lblSelectedFiles,
			lblFileCount;
	private JButton btnSetRoot, btnSearch, btnAddFiles, btnDelete, btnReset,
			btnCompare;
	private JRadioButton rdbtnTxt, rdbtnXml, rdbtnJson, rdbtnAll;
	private ButtonGroup bg;
	private DefaultListModel<String> model;
	private JList<String> listFilePath;

	public FileSelectionView() {
		management = Management.getInstance();

		// Panel
		panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.setLayout(new MigLayout("", "[][100px,grow,center][]",
				"[][][][10px][5][grow][][][][][][][10][]"));

		// Label, Eingabefeld Buttons, Radiobuttons
		lblRoot = new JLabel("Root path:");
		panel.add(lblRoot, "cell 0 0");

		lblRootPath = new JLabel("CurrentPath");
		panel.add(lblRootPath, "cell 1 0,growx");

		btnSetRoot = new JButton("Set directory..");
		panel.add(btnSetRoot, "cell 2 0,growx");

		lblDateiname = new JLabel("File name:");
		panel.add(lblDateiname, "cell 0 1");

		lblSelectedFiles = new JLabel("Selected Files:");
		panel.add(lblSelectedFiles, "flowx,cell 0 11");

		lblFileCount = new JLabel("0");
		panel.add(lblFileCount, "cell 0 11");

		textFieldFileName = new JTextField(" ");
		panel.add(textFieldFileName, "cell 1 1,growx");
		textFieldFileName.setColumns(10);

		btnSearch = new JButton("Search");
		panel.add(btnSearch, "cell 2 1,growx");

		btnAddFiles = new JButton("Add..");
		panel.add(btnAddFiles, "flowx,cell 0 13");

		btnDelete = new JButton("Delete");
		panel.add(btnDelete, "cell 0 13");

		btnReset = new JButton("Reset");
		panel.add(btnReset, "flowx,cell 2 13,alignx center");

		btnCompare = new JButton("Compare");
		panel.add(btnCompare, "cell 2 13,alignx center");

		rdbtnTxt = new JRadioButton(".txt");
		panel.add(rdbtnTxt, "flowx,cell 1 2");

		rdbtnXml = new JRadioButton(".xml");
		panel.add(rdbtnXml, "cell 1 2");

		rdbtnJson = new JRadioButton(".json");
		panel.add(rdbtnJson, "cell 1 2");

		rdbtnAll = new JRadioButton("All Files");
		panel.add(rdbtnAll, "cell 1 3");

		fileExtToButton();

		bg = new ButtonGroup();
		bg.add(rdbtnTxt);
		bg.add(rdbtnXml);
		bg.add(rdbtnJson);
		bg.add(rdbtnAll);

		// Dateiliste
		model = new DefaultListModel<>();
		listFilePath = new JList<String>(model);
		scrollPane = new JScrollPane(listFilePath);
		panel.add(scrollPane, "cell 0 5 3 6,grow");

		// Config Parameter
		textFieldFileName.setText(management.getFileImporter().getConfig()
				.getDateiname());

		// Controller
		management
				.setFileSelectionController(new FileSelectionController(this));

		// Frame
		this.setContentPane(panel);
		this.setTitle("File selection");
		this.setBounds(100, 100, 750, 500);
		this.getRootPane().setDefaultButton(btnSearch);
		this.pack();
		this.setLocationRelativeTo(null);
	}

	public JLabel getLblRootPath() {
		return lblRootPath;
	}

	public JTextField getTextFieldFileName() {
		return textFieldFileName;
	}

	public JList<String> getListFilePath() {
		return listFilePath;
	}

	public JLabel getLblFileCount() {
		return lblFileCount;
	}

	public DefaultListModel<String> getModel() {
		return model;
	}

	public JRadioButton getRdbtnTxt() {
		return rdbtnTxt;
	}

	public JRadioButton getRdbtnXml() {
		return rdbtnXml;
	}

	public JRadioButton getRdbtnJson() {
		return rdbtnJson;
	}

	public JRadioButton getRdbtnAll() {
		return rdbtnAll;
	}

	public int getRadioButton() {
		if (rdbtnTxt.isSelected())
			return 0;
		if (rdbtnXml.isSelected())
			return 1;
		if (rdbtnJson.isSelected())
			return 2;
		if (rdbtnAll.isSelected())
			return 3;
		else
			return -1;
	}

	private void fileExtToButton() {
		switch (management.getFileImporter().getConfig().getDateityp()) {
		case (".txt"):
			rdbtnTxt.setSelected(true);
			break;

		case (".xml"):
			rdbtnXml.setSelected(true);
			break;

		case (".json"):
			rdbtnJson.setSelected(true);
			break;

		case (""):
			rdbtnAll.setSelected(true);
			break;

		default:
			rdbtnTxt.setSelected(true);
			break;

		}

	}

	public void addSetRootListener(ActionListener e) {
		btnSetRoot.addActionListener(e);
	}

	public void addSearchListener(ActionListener e) {
		btnSearch.addActionListener(e);
	}

	public void addAddFilesListener(ActionListener e) {
		btnAddFiles.addActionListener(e);
	}

	public void addDeleteListener(ActionListener e) {
		btnDelete.addActionListener(e);
	}

	public void addResetListener(ActionListener e) {
		btnReset.addActionListener(e);
	}

	public void addCompareListener(ActionListener e) {
		btnCompare.addActionListener(e);
	}

}

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
import de.thkoeln.syp.mtc.gui.control.DateiauswahlController;

public class DateiauswahlView extends JFrame {
	private DateiauswahlController dateiauswahlController;
	private JPanel contentPane;
	private JScrollPane scrollPane;
	private JTextField textFieldDateiname;
	private JLabel lblRoot;
	private JLabel lblRootPath;
	private JLabel lblDateiname;
	private JButton btnSetRoot;
	private JButton btnSuchen;
	private JButton btnEinfacheSuche;
	private JButton btnReset;
	private JButton btnVergleichen;
	private JRadioButton rdbtnTxt;
	private JRadioButton rdbtnXml;
	private JRadioButton rdbtnJson;
	private JRadioButton rdbtnAll;
	private ButtonGroup bg;
	private DefaultListModel<String> model;
	private JList<String> listFilePath;

	public DateiauswahlView(MainView mainView) {
		setTitle("Dateiauswahl");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 750, 500);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("",
				"[][100px,grow,center][center]",
				"[][][][10px][5][grow][][][][][10][]"));

		lblRoot = new JLabel("Wurzelverzeichnis:");
		contentPane.add(lblRoot, "cell 0 0");

		lblRootPath = new JLabel("CurrentPath");
		contentPane.add(lblRootPath, "cell 1 0,growx");

		btnSetRoot = new JButton("Neu waehlen..");
		contentPane.add(btnSetRoot, "cell 2 0,growx");

		lblDateiname = new JLabel("Dateiname:");
		contentPane.add(lblDateiname, "cell 0 1");

		textFieldDateiname = new JTextField();
		contentPane.add(textFieldDateiname, "cell 1 1,growx");
		textFieldDateiname.setColumns(10);

		btnSuchen = new JButton("Suchen");
		contentPane.add(btnSuchen, "cell 2 1,growx");

		rdbtnTxt = new JRadioButton(".txt");
		rdbtnTxt.setSelected(true);
		contentPane.add(rdbtnTxt, "flowx,cell 1 2");

		rdbtnXml = new JRadioButton(".xml");
		contentPane.add(rdbtnXml, "cell 1 2");

		rdbtnJson = new JRadioButton(".json");
		contentPane.add(rdbtnJson, "cell 1 2");

		rdbtnAll = new JRadioButton("All Files");
		contentPane.add(rdbtnAll, "cell 1 3");

		bg = new ButtonGroup();
		bg.add(rdbtnTxt);
		bg.add(rdbtnXml);
		bg.add(rdbtnJson);
		bg.add(rdbtnAll);

		btnEinfacheSuche = new JButton("Einfache Suche");
		contentPane.add(btnEinfacheSuche, "cell 2 3,growx");

		model = new DefaultListModel<>();
		listFilePath = new JList<String>(model);
		scrollPane = new JScrollPane(listFilePath);
		contentPane.add(scrollPane, "cell 0 5 3 5,grow");

		btnReset = new JButton("Zuruecksetzen");
		contentPane.add(btnReset, "flowx,cell 1 11,growx");

		btnVergleichen = new JButton("Vergleichen");
		contentPane.add(btnVergleichen, "cell 1 11,growx");

		this.getRootPane().setDefaultButton(btnSuchen);

		dateiauswahlController = new DateiauswahlController(mainView, this);
		dateiauswahlController.updateLblRootPath(this);
	}

	public JLabel getLblRootPath() {
		return lblRootPath;
	}

	public JTextField getTextFieldDateiname() {
		return textFieldDateiname;
	}

	public JList<String> getListFilePath() {
		return listFilePath;
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
			return 1;
		if (rdbtnXml.isSelected())
			return 2;
		if (rdbtnJson.isSelected())
			return 3;
		if (rdbtnAll.isSelected())
			return 4;
		else
			return 0;
	}

	public void addSetRootListener(ActionListener e) {
		btnSetRoot.addActionListener(e);
	}

	public void addSuchenListener(ActionListener e) {
		btnSuchen.addActionListener(e);
	}

	public void addEinfacheSucheListener(ActionListener e) {
		btnEinfacheSuche.addActionListener(e);
	}

	public void addResetListener(ActionListener e) {
		btnReset.addActionListener(e);
	}

	public void addVergleichenListener(ActionListener e) {
		btnVergleichen.addActionListener(e);
	}

}

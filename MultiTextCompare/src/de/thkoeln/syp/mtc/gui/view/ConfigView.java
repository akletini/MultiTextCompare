package de.thkoeln.syp.mtc.gui.view;

import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;
import de.thkoeln.syp.mtc.gui.control.ConfigController;
import de.thkoeln.syp.mtc.gui.control.Management;

public class ConfigView extends JFrame {
	private Management management;
	private JPanel panel;
	private JLabel lblRootPath, lblGeneral, lblXml, lblWhitespaces,
			lblBlankLines, lblPunctMarks, lblCaps,
			lblSelectedPath, lblCompareLines, lblValidation,
			lblSortElements, lblSortAttributes, lblDeleteAttributes,
			lblDeleteComments, lblOnlyTags;
	private JComboBox<String> comboBoxValidation;
	private JCheckBox checkBoxWhitespaces, checkBoxBlankLines,
			checkBoxCaps, checkBoxPunctMarks, checkBoxCompareLines,
			checkBoxSortElements, checkBoxSortAttributes,
			checkBoxDeleteAttributes, checkBoxDeleteComments,
			checkBoxOnlyTags;
	private JButton btnSetRoot, btnDefault, btnSave;
	private IConfig config;

	public ConfigView() {
		management = Management.getInstance();
		config = management.getFileImporter().getConfig();

		// Panel
		panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
		panel.setLayout(new MigLayout("", "[120][60][60][120][60,grow][60]",
				"[][][20][][][][][][][][][]"));

		// Labels (Wurzelverzeichnis)
		lblSelectedPath = new JLabel("Root path ");
		lblSelectedPath.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel.add(lblSelectedPath, "cell 0 1,grow");
		lblRootPath = new JLabel(config.getRootDir());
		panel.add(lblRootPath, "cell 1 1 4 1,grow");

		// Buttons
		btnSetRoot = new JButton("Select");
		panel.add(btnSetRoot, "cell 5 1,grow");
		btnDefault = new JButton("Reset");
		panel.add(btnDefault, "cell 1 11 2 1,grow");
		btnSave = new JButton("Save");
		panel.add(btnSave, "cell 3 11,grow");

		// * Allgemein *
		lblGeneral = new JLabel("General");
		lblGeneral.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel.add(lblGeneral, "cell 0 3");

		// Leerzeichen
		lblWhitespaces = new JLabel("Keep whitespaces ");
		panel.add(lblWhitespaces, "cell 0 4,grow");
		checkBoxWhitespaces = new JCheckBox();
		checkBoxWhitespaces.setSelected(config.getKeepWhitespaces());
		panel.add(checkBoxWhitespaces, "cell 1 4,alignx center,growy");

		// Leerzeilen
		lblBlankLines = new JLabel("Keep blank lines ");
		panel.add(lblBlankLines, "cell 0 5,grow");
		checkBoxBlankLines = new JCheckBox();
		checkBoxBlankLines.setSelected(config.getKeepBlankLines());
		panel.add(checkBoxBlankLines, "cell 1 5,alignx center,growy");

		// Satzzeichen
		lblPunctMarks = new JLabel("Keep punctuation marks ");
		panel.add(lblPunctMarks, "cell 0 6,grow");
		checkBoxPunctMarks = new JCheckBox();
		checkBoxPunctMarks.setSelected(config.getKeepPuctuation());
		panel.add(checkBoxPunctMarks, "cell 1 6,alignx center,growy");

		// Grossschreibung
		lblCaps = new JLabel("Keep capitalization ");
		panel.add(lblCaps, "cell 0 7,grow");
		checkBoxCaps = new JCheckBox();
		panel.add(checkBoxCaps, "cell 1 7,alignx center,growy");
		checkBoxCaps.setSelected(config.getKeepCapitalization());

		// CompareLines
		lblCompareLines = new JLabel("Compare Lines");
		panel.add(lblCompareLines, "cell 0 8,grow");
		checkBoxCompareLines = new JCheckBox();
		panel.add(checkBoxCompareLines, "cell 1 8,alignx center,growy");
		checkBoxCompareLines.setSelected(config.getCompareLines());

		// * XML *
		lblXml = new JLabel("XML");
		lblXml.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel.add(lblXml, "cell 3 3");

		// Validation
		lblValidation = new JLabel("Validation");
		panel.add(lblValidation, "cell 3 4");
		String[] comboBoxStrings = { "None", "Internal XSD", "External XSD",
				"DTD" };
		comboBoxValidation = new JComboBox<String>(comboBoxStrings);
		comboBoxValidation.setSelectedIndex(management.getFileImporter()
				.getConfig().getXmlValidation());
		panel.add(comboBoxValidation, "cell 4 4,alignx center");

		// Sortierte Elemente
		lblSortElements = new JLabel("Sort elements");
		panel.add(lblSortElements, "cell 3 5,grow");
		checkBoxSortElements = new JCheckBox();
		checkBoxSortElements.setSelected(config.getXmlSortElements());
		panel.add(checkBoxSortElements, "cell 4 5,alignx center,growy");

		// Sortierte Attribute
		lblSortAttributes = new JLabel("Sort attributes");
		panel.add(lblSortAttributes, "cell 3 6,grow");
		checkBoxSortAttributes = new JCheckBox();
		checkBoxSortAttributes.setSelected(config.getXmlDeleteAttributes());
		panel.add(checkBoxSortAttributes, "cell 4 6,alignx center,growy");

		// Loesche Attribute
		lblDeleteAttributes = new JLabel("Delete attributes");
		panel.add(lblDeleteAttributes, "cell 3 7,grow");
		checkBoxDeleteAttributes = new JCheckBox();
		checkBoxDeleteAttributes.setSelected(config.getXmlDeleteAttributes());
		panel.add(checkBoxDeleteAttributes, "cell 4 7,alignx center,growy");

		// L�sche Kommentare
		lblDeleteComments = new JLabel("Delete comments");
		panel.add(lblDeleteComments, "cell 3 8,grow");
		checkBoxDeleteComments = new JCheckBox();
		checkBoxDeleteComments.setSelected(config.getXmlDeleteComments());
		panel.add(checkBoxDeleteComments, "cell 4 8,alignx center,growy");

		// Nur Tags
		lblOnlyTags = new JLabel("Only tags");
		panel.add(lblOnlyTags, "cell 3 9,grow");
		checkBoxOnlyTags = new JCheckBox();
		checkBoxOnlyTags.setSelected(config.getXmlOnlyTags());
		panel.add(checkBoxOnlyTags, "cell 4 9,alignx center,growy");

		
		// Frame
		this.getContentPane().add(panel);
		this.setTitle("Configuration");
		this.pack();
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		
		//Controller
		management.setConfigController(new ConfigController(this));
	}

	public void addWurzelverzeichnisListener(ActionListener e) {
		btnSetRoot.addActionListener(e);
	}

	public void addDefaultListener(ActionListener e) {
		btnDefault.addActionListener(e);
	}

	public void addSpeichernListener(ActionListener e) {
		btnSave.addActionListener(e);
	}

	public JCheckBox getCheckBoxLeerzeichen() {
		return checkBoxWhitespaces;
	}

	public JCheckBox getCheckBoxLeerzeilen() {
		return checkBoxBlankLines;
	}

	public JCheckBox getCheckBoxSatzzeichen() {
		return checkBoxPunctMarks;
	}

	public JCheckBox getCheckBoxGrossschreibung() {
		return checkBoxCaps;
	}

	public JCheckBox getCheckBoxCompareLines() {
		return checkBoxCompareLines;
	}

	public JComboBox<String> getComboBoxValidation() {
		return comboBoxValidation;
	}

	public JCheckBox getCheckBoxSortiereElemente() {
		return checkBoxSortElements;
	}

	public JCheckBox getCheckBoxSortiereAttribute() {
		return checkBoxSortAttributes;
	}

	public JCheckBox getCheckBoxLoescheAttribute() {
		return checkBoxDeleteAttributes;
	}

	public JCheckBox getCheckBoxLoescheKommentare() {
		return checkBoxDeleteComments;
	}

	public JCheckBox getCheckBoxNurTags() {
		return checkBoxOnlyTags;
	}
	
	public JLabel getLblRootPath(){
		return lblRootPath;
	}

}

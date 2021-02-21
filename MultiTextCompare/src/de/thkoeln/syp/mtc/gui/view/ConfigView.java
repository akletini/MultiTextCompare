package de.thkoeln.syp.mtc.gui.view;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
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
	private JLabel lblRootPath, lblGeneral, lblXml, lblJson, lblWhitespaces,
			lblBlankLines, lblPunctuation, lblCaps, lblSelectedPath,
			lblCompareLines, lblLineMatch, lblXmlValidation, lblXmlPrint,
			lblXmlSortElements, lblXmlSortAttributes, lblXmlDeleteAttributes,
			lblXmlDeleteComments, lblXmlOnlyTags, lblJsonSortKeys,
			lblJsonDeleteValues;
	private String[] comboBoxXmlValidationStrings, comboBoxXmlPrintStrings;
	private JComboBox<String> comboBoxXmlValidation, comboBoxXmlPrint;
	private JCheckBox checkBoxWhitespaces, checkBoxBlankLines, checkBoxCaps,
			checkBoxPunctuation, checkBoxCompareLines, checkBoxLineMatch,
			checkBoxXmlSortElements, checkBoxXmlSortAttributes,
			checkBoxXmlDeleteAttributes, checkBoxXmlDeleteComments,
			checkBoxXmlOnlyTags, checkBoxJsonSortKeys,
			checkBoxJsonDeleteValues;
	private JButton btnSetRoot, btnDefault, btnSave;
	private IConfig config;

	public ConfigView() {
		management = Management.getInstance();
		config = management.getFileImporter().getConfig();

		// Panel
		panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
		panel.setLayout(new MigLayout("",
				"[120][60][60][120][60][60][120][60][60]",
				"[][][][][][][][][][][][]"));

		// Labels (Wurzelverzeichnis)
		lblSelectedPath = new JLabel("Root path ");
		lblSelectedPath.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel.add(lblSelectedPath, "cell 0 1,grow");
		lblRootPath = new JLabel(config.getRootDir());
		panel.add(lblRootPath, "cell 1 1 6 1,grow");

		// Buttons
		btnSetRoot = new JButton("Select");
		panel.add(btnSetRoot, "cell 7 1,grow");
		btnDefault = new JButton("Reset");
		panel.add(btnDefault, "cell 3 11,grow");
		btnSave = new JButton("Save");
		panel.add(btnSave, "cell 4 11 2 1,grow");

		
		// * Allgemein *
		lblGeneral = new JLabel("General");
		lblGeneral.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel.add(lblGeneral, "cell 0 3");

		// Leerzeichen
		lblWhitespaces = new JLabel("Keep whitespaces");
		panel.add(lblWhitespaces, "cell 0 4");
		checkBoxWhitespaces = new JCheckBox();
		checkBoxWhitespaces.setSelected(config.getKeepWhitespaces());
		panel.add(checkBoxWhitespaces, "cell 1 4,alignx center");

		// Leerzeilen
		lblBlankLines = new JLabel("Keep blank lines");
		panel.add(lblBlankLines, "cell 0 5");
		checkBoxBlankLines = new JCheckBox();
		checkBoxBlankLines.setSelected(config.getKeepBlankLines());
		panel.add(checkBoxBlankLines, "cell 1 5,alignx center");

		// Satzzeichen
		lblPunctuation = new JLabel("Keep punctuation marks");
		panel.add(lblPunctuation, "cell 0 6");
		checkBoxPunctuation = new JCheckBox();
		checkBoxPunctuation.setSelected(config.getKeepPuctuation());
		panel.add(checkBoxPunctuation, "cell 1 6,alignx center");

		// Grossschreibung
		lblCaps = new JLabel("Keep capitalization");
		panel.add(lblCaps, "cell 0 7");
		checkBoxCaps = new JCheckBox();
		panel.add(checkBoxCaps, "cell 1 7,alignx center");
		checkBoxCaps.setSelected(config.getKeepCapitalization());

		// Zeilenweiser Vergleich
		lblCompareLines = new JLabel("Compare lines");
		panel.add(lblCompareLines, "cell 0 9");
		checkBoxCompareLines = new JCheckBox();
		panel.add(checkBoxCompareLines, "cell 1 9,alignx center");
		checkBoxCompareLines.setSelected(config.getCompareLines());

		// Zeilen zuordnen
		lblLineMatch = new JLabel("Match lines");
		panel.add(lblLineMatch, "cell 0 10");
		checkBoxLineMatch = new JCheckBox();
		panel.add(checkBoxLineMatch, "cell 1 10,alignx center");
		checkBoxLineMatch.setSelected(config.getLineMatch());

		
		// * XML *
		lblXml = new JLabel("XML");
		lblXml.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel.add(lblXml, "cell 3 3");

		// Validation
		lblXmlValidation = new JLabel("Validation");
		panel.add(lblXmlValidation, "cell 3 4");
		comboBoxXmlValidationStrings = new String[] { "None", "Internal XSD",
				"DTD" };
		comboBoxXmlValidation = new JComboBox<String>(
				comboBoxXmlValidationStrings);
		comboBoxXmlValidation.setSelectedIndex(config.getXmlValidation());
		panel.add(comboBoxXmlValidation, "cell 4 4,alignx center, grow");

		// Print
		lblXmlPrint = new JLabel("Print");
		panel.add(lblXmlPrint, "cell 3 5");
		comboBoxXmlPrintStrings = new String[] { "Pretty", "Raw", "Compact" };
		comboBoxXmlPrint = new JComboBox<String>(comboBoxXmlPrintStrings);
		comboBoxXmlPrint.setSelectedIndex(config.getXmlPrint());
		panel.add(comboBoxXmlPrint, "cell 4 5,alignx center, grow");

		// Sortierte Elemente
		lblXmlSortElements = new JLabel("Sort elements");
		panel.add(lblXmlSortElements, "cell 3 6");
		checkBoxXmlSortElements = new JCheckBox();
		checkBoxXmlSortElements.setSelected(config.getXmlSortElements());
		panel.add(checkBoxXmlSortElements, "cell 4 6,alignx center");

		// Sortierte Attribute
		lblXmlSortAttributes = new JLabel("Sort attributes");
		panel.add(lblXmlSortAttributes, "cell 3 7");
		checkBoxXmlSortAttributes = new JCheckBox();
		checkBoxXmlSortAttributes.setSelected(config.getXmlDeleteAttributes());
		panel.add(checkBoxXmlSortAttributes, "cell 4 7,alignx center");

		// Loesche Attribute
		lblXmlDeleteAttributes = new JLabel("Delete attributes");
		panel.add(lblXmlDeleteAttributes, "cell 3 8");
		checkBoxXmlDeleteAttributes = new JCheckBox();
		checkBoxXmlDeleteAttributes
				.setSelected(config.getXmlDeleteAttributes());
		panel.add(checkBoxXmlDeleteAttributes, "cell 4 8,alignx center");

		// Loesche Kommentare
		lblXmlDeleteComments = new JLabel("Delete comments");
		panel.add(lblXmlDeleteComments, "cell 3 9");
		checkBoxXmlDeleteComments = new JCheckBox();
		checkBoxXmlDeleteComments.setSelected(config.getXmlDeleteComments());
		panel.add(checkBoxXmlDeleteComments, "cell 4 9,alignx center");

		// Nur Tags
		lblXmlOnlyTags = new JLabel("Only tags");
		panel.add(lblXmlOnlyTags, "cell 3 10");
		checkBoxXmlOnlyTags = new JCheckBox();
		checkBoxXmlOnlyTags.setSelected(config.getXmlOnlyTags());
		panel.add(checkBoxXmlOnlyTags, "cell 4 10,alignx center");

		
		// * JSON *
		lblJson = new JLabel("JSON");
		lblJson.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel.add(lblJson, "cell 6 3");

		// Nach Keys Sortieren
		lblJsonSortKeys = new JLabel("Sort keys");
		panel.add(lblJsonSortKeys, "cell 6 4");
		checkBoxJsonSortKeys = new JCheckBox();
		checkBoxJsonSortKeys.setSelected(config.getJsonSortKeys());
		panel.add(checkBoxJsonSortKeys, "cell 7 4,alignx center");

		// Werte loeschen
		lblJsonDeleteValues = new JLabel("Delete values");
		panel.add(lblJsonDeleteValues, "cell 6 5");
		checkBoxJsonDeleteValues = new JCheckBox();
		checkBoxJsonDeleteValues.setSelected(config.getJsonDeleteValues());
		panel.add(checkBoxJsonDeleteValues, "cell 7 5,alignx center");
		

		// Frame
		this.getContentPane().add(panel);
		this.setTitle("Configuration");
		this.pack();
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		try {
			this.setIconImage(ImageIO.read(new File("res/icon.png")));
		} catch (IOException e) {
		}

		// Controller
		management.setConfigController(new ConfigController(this));
	}

	// -- Getter --
	
	public JCheckBox getCheckBoxWhitespaces() {
		return checkBoxWhitespaces;
	}

	public JCheckBox getCheckBoxBlankLines() {
		return checkBoxBlankLines;
	}

	public JCheckBox getCheckBoxPunctuation() {
		return checkBoxPunctuation;
	}

	public JCheckBox getCheckBoxCaps() {
		return checkBoxCaps;
	}

	public JCheckBox getCheckBoxCompareLines() {
		return checkBoxCompareLines;
	}

	public JCheckBox getCheckBoxLineMatch() {
		return checkBoxLineMatch;
	}

	public JComboBox<String> getComboBoxXmlValidation() {
		return comboBoxXmlValidation;
	}

	public JComboBox<String> getComboBoxXmlPrint() {
		return comboBoxXmlPrint;
	}

	public JCheckBox getCheckBoxXmlSortElements() {
		return checkBoxXmlSortElements;
	}

	public JCheckBox getCheckBoxXmlSortAttributes() {
		return checkBoxXmlSortAttributes;
	}

	public JCheckBox getCheckBoxXmlDeleteAttribute() {
		return checkBoxXmlDeleteAttributes;
	}

	public JCheckBox getCheckBoxXmlDeleteComments() {
		return checkBoxXmlDeleteComments;
	}

	public JCheckBox getCheckBoxXmlOnlyTags() {
		return checkBoxXmlOnlyTags;
	}

	public JCheckBox getCheckBoxJsonSortKeys() {
		return checkBoxJsonSortKeys;
	}

	public JCheckBox getCheckBoxJsonDeleteValues() {
		return checkBoxJsonDeleteValues;
	}

	public JLabel getLblRootPath() {
		return lblRootPath;
	}

	// -- Methoden um die Buttons auf den Controller zu verweisen --
	
	public void addSetRootListener(ActionListener e) {
		btnSetRoot.addActionListener(e);
	}

	public void addDefaultListener(ActionListener e) {
		btnDefault.addActionListener(e);
	}

	public void addSaveListener(ActionListener e) {
		btnSave.addActionListener(e);
	}

}

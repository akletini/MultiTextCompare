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
	private JLabel lblRootPath, lblAllgemein, lblXml, lblLeerzeichen,
			lblLeerzeilen, lblSatzzeichen, lblGrossschreibung,
			lblWurzelverzeichnis, lblLinematch, lblValidation,
			lblSortiereElemente, lblSortiereAttribute, lblLoescheAttribute,
			lblLoescheKommentare, lblNurTags;
	private JComboBox<String> comboBoxValidation;
	private JCheckBox checkBoxLeerzeichen, checkBoxLeerzeilen,
			checkBoxGrossschreibung, checkBoxSatzzeichen, checkBoxLinematch,
			checkBoxSortiereElemente, checkBoxSortiereAttribute,
			checkBoxLoescheAttribute, checkBoxLoescheKommentare,
			checkBoxNurTags;
	private JButton btnWurzelverzeichnis, btnDefault, btnSpeichern;
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
		lblWurzelverzeichnis = new JLabel("Root path ");
		lblWurzelverzeichnis.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel.add(lblWurzelverzeichnis, "cell 0 1,grow");
		lblRootPath = new JLabel(config.getRootDir());
		panel.add(lblRootPath, "cell 1 1 4 1,grow");

		// Buttons
		btnWurzelverzeichnis = new JButton("Select");
		panel.add(btnWurzelverzeichnis, "cell 5 1,grow");
		btnDefault = new JButton("Reset");
		panel.add(btnDefault, "cell 1 11 2 1,grow");
		btnSpeichern = new JButton("Save");
		panel.add(btnSpeichern, "cell 3 11,grow");

		// * Allgemein *
		lblAllgemein = new JLabel("General");
		lblAllgemein.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel.add(lblAllgemein, "cell 0 3");

		// Leerzeichen
		lblLeerzeichen = new JLabel("Keep whitespaces ");
		panel.add(lblLeerzeichen, "cell 0 4,grow");
		checkBoxLeerzeichen = new JCheckBox();
		checkBoxLeerzeichen.setSelected(config.getBeachteLeerzeichen());
		panel.add(checkBoxLeerzeichen, "cell 1 4,alignx center,growy");

		// Leerzeilen
		lblLeerzeilen = new JLabel("Keep blank lines ");
		panel.add(lblLeerzeilen, "cell 0 5,grow");
		checkBoxLeerzeilen = new JCheckBox();
		checkBoxLeerzeilen.setSelected(config.getBeachteLeerzeilen());
		panel.add(checkBoxLeerzeilen, "cell 1 5,alignx center,growy");

		// Satzzeichen
		lblSatzzeichen = new JLabel("Keep punctuation marks ");
		panel.add(lblSatzzeichen, "cell 0 6,grow");
		checkBoxSatzzeichen = new JCheckBox();
		checkBoxSatzzeichen.setSelected(config.getBeachteSatzzeichen());
		panel.add(checkBoxSatzzeichen, "cell 1 6,alignx center,growy");

		// Grossschreibung
		lblGrossschreibung = new JLabel("Keep capitalization ");
		panel.add(lblGrossschreibung, "cell 0 7,grow");
		checkBoxGrossschreibung = new JCheckBox();
		panel.add(checkBoxGrossschreibung, "cell 1 7,alignx center,growy");
		checkBoxGrossschreibung.setSelected(config.getBeachteGrossschreibung());

		// Linematch
		lblLinematch = new JLabel("LineMatch");
		panel.add(lblLinematch, "cell 0 8,grow");
		checkBoxLinematch = new JCheckBox();
		panel.add(checkBoxLinematch, "cell 1 8,alignx center,growy");
		checkBoxLinematch.setSelected(config.getLineMatch());

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
				.getConfig().getValidation());
		panel.add(comboBoxValidation, "cell 4 4,alignx center");

		// Sortierte Elemente
		lblSortiereElemente = new JLabel("Sort elements");
		panel.add(lblSortiereElemente, "cell 3 5,grow");
		checkBoxSortiereElemente = new JCheckBox();
		checkBoxSortiereElemente.setSelected(config.getSortiereElemente());
		panel.add(checkBoxSortiereElemente, "cell 4 5,alignx center,growy");

		// Sortierte Attribute
		lblSortiereAttribute = new JLabel("Sort attributes");
		panel.add(lblSortiereAttribute, "cell 3 6,grow");
		checkBoxSortiereAttribute = new JCheckBox();
		checkBoxSortiereAttribute.setSelected(config.getSortiereAttribute());
		panel.add(checkBoxSortiereAttribute, "cell 4 6,alignx center,growy");

		// Loesche Attribute
		lblLoescheAttribute = new JLabel("Delete attributes");
		panel.add(lblLoescheAttribute, "cell 3 7,grow");
		checkBoxLoescheAttribute = new JCheckBox();
		checkBoxLoescheAttribute.setSelected(config.getLoescheAttribute());
		panel.add(checkBoxLoescheAttribute, "cell 4 7,alignx center,growy");

		// Lösche Kommentare
		lblLoescheKommentare = new JLabel("Delete comments");
		panel.add(lblLoescheKommentare, "cell 3 8,grow");
		checkBoxLoescheKommentare = new JCheckBox();
		checkBoxLoescheKommentare.setSelected(config.getLoescheKommentare());
		panel.add(checkBoxLoescheKommentare, "cell 4 8,alignx center,growy");

		// Nur Tags
		lblNurTags = new JLabel("Only tags");
		panel.add(lblNurTags, "cell 3 9,grow");
		checkBoxNurTags = new JCheckBox();
		checkBoxNurTags.setSelected(config.getNurTags());
		panel.add(checkBoxNurTags, "cell 4 9,alignx center,growy");

		
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
		btnWurzelverzeichnis.addActionListener(e);
	}

	public void addDefaultListener(ActionListener e) {
		btnDefault.addActionListener(e);
	}

	public void addSpeichernListener(ActionListener e) {
		btnSpeichern.addActionListener(e);
	}

	public JCheckBox getCheckBoxLeerzeichen() {
		return checkBoxLeerzeichen;
	}

	public JCheckBox getCheckBoxLeerzeilen() {
		return checkBoxLeerzeilen;
	}

	public JCheckBox getCheckBoxSatzzeichen() {
		return checkBoxSatzzeichen;
	}

	public JCheckBox getCheckBoxGrossschreibung() {
		return checkBoxGrossschreibung;
	}

	public JCheckBox getCheckBoxLinematch() {
		return checkBoxLinematch;
	}

	public JComboBox<String> getComboBoxValidation() {
		return comboBoxValidation;
	}

	public JCheckBox getCheckBoxSortiereElemente() {
		return checkBoxSortiereElemente;
	}

	public JCheckBox getCheckBoxSortiereAttribute() {
		return checkBoxSortiereAttribute;
	}

	public JCheckBox getCheckBoxLoescheAttribute() {
		return checkBoxLoescheAttribute;
	}

	public JCheckBox getCheckBoxLoescheKommentare() {
		return checkBoxLoescheKommentare;
	}

	public JCheckBox getCheckBoxNurTags() {
		return checkBoxNurTags;
	}
	
	public JLabel getLblRootPath(){
		return lblRootPath;
	}

}

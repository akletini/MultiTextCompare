package de.thkoeln.syp.mtc.gui.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;
import de.thkoeln.syp.mtc.datenhaltung.impl.IConfigImpl;
import de.thkoeln.syp.mtc.gui.control.KonfigurationController;
import de.thkoeln.syp.mtc.steuerung.impl.IFileImporterImpl;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;

public class KonfigurationView extends JFrame {
	private JPanel panel;
	private JLabel labelWurzelpfad;
	private JLabel labelLeerzeichen;
	private JLabel labelSatzzeichen;
	private JLabel labelGrossschreibung;
	private JLabel labelWurzelverzeichnis;
	private JLabel labelLinematch;
	private JCheckBox checkBoxLeerzeichen;
	private JCheckBox checkBoxGrossschreibung;
	private JCheckBox checkBoxSatzzeichen;
	private JCheckBox checkBoxLinematch;
	private JButton buttonWurzelverzeichnis;
	private JButton buttonDefault;
	private JButton buttonSpeichern;
	private IFileImporter fileImporter;
	private KonfigurationController konfigurationController;
	private IConfig config;
	

	public KonfigurationView() {
		fileImporter = new IFileImporterImpl();
		labelWurzelpfad = new JLabel(fileImporter.getConfig().getRootDir());
		labelLeerzeichen = new JLabel("Beachte Leerzeichen ");
		labelSatzzeichen = new JLabel("Beachte Satzzeichen ");
		labelGrossschreibung = new JLabel("Beachte Großschreibung ");
		labelWurzelverzeichnis = new JLabel("Wurzelverzeichnis ");
		labelLinematch = new JLabel("Zeilenweiser Vergleich");
		checkBoxLeerzeichen = new JCheckBox();
		checkBoxSatzzeichen = new JCheckBox();
		checkBoxGrossschreibung = new JCheckBox();
		checkBoxLinematch = new JCheckBox();
		buttonWurzelverzeichnis = new JButton("Auswählen");
		buttonDefault = new JButton("Zurücksetzen");
		buttonSpeichern = new JButton("Speichern");
		config = fileImporter.getConfig();

		panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
		panel.setLayout(new GridLayout(0, 2));

		panel.add(labelWurzelverzeichnis);
		panel.add(labelWurzelpfad);
		panel.add(new JLabel(""));
		panel.add(buttonWurzelverzeichnis);

		panel.add(labelLeerzeichen);
		panel.add(checkBoxLeerzeichen);
		if(config.getBeachteLeerzeichen()) checkBoxLeerzeichen.setSelected(true);

		panel.add(labelSatzzeichen);
		panel.add(checkBoxSatzzeichen);
		if(config.getBeachteSatzzeichen()) checkBoxSatzzeichen.setSelected(true);

		panel.add(labelGrossschreibung);
		panel.add(checkBoxGrossschreibung);
		if(config.getBeachteGrossschreibung()) checkBoxGrossschreibung.setSelected(true);

		panel.add(labelLinematch);
		panel.add(checkBoxLinematch);
		if(config.getLineMatch()) checkBoxLinematch.setSelected(true);

		panel.add(buttonDefault);
		panel.add(buttonSpeichern);

		this.add(panel, BorderLayout.CENTER);
		this.setTitle("Konfiguration");
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		konfigurationController = new KonfigurationController(this);
	}

	public void addWurzelverzeichnisListener(ActionListener e) {
		buttonWurzelverzeichnis.addActionListener(e);
	}

	public void addDefaultListener(ActionListener e) {
		buttonDefault.addActionListener(e);
	}

	public void addSpeichernListener(ActionListener e) {
		buttonSpeichern.addActionListener(e);
	}
	
	public void updateWurzelpfad(){
		labelWurzelpfad.setText(fileImporter.getConfig().getRootDir());
	}
	
	public IFileImporter getFileImporter(){
		return fileImporter;
	}
	
	public JCheckBox getCheckboxLeerzeichen(){
		return checkBoxLeerzeichen;
	}
	
	public JCheckBox getCheckboxSatzzeichen(){
		return checkBoxSatzzeichen;
	}
	
	public JCheckBox getCheckboxGrossschreibung(){
		return checkBoxGrossschreibung;
	}
	
	public JCheckBox getCheckboxLinematch(){
		return checkBoxLinematch;
	}
}

package de.thkoeln.syp.mtc.gui.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFileChooser;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;
import de.thkoeln.syp.mtc.gui.view.ConfigView;

public class ConfigController {
	private Management management;
	private ConfigView configView;
	private JFileChooser fc;

	public ConfigController(ConfigView configView) {
		management = Management.getInstance();
		this.configView = configView;
		this.configView
				.addSetRootListener(new SetRootListener());
		this.configView.addDefaultListener(new DefaultListener());
		this.configView.addSaveListener(new SaveListener());
	}

	// Select Button: Auswahl des Wurzelverzeichnisses
	class SetRootListener implements ActionListener {
		public void actionPerformed(ActionEvent action) {
			fc = new JFileChooser();
			fc.setCurrentDirectory(new File(management.getFileImporter()
					.getConfig().getRootDir()));
			fc.setDialogTitle("moin");
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.showOpenDialog(configView);
			management.getFileImporter().setRootDir(fc.getSelectedFile());
			management.updateRootPath();
			configView.pack();
		}
	}

	// Default Button: Enfernt alle Häkchen, setzt ComboBoxes auf 0
	class DefaultListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			configView.getCheckBoxWhitespaces().setSelected(false);
			configView.getCheckBoxBlankLines().setSelected(false);
			configView.getCheckBoxPunctuation().setSelected(false);
			configView.getCheckBoxCaps().setSelected(false);
			configView.getCheckBoxCompareLines().setSelected(false);
			configView.getCheckBoxLineMatch().setSelected(false);
			configView.getComboBoxXmlValidation().setSelectedIndex(0);
			configView.getComboBoxXmlPrint().setSelectedIndex(0);
			configView.getCheckBoxXmlSortElements().setSelected(false);
			configView.getCheckBoxXmlSortAttributes().setSelected(false);
			configView.getCheckBoxXmlDeleteAttribute().setSelected(false);
			configView.getCheckBoxXmlDeleteComments().setSelected(false);
			configView.getCheckBoxXmlOnlyTags().setSelected(false);
			configView.getCheckBoxJsonSortKeys().setSelected(false);
			configView.getCheckBoxJsonDeleteValues().setSelected(false);
		}
	}

	// Save Button: Speichert die Konfiguration in der config Datei
	class SaveListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			IConfig config = management.getFileImporter().getConfig();

			config.setKeepWhitespaces(configView.getCheckBoxWhitespaces()
					.isSelected());
			config.setKeepBlankLines(configView.getCheckBoxBlankLines()
					.isSelected());
			config.setKeepCapitalization(configView
					.getCheckBoxCaps().isSelected());
			config.setKeepPuctuation(configView.getCheckBoxPunctuation()
					.isSelected());
			config.setCompareLines(configView.getCheckBoxCompareLines().isSelected());
			config.setLineMatch(configView.getCheckBoxLineMatch().isSelected());

			config.setXmlSortElements(configView.getCheckBoxXmlSortElements()
					.isSelected());
			config.setXmlSortAttributes((configView
					.getCheckBoxXmlSortAttributes().isSelected()));
			config.setXmlDeleteAttributes(configView.getCheckBoxXmlDeleteAttribute()
					.isSelected());
			config.setXmlDeleteComments(configView
					.getCheckBoxXmlDeleteComments().isSelected());
			config.setXmlOnlyTags(configView.getCheckBoxXmlOnlyTags().isSelected());
			
			config.setJsonSortKeys(configView.getCheckBoxJsonSortKeys().isSelected());
			config.setJsonDeleteValues(configView.getCheckBoxJsonDeleteValues().isSelected());
			
			switch (configView.getComboBoxXmlValidation().getSelectedItem()
					.toString()) {
			case "Internal XSD":
				config.setXmlValidation(1);
				break;
			case "External XSD":
				config.setXmlValidation(2);
				break;
			case "DTD":
				config.setXmlValidation(3);
				break;
			default:
				config.setXmlValidation(0);
				break;
			}
			
			switch (configView.getComboBoxXmlPrint().getSelectedItem().toString()) {
			case "Raw":
				config.setXmlPrint(1);
				break;
			case "Compact":
				config.setXmlPrint(2);
				break;
			default:
				config.setXmlPrint(0);
				break;
			}
			
			management.getFileImporter().exportConfigdatei();
			management.appendToLog("Configuration has been saved");
			configView.dispatchEvent(new WindowEvent(configView,
					WindowEvent.WINDOW_CLOSING));
		}
	}
}

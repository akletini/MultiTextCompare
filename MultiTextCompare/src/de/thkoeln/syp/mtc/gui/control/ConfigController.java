package de.thkoeln.syp.mtc.gui.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFileChooser;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;
import de.thkoeln.syp.mtc.gui.view.ConfigView;
import de.thkoeln.syp.mtc.gui.view.PopupView;

public class ConfigController {
	private Management management;
	private ConfigView configView;
	private JFileChooser fc;

	public ConfigController(ConfigView configView) {
		management = Management.getInstance();
		this.configView = configView;
		this.configView
				.addWurzelverzeichnisListener(new WurzelverzeichnisListener());
		this.configView.addDefaultListener(new DefaultListener());
		this.configView.addSpeichernListener(new SpeichernListener());
	}

	class WurzelverzeichnisListener implements ActionListener {
		public void actionPerformed(ActionEvent action) {
			fc = new JFileChooser();
			fc.setCurrentDirectory(new File(management.getFileImporter()
					.getConfig().getRootDir()));
			fc.setDialogTitle("moin");
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.showOpenDialog(configView);
			management.getFileImporter().setRootDir(fc.getSelectedFile());
			configView.updateWurzelpfad();
			configView.pack();
		}
	}

	class DefaultListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			configView.getCheckboxLeerzeichen().setSelected(false);
			configView.getCheckBoxLeerzeilen().setSelected(false);
			configView.getCheckboxSatzzeichen().setSelected(false);
			configView.getCheckboxGrossschreibung().setSelected(false);
			configView.getCheckboxLinematch().setSelected(false);
			configView.getComboBoxValidation().setSelectedIndex(0);
			configView.getCheckBoxSortiereElemente().setSelected(false);
			configView.getCheckBoxSortiereAttribute().setSelected(false);
			configView.getCheckBoxLoescheAttribute().setSelected(false);
			configView.getCheckBoxLoescheKommentare().setSelected(false);
			configView.getCheckBoxNurTags().setSelected(false);
			configView.getCheckboxLeerzeichen().setSelected(false);
		}
	}

	class SpeichernListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			IConfig config = management.getFileImporter().getConfig();

			config.setBeachteLeerzeichen(configView.getCheckboxLeerzeichen()
					.isSelected());

			config.setBeachteLeerzeilen(configView.getCheckBoxLeerzeilen()
					.isSelected());

			config.setBeachteGrossschreibung(configView
					.getCheckboxGrossschreibung().isSelected());

			config.setBeachteSatzzeichen(configView.getCheckboxSatzzeichen()
					.isSelected());

			config.setLineMatch(configView.getCheckboxLinematch().isSelected());

			switch (configView.getComboBoxValidation().getSelectedItem()
					.toString()) {
			case "Internal XSD":
				config.setValidation(1);
				break;
			case "External XSD":
				config.setValidation(2);
				break;
			case "DTD":
				config.setValidation(3);
				break;
			default:
				config.setValidation(0);
				break;
			}

			config.setSortiereElemente(configView.getCheckBoxSortiereElemente()
					.isSelected());

			config.setSortiereAttribute((configView
					.getCheckBoxSortiereAttribute().isSelected()));

			config.setLoescheAttribute(configView.getCheckBoxLoescheAttribute()
					.isSelected());

			config.setLoescheKommentare(configView
					.getCheckBoxLoescheKommentare().isSelected());

			config.setNurTags(configView.getCheckBoxNurTags().isSelected());

			management.getFileImporter().exportConfigdatei();

			new PopupView("", "The settings have been saved.");
			configView.dispatchEvent(new WindowEvent(configView,
					WindowEvent.WINDOW_CLOSING));
		}
	}
}

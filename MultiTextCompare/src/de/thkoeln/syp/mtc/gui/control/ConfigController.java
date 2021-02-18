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
			management.updateWurzelpfad();
			configView.pack();
		}
	}

	class DefaultListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			configView.getCheckBoxLeerzeichen().setSelected(false);
			configView.getCheckBoxLeerzeilen().setSelected(false);
			configView.getCheckBoxSatzzeichen().setSelected(false);
			configView.getCheckBoxGrossschreibung().setSelected(false);
			configView.getCheckBoxCompareLines().setSelected(false);
			configView.getComboBoxValidation().setSelectedIndex(0);
			configView.getCheckBoxSortiereElemente().setSelected(false);
			configView.getCheckBoxSortiereAttribute().setSelected(false);
			configView.getCheckBoxLoescheAttribute().setSelected(false);
			configView.getCheckBoxLoescheKommentare().setSelected(false);
			configView.getCheckBoxNurTags().setSelected(false);
			configView.getCheckBoxLeerzeichen().setSelected(false);
		}
	}

	class SpeichernListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			IConfig config = management.getFileImporter().getConfig();

			config.setKeepWhitespaces(configView.getCheckBoxLeerzeichen()
					.isSelected());

			config.setKeepBlankLines(configView.getCheckBoxLeerzeilen()
					.isSelected());

			config.setKeepCapitalization(configView
					.getCheckBoxGrossschreibung().isSelected());

			config.setKeepPuctuation(configView.getCheckBoxSatzzeichen()
					.isSelected());

			config.setCompareLines(configView.getCheckBoxCompareLines().isSelected());

			switch (configView.getComboBoxValidation().getSelectedItem()
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

			config.setXmlSortElements(configView.getCheckBoxSortiereElemente()
					.isSelected());

			config.setXmlSortAttributes((configView
					.getCheckBoxSortiereAttribute().isSelected()));

			config.setXmlDeleteAttributes(configView.getCheckBoxLoescheAttribute()
					.isSelected());

			config.setXmlDeleteComments(configView
					.getCheckBoxLoescheKommentare().isSelected());

			config.setXmlOnlyTags(configView.getCheckBoxNurTags().isSelected());

			management.getFileImporter().exportConfigdatei();

			new PopupView("", "The settings have been saved.");
			configView.dispatchEvent(new WindowEvent(configView,
					WindowEvent.WINDOW_CLOSING));
		}
	}
}

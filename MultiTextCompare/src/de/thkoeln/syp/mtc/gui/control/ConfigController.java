package de.thkoeln.syp.mtc.gui.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFileChooser;

import de.thkoeln.syp.mtc.gui.view.ConfigView;

public class ConfigController {
	private Management management;
	private ConfigView configView;
	private JFileChooser fc;
	@SuppressWarnings("unused")
	private Logger logger;

	public ConfigController(ConfigView configView) {
		management = Management.getInstance();
		logger = management.getLogger();
		this.configView = configView;
		this.configView
				.addSetRootListener(new SetRootListener());
		this.configView.addDefaultListener(new DefaultListener());
		this.configView.addSaveListener(new SaveListener());
		this.configView.addCancelistener(new CancelListener());
		configView.addSaveAsListener(new SaveAsListener());
		logger = management.getLogger();
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
			configView.getComboBoxComparisonModes().setSelectedIndex(0);
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
			configView.getCheckBoxBestMatch().setSelected(false);
			configView.getComboBoxComparisonModes().setSelectedIndex(0);
			configView.getCheckBoxJSONSemantic().setSelected(false);
			configView.getCheckBoxXMLSemantic().setSelected(false);
		}
	}

	// Save Button: Speichert die Konfiguration in der config Datei
	public class SaveListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			management.saveConfig();
			logger.setMessage("Configuration has been saved", logger.LEVEL_INFO);
			configView.dispatchEvent(new WindowEvent(configView,
					WindowEvent.WINDOW_CLOSING));
		}
	}
	
	// Save As Button: Speichert die Konfiguration in der config Datei
		public class SaveAsListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				management.saveConfigAs();
				logger.setMessage("Configuration has been saved", logger.LEVEL_INFO);
				configView.dispatchEvent(new WindowEvent(configView,
						WindowEvent.WINDOW_CLOSING));
			}
		}
	
	class CancelListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			configView.dispatchEvent(new WindowEvent(configView, WindowEvent.WINDOW_CLOSING));
		}
	}
}

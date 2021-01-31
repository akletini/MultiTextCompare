package de.thkoeln.syp.mtc.gui.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFileChooser;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;
import de.thkoeln.syp.mtc.gui.view.KonfigurationView;
import de.thkoeln.syp.mtc.gui.view.PopupView;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;

public class KonfigurationController {
	private KonfigurationView konfigurationView;
	private JFileChooser fc;
	private IFileImporter fileImporter;

	public KonfigurationController(KonfigurationView konfigurationView) {
		this.konfigurationView = konfigurationView;
		this.konfigurationView
				.addWurzelverzeichnisListener(new WurzelverzeichnisListener());
		this.konfigurationView.addDefaultListener(new DefaultListener());
		this.konfigurationView.addSpeichernListener(new SpeichernListener());
		fileImporter = this.konfigurationView.getFileImporter();
		}

	class WurzelverzeichnisListener implements ActionListener {
		public void actionPerformed(ActionEvent action) {
			fc = new JFileChooser();
			fc.setCurrentDirectory(new File(fileImporter.getConfig()
					.getRootDir()));
			fc.setDialogTitle("moin");
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.showOpenDialog(konfigurationView);
			fileImporter.setRootDir(fc.getSelectedFile());
			konfigurationView.updateWurzelpfad();
			konfigurationView.pack();
		}
	}

	class DefaultListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

		}
	}

	class SpeichernListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			IConfig config = fileImporter.getConfig();
			
			if(konfigurationView.getCheckboxLeerzeichen().isSelected()) config.setBeachteLeerzeichen(true);
			else config.setBeachteLeerzeichen(false);
			
			if(konfigurationView.getCheckboxGrossschreibung().isSelected()) config.setBeachteGrossschreibung(true);
			else config.setBeachteGrossschreibung(false);
			
			if(konfigurationView.getCheckboxSatzzeichen().isSelected()) config.setBeachteSatzzeichen(true);
			else config.setBeachteSatzzeichen(false);
			
			if(konfigurationView.getCheckboxLinematch().isSelected()) config.setLineMatch(true);
			else config.setLineMatch(false);
			
			fileImporter.exportConfigdatei();
			
			new PopupView("Erfolg!", "Die Einstellungen wurden gespeichert.");
			konfigurationView.dispatchEvent(new WindowEvent(konfigurationView,
					WindowEvent.WINDOW_CLOSING));
		}
	}
}

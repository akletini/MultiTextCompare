package de.thkoeln.syp.mtc.gui.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFileChooser;

import de.thkoeln.syp.mtc.gui.view.KonfigurationView;
import de.thkoeln.syp.mtc.gui.view.PopupView;
import de.thkoeln.syp.mtc.steuerung.impl.IFileImporterImpl;
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
			new PopupView("Die Einstellungen wurden gespeichert.");
			konfigurationView.dispatchEvent(new WindowEvent(konfigurationView,
					WindowEvent.WINDOW_CLOSING));
		}
	}
}

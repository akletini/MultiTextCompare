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
	private Management management;
	private JFileChooser fc;

	public KonfigurationController(KonfigurationView konfigurationView) {
		management = Management.getInstance();
		konfigurationView.addWurzelverzeichnisListener(
				new WurzelverzeichnisListener());
		konfigurationView.addDefaultListener(
				new DefaultListener());
		konfigurationView.addSpeichernListener(
				new SpeichernListener());
	}

	class WurzelverzeichnisListener implements ActionListener {
		public void actionPerformed(ActionEvent action) {
			fc = new JFileChooser();
			fc.setCurrentDirectory(new File(management.getFileImporter()
					.getConfig().getRootDir()));
			fc.setDialogTitle("moin");
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.showOpenDialog(management.getKonfigurationView());
			management.getFileImporter().setRootDir(fc.getSelectedFile());
			management.getKonfigurationView().updateWurzelpfad();
			management.getKonfigurationView().pack();
		}
	}

	class DefaultListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

		}
	}

	class SpeichernListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			IConfig config = management.getFileImporter().getConfig();

			if (management.getKonfigurationView().getCheckboxLeerzeichen()
					.isSelected())
				config.setBeachteLeerzeichen(true);
			else
				config.setBeachteLeerzeichen(false);

			if (management.getKonfigurationView().getCheckboxGrossschreibung()
					.isSelected())
				config.setBeachteGrossschreibung(true);
			else
				config.setBeachteGrossschreibung(false);

			if (management.getKonfigurationView().getCheckboxSatzzeichen()
					.isSelected())
				config.setBeachteSatzzeichen(true);
			else
				config.setBeachteSatzzeichen(false);

			if (management.getKonfigurationView().getCheckboxLinematch()
					.isSelected())
				config.setLineMatch(true);
			else
				config.setLineMatch(false);

			management.getFileImporter().exportConfigdatei();

			new PopupView("Erfolg!", "Die Einstellungen wurden gespeichert.");
			management.getKonfigurationView().dispatchEvent(
					new WindowEvent(management.getKonfigurationView(),
							WindowEvent.WINDOW_CLOSING));
		}
	}
}

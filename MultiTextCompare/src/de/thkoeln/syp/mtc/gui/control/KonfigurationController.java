package de.thkoeln.syp.mtc.gui.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.thkoeln.syp.mtc.gui.control.HomeController.DateiauswahlListener;
import de.thkoeln.syp.mtc.gui.view.DateiauswahlView;
import de.thkoeln.syp.mtc.gui.view.KonfigurationView;

public class KonfigurationController {
	private KonfigurationView konfigurationView;
	
	public KonfigurationController(KonfigurationView konfigurationView){
		this.konfigurationView = konfigurationView;
		this.konfigurationView.addWurzelverzeichnisListener(new WurzelverzeichnisListener());
	}
	
	class WurzelverzeichnisListener implements ActionListener{
		public void actionPerformed(ActionEvent action) {
			// Methode für RootDir
		}
	}
}

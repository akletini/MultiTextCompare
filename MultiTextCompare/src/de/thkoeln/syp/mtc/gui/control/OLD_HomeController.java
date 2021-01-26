package de.thkoeln.syp.mtc.gui.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import de.thkoeln.syp.mtc.gui.view.HilfeView;
import de.thkoeln.syp.mtc.gui.view.KonfigurationView;
import de.thkoeln.syp.mtc.gui.view.OLD_Dateiauswahl2View;
import de.thkoeln.syp.mtc.gui.view.OLD_HomeView;

public class OLD_HomeController {
	private OLD_HomeView homeView;

	public OLD_HomeController(OLD_HomeView homeView) {
		this.homeView = homeView;
		this.homeView.addDateiauswahlListener(new DateiauswahlListener());
		this.homeView.addDateiauswahl2Listener(new Dateiauswahl2Listener());
		this.homeView.addKonfigurationListener(new KonfigurationListener());
		this.homeView.addHilfeListener(new HilfeListener());

		// this.homeView.addMatrixListenerTEST(new MatrixListenerTEST());

	}

	class DateiauswahlListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			//new DateiauswahlView();
		}
	}

	class Dateiauswahl2Listener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			new OLD_Dateiauswahl2View();
		}
	}

	class KonfigurationListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			new KonfigurationView();
		}
	}

	class HilfeListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				new HilfeView();
			} catch (IOException io) {
			}
		}
	}
}

// class MatrixListenerTEST implements ActionListener{
// public void actionPerformed(ActionEvent action) {
// new MatrixView();
// }
// }


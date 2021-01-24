package de.thkoeln.syp.mtc.gui.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;
import de.thkoeln.syp.mtc.gui.view.Dateiauswahl2View;
import de.thkoeln.syp.mtc.gui.view.DateiauswahlView;
import de.thkoeln.syp.mtc.gui.view.HilfeView;
import de.thkoeln.syp.mtc.gui.view.HomeView;
import de.thkoeln.syp.mtc.gui.view.KonfigurationView;

public class HomeController {
	private HomeView homeView;

	public HomeController(HomeView homeView) {
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
			new Dateiauswahl2View();
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


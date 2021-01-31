package de.thkoeln.syp.mtc.gui.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import de.thkoeln.syp.mtc.gui.view.DateiauswahlView;
import de.thkoeln.syp.mtc.gui.view.HilfeView;
import de.thkoeln.syp.mtc.gui.view.KonfigurationView;
import de.thkoeln.syp.mtc.gui.view.MainView;

public class MainController {
	private MainView mainView;

	public MainController(MainView mainView) {
		this.mainView = mainView;
		this.mainView.addDateiauswahlListener(new DateiauswahlListener());
		this.mainView.addKonfigurationListener(new KonfigurationListener());
		this.mainView.addHilfeListener(new HilfeListener());
	}

	class DateiauswahlListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			DateiauswahlView dateiauswahlView = new DateiauswahlView(mainView);
			dateiauswahlView.setVisible(true);
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

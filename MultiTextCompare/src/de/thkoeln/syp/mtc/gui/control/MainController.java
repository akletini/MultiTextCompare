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
	private Management management;

	public MainController(MainView mainView) {
		management = Management.getInstance();
		mainView.addDateiauswahlListener(
				new DateiauswahlListener());
		mainView.addKonfigurationListener(
				new KonfigurationListener());
		mainView.addHilfeListener(new HilfeListener());
	}

	class DateiauswahlListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			management.setDateiauswahlView(new DateiauswahlView());
			management.getDateiauswahlView().setVisible(true);
		}
	}

	class KonfigurationListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			management.setKonfigurationView(new KonfigurationView());
			management.getKonfigurationView().setVisible(true);
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

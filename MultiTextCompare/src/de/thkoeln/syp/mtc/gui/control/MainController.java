package de.thkoeln.syp.mtc.gui.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import de.thkoeln.syp.mtc.gui.view.ConfigView;
import de.thkoeln.syp.mtc.gui.view.FileSelectionView;
import de.thkoeln.syp.mtc.gui.view.HelpView;
import de.thkoeln.syp.mtc.gui.view.MainView;

public class MainController {
	private Management management;

	public MainController(MainView mainView) {
		management = Management.getInstance();
		management.setMainController(this);
		mainView.addDateiauswahlListener(new DateiauswahlListener());
		mainView.addKonfigurationListener(new KonfigurationListener());
		mainView.addHilfeListener(new HilfeListener());
	}

	class DateiauswahlListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			if (Management.getInstance().getFileSelectionView() == null)
				management.setFileSelectionView((new FileSelectionView()));
			management.getFileSelectionView().setVisible(true);
			management.getFileSelectionView().toFront();
		}
	}

	class KonfigurationListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (Management.getInstance().getKonfigurationView() == null)
				management.setKonfigurationView(new ConfigView());
			management.getKonfigurationView().setVisible(true);
			management.getKonfigurationView().toFront();
		}
	}

	class HilfeListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			new HelpView();
		}
	}
}

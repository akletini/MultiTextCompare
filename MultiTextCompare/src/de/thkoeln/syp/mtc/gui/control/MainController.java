package de.thkoeln.syp.mtc.gui.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.thkoeln.syp.mtc.gui.view.AboutView;
import de.thkoeln.syp.mtc.gui.view.ConfigView;
import de.thkoeln.syp.mtc.gui.view.FileSelectionView;
import de.thkoeln.syp.mtc.gui.view.HelpView;
import de.thkoeln.syp.mtc.gui.view.MainView;

public class MainController {
	private Management management;

	public MainController(MainView mainView) {
		management = Management.getInstance();
		management.setMainController(this);
		mainView.addFileSelectionListener(new FileSelectionListener());
		mainView.addConfigListener(new ConfigListener());
		mainView.addHelpListener(new HelpListener());
		mainView.addAboutListener(new AboutListener());
	}

	class FileSelectionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			if (Management.getInstance().getFileSelectionView() == null)
				management.setFileSelectionView((new FileSelectionView()));
			management.getFileSelectionView().setVisible(true);
			management.getFileSelectionView().toFront();
		}
	}

	class ConfigListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (Management.getInstance().getConfigView() == null)
				management.setConfigView(new ConfigView());
			management.getConfigView().setVisible(true);
			management.getConfigView().toFront();
		}
	}

	class HelpListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (Management.getInstance().getHelpView() == null)
				management.setHelpView(new HelpView());
			management.getHelpView().setVisible(true);
			management.getHelpView().toFront();
		}
	}

	class AboutListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (Management.getInstance().getAboutView() == null)
				management.setAboutView(new AboutView());
			management.getAboutView().setVisible(true);
			management.getAboutView().toFront();
		}
	}
}

package de.thkoeln.syp.mtc.gui;

import java.awt.EventQueue;

import de.thkoeln.syp.mtc.gui.control.FileSelectionController;
import de.thkoeln.syp.mtc.gui.control.ConfigController;
import de.thkoeln.syp.mtc.gui.control.MainController;
import de.thkoeln.syp.mtc.gui.control.Management;
import de.thkoeln.syp.mtc.gui.view.FileSelectionView;
import de.thkoeln.syp.mtc.gui.view.ConfigView;
import de.thkoeln.syp.mtc.gui.view.MainView;

public class Main {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Management.getInstance().setMainView(new MainView());					
					Management.getInstance().getMainView().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}

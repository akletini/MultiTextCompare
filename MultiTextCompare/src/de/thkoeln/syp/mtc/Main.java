package de.thkoeln.syp.mtc;

import java.awt.EventQueue;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import de.thkoeln.syp.mtc.gui.control.Management;
import de.thkoeln.syp.mtc.gui.view.MainView;

public class Main {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					Management.getInstance().setMainView(new MainView());					
					Management.getInstance().getMainView().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}

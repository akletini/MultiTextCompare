package de.thkoeln.syp.mtc.gui;

import java.awt.EventQueue;

import de.thkoeln.syp.mtc.gui.view.MainView;

public class Main {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainView mainView = new MainView();
					mainView.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}

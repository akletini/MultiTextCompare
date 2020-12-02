package de.thkoeln.syp.mtc.gui;

import de.thkoeln.syp.mtc.gui.control.HomeController;
import de.thkoeln.syp.mtc.gui.view.HomeView;

public class Main {

	public static void main(String[] args) {
		HomeView homeView = new HomeView();
		new HomeController(homeView);
	}

}

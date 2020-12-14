package de.thkoeln.syp.mtc.gui.view;

import java.awt.FileDialog;

import javax.swing.JFrame;

public class Dateiauswahl2View extends JFrame{
	FileDialog fd;
	
	public Dateiauswahl2View(){
		fd = new FileDialog(this, "Verzeichniswahl", FileDialog.LOAD);
	}

}

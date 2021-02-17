package de.thkoeln.syp.mtc.gui.view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

public class HelpView extends JFrame {

	public HelpView() {
		File helpFile = new File(System.getProperty("user.dir")
				+ File.separator + "help_docs" + File.separator + "help.pdf");
		try {
			Desktop d = Desktop.getDesktop();
			d.open(helpFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

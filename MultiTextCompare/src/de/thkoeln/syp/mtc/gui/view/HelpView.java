package de.thkoeln.syp.mtc.gui.view;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.swing.JFrame;

import de.thkoeln.syp.mtc.gui.control.Management;
import de.thkoeln.syp.mtc.logging.Logger;

/**
 * Handler um die Anleitung zu oeffnen
 * 
 * @author Allen Kletinitch
 *
 */
public class HelpView extends JFrame {

	private static final long serialVersionUID = -8187518366010915651L;
	private Management management;
	private Logger logger;

	public HelpView() {
		management = Management.getInstance();
		logger = management.getLogger();
		try {
			String pdf = "help.pdf";
			InputStream pdfStream = Thread.currentThread()
					.getContextClassLoader().getResourceAsStream(pdf);

			Path tempOutput = Files.createTempFile("TempManual", ".pdf");
			tempOutput.toFile().deleteOnExit();

			Files.copy(pdfStream, tempOutput,
					StandardCopyOption.REPLACE_EXISTING);

			File userManual = tempOutput.toFile();

			if (userManual.exists()) {
				Desktop d = Desktop.getDesktop();
				d.open(userManual);
			}
		} catch (IOException e) {
			logger.setMessage(e.toString(), Logger.LEVEL_ERROR);
		}
	}
}

package de.thkoeln.syp.mtc;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import de.thkoeln.syp.mtc.gui.control.Logger;
import de.thkoeln.syp.mtc.gui.control.Management;
import de.thkoeln.syp.mtc.gui.view.MainView;

public class Main {
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			private Management management;
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					management = Management.getInstance();
					management.setMainView(new MainView());					
					management.getMainView().setVisible(true);
					
					Runtime.getRuntime().addShutdownHook(new Thread(){
						public void run(){
							management.getLogger().writeToLogFile("Exit Application MultiTextCompare\n\n", true);
						}
					});
				} catch (Exception e) {
					Logger logger = management.getLogger();
					logger.setMessage(logger.exceptionToString(e), logger.LEVEL_ERROR);
				}
			}
		});
	}
}

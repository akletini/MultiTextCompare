package de.thkoeln.syp.mtc;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import de.thkoeln.syp.mtc.gui.control.Management;
import de.thkoeln.syp.mtc.gui.view.MainView;
import de.thkoeln.syp.mtc.logging.Logger;

public class Main {
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			private Management management;
			private Logger logger;
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					management = Management.getInstance();
					management.setMainView(new MainView());					
					management.getMainView().setVisible(true);
					logger = management.getLogger();
					if (management.getFileImporter().getConfig().getOpenLastComparison()) {
						management.getMainController().new MenuLoadComparisonListener().actionPerformed(null);
						logger.setMessage("Successfully opened last comparison", Logger.LEVEL_INFO);
					}
					
					Runtime.getRuntime().addShutdownHook(new Thread(){
						public void run(){
							management.getLogger().writeToLogFile("Exit Application MultiTextCompare\n\n", true);
						}
					});
				} catch (Exception e) {
					logger.setMessage(e.toString(), Logger.LEVEL_ERROR);
				}
			}
		});
	}
}

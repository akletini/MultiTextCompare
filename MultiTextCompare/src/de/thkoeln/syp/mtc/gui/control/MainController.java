package de.thkoeln.syp.mtc.gui.control;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;
import de.thkoeln.syp.mtc.gui.resources.RowNumberTable;
import de.thkoeln.syp.mtc.gui.view.AboutView;
import de.thkoeln.syp.mtc.gui.view.ConfigView;
import de.thkoeln.syp.mtc.gui.view.FileSelectionView;
import de.thkoeln.syp.mtc.gui.view.FileView;
import de.thkoeln.syp.mtc.gui.view.HelpView;
import de.thkoeln.syp.mtc.gui.view.MainView;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;

public class MainController {
	private Management management;
	private Logger logger;
	private ConfigView configView;

	public MainController(MainView mainView) {
		management = Management.getInstance();
		management.setMainController(this);
		configView = management.getConfigView();
		logger = management.getLogger();
		mainView.addFileSelectionListener(new FileSelectionListener());
		mainView.addConfigListener(new ConfigListener());
		mainView.addHelpListener(new HelpListener());
		mainView.addAboutListener(new AboutListener());
		mainView.addZoomListener(new ZoomListener());
		mainView.addMenuFileSelection(new MenuFileSelectionListener());
		mainView.addLogClearListener(new LogClearListener());
		mainView.addMenuSettingsListener(new MenuSettingsListener());
		mainView.addMenuImportConfigListener(new MenuLoadConfigListener());
		mainView.addMenuSaveConfigAsListener(new MenuSaveConfigAsListener());
		mainView.addMenuSaveConfigListener(new MenuSaveConfigListener());
		mainView.addMenuAboutListener(new MenuAboutListener());
		mainView.addMenuHelpListener(new MenuHelpListener());
		mainView.addMenuShowInfosListener(new MenuShowInfosListener());
		mainView.addMenuShowWarningsListener(new MenuShowWarningsListener());
		mainView.addMenuShowErrorsListener(new MenuShowErrorsListener());
		mainView.addMenuShowLogListener(new MenuShowLogListener());
		mainView.addToolbarLogClearListener(new ToolbarLogClearListener());
		mainView.addToolbarZoomInListener(new ToolbarZoomInListener());
		mainView.addToolbarZoomOutListener(new ToolbarZoomOutListener());
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
			management.setHelpView(null);
			logger.setMessage("Opening help file...", logger.LEVEL_INFO);
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

	class ToolbarLogClearListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			management.clearLog();
		}
	}

	class ToolbarZoomInListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JTable tableMatrix = management.getMainView().getTableMatrix();
			JTable rowNumb = management.getMainView().getRowNumberTable();
			if (tableMatrix != null) {
				int maximumHeight = 100;
				int currentRowHeight = 0, updatedRowHeight = 0;
				// H�he berechnen
				currentRowHeight = tableMatrix.getRowHeight();
				updatedRowHeight = (int) ((double) currentRowHeight + 5);
				if (updatedRowHeight <= maximumHeight) {
					tableMatrix.setRowHeight(updatedRowHeight);
					rowNumb.setRowHeight(updatedRowHeight);
					// Breite berechnen
					int numberOfColumns = tableMatrix.getColumnCount();
					for (int i = 0; i < numberOfColumns; i++) {
						tableMatrix
								.getColumnModel()
								.getColumn(i)
								.setPreferredWidth(
										(int) (updatedRowHeight * 1.25));
					}
					tableMatrix.repaint();

				}
			}
		}
	}

	class ToolbarZoomOutListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JTable tableMatrix = management.getMainView().getTableMatrix();
			JTable rowNumb = management.getMainView().getRowNumberTable();
			if (tableMatrix != null) {
				int minimumHeight = 15;
				int currentRowHeight = 0, updatedRowHeight = 0;

				currentRowHeight = tableMatrix.getRowHeight();
				updatedRowHeight = (int) ((double) currentRowHeight - 5);
				if (updatedRowHeight >= minimumHeight) {
					tableMatrix.setRowHeight(updatedRowHeight);
					rowNumb.setRowHeight(updatedRowHeight);
					// Breite berechnen
					int numberOfColumns = tableMatrix.getColumnCount();
					for (int i = 0; i < numberOfColumns; i++) {
						tableMatrix
								.getColumnModel()
								.getColumn(i)
								.setPreferredWidth(
										(int) (updatedRowHeight * 1.25));
					}
					tableMatrix.repaint();
				}
			}
		}
	}

	class MenuFileSelectionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			if (Management.getInstance().getFileSelectionView() == null)
				management.setFileSelectionView((new FileSelectionView()));
			management.getFileSelectionView().setVisible(true);
			management.getFileSelectionView().toFront();
		}
	}

	class MenuSettingsListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			if (Management.getInstance().getConfigView() == null)
				management.setConfigView((new ConfigView()));
			management.getConfigView().setVisible(true);
			management.getConfigView().toFront();
		}
	}

	class MenuAboutListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (Management.getInstance().getAboutView() == null)
				management.setAboutView(new AboutView());
			management.getAboutView().setVisible(true);
			management.getAboutView().toFront();
		}
	}

	class MenuHelpListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (Management.getInstance().getHelpView() == null)
				management.setHelpView(new HelpView());
			management.setHelpView(null);
			logger.setMessage("Opening help file...", logger.LEVEL_INFO);
		}
	}

	class MenuShowInfosListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			management
					.getFileImporter()
					.getConfig()
					.setShowInfos(management.getMainView().getInfo().getState());
			management.getFileImporter().exportConfigdatei();
		}
	}

	class MenuShowWarningsListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			management
					.getFileImporter()
					.getConfig()
					.setShowWarnings(
							management.getMainView().getWarning().getState());
			management.getFileImporter().exportConfigdatei();
		}
	}

	class MenuShowErrorsListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			management
					.getFileImporter()
					.getConfig()
					.setShowErrors(
							management.getMainView().getError().getState());
			management.getFileImporter().exportConfigdatei();
		}
	}

	class MenuSaveConfigListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			management.saveConfig(e);
		}
	}

	class MenuLoadConfigListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (Management.getInstance().getConfigView() == null) {
				management.setConfigView(new ConfigView());
			}
			ConfigView configView = management.getConfigView();
			configView.setVisible(false);
			MainView mainView = management.getMainView();
			IFileImporter fileImporter = management.getFileImporter();
			FileDialog fd = new FileDialog(management.getMainView(),
					"Select config", FileDialog.LOAD);
			fd.setLocationRelativeTo(null);
			fd.setMultipleMode(false);
			fd.setDirectory(System.getProperty("user.dir") + File.separator
					+ "configs");
			fd.setFile("*" + ".properties");
			fd.setVisible(true);
			try {
				fd.setIconImage(ImageIO.read(new File("res/icon.png")));
			} catch (IOException ioe) {
				logger.setMessage(ioe.toString(),
						logger.LEVEL_ERROR);
			}
			if (fd.getFiles().length == 1) {
				// neue config ziehen
				File newConfig = fd.getFiles()[0];

				// neue config in default config referenzieren
				fileImporter.importConfigdatei(IFileImporter.DEFAULT_CONFIG);
				IConfig config = fileImporter.getConfig();
				config.setPathCurrent(newConfig.getAbsolutePath());
				fileImporter.exportConfigdatei();

				// neue config aktivieren
				fileImporter.importConfigdatei(newConfig);
				config = fileImporter.getConfig();

				configView.getCheckBoxWhitespaces().setSelected(
						config.getKeepWhitespaces());
				configView.getCheckBoxBlankLines().setSelected(
						config.getKeepBlankLines());
				configView.getCheckBoxPunctuation().setSelected(
						config.getKeepPuctuation());
				configView.getCheckBoxCaps().setSelected(
						config.getKeepCapitalization());
				configView.getComboBoxComparisonModes().setSelectedIndex(
						config.getCompareLines() ? 1 : 0);
				configView.getCheckBoxLineMatch().setSelected(
						config.getLineMatch());
				configView.getComboBoxXmlValidation().setSelectedIndex(
						config.getXmlValidation());
				configView.getComboBoxXmlPrint().setSelectedIndex(
						config.getXmlPrint());
				configView.getCheckBoxXmlSortElements().setSelected(
						config.getXmlSortElements());
				configView.getCheckBoxXmlSortAttributes().setSelected(
						config.getXmlSortAttributes());
				configView.getCheckBoxXmlDeleteAttribute().setSelected(
						config.getXmlDeleteAttributes());
				configView.getCheckBoxXmlDeleteComments().setSelected(
						config.getXmlDeleteComments());
				configView.getCheckBoxXmlOnlyTags().setSelected(
						config.getXmlOnlyTags());
				configView.getCheckBoxJsonSortKeys().setSelected(
						config.getJsonSortKeys());
				configView.getCheckBoxJsonDeleteValues().setSelected(
						config.getJsonDeleteValues());
				configView.getCheckBoxBestMatch().setSelected(
						config.getBestMatch());
				configView.getMatchAtSlider().setValue(
						(int) (config.getMatchAt() * 100));
				configView.getTextFieldLookahead().setText(
						"" + config.getMatchingLookahead());

				mainView.getInfo().setState(config.getShowInfos());
				mainView.getWarning().setState(config.getShowWarnings());
				mainView.getError().setState(config.getShowErrors());

				configView.setTitle("Settings using " + config.getPath());
				configView.repaint();
			}
		}
	}

	class MenuSaveConfigAsListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			management.saveConfigAs(e);
		}
	}

	class LogClearListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			management.clearLog();
		}
	}

	class MenuShowLogListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (Management.getInstance().getFileView() == null) {
				management.setFileView(new FileView());
			}
			FileView fileView = management.getFileView();
			Logger logger = management.getLogger();
			try {
				File logFile = logger.getCurrentLogFile();
				BufferedReader input = new BufferedReader(
						new InputStreamReader(new FileInputStream(logFile),
								"UTF-8"));
				fileView.getTextPane().read(input, "Reading log...");
				management.getFileView().getTextPane().setCaretPosition(0);

				fileView.setTitle(logFile.getName());
				management.getFileView().setVisible(true);
			} catch (IOException ioe) {
				logger.setMessage(ioe.toString(),
						logger.LEVEL_ERROR);
			}
		}
	}

	class ZoomListener implements MouseWheelListener {
		public void mouseWheelMoved(MouseWheelEvent e) {
			int minimumHeight = 15;
			int maximumHeight = 100;
			int currentRowHeight = 0, updatedRowHeight = 0;
			JTable tableMatrix = management.getMainView().getTableMatrix();
			JScrollPane matrixScroll = management.getMainView()
					.getMatrixScrollpane();
			RowNumberTable rowNumb = management.getMainView()
					.getRowNumberTable();
			
			if(tableMatrix == null){
				return;
			}
			if (e.isControlDown()) {
				// Kleiner machen
				if (e.getWheelRotation() > 0) {

					// H�he berechnen
					currentRowHeight = tableMatrix.getRowHeight();
					updatedRowHeight = (int) ((double) currentRowHeight - 5);
					if (updatedRowHeight >= minimumHeight) {
						tableMatrix.setRowHeight(updatedRowHeight);
						rowNumb.setRowHeight(updatedRowHeight);

						// Breite berechnen
						int numberOfColumns = tableMatrix.getColumnCount();
						for (int i = 0; i < numberOfColumns; i++) {
							tableMatrix
									.getColumnModel()
									.getColumn(i)
									.setPreferredWidth(
											(int) (updatedRowHeight * 1.25));
						}
						tableMatrix.repaint();
					}
				}
				// Groesser machen
				else {

					// H�he berechnen
					currentRowHeight = tableMatrix.getRowHeight();
					updatedRowHeight = (int) ((double) currentRowHeight + 5);
					if (updatedRowHeight <= maximumHeight) {
						tableMatrix.setRowHeight(updatedRowHeight);
						rowNumb.setRowHeight(updatedRowHeight);
						// Breite berechnen
						int numberOfColumns = tableMatrix.getColumnCount();
						for (int i = 0; i < numberOfColumns; i++) {
							int rowHeight = (int) (updatedRowHeight * 1.25);
							tableMatrix.getColumnModel().getColumn(i)
									.setPreferredWidth(rowHeight);
						}

						tableMatrix.repaint();
					}
				}
			} else {
				Management.getInstance().getMainView().getMatrixScrollpane()
						.dispatchEvent(e);
			}
		}
	}

}

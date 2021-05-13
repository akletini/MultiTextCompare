package de.thkoeln.syp.mtc.gui.control;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;
import de.thkoeln.syp.mtc.datenhaltung.api.IMatrix;
import de.thkoeln.syp.mtc.datenhaltung.impl.IAehnlichkeitImpl;
import de.thkoeln.syp.mtc.datenhaltung.impl.IMatrixImpl;
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
		mainView.addMenuSaveComparisonListener(new MenuSaveComparisonListener());
		mainView.addMenuLoadComparisonListener(new MenuLoadComparisonListener());
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
				// Höhe berechnen
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

	class MenuSaveComparisonListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			// init
			List<IAehnlichkeitImpl> matrix = management.getComparisons();
			DefaultListModel<String> fileSelection = management
					.getCurrentFileSelection();
			Map<File, File> tempFileMap = management.getFileImporter()
					.getTempFilesMap();
			
			if(management.getMainView().getTableMatrix() == null){
				logger.setMessage("Please create a comparison first", logger.LEVEL_WARNING);
				return;
			}

			String compPath = System.getProperty("user.dir") + File.separator
					+ "comparisons";
			FileDialog fd = new FileDialog(management.getMainView(),
					"Save comparison as", FileDialog.SAVE);
			fd.setLocationRelativeTo(null);
			fd.setMultipleMode(false);
			fd.setDirectory(compPath);
			fd.setVisible(true);
			try {
				fd.setIconImage(ImageIO.read(new File("res/icon.png")));
			} catch (IOException ioe) {
				logger.setMessage(
						"Could not locate the MultiTextCompare logo. It was either moved or deleted",
						logger.LEVEL_ERROR);
			}

			if (fd.getFiles().length == 1) {
				File comparison = new File(fd.getFiles()[0].getAbsolutePath());
				if (!comparison.exists()) {
					comparison = new File(comparison.getAbsolutePath() + ".mtc");
				}
				saveComparison(compPath, comparison.getAbsolutePath(), matrix, fileSelection, tempFileMap);
			}

		}

		public void saveComparison(String filePath,
				String fileName,
				List<IAehnlichkeitImpl> matrix,
				DefaultListModel<String> fileSelection,
				Map<File, File> tempFileMap) {
			ObjectOutputStream oos = null;
			FileOutputStream fout = null;

			File compDir = new File(filePath);
			compDir.mkdir();
			File comparison = new File(fileName);
			try {
				comparison.createNewFile();

				fout = new FileOutputStream(comparison);
				oos = new ObjectOutputStream(fout);
				// write objects
				oos.writeObject(matrix);
				oos.writeObject(fileSelection);
				oos.writeObject(tempFileMap);
				
				fout.close();
				oos.close();
			} catch (IOException e) {
				logger.setMessage(e.toString(), logger.LEVEL_ERROR);
			} 
		}
	}

	class MenuLoadComparisonListener implements ActionListener {

		@SuppressWarnings("unchecked")
		public void actionPerformed(ActionEvent e) {
			List<IAehnlichkeitImpl> matrix = new ArrayList<IAehnlichkeitImpl>();
			DefaultListModel<String> fileSelection = new DefaultListModel<String>();
			Map<File, File> tempFileMap = new LinkedHashMap<File, File>();
			FileInputStream fis = null;
			ObjectInputStream ois = null;
			
			String compPath = System.getProperty("user.dir") + File.separator
					+ "comparisons";
			FileDialog fd = new FileDialog(management.getMainView(),
					"Save comparison as", FileDialog.LOAD);
			fd.setLocationRelativeTo(null);
			fd.setFile("*.mtc");
			fd.setMultipleMode(false);
			fd.setDirectory(compPath);
			fd.setVisible(true);
			try {
				fd.setIconImage(ImageIO.read(new File("res/icon.png")));
			} catch (IOException ioe) {
				logger.setMessage(
						"Failed to locate MultiTextCompare logo. It has either been moved or deleted",
						logger.LEVEL_ERROR);
			}

			File[] files = fd.getFiles();
			
			if(files.length == 0){
				return;
			}
			File comparison = files[0];
			if(!comparison.getAbsolutePath().endsWith(".mtc")){
				logger.setMessage("Wrong file type. Only files with the extension .mtc are allowed!", logger.LEVEL_WARNING);
				return;
			}

			try {
				fis = new FileInputStream(comparison);
				ois = new ObjectInputStream(fis);

				matrix = (ArrayList<IAehnlichkeitImpl>) ois.readObject();
				fileSelection = (DefaultListModel<String>) ois.readObject();
				tempFileMap = (LinkedHashMap<File, File>) ois.readObject();

				IMatrix m = new IMatrixImpl();
				m.setInhalt(matrix);
				ArrayList<String> fileNamesFull = Collections
						.list(fileSelection.elements());
				List<String> fileNames = cutFileNamePreamble(fileNamesFull);

				management.getFileImporter().setTempFiles(tempFileMap);
				management.getFileImporter().setTextdateien(
						pathNamesToFileList(fileNames));
				management.setCurrentFileSelection(fileSelection);
				management.getMainView().updateMatrix(m, fileSelection.size(),
						management.getFileNames(fileSelection.size()));

				if (management.getFileSelectionView() == null) {
					management.setFileSelectionView(new FileSelectionView());
				}
				management.getFileSelectionController().updateListFilePath();
				management.setCurrentComparison(comparison);
				management.getMainView().setTitle(management.getMainView().getTitle() + " - " + comparison.getName());
				
				fis.close();
				ois.close();

			} catch (IOException | ClassNotFoundException ex) {
				logger.setMessage(ex.toString(), logger.LEVEL_ERROR);
			}
		}

		private List<String> cutFileNamePreamble(List<String> list) {
			List<String> pathList = new ArrayList<String>();
			for (int i = 0; i < list.size(); i++) {
				pathList.add(list.get(i).split("\\|")[1].trim());
			}
			return pathList;
		}

		private List<File> pathNamesToFileList(List<String> paths) {
			List<File> files = new ArrayList<File>();
			for (int i = 0; i < paths.size(); i++) {
				files.add(new File(paths.get(i)));
			}
			return files;
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
				logger.setMessage("Failed to locate MultiTextCompare logo. It has either been moved or deleted", logger.LEVEL_ERROR);
			}
			if (fd.getFiles().length == 1) {
				// neue config ziehen
				File newConfig = fd.getFiles()[0];
				
				if(!newConfig.getAbsolutePath().endsWith(".properties")){
					logger.setMessage("Wrong file type. Only files with the extension .properties are allowed!", logger.LEVEL_WARNING);
					return;
				}

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
				logger.setMessage(ioe.toString(), logger.LEVEL_ERROR);
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

			if (tableMatrix == null) {
				return;
			}
			if (e.isControlDown()) {
				// Kleiner machen
				if (e.getWheelRotation() > 0) {

					// Höhe berechnen
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

					// Höhe berechnen
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

package de.thkoeln.syp.mtc.gui.control;

import java.awt.FileDialog;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;
import de.thkoeln.syp.mtc.datenhaltung.api.IParseError;
import de.thkoeln.syp.mtc.datenhaltung.impl.IComparisonImpl;
import de.thkoeln.syp.mtc.gui.control.FileSelectionController.CompareListener.CompareThread;
import de.thkoeln.syp.mtc.gui.view.AboutView;
import de.thkoeln.syp.mtc.gui.view.ComparisonView;
import de.thkoeln.syp.mtc.gui.view.ConfigView;
import de.thkoeln.syp.mtc.gui.view.ErrorListView;
import de.thkoeln.syp.mtc.gui.view.FileSelectionView;
import de.thkoeln.syp.mtc.gui.view.FileView;
import de.thkoeln.syp.mtc.gui.view.HelpView;
import de.thkoeln.syp.mtc.gui.view.MainView;
import de.thkoeln.syp.mtc.gui.view.ParseErrorView;
import de.thkoeln.syp.mtc.logging.Logger;
import de.thkoeln.syp.mtc.steuerung.impl.IFileImporterImpl;
import de.thkoeln.syp.mtc.steuerung.impl.IJSONHandlerImpl;
import de.thkoeln.syp.mtc.steuerung.impl.ITextvergleicherImpl;
import de.thkoeln.syp.mtc.steuerung.impl.IXMLHandlerImpl;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;
import de.thkoeln.syp.mtc.steuerung.services.IJSONHandler;
import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;
import de.thkoeln.syp.mtc.steuerung.services.IXMLHandler;

public class Management {
	private static Management instance;

	private ComparisonView comparisonView;
	private FileSelectionView fileSelectionView;
	private HelpView helpView;
	private ConfigView configView;
	private MainView mainView;
	private AboutView aboutView;
	private FileView fileView;
	private ErrorListView errorListPane;
	private ParseErrorView parseErrorView;

	private FileSelectionController fileSelectionController;
	private ConfigController configController;
	private MainController mainController;
	private ComparisonController comparisonController;
	private Logger logger;

	private DefaultListModel<String> currentFileSelection;
	private List<IComparisonImpl> comparisons;
	private boolean isMatrixGreyedOut, isReferenceSet;
	private boolean newSelection;
	private int referenceRow, referenceCol;
	private File currentComparison;
	private List<IParseError> currentErrorList;

	private IFileImporter fileImporter;
	private ITextvergleicher textvergleicher;
	private IXMLHandler xmlvergleicher;
	private IJSONHandler jsonvergleicher;

	private CompareThread compareThread;
	private ExecutorService executorService;

	private Management() {
		fileImporter = new IFileImporterImpl();
		textvergleicher = new ITextvergleicherImpl();
		xmlvergleicher = new IXMLHandlerImpl(fileImporter);
		jsonvergleicher = new IJSONHandlerImpl(fileImporter);

		comparisons = new ArrayList<IComparisonImpl>();
		currentErrorList = new ArrayList<>();
		errorListPane = new ErrorListView();
		isMatrixGreyedOut = false;
		currentComparison = null;
	}

	public static Management getInstance() {
		if (instance == null)
			instance = new Management();
		return instance;
	}

	public ComparisonView getComparisonView() {
		return comparisonView;
	}

	public void setComparisonView(ComparisonView comparisonView) {
		this.comparisonView = comparisonView;
	}

	public FileSelectionView getFileSelectionView() {
		return fileSelectionView;
	}

	public void setFileSelectionView(FileSelectionView fileSelectionView) {
		this.fileSelectionView = fileSelectionView;
	}

	public HelpView getHelpView() {
		return helpView;
	}

	public void setHelpView(HelpView hilfeView) {
		this.helpView = hilfeView;
	}

	public ConfigView getConfigView() {
		return configView;
	}

	public void setConfigView(ConfigView konfigurationView) {
		this.configView = konfigurationView;
	}

	public MainView getMainView() {
		return mainView;
	}

	public void setMainView(MainView mainView) {
		this.mainView = mainView;
	}

	public AboutView getAboutView() {
		return aboutView;
	}

	public void setAboutView(AboutView aboutView) {
		this.aboutView = aboutView;
	}

	public void setFileView(FileView fileView) {
		this.fileView = fileView;
	}

	public FileView getFileView() {
		return fileView;
	}

	public FileSelectionController getFileSelectionController() {
		return fileSelectionController;
	}

	public void setFileSelectionController(
			FileSelectionController fileSelectionController) {
		this.fileSelectionController = fileSelectionController;
	}

	public ConfigController getConfigController() {
		return configController;
	}

	public void setConfigController(ConfigController configController) {
		this.configController = configController;
	}

	public MainController getMainController() {
		return mainController;
	}

	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}

	public IFileImporter getFileImporter() {
		return fileImporter;
	}

	public void setFileImporter(IFileImporter fileImporter) {
		this.fileImporter = fileImporter;
	}

	public ITextvergleicher getTextvergleicher() {
		return textvergleicher;
	}

	public void setTextvergleicher(ITextvergleicher textVergleicher) {
		this.textvergleicher = textVergleicher;
	}

	public IXMLHandler getXmlvergleicher() {
		return xmlvergleicher;
	}

	public void setXmlvergleicher(IXMLHandler xmlVergleicher) {
		this.xmlvergleicher = xmlVergleicher;
	}

	public IJSONHandler getJsonvergleicher() {
		return jsonvergleicher;
	}

	public void setJsonvergleicher(IJSONHandler jsonVergleicher) {
		this.jsonvergleicher = jsonVergleicher;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public ComparisonController getComparisonController() {
		return comparisonController;
	}

	public void setComparisonController(
			ComparisonController comparisonController) {
		this.comparisonController = comparisonController;
	}

	public DefaultListModel<String> getCurrentFileSelection() {
		return currentFileSelection;
	}

	public void setCurrentFileSelection(
			DefaultListModel<String> currentFileSelection) {
		this.currentFileSelection = new DefaultListModel<String>();
		for (int i = 0; i < currentFileSelection.size(); i++) {
			this.currentFileSelection.add(i, currentFileSelection.elementAt(i));
		}
	}

	public List<IComparisonImpl> getComparisons() {
		return comparisons;
	}

	public void setComparisons(List<IComparisonImpl> comparisons) {
		this.comparisons = comparisons;
	}

	public CompareThread getCompareThread() {
		return compareThread;
	}

	public void setCompareThread(CompareThread compareThread) {
		this.compareThread = compareThread;
	}

	public boolean isMatrixGreyedOut() {
		return isMatrixGreyedOut;
	}

	public void setIsMatrixGreyedOut(boolean greyOutMatrix) {
		this.isMatrixGreyedOut = greyOutMatrix;
	}

	// Aktualisiert Wurzelpfad Anzeige in fileSelectionView & configView
	public void updateRootPath() {
		if (configView != null)
			configView.getBtnSetRootPath().setText(
					fileImporter.getConfig().getRootDir());
		if (fileSelectionView != null)
			fileSelectionView.getLblRootPath().setText(
					fileImporter.getConfig().getRootDir());
	}

	public void clearLog() {
		mainView.getTextArea().setText("");
	}

	// Gibt die Dateipfade aller Dateien im FileImporter wieder
	public String[] getPaths() {
		String[] pathArray = new String[fileImporter.getTextdateien().size()];
		for (int i = 0; i < fileImporter.getTextdateien().size(); i++) {
			pathArray[i] = fileImporter.getTextdateien().get(i)
					.getAbsolutePath();
		}
		return pathArray;
	}

	public void saveConfig() {
		IConfig config = fileImporter.getConfig();

		// general
		config.setKeepWhitespaces(configView.getCheckBoxWhitespaces()
				.isSelected());
		config.setKeepBlankLines(configView.getCheckBoxBlankLines()
				.isSelected());
		config.setKeepCapitalization(configView.getCheckBoxCaps().isSelected());
		config.setKeepPuctuation(configView.getCheckBoxPunctuation()
				.isSelected());
		config.setMaxLineLength(configView.getTextFieldMaxLineLengthValue());
		config.setLineMatch(configView.getCheckBoxLineMatch().isSelected());
		config.setMatchAt(((double) configView.getMatchAtSlider().getValue()) / 100);
		config.setOpenLastComparison(configView.getCheckBoxOpenLastComparison()
				.isSelected());
		config.setMatchingLookahead(configView.getTextFieldLookaheadValue());
		config.setBestMatch(configView.getCheckBoxBestMatch().isSelected());

		//xml
		config.setXmlSortElements(configView.getCheckBoxXmlSortElements()
				.isSelected());
		config.setXmlSortAttributes((configView.getCheckBoxXmlSortAttributes()
				.isSelected()));
		config.setXmlDeleteAttributes(configView
				.getCheckBoxXmlDeleteAttribute().isSelected());
		config.setXmlDeleteComments(configView.getCheckBoxXmlDeleteComments()
				.isSelected());
		config.setXmlOnlyTags(configView.getCheckBoxXmlOnlyTags().isSelected());
		config.setXmlUseSemanticComparison(configView.getCheckBoxXMLSemantic()
				.isSelected());

		//json
		config.setJsonSortKeys(configView.getCheckBoxJsonSortKeys()
				.isSelected());
		config.setJsonDeleteValues(configView.getCheckBoxJsonDeleteValues()
				.isSelected());
		config.setJsonUseSemanticComparison(configView
				.getCheckBoxJSONSemantic().isSelected());

		switch (configView.getComboBoxComparisonModes().getSelectedItem()
				.toString()) {
		case "Compare lines":
			config.setCompareLines(true);
			break;
		default:
			config.setCompareLines(false);
			break;
		}

		switch (configView.getComboBoxXmlValidation().getSelectedItem()
				.toString()) {
		case "Internal XSD":
			config.setXmlValidation(1);
			break;
		case "External XSD":
			config.setXmlValidation(2);
			break;
		case "DTD":
			config.setXmlValidation(3);
			break;
		default:
			config.setXmlValidation(0);
			break;
		}

		switch (configView.getComboBoxXmlPrint().getSelectedItem().toString()) {
		case "Raw":
			config.setXmlPrint(1);
			break;
		case "Compact":
			config.setXmlPrint(2);
			break;
		default:
			config.setXmlPrint(0);
			break;
		}

		fileImporter.exportConfigdatei();
	}

	public void saveConfigAs() {
		if (Management.getInstance().getConfigView() == null) {
			setConfigView(new ConfigView());
		}
		String configDir = System.getProperty("user.dir") + File.separator
				+ "configs";
		ConfigView configView = getConfigView();
		IFileImporter fileImporter = getFileImporter();
		FileDialog fd = new FileDialog(getMainView(), "Save config as",
				FileDialog.SAVE);
		fd.setLocationRelativeTo(null);
		fd.setMultipleMode(false);
		fd.setDirectory(configDir);
		fd.setVisible(true);
		try {
			fd.setIconImage(ImageIO.read(new File("res/icon.png")));
		} catch (IOException ioe) {
			logger.setMessage(
					"Failed to locate MultiTextCompare logo. It has either been moved or deleted",
					Logger.LEVEL_ERROR);
		}

		if (fd.getFiles().length == 1) {
			try {
				File newConfig = new File(fd.getFiles()[0].getAbsolutePath());
				// aktuelle config in neue config kopieren
				IConfig config = fileImporter.getConfig();
				if (!newConfig.exists()) {
					newConfig = new File(newConfig.getAbsolutePath()
							+ ".properties");
				}
				newConfig.createNewFile();
				config.setPath(newConfig.getAbsolutePath());
				fileImporter.exportConfigdatei();

				// neue config in default config referenzieren
				fileImporter.importConfigdatei(IFileImporter.DEFAULT_CONFIG);
				config = fileImporter.getConfig();
				config.setPathCurrent(newConfig.getAbsolutePath());
				fileImporter.exportConfigdatei();
				// neue config aktivieren
				fileImporter.importConfigdatei(newConfig);
				saveConfig();
				config = fileImporter.getConfig();

				configView.setTitle("Settings using " + config.getPath());
				configView.repaint();
				logger.setMessage("Configuration has been saved", Logger.LEVEL_INFO);
			} catch (IOException e1) {
				logger.setMessage("Something went wrong, please try again",
						Logger.LEVEL_ERROR);
			}
		}
	}

	// Gibt vollstaendige Liste wieder (A,B,C,..AA,AB,AC,..)
	public String[] getFileNames(int length) {
		String[] fileNames = new String[length];

		for (int i = 0; i < fileNames.length; i++) {
			fileNames[i] = intToFilename(i + 1);
		}
		return fileNames;
	}

	// Gibt passenden Buchstaben fuer Index
	private String intToFilename(int n) {
		char[] buf = new char[(int) Math.floor(Math
				.log(25 * (n + 1)) / Math.log(26))];
		for (int i = buf.length - 1; i >= 0; i--) {
			n--;
			buf[i] = (char) ('A' + n % 26);
			n /= 26;
		}
		return new String(buf);
	}

	public boolean isReferenceSet() {
		return isReferenceSet;
	}

	public void setReferenceSet(boolean isReferenceSet) {
		this.isReferenceSet = isReferenceSet;
	}

	public int getReferenceRow() {
		return referenceRow;
	}

	public void setReferenceRow(int referenceRow) {
		this.referenceRow = referenceRow;
	}

	public int getReferenceCol() {
		return referenceCol;
	}

	public void setReferenceCol(int referenceCol) {
		this.referenceCol = referenceCol;
	}

	public boolean isNewSelection() {
		return newSelection;
	}

	public void setNewSelection(boolean newSelection) {
		this.newSelection = newSelection;
	}

	public File getCurrentComparison() {
		return currentComparison;
	}

	public void setCurrentComparison(File currentComparison) {
		this.currentComparison = currentComparison;
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	public List<IParseError> getCurrentErrorList() {
		return currentErrorList;
	}

	public void setCurrentErrorList(List<IParseError> list) {
		this.currentErrorList = list;
	}

	public ErrorListView getErrorListPane() {
		return errorListPane;
	}

	public void setErrorListPane(ErrorListView errorListPane) {
		this.errorListPane = errorListPane;
	}

	public ParseErrorView getParseErrorView() {
		return parseErrorView;
	}

	public void setParseErrorView(ParseErrorView parseErrorView) {
		this.parseErrorView = parseErrorView;
	}

}

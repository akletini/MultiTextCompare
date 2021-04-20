package de.thkoeln.syp.mtc.gui.control;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.DefaultListModel;

import de.thkoeln.syp.mtc.datenhaltung.impl.IAehnlichkeitImpl;
import de.thkoeln.syp.mtc.gui.view.AboutView;
import de.thkoeln.syp.mtc.gui.view.ComparisonView;
import de.thkoeln.syp.mtc.gui.view.ConfigView;
import de.thkoeln.syp.mtc.gui.view.FileSelectionView;
import de.thkoeln.syp.mtc.gui.view.FileView;
import de.thkoeln.syp.mtc.gui.view.HelpView;
import de.thkoeln.syp.mtc.gui.view.MainView;
import de.thkoeln.syp.mtc.steuerung.impl.IFileImporterImpl;
import de.thkoeln.syp.mtc.steuerung.impl.IJSONvergleicherImpl;
import de.thkoeln.syp.mtc.steuerung.impl.ITextvergleicherImpl;
import de.thkoeln.syp.mtc.steuerung.impl.IXMLvergleicherImpl;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;
import de.thkoeln.syp.mtc.steuerung.services.IJSONvergleicher;
import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;
import de.thkoeln.syp.mtc.steuerung.services.IXMLvergleicher;

public class Management {
	private static Management instance;

	private ComparisonView comparisonView;
	private FileSelectionView fileSelectionView;
	private HelpView helpView;
	private ConfigView configView;
	private MainView mainView;
	private AboutView aboutView;
	private FileView fileView;

	private FileSelectionController fileSelectionController;
	private ConfigController configController;
	private MainController mainController;
	private ComparisonController comparisonController;
	private Logger logger;
	
	private DefaultListModel<String> currentFileSelection;
	private List<IAehnlichkeitImpl> comparisons;

	private IFileImporter fileImporter;
	private ITextvergleicher textvergleicher;
	private IXMLvergleicher xmlvergleicher;
	private IJSONvergleicher jsonvergleicher;

	private Management() {
		fileImporter = new IFileImporterImpl();
		textvergleicher = new ITextvergleicherImpl();
		xmlvergleicher = new IXMLvergleicherImpl(fileImporter);
		jsonvergleicher = new IJSONvergleicherImpl(fileImporter);
		
		comparisons = new ArrayList<IAehnlichkeitImpl>();
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

	public IXMLvergleicher getXmlvergleicher() {
		return xmlvergleicher;
	}

	public void setXmlvergleicher(IXMLvergleicher xmlVergleicher) {
		this.xmlvergleicher = xmlVergleicher;
	}

	public IJSONvergleicher getJsonvergleicher() {
		return jsonvergleicher;
	}

	public void setJsonvergleicher(IJSONvergleicher jsonVergleicher) {
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

	public void setComparisonController(ComparisonController comparisonController) {
		this.comparisonController = comparisonController;
	}

	public DefaultListModel<String> getCurrentFileSelection() {
		return currentFileSelection;
	}

	public void setCurrentFileSelection(DefaultListModel<String> currentFileSelection) {
		this.currentFileSelection = new DefaultListModel<String>();
		for(int i = 0; i < currentFileSelection.size(); i++){
			this.currentFileSelection.add(i, currentFileSelection.elementAt(i));
		}
	}

	public List<IAehnlichkeitImpl> getComparisons() {
		return comparisons;
	}

	public void setComparisons(List<IAehnlichkeitImpl> comparisons) {
		this.comparisons = comparisons;
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


	
	public void clearLog(){
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
}

package de.thkoeln.syp.mtc.gui.control;

import de.thkoeln.syp.mtc.gui.view.ComparisonView;
import de.thkoeln.syp.mtc.gui.view.FileSelectionView;
import de.thkoeln.syp.mtc.gui.view.HelpView;
import de.thkoeln.syp.mtc.gui.view.ConfigView;
import de.thkoeln.syp.mtc.gui.view.MainView;
import de.thkoeln.syp.mtc.steuerung.impl.IFileImporterImpl;
import de.thkoeln.syp.mtc.steuerung.impl.ITextvergleicherImpl;
import de.thkoeln.syp.mtc.steuerung.impl.IXMLvergleicherImpl;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;
import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;
import de.thkoeln.syp.mtc.steuerung.services.IXMLvergleicher;

public class Management {
	private static Management instance;

	private ComparisonView comparisonView;
	private FileSelectionView fileSelectionView;
	private HelpView hilfeView;
	private ConfigView konfigurationView;
	private MainView mainView;

	private FileSelectionController fileSelectionController;
	private ConfigController configController;
	private MainController mainController;

	private IFileImporter fileImporter;
	private ITextvergleicher textvergleicher;
	private IXMLvergleicher xmlvergleicher;

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

	public HelpView getHilfeView() {
		return hilfeView;
	}

	public void setHilfeView(HelpView hilfeView) {
		this.hilfeView = hilfeView;
	}

	public ConfigView getKonfigurationView() {
		return konfigurationView;
	}

	public void setKonfigurationView(ConfigView konfigurationView) {
		this.konfigurationView = konfigurationView;
	}

	public MainView getMainView() {
		return mainView;
	}

	public void setMainView(MainView mainView) {
		this.mainView = mainView;
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

	public void setConfigController(
			ConfigController configController) {
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

	public ITextvergleicher getTextVergleicher() {
		return textvergleicher;
	}

	public void setTextVergleicher(ITextvergleicher textVergleicher) {
		this.textvergleicher = textVergleicher;
	}

	public IXMLvergleicher getXmlvergleicher() {
		return xmlvergleicher;
	}

	public void setXmlvergleicher(IXMLvergleicher xmlvergleicher) {
		this.xmlvergleicher = xmlvergleicher;
	}

	private Management() {
		fileImporter = new IFileImporterImpl();
		textvergleicher = new ITextvergleicherImpl();
		xmlvergleicher = new IXMLvergleicherImpl();
	}

	public static Management getInstance() {
		if (instance == null)
			instance = new Management();
		return instance;
	}
}

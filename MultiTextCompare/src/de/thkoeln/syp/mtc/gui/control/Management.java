package de.thkoeln.syp.mtc.gui.control;

import de.thkoeln.syp.mtc.datenhaltung.api.IMatrix;
import de.thkoeln.syp.mtc.gui.view.ComparisonView;
import de.thkoeln.syp.mtc.gui.view.DateiauswahlView;
import de.thkoeln.syp.mtc.gui.view.HilfeView;
import de.thkoeln.syp.mtc.gui.view.KonfigurationView;
import de.thkoeln.syp.mtc.gui.view.MainView;
import de.thkoeln.syp.mtc.gui.view.PopupView;
import de.thkoeln.syp.mtc.steuerung.impl.IFileImporterImpl;
import de.thkoeln.syp.mtc.steuerung.impl.ITextvergleicherImpl;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;
import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;

public class Management {
	private static Management instance;

	private ComparisonView comparisonView;
	private DateiauswahlView dateiauswahlView;
	private HilfeView hilfeView;
	private KonfigurationView konfigurationView;
	private MainView mainView;

	private DateiauswahlController dateiauswahlController;
	private KonfigurationController konfigurationController;
	private MainController mainController;

	private IFileImporter fileImporter;
	private ITextvergleicher textvergleicher;

	public ComparisonView getComparisonView() {
		return comparisonView;
	}

	public void setComparisonView(ComparisonView comparisonView) {
		this.comparisonView = comparisonView;
	}

	public DateiauswahlView getDateiauswahlView() {
		return dateiauswahlView;
	}

	public void setDateiauswahlView(DateiauswahlView dateiauswahlView) {
		this.dateiauswahlView = dateiauswahlView;
	}

	public HilfeView getHilfeView() {
		return hilfeView;
	}

	public void setHilfeView(HilfeView hilfeView) {
		this.hilfeView = hilfeView;
	}

	public KonfigurationView getKonfigurationView() {
		return konfigurationView;
	}

	public void setKonfigurationView(KonfigurationView konfigurationView) {
		this.konfigurationView = konfigurationView;
	}

	public MainView getMainView() {
		return mainView;
	}

	public void setMainView(MainView mainView) {
		this.mainView = mainView;
	}

	public DateiauswahlController getDateiauswahlController() {
		return dateiauswahlController;
	}

	public void setDateiauswahlController(
			DateiauswahlController dateiauswahlController) {
		this.dateiauswahlController = dateiauswahlController;
	}

	public KonfigurationController getKonfigurationController() {
		return konfigurationController;
	}

	public void setKonfigurationController(
			KonfigurationController konfigurationController) {
		this.konfigurationController = konfigurationController;
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

	private Management() {
		fileImporter = new IFileImporterImpl();
		textvergleicher = new ITextvergleicherImpl();
	}

	public static Management getInstance() {
		if (instance == null)
			instance = new Management();
		return instance;
	}
}

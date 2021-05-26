package de.thkoeln.syp.mtc.gui.control;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;
import de.thkoeln.syp.mtc.datenhaltung.api.IJSONParseError;
import de.thkoeln.syp.mtc.datenhaltung.api.IMatrix;
import de.thkoeln.syp.mtc.datenhaltung.api.IXMLParseError;
import de.thkoeln.syp.mtc.datenhaltung.impl.IComparisonImpl;
import de.thkoeln.syp.mtc.datenhaltung.impl.IMatrixImpl;
import de.thkoeln.syp.mtc.gui.view.FileSelectionView;
import de.thkoeln.syp.mtc.gui.view.FileView;
import de.thkoeln.syp.mtc.gui.view.PopupView;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;
import de.thkoeln.syp.mtc.steuerung.services.IJSONvergleicher;
import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;
import de.thkoeln.syp.mtc.steuerung.services.IXMLvergleicher;

public class FileSelectionController extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4028005317141193789L;
	private Management management;
	private JPanel panel;
	private FileDialog fd;
	private JFileChooser fc;
	private File[] selection;
	private List<File> selectionList, lastComparisonFiles;
	private IMatrix matrix;
	private IFileImporter fileImporter;
	private ITextvergleicher textvergleicher;
	private IXMLvergleicher xmlvergleicher;
	private IJSONvergleicher jsonvergleicher;
	private int mode;

	private Logger logger;

	public FileSelectionController(FileSelectionView fileSelectionView) {
		// Management Variablen
		management = Management.getInstance();
		management.setFileSelectionController(this);
		fileImporter = management.getFileImporter();
		textvergleicher = management.getTextvergleicher();
		xmlvergleicher = management.getXmlvergleicher();
		jsonvergleicher = management.getJsonvergleicher();
		logger = management.getLogger();

		// Panel & neue Matrix fuer den naechsten Vergleich
		panel = new JPanel();
		matrix = new IMatrixImpl();
		lastComparisonFiles = new ArrayList<File>();

		// Implementation der Button Methoden
		fileSelectionView.addSetRootListener(new SetRootListener());
		fileSelectionView.addSearchListener(new SearchListener());
		fileSelectionView.addAddFilesListener(new AddFilesListener());
		fileSelectionView.addDeleteListener(new DeleteListener());
		fileSelectionView.addResetListener(new ResetListener());
		fileSelectionView.addCompareListener(new CompareListener());
		fileSelectionView.addFileViewListener(new FileViewListener());

		// Wurzelverzeichnis anzeigen
		fileSelectionView.getLblRootPath().setText(
				fileImporter.getConfig().getRootDir());

		// Variable um zu bestimmen ob nach Anzeige der Matrix die Selection
		// geaendert wurde
		management.setNewSelection(false);
	}

	class SetRootListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			fc = new JFileChooser();
			fc.getActionMap().get("viewTypeDetails").actionPerformed(null);
			fc.setCurrentDirectory(new File(fileImporter.getConfig()
					.getRootDir()));
			fc.setDialogTitle("Root path selection");
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.showOpenDialog(management.getFileSelectionView());
			fileImporter.setRootDir(fc.getSelectedFile());
			management.updateRootPath();
			management.getFileSelectionView().pack();
		}
	}

	class SearchListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			logger.setMessage("Searching for files..", logger.LEVEL_INFO);

			class rootSearchThread extends SwingWorker<String, Void> {
				List<File> reference;
				long start_time;

				@Override
				public String doInBackground() {
					start_time = System.nanoTime();
					reference = new ArrayList<File>(
							fileImporter.getTextdateien());

					// Importiert & startet Suche ueber Wurzelverzeichnis
					fileImporter.importTextRoot(management
							.getFileSelectionView().getTextFieldFileName()
							.getText()
							+ getFileExt());
					fileImporter.getRootImporter().start();
					try {
						fileImporter.getRootImporter().join();
					} catch (InterruptedException e) {
						logger.setMessage(e.toString(), logger.LEVEL_ERROR);
					}
					return null;
				}

				@Override
				public void done() {
					// Gibt einen Hinweis aus, falls keine neuen Dateien
					// gefunden wurden
					if (fileImporter.getTextdateien().equals(reference)) {
						new PopupView("Attention", "No more files found");
						logger.setMessage("No more files found \n",
								logger.LEVEL_INFO);
						return;
					}

					// Aktualisiert Anzeige
					setRdbtn(fileImporter.getTextdateien().isEmpty());
					updateListFilePath();
					long end_time = System.nanoTime();
					double time_difference = (end_time - start_time) / 1e6;
					String timeDiffAsString;
					if (time_difference > 1000) {
						time_difference /= 1000;
						time_difference = Math.round(time_difference * 100.0) / 100.0;
						timeDiffAsString = " (seek time: " + time_difference
								+ "s)";
					} else {
						timeDiffAsString = " (seek time: " + time_difference
								+ "ms)";
					}
					int foundFiles = fileImporter.getTextdateien().size();
					logger.setMessage("Found " + foundFiles + " files! "
							+ timeDiffAsString + "\n", logger.LEVEL_INFO);

					fileImporter.getConfig().setFilename(
							management.getFileSelectionView()
									.getTextFieldFileName().getText());
					fileImporter.getConfig().setFiletype(getFileExt());
					fileImporter.exportConfigdatei();
					mode = management.getFileSelectionView().getRadioButton();
				}
			}
			new rootSearchThread().execute();
		}
	}

	class AddFilesListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			// Windows Dateiauswahl
			fd = new FileDialog(management.getFileSelectionView(),
					"File selection", FileDialog.LOAD);
			fd.setLocationRelativeTo(null);
			fd.setMultipleMode(true);
			fd.setDirectory(fileImporter.getConfig().getRootDir());
			fd.setFile("*" + getFileExt());
			fd.setVisible(true);
			try {
				fd.setIconImage(ImageIO.read(new File("res/icon.png")));
			} catch (IOException ioe) {
				logger.setMessage(ioe.toString(), logger.LEVEL_ERROR);
			}

			// Die Dateien dem FileImporter uebergeben
			selection = fd.getFiles();
			selectionList = new ArrayList<File>();
			for (File f : selection) {
				selectionList.add(f);
			}
			fileImporter.importTextdateien(selectionList);

			// Fenster zentrieren
			management.getFileSelectionView().setLocationRelativeTo(null);

			// Ggf. Radio Buttons ausgrauen und gewaehlten Dateityp speichern
			setRdbtn(fileImporter.getTextdateien().isEmpty());
			fileImporter.getConfig().setFiletype(getFileExt());
			fileImporter.exportConfigdatei();
			mode = management.getFileSelectionView().getRadioButton();

			// JList Anzeige aktualisieren
			updateListFilePath();
		}
	}

	class DeleteListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Datei(en) aus dem FileImporter loeschen
			for (File f : getListSelection()) {
				fileImporter.deleteImport(f);
				management.setNewSelection(true);
			}

			// Anzeige aktualsieren
			updateListFilePath();
			if (management.getFileSelectionView().getModel().isEmpty())
				management.getFileSelectionView().getLblFileCount()
						.setText("0");
		}
	}

	class ResetListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Zueruecksetzen der Auswahl im FileImporter & der Anzeige
			fileImporter.deleteImports();
			management.getFileSelectionView().getModel().clear();
			management.getFileSelectionView().getLblFileCount().setText("0");
			setRdbtn(true);
			try {
				if (management.getCompareThread() != null
						&& management.getExecutorService() != null) {
						ExecutorService executorService = management.getExecutorService();
						executorService.shutdown();
						try {
						    if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
						        executorService.shutdownNow();
						    } 
						} catch (InterruptedException ex) {
						    executorService.shutdownNow();
						}
					management.getCompareThread().cancel(true);
					
				}
			} catch (Exception ex) {
				logger.setMessage(ex.toString(), logger.LEVEL_ERROR);
				management.getFileSelectionView().getBtnCompare()
						.setEnabled(true);
			}
			management.getFileSelectionView().getBtnCompare().setEnabled(true);
			management.setNewSelection(true);
		}
	}

	public class CompareListener implements ActionListener {
		private CompareThread compareThread;

		public void actionPerformed(ActionEvent e) {

			compareThread = new CompareThread();
			management.setCompareThread(compareThread);
			compareThread.execute();
		}

		public class CompareThread extends SwingWorker<Void, Integer> {
			int anzDateien;
			long start_time;
			Management management;
			JProgressBar progressBar;

			public void publishData(int i) {
				publish(i);
			}

			@Override
			protected Void doInBackground() throws Exception {

				management = Management.getInstance();
				progressBar = management.getMainView().getProgressBar();
				management.setCurrentFileSelection(management
						.getFileSelectionView().getModel());
				management.getFileSelectionView().getBtnCompare()
						.setEnabled(false);
				management.getFileSelectionView().getBtnAddFiles()
				.setEnabled(false);
				management.getFileSelectionView().getBtnDelete()
				.setEnabled(false);

				anzDateien = fileImporter.getTextdateien().size();
				if (anzDateien < 2) {
					return null;
				}

				fileImporter.deleteTempFiles();
				fileImporter.createTempFiles();
				xmlvergleicher.clearErrorList();
				logger.setMessage("Start comparing...", logger.LEVEL_INFO);
				start_time = System.nanoTime();

				// XML Vergleich
				if (mode == 1) {
					fileImporter.setTempFiles((xmlvergleicher
							.xmlPrepare(fileImporter.getTempFilesMap())));
					for (IXMLParseError error : xmlvergleicher.getErrorList())
						logger.setMessage(error.getMessage(),
								logger.LEVEL_WARNING);
				}

				// JSON Vergleich
				else if (mode == 2) {
					fileImporter.setTempFiles((jsonvergleicher
							.jsonPrepare(fileImporter.getTempFilesMap())));
					for (IJSONParseError error : jsonvergleicher.getErrorList())
						logger.setMessage(error.getMessage() + "\n",
								logger.LEVEL_WARNING);

				}

				// Vergleich
				textvergleicher.setFileImporter(fileImporter);
				IConfig currentConfig = fileImporter.getConfig();
				if (!(currentConfig.getKeepBlankLines()
						&& currentConfig.getKeepCapitalization()
						&& currentConfig.getKeepPuctuation() && currentConfig
							.getKeepWhitespaces())) {
					fileImporter.normTempFiles();
				}
				textvergleicher.getTempfilesFromHashMap(management
						.getFileImporter().getTempFilesMap());
				textvergleicher.getVergleiche(textvergleicher.getTempFiles());
				textvergleicher.createBatches();

				progressBar.setVisible(true);
				progressBar.setStringPainted(true);
				progressBar.setMinimum(0);
				progressBar.setMaximum(textvergleicher.getPaarungen().size());
				progressBar.setValue(0);

				ExecutorService es = Executors.newFixedThreadPool(Runtime
						.getRuntime().availableProcessors());
				management.setExecutorService(es);

				if (mode == 2
						&& fileImporter.getConfig()
								.isJsonUseSemanticComparison()) {
					for (int i = 0; i < textvergleicher.getBatches().size(); i++) {
						final List<IComparisonImpl> currentBatch = textvergleicher
								.getBatches().get(i).getInhalt();
						es.execute(new Runnable() {

							@Override
							public void run() {
								textvergleicher.compareJSON(currentBatch);
							}

						});

					}

					es.shutdown();
					boolean finished = es.awaitTermination(Long.MAX_VALUE,
							TimeUnit.MINUTES);
					if (finished) {
						logger.writeToLogFile("Comparison finished", true);
					} else {
						logger.writeToLogFile("Comparison error", true);
					}
				} else if (mode == 1
						&& fileImporter.getConfig()
								.isXmlUseSemanticComparison()) {
					for (int i = 0; i < textvergleicher.getBatches().size(); i++) {
						final List<IComparisonImpl> currentBatch = textvergleicher
								.getBatches().get(i).getInhalt();
						es.execute(new Runnable() {

							@Override
							public void run() {
								textvergleicher.compareXML(currentBatch);

							}

						});

					}

					es.shutdown();
					boolean finished = es.awaitTermination(Long.MAX_VALUE,
							TimeUnit.MINUTES);
					if (finished) {
						logger.writeToLogFile("Comparison finished", true);
					} else {
						logger.writeToLogFile("Comparison error", true);
					}
				} else {

					if (fileImporter.getConfig().getCompareLines() == false) {

						for (int i = 0; i < textvergleicher.getBatches().size(); i++) {
							final List<IComparisonImpl> currentBatch = textvergleicher
									.getBatches().get(i).getInhalt();
							es.execute(new Runnable() {

								@Override
								public void run() {
									textvergleicher
											.vergleicheUeberGanzesDokument(currentBatch);

								}

							});

						}

						es.shutdown();
						boolean finished = es.awaitTermination(Long.MAX_VALUE,
								TimeUnit.MINUTES);
						if (finished) {
							logger.writeToLogFile("Comparison finished", true);
						} else {
							logger.writeToLogFile("Comparison error", true);
						}

					} else {
						for (int i = 0; i < textvergleicher.getBatches().size(); i++) {
							final List<IComparisonImpl> currentBatch = textvergleicher
									.getBatches().get(i).getInhalt();
							es.execute(new Runnable() {

								@Override
								public void run() {
									textvergleicher
											.vergleicheZeilenweise(currentBatch);

								}

							});

						}
						es.shutdown();
						boolean finished = es.awaitTermination(Long.MAX_VALUE,
								TimeUnit.MINUTES);
						if (finished) {
							logger.writeToLogFile("Comparison finished", true);
						} else {
							logger.writeToLogFile("Comparison error", true);
						}
					}
				}
				management.setNewSelection(false);
				return null;
			}

			@Override
			protected void process(List<Integer> chunks) {
				int i = chunks.get(chunks.size() - 1);
				progressBar.setValue(i);
				progressBar.setToolTipText(i + " / " + progressBar.getMaximum()
						+ " comparisons finished");
			}

			@Override
			protected void done() {
				progressBar.setValue(progressBar.getMaximum());

				if (anzDateien < 2) {
					new PopupView("Error",
							"Please select at least two files for comparison");
					management.getFileSelectionView().getBtnCompare()
					.setEnabled(true);
					management.getFileSelectionView().getBtnAddFiles()
					.setEnabled(true);
					management.getFileSelectionView().getBtnDelete()
					.setEnabled(true);
					return;
				}
				textvergleicher.mergeBatches();


				textvergleicher.fillMatrix();
				management.setComparisons(textvergleicher.getPaarungen());

				management.getMainView().updateMatrix(
						textvergleicher.getMatrix(), anzDateien,
						management.getFileNames(anzDateien));

				lastComparisonFiles.clear();
				lastComparisonFiles.addAll(fileImporter.getTextdateien());

				long end_time = System.nanoTime();
				double time_difference = (end_time - start_time) / 1e6;
				String timeDiffAsString;
				if (time_difference > 1000) {
					time_difference /= 1000;
					time_difference = Math.round(time_difference * 100.0) / 100.0;
					timeDiffAsString = " (time taken: " + time_difference
							+ "s)";
				} else {
					timeDiffAsString = " (time taken: " + time_difference
							+ "ms)";
				}
				if (!xmlvergleicher.getErrorList().isEmpty()) {
					logger.setMessage(
							"A matrix with "
									+ anzDateien
									+ " files has been created, but the file selection contained "
									+ xmlvergleicher.getErrorList().size()
									+ " XML errors." + timeDiffAsString,
							logger.LEVEL_INFO);
				}

				else {
					logger.setMessage("A matrix with " + anzDateien
							+ " files has been created successfully!"
							+ timeDiffAsString, logger.LEVEL_INFO);
				}
				management.getFileSelectionView().getBtnCompare()
				.setEnabled(true);
				management.getFileSelectionView().getBtnAddFiles()
				.setEnabled(true);
				management.getFileSelectionView().getBtnDelete()
				.setEnabled(true);
				progressBar.setToolTipText(null);
				progressBar.setVisible(false);
			}
		}

	}

	class FileViewListener extends MouseAdapter {
		public void mouseClicked(MouseEvent evt) {

			JList<?> list = (JList<?>) evt.getSource();
			if (Management.getInstance().getFileView() == null) {
				management.setFileView(new FileView());
			}
			if (evt.getClickCount() == 2) {

				int index = list.locationToIndex(evt.getPoint());
				String fileName = management.getFileSelectionView().getModel()
						.get(index).split("\\|")[1].trim();
				File selectedFile = new File(fileName);

				try {
					BufferedReader input = new BufferedReader(
							new InputStreamReader(new FileInputStream(
									selectedFile), "UTF-8"));
					management.getFileView().getTextPane().setText(null);
					management.getFileView().getTextPane()
							.read(input, "Reading file...");
					management.getFileView().getTextPane().setCaretPosition(0);

					management.getFileView().setTitle(fileName);
					management.getFileView().setVisible(true);

				} catch (IOException e) {
					logger.setMessage(e.toString(), logger.LEVEL_ERROR);
				}

			}
		}
	}

	// Gibt je nach Radiobutton Auswahl die Dateiendung als String zurueck
	private String getFileExt() {
		if (management.getFileSelectionView().getRadioButton() == 0)
			return ".txt";
		if (management.getFileSelectionView().getRadioButton() == 1)
			return ".xml";
		if (management.getFileSelectionView().getRadioButton() == 2)
			return ".json";
		else
			return "";
	}

	// Alle Buttons enablen/disablen
	private void setRdbtn(boolean b) {
		management.getFileSelectionView().getRdbtnTxt().setEnabled(b);
		management.getFileSelectionView().getRdbtnXml().setEnabled(b);
		management.getFileSelectionView().getRdbtnJson().setEnabled(b);
		management.getFileSelectionView().getRdbtnAll().setEnabled(b);
	}

	// Aktualsieren der JList + Anzahl an Dateien
	public void updateListFilePath() {
		int importSize = fileImporter.getTextdateien().size();
		String[] fileNames = management.getFileNames(importSize);
		management.getFileSelectionView().getModel().clear();
		for (int i = 0; i < importSize; i++) {
			management
					.getFileSelectionView()
					.getModel()
					.addElement(
							fileNames[i]
									+ " |  "
									+ fileImporter.getTextdateien().get(i)
											.getAbsolutePath());

			management.getFileSelectionView().getLblFileCount()
					.setText(String.valueOf(importSize));

		}
	}

	// Konvertiert die Anzeige Liste in Liste der Pfade (ohne bspw. "AB: ")
	public List<String> convertToPaths(List<String> list) {
		List<String> pathList = new ArrayList<String>();
		for (int i = 0; i < management.getFileSelectionView().getListFilePath()
				.getSelectedValuesList().size(); i++) {
			pathList.add(list.get(i).split("\\|")[1].trim());
		}
		return pathList;
	}

	// Gibt die Liste an zurzeit ausgewaehlten Dateien wieder
	private List<File> getListSelection() {
		List<String> selectedPaths = convertToPaths(management
				.getFileSelectionView().getListFilePath()
				.getSelectedValuesList());
		List<File> selectedFiles = new ArrayList<File>();
		for (String s : selectedPaths) {
			Path path = Paths.get(s);
			selectedFiles.add(path.toFile());
		}
		return selectedFiles;
	}

	// - Getter -

	public File[] getAuswahl() {
		return selection;
	}

	public IMatrix getMatrix() {
		return matrix;
	}

	public int getMode() {
		return mode;
	}

}

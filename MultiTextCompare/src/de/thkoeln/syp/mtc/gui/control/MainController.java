package de.thkoeln.syp.mtc.gui.control;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import de.thkoeln.syp.mtc.gui.view.AboutView;
import de.thkoeln.syp.mtc.gui.view.ConfigView;
import de.thkoeln.syp.mtc.gui.view.FileSelectionView;
import de.thkoeln.syp.mtc.gui.view.HelpView;
import de.thkoeln.syp.mtc.gui.view.MainView;

public class MainController {
	private Management management;

	public MainController(MainView mainView) {
		management = Management.getInstance();
		management.setMainController(this);
		mainView.addFileSelectionListener(new FileSelectionListener());
		mainView.addConfigListener(new ConfigListener());
		mainView.addHelpListener(new HelpListener());
		mainView.addAboutListener(new AboutListener());
		mainView.addZoomListener(new ZoomListener());
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
			management.appendToLog("Opening help file...");
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

	class ZoomListener implements MouseWheelListener {
		public void mouseWheelMoved(MouseWheelEvent e) {
			int minimumHeight = 15;
			int maximumHeight = 300;
			int currentRowHeight = 0, updatedRowHeight = 0, currentColumnWidth, updatedColumnWidth;
			JTable tableMatrix = Management.getInstance().getMainView()
					.getTableMatrix();

			if (e.isControlDown()) {
				// Kleiner machen
				if (e.getWheelRotation() < 0) {

					// Höhe berechnen
					currentRowHeight = tableMatrix.getRowHeight();
					updatedRowHeight = (int) ((double) currentRowHeight * 0.9);
					if (updatedRowHeight >= minimumHeight) {
						tableMatrix.setRowHeight(updatedRowHeight);

						// Breite berechnen
						int numberOfColumns = tableMatrix.getColumnCount();
						for (int i = 0; i < numberOfColumns; i++) {
							tableMatrix
									.getColumnModel()
									.getColumn(i)
									.setPreferredWidth(
											(int) (updatedRowHeight * 1.25));

						}
					}
				}
				// Groesser machen
				else {

					// Höhe berechnen
					currentRowHeight = tableMatrix.getRowHeight();
					updatedRowHeight = (int) ((double) currentRowHeight * 1.1);
					if (updatedRowHeight <= maximumHeight) {
						tableMatrix.setRowHeight(updatedRowHeight);

						// Breite berechnen
						int numberOfColumns = tableMatrix.getColumnCount();
						for (int i = 0; i < numberOfColumns; i++) {
							tableMatrix
									.getColumnModel()
									.getColumn(i)
									.setPreferredWidth(
											(int) (updatedRowHeight * 1.25));
						}
					}
				}
			} else {
				Management.getInstance().getMainView().getMatrixScrollpane()
						.dispatchEvent(e);
			}
		}
	}
	
	
}
